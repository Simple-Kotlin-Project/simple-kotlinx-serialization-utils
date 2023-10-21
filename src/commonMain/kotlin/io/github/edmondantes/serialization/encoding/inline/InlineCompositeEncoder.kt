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
import io.github.edmondantes.serialization.encoding.CustomSerializationStrategy
import io.github.edmondantes.serialization.encoding.delegate.DelegateCompositeEncoder
import io.github.edmondantes.serialization.util.DelegateIdResolveStrategy
import io.github.edmondantes.serialization.util.getElementAllAnnotation
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

/**
 * This [CompositeEncoder] add supports to inline complex properties
 * @see InlineSerialization
 */
public class InlineCompositeEncoder(
    delegate: CompositeEncoder,
    private val encoderDelegate: Encoder,
    private val isInline: Boolean = false,
    serializersModule: SerializersModule = EmptySerializersModule(),
) : DelegateCompositeEncoder(
        delegate = delegate,
        currentId = DEFAULT_ID,
        idResolveStrategy = DelegateIdResolveStrategy.DELEGATE,
        serializersModule = serializersModule,
    ) {
    @ExperimentalSerializationApi
    override fun <T : Any> encodeNullableSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T?,
    ) {
        val isInline = descriptor.getElementAllAnnotation(index).filterIsInstance<InlineSerialization>().isNotEmpty()

        if (value != null && isInline) {
            serializer.serialize(InlineEncoder(encoderDelegate, this, true), value)
            return
        }

        val inlineEncodingStrategy =
            CustomSerializationStrategy(serializer) {
                InlineEncoder(it, this)
            }

        delegate.encodeNullableSerializableElement(
            descriptor,
            index,
            inlineEncodingStrategy,
            value,
        )
    }

    override fun <T : Any?> encodeSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T,
    ) {
        val isInline = descriptor.getElementAllAnnotation(index).filterIsInstance<InlineSerialization>().isNotEmpty()

        if (isInline) {
            serializer.serialize(InlineEncoder(encoderDelegate, this, true), value)
            return
        }

        val inlineEncodingStrategy =
            CustomSerializationStrategy(serializer) {
                InlineEncoder(it, this)
            }

        delegate.encodeSerializableElement(
            descriptor,
            index,
            inlineEncodingStrategy,
            value,
        )
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        if (!isInline) {
            delegate.endStructure(descriptor)
        }
    }

    override fun transformerEncoder(encoder: Encoder): Encoder = InlineEncoder(encoder, this, isInline, serializersModule)

    override fun transformerCompositeEncoder(encoder: CompositeEncoder): CompositeEncoder =
        InlineCompositeEncoder(encoder, encoderDelegate, isInline, serializersModule)

    public companion object {
        public const val DEFAULT_ID: String =
            "io.github.edmondantes.serialization.encoding.inline.InlineCompositeEncoder"
    }
}
