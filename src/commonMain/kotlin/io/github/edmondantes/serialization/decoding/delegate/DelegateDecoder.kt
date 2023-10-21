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
import io.github.edmondantes.serialization.decoding.UniqueDecoder
import io.github.edmondantes.serialization.encoding.delegate.DelegateEncoder
import io.github.edmondantes.serialization.util.DelegateIdResolveStrategy
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

/**
 * This class helps to create a [Decoder] that delegate calls to [delegate]
 *
 * @param delegate [Decoder] for delegates calls
 * @param currentId [id] for this [Decoder] (null if not set) (default is null)
 * @param idResolveStrategy Strategy for resolve [id] (default is [DelegateIdResolveStrategy.DELEGATE])
 * @param serializersModule Custom serializers module
 * @see Decoder
 * @see UniqueDecoder
 * @see DelegateCompositeDecoder
 * @see DelegateIdResolveStrategy
 */
public abstract class DelegateDecoder(
    protected val delegate: Decoder,
    currentId: String? = DelegateEncoder.DEFAULT_ID,
    idResolveStrategy: DelegateIdResolveStrategy = DelegateIdResolveStrategy.DELEGATE,
    override val serializersModule: SerializersModule = EmptySerializersModule(),
) : UniqueDecoder, Decoder by delegate {
    final override val id: String =
        when (idResolveStrategy) {
            DelegateIdResolveStrategy.DELEGATE -> (delegate as? UniqueDecoder)?.id ?: currentId
            DelegateIdResolveStrategy.CURRENT -> currentId ?: (delegate as? UniqueDecoder)?.id
        } ?: DelegateEncoder.DEFAULT_ID

    override fun decodeInline(descriptor: SerialDescriptor): Decoder = transformerDecoder(delegate.decodeInline(descriptor))

    override fun <T> decodeSerializableValue(deserializer: DeserializationStrategy<T>): T =
        delegate.decodeSerializableValue(CustomDeserializationStrategy(deserializer, ::transformerDecoder))

    @ExperimentalSerializationApi
    override fun <T : Any> decodeNullableSerializableValue(deserializer: DeserializationStrategy<T?>): T? =
        delegate.decodeNullableSerializableValue(CustomDeserializationStrategy(deserializer, ::transformerDecoder))

    protected abstract fun transformerDecoder(encoder: Decoder): Decoder

    public companion object {
        /**
         * Default id for [DelegateEncoder]
         */
        public const val DEFAULT_ID: String =
            "io.github.edmondantes.serialization.decoding.delegate.DelegateDecoder"
    }
}
