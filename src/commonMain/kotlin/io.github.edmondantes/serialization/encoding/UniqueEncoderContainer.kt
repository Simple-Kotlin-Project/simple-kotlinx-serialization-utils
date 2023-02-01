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

import kotlinx.serialization.encoding.Encoder

public class UniqueEncoderContainer(private val delegate: Encoder, override val id: String) :
    UniqueEncoder,
    Encoder by delegate

public fun Encoder.withId(id: String): UniqueEncoder =
    UniqueEncoderContainer(this, id)

public fun UniqueEncoder.withId(id: String): UniqueEncoder {
    error("Can not set id for UniqueEncoder")
}
