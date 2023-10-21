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
@file:OptIn(ExperimentalSerializationApi::class)

package io.github.edmondantes.serialization.encoding.filter

import io.github.edmondantes.serialization.annotation.AllowEncoder
import io.github.edmondantes.serialization.annotation.IgnoreEncoder
import io.github.edmondantes.serialization.encoding.CustomSerializationStrategy
import io.github.edmondantes.serialization.encoding.EmptyCompositeEncoder
import io.github.edmondantes.serialization.encoding.EmptyEncoder
import io.github.edmondantes.serialization.encoding.UniqueCompositeEncoder
import io.github.edmondantes.serialization.encoding.UniqueEncoder
import io.github.edmondantes.serialization.encoding.delegate.DelegateEncoder
import io.github.edmondantes.serialization.util.DelegateIdResolveStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

/**
 * This class add filter feature to [Encoder]
 * @param delegate [Encoder] which will be get method calls after filtering
 * @param filter A function which filter [Encoder] method calls
 * @param contextualFilter A function which filter contextual data from [Encoder] method calls
 * @see AllowEncoder
 * @see IgnoreEncoder
 * @see SerializationFilter
 * @see ContextualSerializationFilter
 */
public open class FilterEncoder(
    delegate: Encoder,
    protected val filter: SerializationFilter,
    protected val contextualFilter: ContextualSerializationFilter,
    serializersModule: SerializersModule = EmptySerializersModule(),
) : DelegateEncoder(
        delegate = delegate,
        currentId = "io.github.edmondantes.serialization.encoding.filter.FilterEncoder",
        idResolveStrategy = DelegateIdResolveStrategy.DELEGATE,
        serializersModule = serializersModule,
    ) {
    override fun beginCollection(
        descriptor: SerialDescriptor,
        collectionSize: Int,
    ): CompositeEncoder =
        if (isProcessing(descriptor)) {
            super.beginCollection(descriptor, collectionSize)
        } else {
            EmptyCompositeEncoder
        }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder =
        if (isProcessing(descriptor)) {
            super.beginStructure(descriptor)
        } else {
            EmptyCompositeEncoder
        }

    override fun encodeInline(descriptor: SerialDescriptor): Encoder =
        if (isProcessing(descriptor)) {
            super.encodeInline(descriptor)
        } else {
            EmptyEncoder
        }

    override fun encodeEnum(
        enumDescriptor: SerialDescriptor,
        index: Int,
    ) {
        if (isProcessing(enumDescriptor, index)) {
            super.encodeEnum(enumDescriptor, index)
        }
    }

    /**
     * Function which helps to check all filters
     * @param descriptor [SerialDescriptor] of encoding object
     */
    protected open fun isProcessing(descriptor: SerialDescriptor): Boolean = filter.filter(descriptor)

    /**
     * Function which helps to check all filters
     * @param descriptor [SerialDescriptor] of encoding object
     * @param index Index of encoding field
     */
    protected open fun isProcessing(
        descriptor: SerialDescriptor,
        index: Int,
    ): Boolean = filter.filter(descriptor, index)

    override fun transformerEncoder(encoder: Encoder): Encoder = FilterEncoder(encoder, filter, contextualFilter, serializersModule)

    override fun transformerCompositeEncoder(encoder: CompositeEncoder): CompositeEncoder =
        FilterCompositeEncoder(encoder, filter, contextualFilter, serializersModule)
}

/**
 * Create new [Encoder] with filter from current
 * @param filter A function which filter [Encoder] method calls
 * @param contextualFilter A function which filter contextual data from [Encoder] method calls
 */
public fun Encoder.filterBy(
    filter: SerializationFilter,
    contextualFilter: ContextualSerializationFilter = ContextualSerializationFilter.SKIP,
): Encoder = FilterEncoder(this, filter, contextualFilter)

/**
 * Create new [CompositeEncoder] with filter from current
 * @param filter A function which filter [Encoder] method calls
 * @param contextualFilter A function which filter contextual data from [Encoder] method calls
 */
public fun CompositeEncoder.filterBy(
    filter: SerializationFilter,
    contextualFilter: ContextualSerializationFilter = ContextualSerializationFilter.SKIP,
): CompositeEncoder = FilterCompositeEncoder(this, filter, contextualFilter)

/**
 * Create new [SerializationStrategy] with filter from current
 * @param filter A function which filter [Encoder] method calls
 * @param contextualFilter A function which filter contextual data from [Encoder] method calls
 */
public fun <T> SerializationStrategy<T>.filterBy(
    filter: SerializationFilter,
    contextualFilter: ContextualSerializationFilter = ContextualSerializationFilter.SKIP,
): SerializationStrategy<T> = CustomSerializationStrategy(this) { it.filterBy(filter, contextualFilter) }

/**
 * Create new [UniqueEncoder] with filter by identifier
 */
public inline fun <reified T : UniqueEncoder> T.filterByIdentifier(): UniqueEncoder =
    FilterEncoder(this, SerializationFilterByIdentifier(id), ContextualSerializationFilter.SKIP)

/**
 * Create new [UniqueCompositeEncoder] with filter by identifier
 */
public inline fun <reified T : UniqueCompositeEncoder> T.filterByIdentifier(): UniqueCompositeEncoder =
    FilterCompositeEncoder(this, SerializationFilterByIdentifier(id), ContextualSerializationFilter.SKIP)
