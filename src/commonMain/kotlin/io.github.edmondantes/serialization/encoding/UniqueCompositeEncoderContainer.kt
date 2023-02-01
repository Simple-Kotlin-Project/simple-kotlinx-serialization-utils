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

import kotlinx.serialization.encoding.CompositeEncoder

public class UniqueCompositeEncoderContainer(private val delegate: CompositeEncoder, override val id: String) :
    UniqueCompositeEncoder,
    CompositeEncoder by delegate

public fun CompositeEncoder.withId(id: String): UniqueCompositeEncoder =
    UniqueCompositeEncoderContainer(this, id)

public fun UniqueCompositeEncoder.withId(id: String): UniqueCompositeEncoder {
    error("Can not set id for UniqueCompositeEncoder")
}
