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
package io.github.edmondantes.serialization.encoding.delegate

import io.github.edmondantes.serialization.encoding.CustomSerializationStrategy
import io.github.edmondantes.serialization.encoding.UniqueCompositeEncoder
import io.github.edmondantes.serialization.util.DelegateIdResolveStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

/**
 * This class helps to create a [CompositeEncoder] which delegate calls to [delegate] and transform [Encoder] and [CompositeEncoder]
 *
 * @param delegate [CompositeEncoder] for delegates calls
 * @param currentId [id] for this encoder (null if not set) (default is null)
 * @param idResolveStrategy Strategy for resolve [id] (default is [DelegateIdResolveStrategy.DELEGATE])
 * @param serializersModule Custom serializers module
 * @see CompositeEncoder
 * @see UniqueCompositeEncoder
 * @see DelegateEncoder
 * @see DelegateIdResolveStrategy
 */
@OptIn(ExperimentalSerializationApi::class)
public abstract class DelegateCompositeEncoder(
    protected val delegate: CompositeEncoder,
    currentId: String? = null,
    idResolveStrategy: DelegateIdResolveStrategy = DelegateIdResolveStrategy.DELEGATE,
    override val serializersModule: SerializersModule = EmptySerializersModule(),
) : UniqueCompositeEncoder, CompositeEncoder by delegate {
    final override val id: String =
        idResolveStrategy.resolveId(
            { (delegate as? UniqueCompositeEncoder)?.id },
            { currentId },
            DEFAULT_ID,
        )

    override fun encodeInlineElement(
        descriptor: SerialDescriptor,
        index: Int,
    ): Encoder = transformerEncoder(delegate.encodeInlineElement(descriptor, index))

    override fun <T> encodeSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T,
    ) {
        delegate.encodeSerializableElement(
            descriptor,
            index,
            CustomSerializationStrategy(serializer, ::transformerEncoder),
            value,
        )
    }

    override fun <T : Any> encodeNullableSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T?,
    ) {
        delegate.encodeNullableSerializableElement(
            descriptor,
            index,
            CustomSerializationStrategy(serializer, ::transformerEncoder),
            value,
        )
    }

    /**
     * Method for transform [Encoder]
     * It will be called on serialize in [encodeSerializableElement] and [encodeNullableSerializableElement]
     * @return new [Encoder]
     */
    protected abstract fun transformerEncoder(encoder: Encoder): Encoder

    /**
     * Method for transform [CompositeEncoder]
     * It will be called in [encodeInlineElement]
     * @return new [CompositeEncoder]
     */
    protected abstract fun transformerCompositeEncoder(encoder: CompositeEncoder): CompositeEncoder

    public companion object {
        /**
         * Default id for [DelegateCompositeEncoder]
         */
        public const val DEFAULT_ID: String =
            "io.github.edmondantes.serialization.encoding.delegate.DelegateCompositeEncoder"
    }
}
