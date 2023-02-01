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

import io.github.edmondantes.serialization.annotation.AllowEncoder
import io.github.edmondantes.serialization.annotation.IgnoreEncoder
import io.github.edmondantes.serialization.getElementAllAnnotation
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule

/**
 * This class add filter feature to [Encoder]
 * @param delegate [Encoder] which will be get method calls after filtering
 * @param filter A function which filter [Encoder] method calls
 * @param contextualFilter A function which filter contextual data from [Encoder] method calls
 * @see AllowEncoder
 * @see IgnoreEncoder
 * @see ContextualFilter
 * @see AllowContextualFilter
 * @see IgnoreContextualFilter
 */
public open class FilterEncoder(
    protected val delegate: Encoder,
    protected val filter: (SerialDescriptor, Int?) -> Boolean,
    protected val contextualFilter: (SerialDescriptor, Int?, Any?) -> Boolean,
) : Encoder {
    override val serializersModule: SerializersModule
        get() = delegate.serializersModule

    override fun beginCollection(descriptor: SerialDescriptor, collectionSize: Int): CompositeEncoder =
        if (isProcessing(descriptor)) {
            delegate.beginCollection(descriptor, collectionSize).filterBy(filter, contextualFilter)
        } else {
            EmptyCompositeEncoder
        }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder =
        if (isProcessing(descriptor)) {
            delegate.beginStructure(descriptor).filterBy(filter, contextualFilter)
        } else {
            EmptyCompositeEncoder
        }

    override fun encodeBoolean(value: Boolean) {
        delegate.encodeBoolean(value)
    }

    override fun encodeByte(value: Byte) {
        delegate.encodeByte(value)
    }

    override fun encodeChar(value: Char) {
        delegate.encodeChar(value)
    }

    override fun encodeDouble(value: Double) {
        delegate.encodeDouble(value)
    }

    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) {
        if (isProcessing(enumDescriptor) && isProcessing(enumDescriptor, index)) {
            delegate.encodeEnum(enumDescriptor, index)
        }
    }

    override fun encodeFloat(value: Float) {
        delegate.encodeFloat(value)
    }

    override fun encodeInline(descriptor: SerialDescriptor): Encoder =
        if (isProcessing(descriptor)) {
            delegate.encodeInline(descriptor).filterBy(filter, contextualFilter)
        } else {
            EmptyEncoder
        }

    override fun encodeInt(value: Int) {
        delegate.encodeInt(value)
    }

    override fun encodeLong(value: Long) {
        delegate.encodeLong(value)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun encodeNull() {
        delegate.encodeNull()
    }

    override fun encodeShort(value: Short) {
        delegate.encodeShort(value)
    }

    override fun encodeString(value: String) {
        delegate.encodeString(value)
    }

    /**
     * Function which helps to check all filters
     * @param descriptor [SerialDescriptor] is for encoding object (It is optional)
     * @param index Index of encoding field (It is optional)
     */
    protected fun isProcessing(descriptor: SerialDescriptor, index: Int? = null): Boolean =
        filter(descriptor, null) && contextualFilter(descriptor, index, null)
}

/**
 * Create new [Encoder] with filter from current
 * @param filter A function which filter [Encoder] method calls
 * @param contextualFilter A function which filter contextual data from [Encoder] method calls
 */
public fun Encoder.filterBy(
    filter: (SerialDescriptor, Int?) -> Boolean,
    contextualFilter: (SerialDescriptor, Int?, Any?) -> Boolean = { _, _, _ -> true },
): Encoder = FilterEncoder(this, filter, contextualFilter)

/**
 * Create new [CompositeEncoder] with filter from current
 * @param filter A function which filter [Encoder] method calls
 * @param contextualFilter A function which filter contextual data from [Encoder] method calls
 */
public fun CompositeEncoder.filterBy(
    filter: (SerialDescriptor, Int?) -> Boolean,
    contextualFilter: (SerialDescriptor, Int?, Any?) -> Boolean = { _, _, _ -> true },
): CompositeEncoder =
    FilterCompositeEncoder(this, filter, contextualFilter)

/**
 * Create new [SerializationStrategy] with filter from current
 * @param filter A function which filter [Encoder] method calls
 * @param contextualFilter A function which filter contextual data from [Encoder] method calls
 */
public fun <T> SerializationStrategy<T>.filterBy(
    filter: (SerialDescriptor, Int?) -> Boolean,
    contextualFilter: (SerialDescriptor, Int?, Any?) -> Boolean = { _, _, _ -> true },
): SerializationStrategy<T> =
    CustomSerializationStrategy(this) { it.filterBy(filter, contextualFilter) }

/**
 * Create new [Encoder] from [UniqueEncoder] with filter by identifier
 */
@OptIn(ExperimentalSerializationApi::class)
public inline fun <reified T : UniqueEncoder> T.filterByIdentifier(): Encoder =
    FilterEncoder(this, { descriptor, index ->
        val annotations = if (index == null) {
            descriptor.annotations
        } else {
            descriptor.getElementAllAnnotation(index) + descriptor.annotations
        }

        val allowAnnotations = annotations.filterIsInstance<AllowEncoder>()
        val ignoreAnnotations = annotations.filterIsInstance<IgnoreEncoder>()

        ignoreAnnotations.none { it.ignore.any { it == id } } &&
            (allowAnnotations.all { it.allow.isEmpty() } || allowAnnotations.any { it.allow.any { it == id } })
    }, { _, _, value ->
        if (value is ContextualFilter<*>) {
            value.canEncodeWith(id)
        } else {
            true
        }
    })

/**
 * Create new [CompositeEncoder] from [UniqueCompositeEncoder] with filter by identifier
 */
@OptIn(ExperimentalSerializationApi::class)
public inline fun <reified T : UniqueCompositeEncoder> T.filterByIdentifier(): CompositeEncoder =
    FilterCompositeEncoder(this, { descriptor, index ->
        val annotations = if (index == null) {
            descriptor.annotations
        } else {
            descriptor.getElementAllAnnotation(index) + descriptor.annotations
        }

        val allowAnnotations = annotations.filterIsInstance<AllowEncoder>()
        val ignoreAnnotations = annotations.filterIsInstance<IgnoreEncoder>()

        ignoreAnnotations.none { it.ignore.any { it == id } } &&
            (allowAnnotations.all { it.allow.isEmpty() } || allowAnnotations.any { it.allow.any { it == id } })
    }, { _, _, value ->
        if (value is ContextualFilter<*>) {
            value.canEncodeWith(id)
        } else {
            true
        }
    })
