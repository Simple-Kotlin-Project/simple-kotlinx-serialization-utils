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
import io.github.edmondantes.serialization.encoding.delegate.DelegateEncoder
import io.github.edmondantes.serialization.encoding.sequence.EncoderSequence
import io.github.edmondantes.serialization.util.DelegateIdResolveStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

/**
 * This [Encoder] add supports to inline complex properties
 * @see InlineSerialization
 */
public class InlineEncoder(
    delegate: Encoder,
    private val parentCompositeEncoder: InlineCompositeEncoder? = null,
    private val isInline: Boolean = false,
    serializersModule: SerializersModule = EmptySerializersModule(),
) : DelegateEncoder(
        delegate = delegate,
        currentId = DEFAULT_ID,
        idResolveStrategy = DelegateIdResolveStrategy.DELEGATE,
        serializersModule = serializersModule,
    ) {
    override fun beginCollection(
        descriptor: SerialDescriptor,
        collectionSize: Int,
    ): CompositeEncoder =
        checkInlineBeforeDelegate(descriptor) {
            delegate.beginCollection(descriptor, collectionSize)
        }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder =
        checkInlineBeforeDelegate(descriptor) {
            delegate.beginStructure(descriptor)
        }

    override fun encodeInline(descriptor: SerialDescriptor): Encoder =
        InlineEncoder(delegate.encodeInline(descriptor), parentCompositeEncoder, true, serializersModule)

    override fun transformerEncoder(encoder: Encoder): Encoder = InlineEncoder(encoder, parentCompositeEncoder, isInline, serializersModule)

    override fun transformerCompositeEncoder(encoder: CompositeEncoder): CompositeEncoder =
        InlineCompositeEncoder(encoder, this, isInline, serializersModule)

    private fun checkInlineBeforeDelegate(
        descriptor: SerialDescriptor,
        block: () -> CompositeEncoder,
    ): CompositeEncoder =
        if (parentCompositeEncoder != null && checkInline(descriptor)) {
            InlineCompositeEncoder(parentCompositeEncoder, this, true)
        } else {
            InlineCompositeEncoder(
                block(),
                this,
                false,
            )
        }

    @OptIn(ExperimentalSerializationApi::class)
    private fun checkInline(descriptor: SerialDescriptor): Boolean =
        descriptor.annotations.filterIsInstance<InlineSerialization>().isNotEmpty() || isInline

    public companion object {
        /**
         * Default id for [InlineEncoder]
         */
        public const val DEFAULT_ID: String = "io.github.edmondantes.serialization.encoding.inline.InlineEncoder"
    }
}

public fun Encoder.supportInline(): InlineEncoder = InlineEncoder(this)

public fun <T : EncoderSequence<T>> T.sequenceSupportInline(): T = transform { it.supportInline() }
