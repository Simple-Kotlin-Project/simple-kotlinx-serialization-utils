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
package io.github.edmondantes.serialization.encoding.element

import io.github.edmondantes.serialization.element.AnyEncodedElement
import io.github.edmondantes.serialization.element.ComplexEncodedElementBuilder
import io.github.edmondantes.serialization.element.DefaultEncodedElement
import io.github.edmondantes.serialization.element.EncodedElement
import io.github.edmondantes.serialization.element.EncodedElementType
import io.github.edmondantes.serialization.element.add
import io.github.edmondantes.serialization.element.toEncodedElementType
import io.github.edmondantes.serialization.encoding.UniqueCompositeEncoder
import io.github.edmondantes.serialization.util.nullableOptional
import io.github.edmondantes.serialization.util.tryGetElementDescriptor
import io.github.edmondantes.serialization.util.tryGetElementName
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

/**
 * [CompositeElementEncoder] that encode complex value to [EncodedElement]
 *
 * @param builder complex builder for [EncodedElement]
 * @param parentBuilder build for parent element
 * @param id id for encoder
 */
public open class CompositeElementEncoder(
    protected val builder: ComplexEncodedElementBuilder,
    protected val parentBuilder: ComplexEncodedElementBuilder? = null,
    id: String? = null,
    override val serializersModule: SerializersModule = EmptySerializersModule(),
) : UniqueCompositeEncoder {
    override val id: String = id ?: DEFAULT_ID

    override fun encodeBooleanElement(
        descriptor: SerialDescriptor,
        index: Int,
        value: Boolean,
    ) {
        encode(descriptor, index, value)
    }

    override fun encodeByteElement(
        descriptor: SerialDescriptor,
        index: Int,
        value: Byte,
    ) {
        encode(descriptor, index, value)
    }

    override fun encodeCharElement(
        descriptor: SerialDescriptor,
        index: Int,
        value: Char,
    ) {
        encode(descriptor, index, value)
    }

    override fun encodeShortElement(
        descriptor: SerialDescriptor,
        index: Int,
        value: Short,
    ) {
        encode(descriptor, index, value)
    }

    override fun encodeIntElement(
        descriptor: SerialDescriptor,
        index: Int,
        value: Int,
    ) {
        encode(descriptor, index, value)
    }

    override fun encodeLongElement(
        descriptor: SerialDescriptor,
        index: Int,
        value: Long,
    ) {
        encode(descriptor, index, value)
    }

    override fun encodeFloatElement(
        descriptor: SerialDescriptor,
        index: Int,
        value: Float,
    ) {
        encode(descriptor, index, value)
    }

    override fun encodeDoubleElement(
        descriptor: SerialDescriptor,
        index: Int,
        value: Double,
    ) {
        encode(descriptor, index, value)
    }

    override fun encodeStringElement(
        descriptor: SerialDescriptor,
        index: Int,
        value: String,
    ) {
        encode(descriptor, index, value)
    }

    override fun encodeInlineElement(
        descriptor: SerialDescriptor,
        index: Int,
    ): Encoder =
        ElementEncoder(
            builder = null,
            id = id,
            currentDescriptor = descriptor.tryGetElementDescriptor(index),
            parentBuilder = builder,
            parentDescriptor = descriptor,
            indexInParent = index,
            serializersModule = serializersModule,
        )

    @ExperimentalSerializationApi
    override fun <T : Any> encodeNullableSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T?,
    ) {
        if (value == null) {
            builder.add(
                DefaultEncodedElement<T?>(
                    EncodedElementType.NULL,
                    descriptor.tryGetElementDescriptor(index)?.serialName,
                    descriptor.tryGetElementName(index),
                    value.nullableOptional(),
                ),
            )
        } else {
            serializer.serialize(
                ElementEncoder(
                    builder = null,
                    id = id,
                    currentDescriptor = descriptor.tryGetElementDescriptor(index),
                    parentBuilder = builder,
                    parentDescriptor = descriptor,
                    indexInParent = index,
                    serializersModule = serializersModule,
                ),
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
        serializer.serialize(
            ElementEncoder(
                builder = null,
                id = id,
                currentDescriptor = descriptor.tryGetElementDescriptor(index),
                parentBuilder = builder,
                parentDescriptor = descriptor,
                indexInParent = index,
                serializersModule = serializersModule,
            ),
            value,
        )
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        parentBuilder?.add(builder.build() as AnyEncodedElement)
    }

    @OptIn(ExperimentalSerializationApi::class)
    protected open fun encode(
        descriptor: SerialDescriptor,
        index: Int,
        value: Any?,
    ) {
        builder.value.value.add(
            DefaultEncodedElement.Builder<Any?>()
                .type(value == null, descriptor.kind.toEncodedElementType(parentBuilder?.type))
                .descriptorName(descriptor, index)
                .name(descriptor, index)
                .value(value)
                .build(),
        )
    }

    public companion object {
        /**
         * Default id for [CompositeElementEncoder]
         */
        public const val DEFAULT_ID: String =
            "io.github.edmondantes.serialization.encoding.element.CompositeElementEncoder"
    }
}
