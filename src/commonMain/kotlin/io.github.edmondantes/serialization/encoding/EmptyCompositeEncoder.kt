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
package io.github.edmondantes.serialization.encoding

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

/**
 * [CompositeEncoder] which doing nothing
 * @see CompositeEncoder
 * @see UniqueCompositeEncoder
 */
public object EmptyCompositeEncoder : UniqueCompositeEncoder {

    override val id: String = "io.github.edmondantes.serialization.encoding.EmptyCompositeEncoder"

    override val serializersModule: SerializersModule = EmptySerializersModule()

    override fun encodeBooleanElement(descriptor: SerialDescriptor, index: Int, value: Boolean) {}
    override fun encodeByteElement(descriptor: SerialDescriptor, index: Int, value: Byte) {}
    override fun encodeCharElement(descriptor: SerialDescriptor, index: Int, value: Char) {}
    override fun encodeDoubleElement(descriptor: SerialDescriptor, index: Int, value: Double) {}
    override fun encodeFloatElement(descriptor: SerialDescriptor, index: Int, value: Float) {}
    override fun encodeInlineElement(descriptor: SerialDescriptor, index: Int): Encoder = EmptyEncoder
    override fun encodeIntElement(descriptor: SerialDescriptor, index: Int, value: Int) {}
    override fun encodeLongElement(descriptor: SerialDescriptor, index: Int, value: Long) {}

    @ExperimentalSerializationApi
    override fun <T : Any> encodeNullableSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T?,
    ) {
    }

    override fun <T> encodeSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T,
    ) {
    }

    override fun encodeShortElement(descriptor: SerialDescriptor, index: Int, value: Short) {}
    override fun encodeStringElement(descriptor: SerialDescriptor, index: Int, value: String) {}
    override fun endStructure(descriptor: SerialDescriptor) {}
}
