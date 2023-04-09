/*
 * Copyright (c) 2023. Ilia Loginov
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.edmondantes.serialization.encoding.format

import io.github.edmondantes.serialization.annotation.EncodeBy
import io.github.edmondantes.serialization.encoding.CustomSerializationStrategy
import io.github.edmondantes.serialization.getElementAllAnnotation
import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.StringFormat
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer

public class FormatCompositeEncoder internal constructor(
    private val defaultCompositeEncoder: CompositeEncoder,
    private val formats: Map<String, EncodeFormat>,
) : CompositeEncoder {

    override val serializersModule: SerializersModule
        get() = defaultCompositeEncoder.serializersModule

    override fun encodeBooleanElement(descriptor: SerialDescriptor, index: Int, value: Boolean) {
        encode(descriptor, index, value, defaultCompositeEncoder::encodeBooleanElement)
    }

    override fun encodeByteElement(descriptor: SerialDescriptor, index: Int, value: Byte) {
        encode(descriptor, index, value, defaultCompositeEncoder::encodeByteElement)
    }

    override fun encodeCharElement(descriptor: SerialDescriptor, index: Int, value: Char) {
        encode(descriptor, index, value, defaultCompositeEncoder::encodeCharElement)
    }

    override fun encodeShortElement(descriptor: SerialDescriptor, index: Int, value: Short) {
        encode(descriptor, index, value, defaultCompositeEncoder::encodeShortElement)
    }

    override fun encodeIntElement(descriptor: SerialDescriptor, index: Int, value: Int) {
        encode(descriptor, index, value, defaultCompositeEncoder::encodeIntElement)
    }

    override fun encodeLongElement(descriptor: SerialDescriptor, index: Int, value: Long) {
        encode(descriptor, index, value, defaultCompositeEncoder::encodeLongElement)
    }

    override fun encodeFloatElement(descriptor: SerialDescriptor, index: Int, value: Float) {
        encode(descriptor, index, value, defaultCompositeEncoder::encodeFloatElement)
    }

    override fun encodeDoubleElement(descriptor: SerialDescriptor, index: Int, value: Double) {
        encode(descriptor, index, value, defaultCompositeEncoder::encodeDoubleElement)
    }

    override fun encodeStringElement(descriptor: SerialDescriptor, index: Int, value: String) {
        encode(descriptor, index, value, defaultCompositeEncoder::encodeStringElement)
    }

    override fun encodeInlineElement(descriptor: SerialDescriptor, index: Int): Encoder =
        defaultCompositeEncoder.encodeInlineElement(descriptor, index).let { encoder ->
            getEncodeByAnnotation(descriptor, index)?.let { FormatEncoder(encoder, formats) } ?: encoder
        }

    @ExperimentalSerializationApi
    override fun <T : Any> encodeNullableSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T?,
    ) {
        val defaultAction =
            {
                defaultCompositeEncoder.encodeNullableSerializableElement(
                    descriptor,
                    index,
                    CustomSerializationStrategy(serializer) { FormatEncoder(it, formats) },
                    value,
                )
            }

        encodeSerializableElement(descriptor, index, serializer, value ?: return defaultAction(), defaultAction)
    }

    override fun <T> encodeSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T,
    ) {
        encodeSerializableElement(
            descriptor,
            index,
            serializer,
            value,
        ) {
            defaultCompositeEncoder.encodeSerializableElement(
                descriptor,
                index,
                CustomSerializationStrategy(serializer) { FormatEncoder(it, formats) },
                value,
            )
        }
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        defaultCompositeEncoder.endStructure(descriptor)
    }

    /**
     * Please don't try to merge this method to one below, because we can not use inline and reified
     * in methods encodeSerializableElement and encodeNullableSerializableElement
     */
    @Suppress("UNCHECKED_CAST")
    private fun <T> encodeSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T,
        defaultAction: () -> Unit,
    ) {
        val (id, seriazableFormat) = getFormat(descriptor, index)
            ?: return defaultAction()

        val (formatValue, formatSerializer) = encode(
            seriazableFormat,
            { format -> format.encodeToString(serializer, value) },
            { format -> format.encodeToByteArray(serializer, value) },
        ) ?: error("Format with id '$id' has not one of field 'stringFormat' or 'binaryFormat'")

        defaultCompositeEncoder.encodeSerializableElement(
            descriptor,
            index,
            formatSerializer as SerializationStrategy<Any>,
            formatValue,
        )
    }

    @Suppress("UNCHECKED_CAST")
    private inline fun <reified T> encode(
        descriptor: SerialDescriptor,
        index: Int,
        value: T,
        defaultAction: (SerialDescriptor, Int, T) -> Unit,
    ) {
        val (id, formatSerializer) = getFormat(descriptor, index) ?: return defaultAction(descriptor, index, value)

        val (formatValue, serializer) = encode(
            formatSerializer,
            { format -> format.encodeToString(value) },
            { format -> format.encodeToByteArray(value) },
        )
            ?: error("Format with id '$id' has not one of field 'stringFormat' or 'binaryFormat'")

        defaultCompositeEncoder.encodeSerializableElement(
            descriptor,
            index,
            serializer as SerializationStrategy<Any>,
            formatValue,
        )
    }

    private inline fun encode(
        format: EncodeFormat,
        stringEncodeAction: (StringFormat) -> String,
        binaryEncodeAction: (BinaryFormat) -> ByteArray,
    ): Pair<Any, SerializationStrategy<*>>? {
        return format.stringFormat?.let { stringEncodeAction(it) }?.let { it to STRING_SERIALIZER }
            ?: format.binaryFormat?.let { binaryEncodeAction(it) }?.let { it to BYTE_ARRAY_SERIALIZER }
    }

    private fun getFormat(descriptor: SerialDescriptor, index: Int): Pair<String, EncodeFormat>? =
        getEncodeByAnnotation(descriptor, index)?.id?.let { id ->
            formats[id]?.let { id to it }
        }

    @OptIn(ExperimentalSerializationApi::class)
    private fun getEncodeByAnnotation(descriptor: SerialDescriptor, index: Int): EncodeBy? =
        descriptor.getElementAllAnnotation(index).filterIsInstance<EncodeBy>().firstOrNull()
            ?: descriptor.annotations.filterIsInstance<EncodeBy>().firstOrNull()

    private companion object {
        val STRING_SERIALIZER = serializer<String>()
        val BYTE_ARRAY_SERIALIZER = serializer<ByteArray>()
    }
}
