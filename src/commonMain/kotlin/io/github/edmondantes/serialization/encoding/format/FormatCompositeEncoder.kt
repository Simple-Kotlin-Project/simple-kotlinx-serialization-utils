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

import io.github.edmondantes.serialization.annotation.SerializationFormat
import io.github.edmondantes.serialization.encoding.delegate.DelegateCompositeEncoder
import io.github.edmondantes.serialization.util.DelegateIdResolveStrategy
import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialFormat
import kotlinx.serialization.SerializationException
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.StringFormat
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer

/**
 * This [CompositeEncoder] helps to serializer properties as another formats.
 * For example, you can start serialization of json, and serialize one property as xml string.
 * @param delegate original format [CompositeEncoder]
 * @param formats [Map] when keys is format's ids and values is formats.
 * @see SerializationFormat
 */
public class FormatCompositeEncoder internal constructor(
    delegate: CompositeEncoder,
    private val formats: Map<String, SerialFormat>,
    serializersModule: SerializersModule = EmptySerializersModule(),
) : DelegateCompositeEncoder(
        delegate = delegate,
        currentId = "io.github.edmondantes.serialization.encoding.format.FormatCompositeEncoder",
        idResolveStrategy = DelegateIdResolveStrategy.DELEGATE,
        serializersModule = serializersModule,
    ) {
    override fun encodeBooleanElement(
        descriptor: SerialDescriptor,
        index: Int,
        value: Boolean,
    ) {
        encodeElement(descriptor, index, value, delegate::encodeBooleanElement)
    }

    override fun encodeByteElement(
        descriptor: SerialDescriptor,
        index: Int,
        value: Byte,
    ) {
        encodeElement(descriptor, index, value, delegate::encodeByteElement)
    }

    override fun encodeCharElement(
        descriptor: SerialDescriptor,
        index: Int,
        value: Char,
    ) {
        encodeElement(descriptor, index, value, delegate::encodeCharElement)
    }

    override fun encodeShortElement(
        descriptor: SerialDescriptor,
        index: Int,
        value: Short,
    ) {
        encodeElement(descriptor, index, value, delegate::encodeShortElement)
    }

    override fun encodeIntElement(
        descriptor: SerialDescriptor,
        index: Int,
        value: Int,
    ) {
        encodeElement(descriptor, index, value, delegate::encodeIntElement)
    }

    override fun encodeLongElement(
        descriptor: SerialDescriptor,
        index: Int,
        value: Long,
    ) {
        encodeElement(descriptor, index, value, delegate::encodeLongElement)
    }

    override fun encodeFloatElement(
        descriptor: SerialDescriptor,
        index: Int,
        value: Float,
    ) {
        encodeElement(descriptor, index, value, delegate::encodeFloatElement)
    }

    override fun encodeDoubleElement(
        descriptor: SerialDescriptor,
        index: Int,
        value: Double,
    ) {
        encodeElement(descriptor, index, value, delegate::encodeDoubleElement)
    }

    override fun encodeStringElement(
        descriptor: SerialDescriptor,
        index: Int,
        value: String,
    ) {
        encodeElement(descriptor, index, value, delegate::encodeStringElement)
    }

    override fun encodeInlineElement(
        descriptor: SerialDescriptor,
        index: Int,
    ): Encoder = FormatEncoder(delegate.encodeInlineElement(descriptor, index))

    override fun <T> encodeSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T,
    ) {
        encodeElement(
            descriptor,
            index,
            serializer,
            value,
        ) {
            super.encodeSerializableElement(descriptor, index, serializer, value)
        }
    }

    @ExperimentalSerializationApi
    override fun <T : Any> encodeNullableSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T?,
    ) {
        val defaultAction = { super.encodeNullableSerializableElement(descriptor, index, serializer, value) }
        encodeElement(descriptor, index, serializer, value ?: return defaultAction(), defaultAction)
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        delegate.endStructure(descriptor)
    }

    override fun transformerEncoder(encoder: Encoder): Encoder = FormatEncoder(encoder, formats, serializersModule)

    override fun transformerCompositeEncoder(encoder: CompositeEncoder): CompositeEncoder =
        FormatCompositeEncoder(encoder, formats, serializersModule)

    /**
     * Please don't try to merge this method to one below, because we can not use reified
     * in methods encodeSerializableElement and encodeNullableSerializableElement
     */
    @Suppress("UNCHECKED_CAST")
    private inline fun <T> encodeElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T,
        defaultAction: () -> Unit,
    ) {
        val (formatValue, formatSerializer) =
            getFormatValueAndSerializer(
                descriptor,
                index,
                serializer,
                value,
                defaultAction,
            ) ?: return

        delegate.encodeSerializableElement(
            descriptor,
            index,
            formatSerializer as SerializationStrategy<Any>,
            formatValue,
        )
    }

    private inline fun <reified T> encodeElement(
        descriptor: SerialDescriptor,
        index: Int,
        value: T,
        defaultAction: (SerialDescriptor, Int, T) -> Unit,
    ) {
        encodeElement(descriptor, index, serializersModule.serializer<T>(), value) {
            defaultAction(descriptor, index, value)
        }
    }

    private inline fun <T> getFormatValueAndSerializer(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T,
        defaultAction: () -> Unit,
    ): Pair<Any, SerializationStrategy<*>>? {
        val (id, format) = getFormat(descriptor, index) ?: return defaultAction().let { null }

        return try {
            encodeByFormat(format, serializer, value)
        } catch (e: SerializationException) {
            throw SerializationException("Can not serialize value by format with id '$id'", e)
        }
    }

    private fun <T> encodeByFormat(
        format: SerialFormat,
        serializer: SerializationStrategy<T>,
        value: T,
    ): Pair<Any, SerializationStrategy<*>> =
        when (format) {
            is StringFormat -> format.encodeToString(serializer, value) to serializersModule.serializer<String>()
            is BinaryFormat -> format.encodeToByteArray(serializer, value) to serializersModule.serializer<ByteArray>()
            else -> throw SerializationException("Can not serialize value with unknown format. Support only StringFormat and BinaryFormat")
        }

    private fun getFormat(
        descriptor: SerialDescriptor,
        index: Int,
    ): Pair<String, SerialFormat>? =
        getFormatId(descriptor, index)?.let { id ->
            formats[id]?.let { id to it }
        }

    @OptIn(ExperimentalSerializationApi::class)
    private fun getFormatId(
        descriptor: SerialDescriptor,
        index: Int,
    ): String? =
        (
            descriptor
                .getElementAnnotations(index)
                .filterIsInstance<SerializationFormat>()
                .firstOrNull()
                ?: descriptor
                    .getElementDescriptor(index)
                    .annotations
                    .filterIsInstance<SerializationFormat>()
                    .firstOrNull()
        )?.id
}
