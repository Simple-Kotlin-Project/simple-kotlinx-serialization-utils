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
@file:Suppress("UNCHECKED_CAST")

package io.github.edmondantes.serialization.decoding.element

import io.github.edmondantes.serialization.decoding.UniqueCompositeDecoder
import io.github.edmondantes.serialization.element.ComplexEncodedElement
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder.Companion.DECODE_DONE
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

/**
 * [CompositeElementDecoder] that decode object from [CompositeElementDecoder]
 * @param element [ComplexEncodedElement] for decoding
 */
@OptIn(ExperimentalSerializationApi::class)
public open class CompositeElementDecoder(
    protected val element: ComplexEncodedElement,
    id: String? = null,
    override val serializersModule: SerializersModule = EmptySerializersModule(),
) : UniqueCompositeDecoder {
    protected var lastId: Int = element.value.valueOrNull?.size ?: DECODE_DONE
    protected var nextId: Int = if (lastId > 0) 0 else DECODE_DONE

    override val id: String = id ?: DEFAULT_ID

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int = if (nextId < lastId) nextId++ else DECODE_DONE

    override fun decodeBooleanElement(
        descriptor: SerialDescriptor,
        index: Int,
    ): Boolean = decode(index)

    override fun decodeByteElement(
        descriptor: SerialDescriptor,
        index: Int,
    ): Byte = decode(index)

    override fun decodeCharElement(
        descriptor: SerialDescriptor,
        index: Int,
    ): Char = decode(index)

    override fun decodeShortElement(
        descriptor: SerialDescriptor,
        index: Int,
    ): Short = decode(index)

    override fun decodeIntElement(
        descriptor: SerialDescriptor,
        index: Int,
    ): Int = decode(index)

    override fun decodeLongElement(
        descriptor: SerialDescriptor,
        index: Int,
    ): Long = decode(index)

    override fun decodeFloatElement(
        descriptor: SerialDescriptor,
        index: Int,
    ): Float = decode(index)

    override fun decodeDoubleElement(
        descriptor: SerialDescriptor,
        index: Int,
    ): Double = decode(index)

    override fun decodeStringElement(
        descriptor: SerialDescriptor,
        index: Int,
    ): String = decode(index)

    override fun decodeInlineElement(
        descriptor: SerialDescriptor,
        index: Int,
    ): Decoder =
        ElementDecoder(
            element = element.value.value[index],
            id = id,
            ignoreDescriptors = false,
            serializersModule = serializersModule,
        )

    override fun <T : Any> decodeNullableSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        deserializer: DeserializationStrategy<T?>,
        previousValue: T?,
    ): T? =
        deserializer.deserialize(
            ElementDecoder(
                element = element.value.value[index],
                id = id,
                ignoreDescriptors = false,
                serializersModule = serializersModule,
            ),
        )

    override fun <T> decodeSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        deserializer: DeserializationStrategy<T>,
        previousValue: T?,
    ): T =
        deserializer.deserialize(
            ElementDecoder(
                element = element.value.value[index],
                id = id,
                ignoreDescriptors = false,
                serializersModule = serializersModule,
            ),
        )

    override fun endStructure(descriptor: SerialDescriptor) {}

    private fun <T> decode(index: Int): T = element.value.value[index].value.value as T

    public companion object {
        /**
         * Default id for [CompositeElementDecoder]
         */
        public const val DEFAULT_ID: String =
            "io.github.edmondantes.serialization.decoding.element.CompositeElementDecoder"
    }
}
