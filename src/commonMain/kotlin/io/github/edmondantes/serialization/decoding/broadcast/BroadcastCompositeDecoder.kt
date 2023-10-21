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
package io.github.edmondantes.serialization.decoding.broadcast

import io.github.edmondantes.serialization.decoding.sequence.CompositeDecoderSequence
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

/**
 * This class helps to broadcast decoding events for some [CompositeDecoder]
 * @param delegate original [CompositeDecoder] which will manage decoding process
 * @param subDecoders [CompositeDecoder] that will get copy of decoding events for [delegate]
 */
public class BroadcastCompositeDecoder(
    private val delegate: CompositeDecoder,
    private val subDecoders: List<CompositeDecoder>,
    override val serializersModule: SerializersModule = EmptySerializersModule(),
) : CompositeDecoder, CompositeDecoderSequence<BroadcastCompositeDecoder> {
    override fun decodeElementIndex(descriptor: SerialDescriptor): Int = delegate { decodeElementIndex(descriptor) }

    override fun decodeBooleanElement(
        descriptor: SerialDescriptor,
        index: Int,
    ): Boolean = delegate { decodeBooleanElement(descriptor, index) }

    override fun decodeByteElement(
        descriptor: SerialDescriptor,
        index: Int,
    ): Byte = delegate { decodeByteElement(descriptor, index) }

    override fun decodeCharElement(
        descriptor: SerialDescriptor,
        index: Int,
    ): Char = delegate { decodeCharElement(descriptor, index) }

    override fun decodeShortElement(
        descriptor: SerialDescriptor,
        index: Int,
    ): Short = delegate { decodeShortElement(descriptor, index) }

    override fun decodeIntElement(
        descriptor: SerialDescriptor,
        index: Int,
    ): Int = delegate { decodeIntElement(descriptor, index) }

    override fun decodeLongElement(
        descriptor: SerialDescriptor,
        index: Int,
    ): Long = delegate { decodeLongElement(descriptor, index) }

    override fun decodeFloatElement(
        descriptor: SerialDescriptor,
        index: Int,
    ): Float = delegate { decodeFloatElement(descriptor, index) }

    override fun decodeDoubleElement(
        descriptor: SerialDescriptor,
        index: Int,
    ): Double = delegate { decodeDoubleElement(descriptor, index) }

    override fun decodeStringElement(
        descriptor: SerialDescriptor,
        index: Int,
    ): String = delegate { decodeStringElement(descriptor, index) }

    override fun decodeInlineElement(
        descriptor: SerialDescriptor,
        index: Int,
    ): Decoder = delegate { decodeInlineElement(descriptor, index) }

    @ExperimentalSerializationApi
    override fun <T : Any> decodeNullableSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        deserializer: DeserializationStrategy<T?>,
        previousValue: T?,
    ): T? = delegate { decodeNullableSerializableElement(descriptor, index, deserializer, previousValue) }

    override fun <T> decodeSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        deserializer: DeserializationStrategy<T>,
        previousValue: T?,
    ): T = delegate { decodeSerializableElement(descriptor, index, deserializer, previousValue) }

    override fun endStructure(descriptor: SerialDescriptor): Unit = delegate { endStructure(descriptor) }

    override fun plus(decoder: CompositeDecoder): BroadcastCompositeDecoder =
        BroadcastCompositeDecoder(delegate = delegate, subDecoders = subDecoders.plus(decoder), serializersModule)

    override fun transform(block: (CompositeDecoder) -> CompositeDecoder): BroadcastCompositeDecoder =
        BroadcastCompositeDecoder(delegate = delegate, subDecoders = subDecoders.map(block), serializersModule)

    override fun iterator(): Iterator<CompositeDecoder> =
        iterator {
            yield(delegate)
            yieldAll(subDecoders)
        }

    private inline fun <T> delegate(block: CompositeDecoder.() -> T): T =
        block(delegate).also {
            subDecoders.forEach { block(it) }
        }
}
