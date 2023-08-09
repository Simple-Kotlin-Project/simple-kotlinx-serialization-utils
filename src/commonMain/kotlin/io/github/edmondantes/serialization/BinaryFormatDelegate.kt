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
package io.github.edmondantes.serialization

import io.github.edmondantes.serialization.decoding.CustomDeserializationStrategy
import io.github.edmondantes.serialization.encoding.CustomSerializationStrategy
import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

public class BinaryFormatDelegate(
    private val delegate: BinaryFormat,
    private val serializationModify: Encoder.() -> Encoder = { this },
    private val deserializationModify: Decoder.() -> Decoder = { this },
) : BinaryFormat by delegate {

    override fun <T> encodeToByteArray(serializer: SerializationStrategy<T>, value: T): ByteArray =
        delegate.encodeToByteArray(CustomSerializationStrategy(serializer) { it.serializationModify() }, value)

    override fun <T> decodeFromByteArray(deserializer: DeserializationStrategy<T>, bytes: ByteArray): T =
        delegate.decodeFromByteArray(CustomDeserializationStrategy(deserializer) { it.deserializationModify() }, bytes)
}

public fun BinaryFormat.modifySerializer(block: Encoder.() -> Encoder): BinaryFormat =
    BinaryFormatDelegate(this, serializationModify = block)

public fun BinaryFormat.modifyDeserializer(block: Decoder.() -> Decoder): BinaryFormat =
    BinaryFormatDelegate(this, deserializationModify = block)
