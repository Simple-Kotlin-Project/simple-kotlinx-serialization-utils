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

import io.github.edmondantes.serialization.encoding.UniqueEncoder
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

class TestEncoder(
    override val id: String,
    private val compositeEncoderConstructor: (SerialDescriptor, UniqueEncoder) -> CompositeEncoder = { _, encoder ->
        TestCompositeEncoder(encoder.id)
    },
    private val _elements: MutableList<TestEncodingElements> = ArrayList(),
) : List<TestEncodingElements> by _elements, UniqueEncoder {

    @Suppress("UNUSED")
    val elements: List<TestEncodingElements>
        get() = _elements

    override val serializersModule: SerializersModule = EmptySerializersModule()

    override fun beginCollection(descriptor: SerialDescriptor, collectionSize: Int): CompositeEncoder {
        val compositeEncoder = compositeEncoderConstructor(descriptor, this)
        addNewElement("beginCollection", descriptor, null, compositeEncoder)
        return compositeEncoder
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        val compositeEncoder = compositeEncoderConstructor(descriptor, this)
        addNewElement("beginStructure", descriptor, null, compositeEncoder)
        return compositeEncoder
    }

    override fun encodeBoolean(value: Boolean) {
        addNewElement("encodeBoolean", null, null, value)
    }

    override fun encodeByte(value: Byte) {
        addNewElement("encodeByte", null, null, value)
    }

    override fun encodeChar(value: Char) {
        addNewElement("encodeChar", null, null, value)
    }

    override fun encodeDouble(value: Double) {
        addNewElement("encodeDouble", null, null, value)
    }

    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) {
        addNewElement("encodeEnum", enumDescriptor, index, null)
    }

    override fun encodeFloat(value: Float) {
        addNewElement("encodeFloat", null, null, value)
    }

    override fun encodeInline(descriptor: SerialDescriptor): Encoder {
        addNewElement("encodeInline", descriptor, null, null)
        return this
    }

    override fun encodeInt(value: Int) {
        addNewElement("encodeInt", null, null, value)
    }

    override fun encodeLong(value: Long) {
        addNewElement("encodeLong", null, null, value)
    }

    @ExperimentalSerializationApi
    override fun encodeNull() {
        addNewElement("encodeNull", null, null, null)
    }

    override fun encodeShort(value: Short) {
        addNewElement("encodeShort", null, null, value)
    }

    override fun encodeString(value: String) {
        addNewElement("encodeString", null, null, value)
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun addNewElement(methodName: String, descriptor: SerialDescriptor?, index: Int?, value: Any?) {
        _elements.add(
            TestEncodingElements(
                methodName,
                descriptor?.serialName,
                index,
                index?.let { descriptor?.getElementName(index) },
                value,
            ),
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other || other is TestEncoder && this._elements == other._elements) {
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
