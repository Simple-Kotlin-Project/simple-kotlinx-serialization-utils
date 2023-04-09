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

import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule

/**
 * This class helps to resolve circular references
 * @param delegate [Encoder] which will be get method calls after check circular reference
 * @see CircularResolver
 * @see Encoder
 */
public class CircularEncoder(
    private val delegate: Encoder,
    private val resolverDelegate: CircularResolver,
) : Encoder by delegate, CircularResolver by resolverDelegate {

    override val serializersModule: SerializersModule = delegate.serializersModule

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder =
        CircularCompositeEncoder(delegate.beginStructure(descriptor), resolverDelegate)

    override fun encodeInline(descriptor: SerialDescriptor): Encoder =
        CircularEncoder(delegate.encodeInline(descriptor), resolverDelegate)
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
public fun Encoder.supportCircular(byHashCode: Boolean = true, useRefEquality: Boolean = false): CircularEncoder =
    CircularEncoder(this, DefaultCircularResolver(byHashCode, useRefEquality))

/**
 * Create [CircularEncoder] from [Encoder].
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
