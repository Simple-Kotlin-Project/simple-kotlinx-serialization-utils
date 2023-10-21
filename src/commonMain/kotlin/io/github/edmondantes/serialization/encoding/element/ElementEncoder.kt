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
import io.github.edmondantes.serialization.element.AnyEncodedElementBuilder
import io.github.edmondantes.serialization.element.ComplexEncodedElementBuilder
import io.github.edmondantes.serialization.element.DefaultEncodedElement
import io.github.edmondantes.serialization.element.EncodedElement
import io.github.edmondantes.serialization.element.EncodedElementBuilder
import io.github.edmondantes.serialization.element.add
import io.github.edmondantes.serialization.element.takeIfComplex
import io.github.edmondantes.serialization.element.toEncodedElementType
import io.github.edmondantes.serialization.encoding.UniqueEncoder
import io.github.edmondantes.serialization.exception.SerializationProcessException
import io.github.edmondantes.serialization.util.tryGetElementDescriptor
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer

/**
 * [Encoder] that encode value to [EncodedElement]
 *
 * @param builder builder for [EncodedElement]
 * @param id id for encoder
 * @param currentDescriptor [SerialDescriptor] of current encoding element
 * @param parentBuilder build for parent element
 * @param parentDescriptor [SerialDescriptor] of parent
 * @param indexInParent index current element in parent
 */
@OptIn(ExperimentalSerializationApi::class)
public open class ElementEncoder(
    protected val builder: AnyEncodedElementBuilder?,
    id: String? = null,
    protected val currentDescriptor: SerialDescriptor? = null,
    protected val parentBuilder: ComplexEncodedElementBuilder? = null,
    protected val parentDescriptor: SerialDescriptor? = null,
    protected val indexInParent: Int? = null,
    override val serializersModule: SerializersModule = EmptySerializersModule(),
) : UniqueEncoder {
    override val id: String = id ?: DEFAULT_ID

    init {
        require(builder != null || parentBuilder != null) {
            "For create ElementEncoder you should specify 'builder' or 'parentBuilder'"
        }
    }

    override fun beginCollection(
        descriptor: SerialDescriptor,
        collectionSize: Int,
    ): CompositeEncoder {
        return beginStructure(descriptor)
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        val elementDescriptor = parentDescriptor.tryGetElementDescriptor(indexInParent) ?: descriptor

        val currentComplexBuilder = builder?.takeIfComplex()

        if (currentComplexBuilder == null && parentBuilder == null) {
            throw SerializationProcessException(
                "Can not encode structure when builder is not ComplexEncodedElementBuilder and encoder doesn't has parent builder",
            )
        }

        val elementBuilder: ComplexEncodedElementBuilder =
            currentComplexBuilder ?: DefaultEncodedElement.Builder()

        elementBuilder
            .value(mutableListOf())
            .type(descriptor.kind.toEncodedElementType(parentBuilder?.type))
            .descriptorName(elementDescriptor.serialName)
            .name(parentDescriptor, indexInParent)

        return CompositeElementEncoder(
            builder = elementBuilder,
            parentBuilder = parentBuilder,
            id = id,
            serializersModule = serializersModule,
        )
    }

    override fun encodeBoolean(value: Boolean) {
        encode(value)
    }

    override fun encodeByte(value: Byte) {
        encode(value)
    }

    override fun encodeChar(value: Char) {
        encode(value)
    }

    override fun encodeShort(value: Short) {
        encode(value)
    }

    override fun encodeInt(value: Int) {
        encode(value)
    }

    override fun encodeLong(value: Long) {
        encode(value)
    }

    override fun encodeFloat(value: Float) {
        encode(value)
    }

    override fun encodeDouble(value: Double) {
        encode(value)
    }

    override fun encodeString(value: String) {
        encode(value)
    }

    override fun encodeNull() {
        encode(null)
    }

    override fun encodeEnum(
        enumDescriptor: SerialDescriptor,
        index: Int,
    ) {
        encode(enumDescriptor.getElementName(index))
    }

    override fun encodeInline(descriptor: SerialDescriptor): Encoder =
        ElementEncoder(
            builder = builder,
            id = id,
            currentDescriptor = descriptor,
            parentBuilder = parentBuilder,
            parentDescriptor = parentDescriptor,
            indexInParent = indexInParent,
            serializersModule = serializersModule,
        )

    protected open fun encode(value: Any?) {
        if (parentBuilder != null) {
            DefaultEncodedElement.Builder<Any?>().encode(value).build().also(parentBuilder::add)
        } else {
            builder?.encode(value)
        }
    }

    protected open fun <T> EncodedElementBuilder<T>.encode(value: T): EncodedElementBuilder<T> =
        apply {
            val descriptor = currentDescriptor ?: parentDescriptor.tryGetElementDescriptor(indexInParent)
            if (descriptor?.isNullable == false && value == null) {
                throw SerializationProcessException("Can not encode <null> to not-nullable property")
            }

            descriptorName(descriptor)
            name(parentDescriptor, indexInParent)
            type(value == null, parentBuilder?.type)
            value(value)
        }

    public companion object {
        /**
         * Default id for [ElementEncoder]
         */
        public const val DEFAULT_ID: String = "io.github.edmondantes.serialization.encoding.element.ElementEncoder"
    }
}

public inline fun <reified T> T.encodeToElement(): AnyEncodedElement =
    DefaultEncodedElement.Builder<Any?>().also { builder ->
        serializer<T>().serialize(ElementEncoder(builder), this)
    }.build()
