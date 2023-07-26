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
package io.github.edmondantes.serialization.encoding.element

import io.github.edmondantes.serialization.encoding.UniqueCompositeEncoder
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

public class CompositeElementEncoder(
    private val builder: AbstractStructureEncodingElement.Builder<*, *>,
    private val parentBuilder: AbstractStructureEncodingElement.Builder<*, *>? = null,
    id: String? = null,
) : UniqueCompositeEncoder {
    override val id: String =
        id ?: "io.github.edmondantes.serialization.encoding.element.CompositeElementEncoder#${ID++}"
    override val serializersModule: SerializersModule
        get() = EmptySerializersModule()

    override fun encodeBooleanElement(descriptor: SerialDescriptor, index: Int, value: Boolean) {
        encode(descriptor, index, value)
    }

    override fun encodeByteElement(descriptor: SerialDescriptor, index: Int, value: Byte) {
        encode(descriptor, index, value)
    }

    override fun encodeCharElement(descriptor: SerialDescriptor, index: Int, value: Char) {
        encode(descriptor, index, value)
    }

    override fun encodeShortElement(descriptor: SerialDescriptor, index: Int, value: Short) {
        encode(descriptor, index, value)
    }

    override fun encodeIntElement(descriptor: SerialDescriptor, index: Int, value: Int) {
        encode(descriptor, index, value)
    }

    override fun encodeLongElement(descriptor: SerialDescriptor, index: Int, value: Long) {
        encode(descriptor, index, value)
    }

    override fun encodeFloatElement(descriptor: SerialDescriptor, index: Int, value: Float) {
        encode(descriptor, index, value)
    }

    override fun encodeDoubleElement(descriptor: SerialDescriptor, index: Int, value: Double) {
        encode(descriptor, index, value)
    }

    override fun encodeStringElement(descriptor: SerialDescriptor, index: Int, value: String) {
        encode(descriptor, index, value)
    }

    override fun encodeInlineElement(descriptor: SerialDescriptor, index: Int): Encoder {
        error("Not support inline element")
    }

    @ExperimentalSerializationApi
    override fun <T : Any> encodeNullableSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T?,
    ) {
        if (value == null) {
            builder.add(SimpleEncodingElement(EncodingElementType.NULL, value, descriptor, index))
        } else {
            serializer.serialize(ElementEncoder(null, builder, descriptor, index), value)
        }
    }

    override fun <T> encodeSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T,
    ) {
        serializer.serialize(ElementEncoder(null, builder, descriptor, index), value!!)
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        parentBuilder?.add(builder.build())
    }

    private fun encode(descriptor: SerialDescriptor, index: Int, value: Any?) {
        builder.add(
            SimpleEncodingElement(
                if (value == null) EncodingElementType.NULL else EncodingElementType.PROPERTY,
                value,
                descriptor,
                index,
            ),
        )
    }

    private companion object {
        var ID = 0
    }
}
