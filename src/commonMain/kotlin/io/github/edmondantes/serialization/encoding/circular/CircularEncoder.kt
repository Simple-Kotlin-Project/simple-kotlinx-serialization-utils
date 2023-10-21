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
package io.github.edmondantes.serialization.encoding.circular

import io.github.edmondantes.serialization.encoding.delegate.DelegateEncoder
import io.github.edmondantes.serialization.encoding.sequence.EncoderSequence
import io.github.edmondantes.serialization.util.DelegateIdResolveStrategy
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

/**
 * This class helps to resolve circular references
 * @param delegate [Encoder] which will be get method calls after check circular reference
 * @see CircularResolver
 * @see Encoder
 */
public class CircularEncoder(
    delegate: Encoder,
    private val resolverDelegate: CircularResolver,
    serializersModule: SerializersModule = EmptySerializersModule(),
) : DelegateEncoder(
        delegate = delegate,
        currentId = DEFAULT_ID,
        idResolveStrategy = DelegateIdResolveStrategy.DELEGATE,
        serializersModule = serializersModule,
    ),
    CircularResolver by resolverDelegate {
    override fun transformerEncoder(encoder: Encoder): Encoder = CircularEncoder(encoder, resolverDelegate, serializersModule)

    override fun transformerCompositeEncoder(encoder: CompositeEncoder): CompositeEncoder =
        CircularCompositeEncoder(encoder, resolverDelegate, serializersModule)

    public companion object {
        /**
         * Default id for [CircularEncoder]
         */
        public const val DEFAULT_ID: String =
            "io.github.edmondantes.serialization.encoding.circular.CircularEncoder"
    }
}

/**
 * Create [CircularEncoder] from [Encoder].
 *
 * **_WARNING_**: If first serialization object can be in circular reference please use another kind of this method
 * @param byHashCode If true, [CircularEncoder] will be use object's hash codes for searching same objects
 * @param useRefEquality If false, [CircularEncoder] will use method 'equals' for determining equality
 * @see CircularEncoder
 * @see CircularResolver
 */
public fun Encoder.supportCircular(
    byHashCode: Boolean = true,
    useRefEquality: Boolean = false,
): CircularEncoder = CircularEncoder(this, DefaultCircularResolver(byHashCode, useRefEquality))

/**
 * Create [CircularEncoder] from [Encoder] with specified first object for serialization.
 * @param objForSerialization First object for serialization. It will be added to [CircularEncoder]
 * @param byHashCode If true, [CircularEncoder] will be use object's hash codes for searching same objects
 * @param useRefEquality If false, [CircularEncoder] will use method 'equals' for determining equality
 * @see CircularEncoder
 * @see CircularResolver
 */
public fun Encoder.supportCircular(
    objForSerialization: Any,
    byHashCode: Boolean = true,
    useRefEquality: Boolean = false,
): CircularEncoder =
    CircularEncoder(this, DefaultCircularResolver(byHashCode, useRefEquality)).also {
        it.add(objForSerialization)
    }

/**
 * Create [CircularEncoder] from each [Encoder] in [EncoderSequence].
 *
 * **_WARNING_**: If first serialization object can be in circular reference please use another kind of this method
 * @param byHashCode If true, [CircularEncoder] will be use object's hash codes for searching same objects
 * @param useRefEquality If false, [CircularEncoder] will use method 'equals' for determining equality
 * @see CircularEncoder
 * @see CircularResolver
 */
public fun <T : EncoderSequence<T>> T.sequenceSupportCircular(
    byHashCode: Boolean = true,
    useRefEquality: Boolean = false,
): T =
    transform {
        it.supportCircular(byHashCode, useRefEquality)
    }

/**
 * Create [CircularEncoder] from each [Encoder] in [EncoderSequence] with specified first object for serialization.
 * @param objForSerialization First object for serialization. It will be added to [CircularEncoder]
 * @param byHashCode If true, [CircularEncoder] will be use object's hash codes for searching same objects
 * @param useRefEquality If false, [CircularEncoder] will use method 'equals' for determining equality
 * @see CircularEncoder
 * @see CircularResolver
 */
public fun <T : EncoderSequence<T>> T.sequenceSupportCircular(
    objForSerialization: Any,
    byHashCode: Boolean = true,
    useRefEquality: Boolean = false,
): T =
    transform {
        it.supportCircular(objForSerialization, byHashCode, useRefEquality)
    }
