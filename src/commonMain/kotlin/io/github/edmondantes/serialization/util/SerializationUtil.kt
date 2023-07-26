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
package io.github.edmondantes.serialization.util

import io.github.edmondantes.serialization.encoding.BroadcastEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer

public inline fun <reified T> T.serialize(vararg encoders: Encoder, block: BroadcastEncoder.() -> Encoder = { this }) {
    serializer<T>().serialize(if (encoders.size == 1) encoders[0] else BroadcastEncoder(*encoders).block(), this)
}

public inline fun <reified T> T.serialize(encoders: List<Encoder>, block: BroadcastEncoder.() -> Encoder = { this }) {
    serializer<T>().serialize(if (encoders.size == 1) encoders[0] else BroadcastEncoder(encoders).block(), this)
}
