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
package io.github.edmondantes.serialization.encoding.element.factory

import io.github.edmondantes.serialization.encoding.element.EncodingElement
import io.github.edmondantes.serialization.encoding.element.EncodingElementType
import io.github.edmondantes.serialization.encoding.element.EncodingElementType.COLLECTION
import io.github.edmondantes.serialization.encoding.element.EncodingElementType.STRUCTURE
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor

public open class DescriptorElementFactory(
    protected val descriptor: SerialDescriptor?,
    type: EncodingElementType?,
    protected val childDescriptor: SerialDescriptor? = null,
    protected val parentDescriptor: SerialDescriptor? = null,
    protected val indexInParent: Int? = null,
) : AbstractElementFactory(type) {
    override fun <T : EncodingElement<*>> prepare(block: (SerialDescriptor?, SerialDescriptor?, SerialDescriptor?, Int?) -> T): T =
        block(descriptor, childDescriptor, parentDescriptor, indexInParent)
}

public inline fun <reified T> structureElement(
    noinline block: StructureEncodingElementBuilder.() -> Unit,
): EncodingElement<*> =
    DescriptorElementFactory(
        descriptor = serialDescriptor<T>(),
        type = STRUCTURE,
    ).structure(block)

@OptIn(ExperimentalSerializationApi::class)
public inline fun <reified T, reified P> structureElement(
    fieldNameInParent: String,
    noinline block: StructureEncodingElementBuilder.() -> Unit,
): EncodingElement<*> =
    DescriptorElementFactory(
        descriptor = serialDescriptor<T>(),
        type = STRUCTURE,
        parentDescriptor = serialDescriptor<P>(),
        indexInParent = serialDescriptor<P>().getElementIndex(fieldNameInParent),
    ).structure(block)

public inline fun <reified T, reified C> structureContextualElement(
    noinline block: StructureEncodingElementBuilder.() -> Unit,
): EncodingElement<*> =
    DescriptorElementFactory(
        descriptor = serialDescriptor<T>(),
        type = STRUCTURE,
        childDescriptor = serialDescriptor<C>(),
    ).structure(block)

@OptIn(ExperimentalSerializationApi::class)
public inline fun <reified T, reified P, reified C> structureContextualElement(
    fieldNameInParent: String,
    noinline block: StructureEncodingElementBuilder.() -> Unit,
): EncodingElement<*> =
    DescriptorElementFactory(
        descriptor = serialDescriptor<T>(),
        type = STRUCTURE,
        childDescriptor = serialDescriptor<C>(),
        parentDescriptor = serialDescriptor<P>(),
        indexInParent = serialDescriptor<P>().getElementIndex(fieldNameInParent),
    ).structure(block)

public inline fun <reified T> collectionElement(
    noinline block: CollectionEncodingElementBuilder.() -> Unit,
): EncodingElement<*> =
    DescriptorElementFactory(
        descriptor = serialDescriptor<T>(),
        type = COLLECTION,
    ).collection(block)

@OptIn(ExperimentalSerializationApi::class)
public inline fun <reified T, reified P> collectionElement(
    fieldNameInParent: String,
    noinline block: CollectionEncodingElementBuilder.() -> Unit,
): EncodingElement<*> =
    DescriptorElementFactory(
        descriptor = serialDescriptor<T>(),
        type = COLLECTION,
        parentDescriptor = serialDescriptor<P>(),
        indexInParent = serialDescriptor<P>().getElementIndex(fieldNameInParent),
    ).collection(block)

public inline fun <reified T, reified C> collectionContextualElement(
    noinline block: CollectionEncodingElementBuilder.() -> Unit,
): EncodingElement<*> =
    DescriptorElementFactory(
        descriptor = serialDescriptor<T>(),
        type = COLLECTION,
        childDescriptor = serialDescriptor<C>(),
    ).collection(block)

@OptIn(ExperimentalSerializationApi::class)
public inline fun <reified T, reified P, reified C> collectionContextualElement(
    fieldNameInParent: String,
    noinline block: CollectionEncodingElementBuilder.() -> Unit,
): EncodingElement<*> =
    DescriptorElementFactory(
        descriptor = serialDescriptor<T>(),
        type = COLLECTION,
        childDescriptor = serialDescriptor<C>(),
        parentDescriptor = serialDescriptor<P>(),
        indexInParent = serialDescriptor<P>().getElementIndex(fieldNameInParent),
    ).collection(block)
