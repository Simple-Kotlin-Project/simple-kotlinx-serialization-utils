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

/**
 * A delegate class that enhances the functionality of a [BinaryFormat] implementation by allowing modification of the serialization and deserialization process.
 * The modifications are done through transformation functions provided by the user.
 * This delegate class implements the [BinaryFormat] interface and delegates all method calls to the provided [delegate] instance.
 *
 * @property delegate The underlying [BinaryFormat] instance to delegate the method calls to.
 * @property serializationModify The transformation function to modify the serialization process.
 * @property deserializationModify The transformation function to modify the deserialization process.
 * @see BinaryFormat
 * @see StringFormatDelegate
 */
public class BinaryFormatDelegate(
    private val delegate: BinaryFormat,
    private val serializationModify: Encoder.() -> Encoder = { this },
    private val deserializationModify: Decoder.() -> Decoder = { this },
) : BinaryFormat by delegate {
    override fun <T> encodeToByteArray(
        serializer: SerializationStrategy<T>,
        value: T,
    ): ByteArray = delegate.encodeToByteArray(CustomSerializationStrategy(serializer) { it.serializationModify() }, value)

    override fun <T> decodeFromByteArray(
        deserializer: DeserializationStrategy<T>,
        bytes: ByteArray,
    ): T = delegate.decodeFromByteArray(CustomDeserializationStrategy(deserializer) { it.deserializationModify() }, bytes)
}

/**
 * Modifies a [BinaryFormat] instance by applying the provided transformation [block] to the serialization process.
 *
 * @param block The transformation function to modify the serialization process.
 * The function takes an original [Encoder] instance and returns an [Encoder], that will be used in serialization.
 *
 * @return A modified [BinaryFormat] instance that applies the given transformation function to the serialization process.
 * @see BinaryFormat
 * @see Encoder
 */
public fun BinaryFormat.modifySerializer(block: Encoder.() -> Encoder): BinaryFormat =
    BinaryFormatDelegate(this, serializationModify = block)

/**
 * Modifies a [BinaryFormat] instance by applying the provided transformation [block] to the deserialization process.
 *
 * @param block The transformation function to modify the deserialization process.
 * The function takes an original [Decoder] instance and returns an [Decoder], that will be used in serialization.
 *
 * @return A modified [BinaryFormat] instance that applies the given transformation function to the deserialization process.
 * @see BinaryFormat
 * @see Decoder
 */
public fun BinaryFormat.modifyDeserializer(block: Decoder.() -> Decoder): BinaryFormat =
    BinaryFormatDelegate(this, deserializationModify = block)
