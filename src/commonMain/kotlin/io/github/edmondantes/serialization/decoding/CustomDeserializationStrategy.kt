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
package io.github.edmondantes.serialization.decoding

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * This class helps for transform [Decoder] in deserialization process
 * @param delegate Parent [DeserializationStrategy]
 * @param decoderTransformer Function which transform [Decoder]
 * @see [SerializationStrategy]
 * @see [Encoder]
 */
public class CustomDeserializationStrategy<T>(
    private val delegate: DeserializationStrategy<T>,
    private val decoderTransformer: (Decoder) -> Decoder,
) : DeserializationStrategy<T> by delegate {
    override fun deserialize(decoder: Decoder): T = delegate.deserialize(decoderTransformer(decoder))
}
