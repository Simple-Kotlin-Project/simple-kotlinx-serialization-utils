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
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.StringFormat
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

public class StringFormatDelegate(
    private val delegate: StringFormat,
    private val serializationModify: Encoder.() -> Encoder = { this },
    private val deserializationModify: Decoder.() -> Decoder = { this },
) : StringFormat by delegate {

    override fun <T> encodeToString(serializer: SerializationStrategy<T>, value: T): String =
        delegate.encodeToString(CustomSerializationStrategy(serializer) { it.serializationModify() }, value)

    override fun <T> decodeFromString(deserializer: DeserializationStrategy<T>, string: String): T =
        delegate.decodeFromString(CustomDeserializationStrategy(deserializer) { it.deserializationModify() }, string)
}

public fun StringFormat.modifySerializer(block: Encoder.() -> Encoder): StringFormat =
    StringFormatDelegate(this, serializationModify = block)

public fun StringFormat.modifyDeserializer(block: Decoder.() -> Decoder): StringFormat =
    StringFormatDelegate(this, deserializationModify = block)
