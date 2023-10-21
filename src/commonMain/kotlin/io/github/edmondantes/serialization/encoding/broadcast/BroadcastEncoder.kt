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
package io.github.edmondantes.serialization.encoding.broadcast

import io.github.edmondantes.serialization.encoding.sequence.EncoderSequence
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

/**
 * This class helps to broadcast encoding events for some [Encoder]
 * @param encoders Encoders which will get encoding events
 */
@OptIn(ExperimentalSerializationApi::class)
public open class BroadcastEncoder(
    protected val encoders: List<Encoder>,
    override val serializersModule: SerializersModule = EmptySerializersModule(),
) : Encoder, EncoderSequence<BroadcastEncoder> {

    public constructor(
        encoders: Array<Encoder>,
        serializersModule: SerializersModule = EmptySerializersModule(),
    ) : this(encoders.toList(), serializersModule)

    public constructor(vararg encoders: Encoder) : this(encoders.toList())

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder =
        BroadcastCompositeEncoder(delegate { it.beginStructure(descriptor) }, serializersModule)

    override fun beginCollection(descriptor: SerialDescriptor, collectionSize: Int): CompositeEncoder =
        BroadcastCompositeEncoder(delegate { it.beginCollection(descriptor, collectionSize) }, serializersModule)

    override fun encodeBoolean(value: Boolean) {
        delegate { it.encodeBoolean(value) }
    }

    override fun encodeByte(value: Byte) {
        delegate { it.encodeByte(value) }
    }

    override fun encodeShort(value: Short) {
        delegate { it.encodeShort(value) }
    }

    override fun encodeChar(value: Char) {
        delegate { it.encodeChar(value) }
    }

    override fun encodeInt(value: Int) {
        delegate { it.encodeInt(value) }
    }

    override fun encodeLong(value: Long) {
        delegate { it.encodeLong(value) }
    }

    override fun encodeFloat(value: Float) {
        delegate { it.encodeFloat(value) }
    }

    override fun encodeDouble(value: Double) {
        delegate { it.encodeDouble(value) }
    }

    override fun encodeString(value: String) {
        delegate { it.encodeString(value) }
    }

    override fun encodeInline(descriptor: SerialDescriptor): Encoder =
        BroadcastEncoder(delegate { it.encodeInline(descriptor) }, serializersModule)

    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) {
        delegate { it.encodeEnum(enumDescriptor, index) }
    }

    override fun encodeNull() {
        delegate { it.encodeNull() }
    }

    override fun encodeNotNullMark() {
        delegate { it.encodeNotNullMark() }
    }

    override fun <T> encodeSerializableValue(serializer: SerializationStrategy<T>, value: T) {
        delegate { it.encodeSerializableValue(serializer, value) }
    }

    override fun <T : Any> encodeNullableSerializableValue(serializer: SerializationStrategy<T>, value: T?) {
        delegate { it.encodeNullableSerializableValue(serializer, value) }
    }

    override fun plus(encoder: Encoder): BroadcastEncoder =
        BroadcastEncoder(encoders.plus(encoder), serializersModule)

    override fun transform(block: (Encoder) -> Encoder): BroadcastEncoder =
        BroadcastEncoder(encoders.map(block), serializersModule)

    override fun iterator(): Iterator<Encoder> =
        encoders.iterator()

    protected inline fun <T> delegate(crossinline func: (Encoder) -> T): List<T> =
        encoders.map(func)
}
