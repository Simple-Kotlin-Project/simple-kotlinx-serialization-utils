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
package io.github.edmondantes.util

import io.github.edmondantes.serialization.encoding.UniqueCompositeEncoder
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

class TestCompositeEncoder(
    override val id: String,
    private val encoderConstructor: (SerialDescriptor, Int?) -> Encoder = { _, _ -> TestEncoder(id) },
    private val _elements: MutableList<TestEncodingElements> = ArrayList(),
) : List<TestEncodingElements> by _elements, UniqueCompositeEncoder {

    @Suppress("UNUSED")
    val elements: List<TestEncodingElements>
        get() = _elements

    override val serializersModule: SerializersModule = EmptySerializersModule()

    override fun encodeBooleanElement(descriptor: SerialDescriptor, index: Int, value: Boolean) {
        addNewElement("encodeBooleanElement", descriptor, index, value)
    }

    override fun encodeByteElement(descriptor: SerialDescriptor, index: Int, value: Byte) {
        addNewElement("encodeByteElement", descriptor, index, value)
    }

    override fun encodeCharElement(descriptor: SerialDescriptor, index: Int, value: Char) {
        addNewElement("encodeCharElement", descriptor, index, value)
    }

    override fun encodeDoubleElement(descriptor: SerialDescriptor, index: Int, value: Double) {
        addNewElement("encodeDoubleElement", descriptor, index, value)
    }

    override fun encodeFloatElement(descriptor: SerialDescriptor, index: Int, value: Float) {
        addNewElement("encodeFloatElement", descriptor, index, value)
    }

    override fun encodeInlineElement(descriptor: SerialDescriptor, index: Int): Encoder {
        addNewElement("encodeInlineElement", descriptor, index, null)
        return encoderConstructor(descriptor, index)
    }

    override fun encodeIntElement(descriptor: SerialDescriptor, index: Int, value: Int) {
        addNewElement("encodeIntElement", descriptor, index, value)
    }

    override fun encodeLongElement(descriptor: SerialDescriptor, index: Int, value: Long) {
        addNewElement("encodeLongElement", descriptor, index, value)
    }

    @ExperimentalSerializationApi
    override fun <T : Any> encodeNullableSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T?,
    ) {
        val encoder = encoderConstructor(descriptor, index)
        addNewElement("encodeNullableSerializableElement", descriptor, index, encoder)
        if (value != null) {
            serializer.serialize(encoder, value)
        } else {
            encoder.encodeNull()
        }
    }

    override fun <T> encodeSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T,
    ) {
        val encoder = encoderConstructor(descriptor, index)
        addNewElement("encodeSerializableElement", descriptor, index, encoder)
        serializer.serialize(encoder, value)
    }

    override fun encodeShortElement(descriptor: SerialDescriptor, index: Int, value: Short) {
        addNewElement("encodeShortElement", descriptor, index, value)
    }

    override fun encodeStringElement(descriptor: SerialDescriptor, index: Int, value: String) {
        addNewElement("encodeStringElement", descriptor, index, value)
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        addNewElement("endStructure", descriptor, null, null)
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun addNewElement(methodName: String, descriptor: SerialDescriptor, index: Int?, value: Any?) {
        _elements.add(
            TestEncodingElements(
                methodName,
                descriptor.serialName,
                index,
                index?.let { descriptor.getElementName(index) },
                value,
            ),
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other || other is TestCompositeEncoder && this._elements == other._elements) {
            return true
        }

        if (other is List<*>) {
            if (other.size != _elements.size) {
                return false
            }
            for (i in other.indices) {
                val elem0 = other[i]
                val elem1 = _elements[i]

                if (elem0 != elem1) {
                    return false
                }
            }
            return true
        }

        return false
    }
    override fun hashCode(): Int =
        _elements.hashCode()

    override fun toString(): String = _elements.toString()
}
