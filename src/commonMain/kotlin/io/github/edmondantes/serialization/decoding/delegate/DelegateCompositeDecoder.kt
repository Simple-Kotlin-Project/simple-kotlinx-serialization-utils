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
package io.github.edmondantes.serialization.decoding.delegate

import io.github.edmondantes.serialization.decoding.CustomDeserializationStrategy
import io.github.edmondantes.serialization.decoding.UniqueCompositeDecoder
import io.github.edmondantes.serialization.decoding.UniqueDecoder
import io.github.edmondantes.serialization.encoding.delegate.DelegateCompositeEncoder
import io.github.edmondantes.serialization.encoding.delegate.DelegateEncoder
import io.github.edmondantes.serialization.util.DelegateIdResolveStrategy
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

/**
 * This class helps to create a [CompositeDecoder] which delegate calls to [delegate]
 *
 * @param delegate [CompositeDecoder] for delegates calls
 * @param currentId [id] for this [CompositeDecoder] (null if not set) (default is null)
 * @param idResolveStrategy Strategy for resolve [id] (default is [DelegateIdResolveStrategy.DELEGATE])
 * @param serializersModule Custom serializers module
 * @see CompositeDecoder
 * @see UniqueCompositeDecoder
 * @see DelegateDecoder
 * @see DelegateIdResolveStrategy
 */
public abstract class DelegateCompositeDecoder(
    protected val delegate: CompositeDecoder,
    currentId: String? = DelegateCompositeEncoder.DEFAULT_ID,
    idResolveStrategy: DelegateIdResolveStrategy = DelegateIdResolveStrategy.DELEGATE,
    override val serializersModule: SerializersModule = EmptySerializersModule(),
) : UniqueCompositeDecoder, CompositeDecoder by delegate {
    final override val id: String =
        idResolveStrategy.resolveId({ (delegate as? UniqueDecoder)?.id }, { currentId }, DEFAULT_ID)

    override fun decodeInlineElement(
        descriptor: SerialDescriptor,
        index: Int,
    ): Decoder = transformerDecoder(delegate.decodeInlineElement(descriptor, index))

    @ExperimentalSerializationApi
    override fun <T : Any> decodeNullableSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        deserializer: DeserializationStrategy<T?>,
        previousValue: T?,
    ): T? =
        delegate.decodeNullableSerializableElement(
            descriptor,
            index,
            CustomDeserializationStrategy(deserializer, ::transformerDecoder),
            previousValue,
        )

    override fun <T> decodeSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        deserializer: DeserializationStrategy<T>,
        previousValue: T?,
    ): T =
        delegate.decodeSerializableElement(
            descriptor,
            index,
            CustomDeserializationStrategy(deserializer, ::transformerDecoder),
            previousValue,
        )

    protected abstract fun transformerDecoder(encoder: Decoder): Decoder

    public companion object {
        /**
         * Default id for [DelegateEncoder]
         */
        public const val DEFAULT_ID: String =
            "io.github.edmondantes.serialization.decoding.delegate.DelegateCompositeDecoder"
    }
}
