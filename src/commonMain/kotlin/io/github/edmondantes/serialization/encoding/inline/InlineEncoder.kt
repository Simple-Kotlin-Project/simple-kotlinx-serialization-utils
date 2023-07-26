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

//FIXME: rewrite
public class InlineEncoder(
    private val delegate: Encoder,
    private val compositeEncoderQueue: ArrayDeque<CompositeEncoder> = ArrayDeque(),
    private var inline: Int = 0,
) : Encoder by delegate {

    private var nextIsInline: Boolean = false

    @OptIn(ExperimentalSerializationApi::class)
    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder =
        if (nextIsInline || descriptor.annotations.filterIsInstance<InlineSerialization>().isNotEmpty()) {
            nextIsInline = false
            inline++
            compositeEncoderQueue.last()
        } else {
            InlineCompositeEncoder(
                delegate.beginStructure(descriptor),
                this,
                ::endStructure,
            ).also(compositeEncoderQueue::addLast)
        }

    public fun changeEncoder(delegate: Encoder, isInline: Boolean = false): InlineEncoder =
        if (this.delegate === delegate) {
            this
        } else {
            InlineEncoder(delegate, compositeEncoderQueue, inline).also {
                if (isInline) {
                    inline++
                }
            }
        }.also {
            it.nextIsInline = isInline
        }

    private fun endStructure(): Boolean {
        if (inline == 0) {
            compositeEncoderQueue.removeLast()
            return true
        } else {
            inline--
        }

        return false
    }
}

public fun Encoder.supportInline(): InlineEncoder = InlineEncoder(this)
