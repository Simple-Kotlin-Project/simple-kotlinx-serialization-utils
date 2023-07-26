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

import io.github.edmondantes.serialization.encoding.ConstructEncoder
import io.github.edmondantes.serialization.encoding.UniqueEncoder
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

public class ElementEncoder(
    id: String? = null,
    private val parentBuilder: AbstractStructureEncodingElement.Builder<*, *>? = null,
    private val parentDescriptor: SerialDescriptor? = null,
    private val indexInParent: Int? = null,
) : UniqueEncoder, ConstructEncoder<EncodingElement<*>> {
    override val id: String = id ?: "io.github.edmondantes.serialization.encoding.element.ElementEncoder#${ID++}"
    override val serializersModule: SerializersModule
        get() = EmptySerializersModule()

    private var encodingElement: EncodingElement<*>? = null
    private var builder: AbstractStructureEncodingElement.Builder<*, *>? = null

    @OptIn(ExperimentalSerializationApi::class)
    override fun beginCollection(descriptor: SerialDescriptor, collectionSize: Int): CompositeEncoder {
        builder =
            CollectionEncodingElement.Builder()
                .descriptorName(
                    (
                        parentDescriptor?.let { parent -> indexInParent?.let { parent.getElementDescriptor(it) } }
                            ?: descriptor
                        ).serialName,
                )
                .parentDescriptor(parentDescriptor)
                .elementIndex(indexInParent)
        return CompositeElementEncoder(builder!!, parentBuilder)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        builder =
            StructureEncodingElement.Builder()
                .descriptorName(
                    (
                        parentDescriptor?.let { parent -> indexInParent?.let { parent.getElementDescriptor(it) } }
                            ?: descriptor
                        ).serialName,
                )
                .parentDescriptor(parentDescriptor)
                .elementIndex(indexInParent)
        return CompositeElementEncoder(builder!!, parentBuilder)
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

    @ExperimentalSerializationApi
    override fun encodeNull() {
        encode(null)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) {
        encode(enumDescriptor.getElementName(index))
    }

    override fun encodeInline(descriptor: SerialDescriptor): Encoder {
        error("Not support encode inline")
    }

    private fun encode(value: Any?) {
        val element = SimpleEncodingElement(
            if (value == null) EncodingElementType.NULL else if (parentBuilder == null) EncodingElementType.ELEMENT else EncodingElementType.PROPERTY,
            value,
            parentDescriptor,
            indexInParent,
        )

        if (parentBuilder != null) {
            parentBuilder.add(element)
        } else {
            encodingElement = element
        }
    }

    override fun finishConstruct(): EncodingElement<*> =
        encodingElement ?: builder?.build() ?: error("Encoder didn't encode any values")

    private companion object {
        var ID = 0
    }
}
