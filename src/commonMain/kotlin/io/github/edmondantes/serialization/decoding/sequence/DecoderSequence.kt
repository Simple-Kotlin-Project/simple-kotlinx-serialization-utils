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
package io.github.edmondantes.serialization.decoding.sequence

import io.github.edmondantes.serialization.decoding.broadcast.BroadcastDecoder
import kotlinx.serialization.encoding.Decoder

/**
 * Generic interface represents mutable sequence of [Decoder]
 * @see BroadcastDecoder
 */
public interface DecoderSequence<T : DecoderSequence<T>> : Sequence<Decoder> {
    /**
     * Add new element to sequence
     * @param decoder new element
     */
    public fun plus(decoder: Decoder): T

    /**
     * Transform each element of sequence
     * @param block transformation function
     */
    public fun transform(block: (Decoder) -> Decoder): T
}
