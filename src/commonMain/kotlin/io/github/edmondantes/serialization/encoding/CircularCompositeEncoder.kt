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

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule

/**
 * This class helps to resolve circular references
 * @param delegate [CompositeEncoder] which will be get method calls after check circular reference
 * @see CircularResolver
 * @see CompositeEncoder
 */
public class CircularCompositeEncoder(
    private val delegate: CompositeEncoder,
    private val resolverDelegate: CircularResolver,
) : CompositeEncoder by delegate, CircularResolver by resolverDelegate {

    override val serializersModule: SerializersModule =
        delegate.serializersModule

    override fun encodeInlineElement(descriptor: SerialDescriptor, index: Int): Encoder =
        CircularEncoder(delegate.encodeInlineElement(descriptor, index), resolverDelegate)

    @ExperimentalSerializationApi
    override fun <T : Any> encodeNullableSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T?,
    ) {
        if (value == null || checkElement(value)) {
            delegate.encodeNullableSerializableElement(
                descriptor,
                index,
                CustomSerializationStrategy(serializer) {
                    CircularEncoder(
                        it,
                        resolverDelegate,
                    )
                },
                value,
            )
        }
    }

    override fun <T> encodeSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T,
    ) {
        if (value == null || checkElement(value)) {
            delegate.encodeSerializableElement(
                descriptor,
                index,
                CustomSerializationStrategy(serializer) {
                    CircularEncoder(
                        it,
                        resolverDelegate,
                    )
                },
                value,
            )
        }
    }

    private fun checkElement(obj: Any): Boolean {
        if (!has(obj)) {
            add(obj)
            return true
        }

        return false
    }
}

/**
 * Create [CircularCompositeEncoder] from [CompositeEncoder].
 *
 * **_WARNING_**: If first serialization object can be in circular reference please use another kind of this method
 * @param byHashCode If true, [CircularEncoder] will be use object's hash codes for searching same objects
 * @param useRefEquality If false, [CircularEncoder] will use method 'equals' for determining equality
 * @see CircularCompositeEncoder
 * @see CircularResolver
 */
public fun CompositeEncoder.supportCircular(
    byHashCode: Boolean = true,
    useRefEquality: Boolean = false,
): CircularCompositeEncoder =
    CircularCompositeEncoder(this, DefaultCircularResolver(byHashCode, useRefEquality))

/**
 * Create [CircularCompositeEncoder] from [CompositeEncoder].
 * @param objForSerialization First object for serialization. It will be added to [CircularEncoder]
 * @param byHashCode If true, [CircularEncoder] will be use object's hash codes for searching same objects
 * @param useRefEquality If false, [CircularEncoder] will use method 'equals' for determining equality
 * @see CircularCompositeEncoder
 * @see CircularResolver
 */
public fun CompositeEncoder.supportCircular(
    objForSerialization: Any,
    byHashCode: Boolean = true,
    useRefEquality: Boolean = false,
): CircularCompositeEncoder =
    CircularCompositeEncoder(this, DefaultCircularResolver(byHashCode, useRefEquality)).also {
        it.add(objForSerialization)
    }
