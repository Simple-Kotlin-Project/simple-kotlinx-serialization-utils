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

import io.github.edmondantes.serialization.encoding.inline.supportInline
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
public class BroadcastEncoder(private val encoders: List<Encoder>) : Encoder {

    public constructor(vararg encoders: Encoder) : this(encoders.toList())

    override val serializersModule: SerializersModule = EmptySerializersModule()

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder =
        BroadcastCompositeEncoder(delegate { it.beginStructure(descriptor) })

    override fun beginCollection(descriptor: SerialDescriptor, collectionSize: Int): CompositeEncoder =
        BroadcastCompositeEncoder(delegate { it.beginCollection(descriptor, collectionSize) })

    @ExperimentalSerializationApi
    override fun encodeNotNullMark() {
        delegate { it.encodeNotNullMark() }
    }

    @ExperimentalSerializationApi
    override fun <T : Any> encodeNullableSerializableValue(serializer: SerializationStrategy<T>, value: T?) {
        delegate { it.encodeNullableSerializableValue(serializer, value) }
    }

    override fun <T> encodeSerializableValue(serializer: SerializationStrategy<T>, value: T) {
        delegate { it.encodeSerializableValue(serializer, value) }
    }

    override fun encodeBoolean(value: Boolean) {
        delegate { it.encodeBoolean(value) }
    }

    override fun encodeByte(value: Byte) {
        delegate { it.encodeByte(value) }
    }

    override fun encodeChar(value: Char) {
        delegate { it.encodeChar(value) }
    }

    override fun encodeDouble(value: Double) {
        delegate { it.encodeDouble(value) }
    }

    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) {
        delegate { it.encodeEnum(enumDescriptor, index) }
    }

    override fun encodeFloat(value: Float) {
        delegate { it.encodeFloat(value) }
    }

    override fun encodeInline(descriptor: SerialDescriptor): Encoder =
        BroadcastEncoder(delegate { it.encodeInline(descriptor) })

    override fun encodeInt(value: Int) {
        delegate { it.encodeInt(value) }
    }

    override fun encodeLong(value: Long) {
        delegate { it.encodeLong(value) }
    }

    @ExperimentalSerializationApi
    override fun encodeNull() {
        delegate { it.encodeNull() }
    }

    override fun encodeShort(value: Short) {
        delegate { it.encodeShort(value) }
    }

    override fun encodeString(value: String) {
        delegate { it.encodeString(value) }
    }

    public fun supportCircular(
        byHashCode: Boolean = true,
        useRefEquality: Boolean = false,
        configure: CircularEncoder.() -> Unit = {},
    ): Encoder =
        BroadcastEncoder(encoders.map { it.supportCircular(byHashCode, useRefEquality).apply(configure) })

    public fun supportCircular(
        objForSerialization: Any,
        byHashCode: Boolean = true,
        useRefEquality: Boolean = false,
        configure: CircularEncoder.() -> Unit = {},
    ): Encoder =
        BroadcastEncoder(
            encoders.map {
                it.supportCircular(objForSerialization, byHashCode, useRefEquality).apply(configure)
            },
        )

    public fun supportInline(): Encoder =
        BroadcastEncoder(encoders.map { it.supportInline() })

    private inline fun <T> delegate(crossinline func: (Encoder) -> T): List<T> =
        encoders.map(func)
}
