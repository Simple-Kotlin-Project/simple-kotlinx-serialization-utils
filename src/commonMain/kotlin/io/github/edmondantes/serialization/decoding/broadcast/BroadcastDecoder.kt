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

import io.github.edmondantes.serialization.decoding.sequence.DecoderSequence
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

/**
 * This class helps to broadcast decoding events for some [Decoder]
 * @param delegate original [Decoder] which will manage decoding process
 * @param subDecoders [Decoder] that will get copy of decoding events for [delegate]
 */
@OptIn(ExperimentalSerializationApi::class)
public class BroadcastDecoder(
    private val delegate: Decoder,
    private val subDecoders: List<Decoder>,
    override val serializersModule: SerializersModule = EmptySerializersModule(),
) : Decoder, DecoderSequence<BroadcastDecoder> {
    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder = delegate { beginStructure(descriptor) }

    override fun decodeBoolean(): Boolean = delegate(Decoder::decodeBoolean)

    override fun decodeByte(): Byte = delegate(Decoder::decodeByte)

    override fun decodeChar(): Char = delegate(Decoder::decodeChar)

    override fun decodeShort(): Short = delegate(Decoder::decodeShort)

    override fun decodeInt(): Int = delegate(Decoder::decodeInt)

    override fun decodeLong(): Long = delegate(Decoder::decodeLong)

    override fun decodeFloat(): Float = delegate(Decoder::decodeFloat)

    override fun decodeDouble(): Double = delegate(Decoder::decodeDouble)

    override fun decodeString(): String = delegate(Decoder::decodeString)

    override fun decodeEnum(enumDescriptor: SerialDescriptor): Int = delegate { decodeEnum(enumDescriptor) }

    override fun decodeInline(descriptor: SerialDescriptor): Decoder = delegate { decodeInline(descriptor) }

    override fun decodeNotNullMark(): Boolean = delegate(Decoder::decodeNotNullMark)

    override fun decodeNull(): Nothing? = delegate(Decoder::decodeNull)

    override fun plus(decoder: Decoder): BroadcastDecoder = BroadcastDecoder(delegate, subDecoders.plus(decoder), serializersModule)

    override fun transform(block: (Decoder) -> Decoder): BroadcastDecoder =
        BroadcastDecoder(block(delegate), subDecoders.map(block), serializersModule)

    override fun iterator(): Iterator<Decoder> =
        iterator {
            yield(delegate)
            yieldAll(subDecoders)
        }

    private inline fun <T> delegate(block: Decoder.() -> T): T =
        block(delegate).also {
            subDecoders.forEach { block(it) }
        }
}
