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
 * This class helps to broadcast encoding for some encoders
 * @param encoders Encoders which get method calls
 */
public class BroadcastCompositeEncoder(private val encoders: List<CompositeEncoder>) : CompositeEncoder {

    public constructor(vararg encoders: CompositeEncoder) : this(encoders.toList())

    override val serializersModule: SerializersModule = EmptySerializersModule()

    override fun encodeBooleanElement(descriptor: SerialDescriptor, index: Int, value: Boolean) {
        delegate { it.encodeBooleanElement(descriptor, index, value) }
    }

    override fun encodeByteElement(descriptor: SerialDescriptor, index: Int, value: Byte) {
        delegate { it.encodeByteElement(descriptor, index, value) }
    }

    override fun encodeCharElement(descriptor: SerialDescriptor, index: Int, value: Char) {
        delegate { it.encodeCharElement(descriptor, index, value) }
    }

    override fun encodeDoubleElement(descriptor: SerialDescriptor, index: Int, value: Double) {
        delegate { it.encodeDoubleElement(descriptor, index, value) }
    }

    override fun encodeFloatElement(descriptor: SerialDescriptor, index: Int, value: Float) {
        delegate { it.encodeFloatElement(descriptor, index, value) }
    }

    override fun encodeInlineElement(descriptor: SerialDescriptor, index: Int): Encoder =
        BroadcastEncoder(delegate { it.encodeInlineElement(descriptor, index) })

    override fun encodeIntElement(descriptor: SerialDescriptor, index: Int, value: Int) {
        delegate { it.encodeIntElement(descriptor, index, value) }
    }

    override fun encodeLongElement(descriptor: SerialDescriptor, index: Int, value: Long) {
        delegate { it.encodeLongElement(descriptor, index, value) }
    }

    @ExperimentalSerializationApi
    override fun <T : Any> encodeNullableSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T?,
    ) {
        delegate { it.encodeNullableSerializableElement(descriptor, index, serializer, value) }
    }

    override fun <T> encodeSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T,
    ) {
        delegate { it.encodeSerializableElement(descriptor, index, serializer, value) }
    }

    override fun encodeShortElement(descriptor: SerialDescriptor, index: Int, value: Short) {
        delegate { it.encodeShortElement(descriptor, index, value) }
    }

    override fun encodeStringElement(descriptor: SerialDescriptor, index: Int, value: String) {
        delegate { it.encodeStringElement(descriptor, index, value) }
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        delegate { it.endStructure(descriptor) }
    }

    public fun supportCircular(byHashCode: Boolean = true, useRefEquality: Boolean = false): CompositeEncoder =
        BroadcastCompositeEncoder(encoders.map { it.supportCircular(byHashCode, useRefEquality) })

    public fun supportCircular(
        objForSerialization: Any,
        byHashCode: Boolean = true,
        useRefEquality: Boolean = false,
    ): CompositeEncoder =
        BroadcastCompositeEncoder(encoders.map { it.supportCircular(objForSerialization, byHashCode, useRefEquality) })

    private inline fun <T> delegate(crossinline func: (CompositeEncoder) -> T): List<T> =
        encoders.map(func)
}
