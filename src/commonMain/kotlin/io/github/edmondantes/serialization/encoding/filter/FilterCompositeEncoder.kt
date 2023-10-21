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
package io.github.edmondantes.serialization.encoding.filter

import io.github.edmondantes.serialization.annotation.AllowEncoder
import io.github.edmondantes.serialization.annotation.IgnoreEncoder
import io.github.edmondantes.serialization.encoding.EmptyEncoder
import io.github.edmondantes.serialization.encoding.delegate.DelegateCompositeEncoder
import io.github.edmondantes.serialization.util.DelegateIdResolveStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

/**
 * This class add filter feature to [CompositeEncoder]
 * @param delegate [CompositeEncoder] which will be get method calls after filtering
 * @param filter A function which filter [CompositeEncoder] method calls
 * @param contextualFilter A function which filter contextual data from [CompositeEncoder] method calls
 * @see AllowEncoder
 * @see IgnoreEncoder
 * @see SerializationFilter
 * @see ContextualSerializationFilter
 */
public open class FilterCompositeEncoder(
    delegate: CompositeEncoder,
    protected val filter: SerializationFilter,
    protected val contextualFilter: ContextualSerializationFilter,
    serializersModule: SerializersModule = EmptySerializersModule(),
) : DelegateCompositeEncoder(
        delegate = delegate,
        currentId = "io.github.edmondantes.serialization.encoding.filter.FilterCompositeEncoder",
        idResolveStrategy = DelegateIdResolveStrategy.DELEGATE,
        serializersModule = serializersModule,
    ) {
    override fun encodeBooleanElement(
        descriptor: SerialDescriptor,
        index: Int,
        value: Boolean,
    ) {
        withFilter(descriptor, index, value, delegate::encodeBooleanElement)
    }

    override fun encodeByteElement(
        descriptor: SerialDescriptor,
        index: Int,
        value: Byte,
    ) {
        withFilter(descriptor, index, value, delegate::encodeByteElement)
    }

    override fun encodeCharElement(
        descriptor: SerialDescriptor,
        index: Int,
        value: Char,
    ) {
        withFilter(descriptor, index, value, delegate::encodeCharElement)
    }

    override fun encodeShortElement(
        descriptor: SerialDescriptor,
        index: Int,
        value: Short,
    ) {
        withFilter(descriptor, index, value, delegate::encodeShortElement)
    }

    override fun encodeIntElement(
        descriptor: SerialDescriptor,
        index: Int,
        value: Int,
    ) {
        withFilter(descriptor, index, value, delegate::encodeIntElement)
    }

    override fun encodeLongElement(
        descriptor: SerialDescriptor,
        index: Int,
        value: Long,
    ) {
        withFilter(descriptor, index, value, delegate::encodeLongElement)
    }

    override fun encodeFloatElement(
        descriptor: SerialDescriptor,
        index: Int,
        value: Float,
    ) {
        withFilter(descriptor, index, value, delegate::encodeFloatElement)
    }

    override fun encodeDoubleElement(
        descriptor: SerialDescriptor,
        index: Int,
        value: Double,
    ) {
        withFilter(descriptor, index, value, delegate::encodeDoubleElement)
    }

    override fun encodeStringElement(
        descriptor: SerialDescriptor,
        index: Int,
        value: String,
    ) {
        withFilter(descriptor, index, value, delegate::encodeStringElement)
    }

    override fun encodeInlineElement(
        descriptor: SerialDescriptor,
        index: Int,
    ): Encoder =
        if (filter.filter(descriptor, index)) {
            super.encodeInlineElement(descriptor, index)
        } else {
            EmptyEncoder
        }

    @Suppress("NAME_SHADOWING")
    override fun <T> encodeSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T,
    ) {
        withFilter(descriptor, index, value) { descriptor, index, value ->
            super.encodeSerializableElement(descriptor, index, serializer, value)
        }
    }

    @Suppress("NAME_SHADOWING")
    override fun <T : Any> encodeNullableSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T?,
    ) {
        withFilter(descriptor, index, value) { descriptor, index, value ->
            super.encodeNullableSerializableElement(descriptor, index, serializer, value)
        }
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        delegate.endStructure(descriptor)
    }

    /**
     * * Function which helps to check all filters and call delegate's method
     * @param descriptor [SerialDescriptor] is for encoding object (It is optional)
     * @param index Index of encoding field (It is optional)
     * @param value Encoding value
     * @param func Delegate's method
     */
    protected open fun <T> withFilter(
        descriptor: SerialDescriptor,
        index: Int,
        value: T,
        func: (SerialDescriptor, Int, T) -> Unit,
    ) {
        if (filter.filter(descriptor, index) && contextualFilter.filter(descriptor, index, value)) {
            func(descriptor, index, value)
        }
    }

    override fun transformerEncoder(encoder: Encoder): Encoder = FilterEncoder(encoder, filter, contextualFilter, serializersModule)

    override fun transformerCompositeEncoder(encoder: CompositeEncoder): CompositeEncoder =
        FilterCompositeEncoder(encoder, filter, contextualFilter, serializersModule)
}
