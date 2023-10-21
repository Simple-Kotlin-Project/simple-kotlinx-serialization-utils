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

import io.github.edmondantes.serialization.decoding.UniqueDecoder
import io.github.edmondantes.serialization.element.AnyEncodedElement
import io.github.edmondantes.serialization.element.EncodedElement
import io.github.edmondantes.serialization.element.EncodedElementType
import io.github.edmondantes.serialization.element.takeIfComplex
import io.github.edmondantes.serialization.exception.DeserializationProcessException
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer

/**
 * [Decoder] that decode object from [EncodedElement]
 * @param element [EncodedElement] for decoding
 * @param ignoreDescriptors if false, [Decoder] will check equality between names [SerialDescriptor] in complex element and [SerialDescriptor] in decoding object (default: false)
 */
@OptIn(ExperimentalSerializationApi::class)
public open class ElementDecoder(
    protected val element: AnyEncodedElement,
    id: String? = null,
    protected val ignoreDescriptors: Boolean = false,
    override val serializersModule: SerializersModule = EmptySerializersModule(),
) : UniqueDecoder {
    override val id: String = id ?: DEFAULT_ID

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        if (!ignoreDescriptors && element.descriptorName != null && descriptor.serialName != element.descriptorName) {
            throw DeserializationProcessException(
                "Can not deserialize object with different SerialDescriptor: " +
                    "expected <${element.descriptorName}>, actual: <${descriptor.serialName}>",
            )
        }

        if (!element.type.isComplex()) {
            throw DeserializationProcessException("Can not deserialize simple element as complex")
        }

        val complexElement =
            element.takeIfComplex()
                ?: throw DeserializationProcessException("Can not get ComplexEncodedElement from element for decoding")

        return CompositeElementDecoder(
            element = complexElement,
            id = id,
            serializersModule = serializersModule,
        )
    }

    override fun decodeBoolean(): Boolean = decode()

    override fun decodeByte(): Byte = decode()

    override fun decodeChar(): Char = decode()

    override fun decodeShort(): Short = decode()

    override fun decodeInt(): Int = decode()

    override fun decodeLong(): Long = decode()

    override fun decodeFloat(): Float = decode()

    override fun decodeDouble(): Double = decode()

    override fun decodeString(): String = decode()

    override fun decodeEnum(enumDescriptor: SerialDescriptor): Int = enumDescriptor.getElementIndex(decode())

    override fun decodeInline(descriptor: SerialDescriptor): Decoder = this

    override fun decodeNotNullMark(): Boolean = EncodedElementType.NULL != element.type

    override fun decodeNull(): Nothing? = null

    private fun <T> decode(): T = element.value.value as T

    public companion object {
        /**
         * Default id for [ElementDecoder]
         */
        public const val DEFAULT_ID: String = "io.github.edmondantes.serialization.decoding.element.ElementDecoder"
    }
}

/**
 * Decode [AnyEncodedElement] to object [T]
 */
public inline fun <reified T> AnyEncodedElement.decode(): T = serializer<T>().deserialize(ElementDecoder(this))
