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
package io.github.edmondantes.serialization.encoding.inline

import io.github.edmondantes.serialization.annotation.InlineSerialization
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder

public class InlineEncoder(
    private val delegate: Encoder,
    private val parentCompositeEncoder: InlineCompositeEncoder? = null,
    private val isInline: Boolean = false,
) : Encoder by delegate {

    @OptIn(ExperimentalSerializationApi::class)
    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder =
        if ((descriptor.annotations.filterIsInstance<InlineSerialization>().isNotEmpty() || isInline) &&
            parentCompositeEncoder != null
        ) {
            parentCompositeEncoder
        } else {
            InlineCompositeEncoder(
                delegate.beginStructure(descriptor),
                this,
            )
        }
}

public fun Encoder.supportInline(): InlineEncoder = InlineEncoder(this)
