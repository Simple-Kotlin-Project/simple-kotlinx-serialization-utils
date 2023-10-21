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

package io.github.edmondantes.serialization.element.factory.structure

import io.github.edmondantes.serialization.element.ComplexEncodedElement
import io.github.edmondantes.serialization.element.EncodedElementType
import io.github.edmondantes.serialization.element.factory.structure.builder.CollectionEncodingElementBuilder
import io.github.edmondantes.serialization.element.factory.structure.builder.DefaultCollectionEncodingElementBuilder
import io.github.edmondantes.serialization.element.factory.structure.builder.DefaultStructureEncodingElementBuilder
import io.github.edmondantes.serialization.element.factory.structure.builder.StructureEncodingElementBuilder
import io.github.edmondantes.serialization.util.serialDescriptor
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

public typealias ComplexEncodedElementFactoryCallback = (ComplexEncodedElement) -> Unit

/**
 * Default implementation of [ComplexEncodedElementFactory]
 * @param descriptor element's [SerialDescriptor]
 * @param descriptorForChildren [SerialDescriptor] that will be used for children (find name, etc.). If null, will use [descriptor]
 * @param parentDescriptor [SerialDescriptor] of element's parent
 * @param indexInParent element's index in parent
 * @param callback function that will be called after build element
 */
public class DefaultComplexEncodedElementFactory(
    private val descriptor: SerialDescriptor,
    private val descriptorForChildren: SerialDescriptor? = null,
    private val parentDescriptor: SerialDescriptor? = null,
    private val indexInParent: Int? = null,
    private val callback: ComplexEncodedElementFactoryCallback? = null,
) : ComplexEncodedElementFactory {
    override fun structure(block: StructureEncodingElementBuilder.() -> Unit): ComplexEncodedElement =
        DefaultStructureEncodingElementBuilder(
            descriptor,
            descriptorForChildren,
            parentDescriptor,
            indexInParent,
        )
            .also(block)
            .build()
            .also { element ->
                callback?.let { it(element) }
            }

    override fun collection(block: CollectionEncodingElementBuilder.() -> Unit): ComplexEncodedElement =
        DefaultCollectionEncodingElementBuilder(
            descriptor,
            descriptorForChildren,
            parentDescriptor,
            indexInParent,
        )
            .also(block)
            .build()
            .also { element ->
                callback?.let { it(element) }
            }
}

/**
 * Create [ComplexEncodedElement] with type [EncodedElementType.STRUCTURE]
 * @param T type of element
 * @param serializersModule module from which the [SerializersModule] for class [T] is taken
 * @param block function for setup [StructureEncodingElementBuilder]
 */
public inline fun <reified T> structureElement(
    serializersModule: SerializersModule = EmptySerializersModule(),
    noinline block: StructureEncodingElementBuilder.() -> Unit,
): ComplexEncodedElement =
    DefaultComplexEncodedElementFactory(
        descriptor = serializersModule.serialDescriptor<T>(),
    ).structure(block)

/**
 * Create [ComplexEncodedElement] with type [EncodedElementType.STRUCTURE]
 * @param T type of element
 * @param P type of element's parent
 * @param fieldNameInParent element's name in parent
 * @param serializersModule module from which the [SerializersModule] for classes [T] and [P] are taken
 * @param block function for setup [StructureEncodingElementBuilder]
 */
public inline fun <reified T, reified P> structureElement(
    fieldNameInParent: String,
    serializersModule: SerializersModule = EmptySerializersModule(),
    noinline block: StructureEncodingElementBuilder.() -> Unit,
): ComplexEncodedElement =
    serializersModule.serialDescriptor<P>().let { parent ->
        DefaultComplexEncodedElementFactory(
            descriptor = serializersModule.serialDescriptor<T>(),
            parentDescriptor = parent,
            indexInParent = parent.getElementIndex(fieldNameInParent),
        )
    }.structure(block)

/**
 * Create [ComplexEncodedElement] with type [EncodedElementType.STRUCTURE]
 * @param T type of element
 * @param C type for children of elements
 * @param serializersModule module from which the [SerializersModule] for classes [T] and [C] are taken
 * @param block function for setup [StructureEncodingElementBuilder]
 */
public inline fun <reified T, reified C> structureContextualElement(
    serializersModule: SerializersModule = EmptySerializersModule(),
    noinline block: StructureEncodingElementBuilder.() -> Unit,
): ComplexEncodedElement =
    DefaultComplexEncodedElementFactory(
        descriptor = serializersModule.serialDescriptor<T>(),
        descriptorForChildren = serializersModule.serialDescriptor<C>(),
    ).structure(block)

/**
 * Create [ComplexEncodedElement] with type [EncodedElementType.STRUCTURE]
 * @param T type of element
 * @param P type of element's parent
 * @param C type for children of elements
 * @param fieldNameInParent element's name in parent
 * @param serializersModule module from which the [SerializersModule] for classes [T], [P] and [C] are taken
 * @param block function for setup [StructureEncodingElementBuilder]
 */
public inline fun <reified T, reified P, reified C> structureContextualElement(
    fieldNameInParent: String,
    serializersModule: SerializersModule = EmptySerializersModule(),
    noinline block: StructureEncodingElementBuilder.() -> Unit,
): ComplexEncodedElement =
    serializersModule.serialDescriptor<T>().let { parent ->
        DefaultComplexEncodedElementFactory(
            descriptor = serializersModule.serialDescriptor<T>(),
            descriptorForChildren = serializersModule.serialDescriptor<C>(),
            parentDescriptor = parent,
            indexInParent = parent.getElementIndex(fieldNameInParent),
        )
    }.structure(block)

/**
 * Create [ComplexEncodedElement] with type [EncodedElementType.COLLECTION]
 * @param T type of element
 * @param serializersModule module from which the [SerializersModule] for classes [T] is taken
 * @param block function for setup [CollectionEncodingElementBuilder]
 */
public inline fun <reified T> collectionElement(
    serializersModule: SerializersModule = EmptySerializersModule(),
    noinline block: CollectionEncodingElementBuilder.() -> Unit,
): ComplexEncodedElement =
    DefaultComplexEncodedElementFactory(
        descriptor = serializersModule.serialDescriptor<T>(),
    ).collection(block)

/**
 * Create [ComplexEncodedElement] with type [EncodedElementType.COLLECTION]
 * @param T type of element
 * @param P type of element's parent
 * @param serializersModule module from which the [SerializersModule] for classes [T] and [P] are taken
 * @param fieldNameInParent element's name in parent
 * @param block function for setup [CollectionEncodingElementBuilder]
 */
public inline fun <reified T, reified P> collectionElement(
    fieldNameInParent: String,
    serializersModule: SerializersModule = EmptySerializersModule(),
    noinline block: CollectionEncodingElementBuilder.() -> Unit,
): ComplexEncodedElement =
    serializersModule.serialDescriptor<P>().let { parent ->
        DefaultComplexEncodedElementFactory(
            descriptor = serializersModule.serialDescriptor<T>(),
            parentDescriptor = parent,
            indexInParent = parent.getElementIndex(fieldNameInParent),
        )
    }.collection(block)

/**
 * Create [ComplexEncodedElement] with type [EncodedElementType.COLLECTION]
 * @param T type of element
 * @param C type for children of elements
 * @param serializersModule module from which the [SerializersModule] for classes [T] and [C] are taken
 * @param block function for setup [CollectionEncodingElementBuilder]
 */
public inline fun <reified T, reified C> collectionContextualElement(
    serializersModule: SerializersModule = EmptySerializersModule(),
    noinline block: CollectionEncodingElementBuilder.() -> Unit,
): ComplexEncodedElement =
    DefaultComplexEncodedElementFactory(
        descriptor = serializersModule.serialDescriptor<T>(),
        descriptorForChildren = serializersModule.serialDescriptor<C>(),
    ).collection(block)

/**
 * Create [ComplexEncodedElement] with type [EncodedElementType.COLLECTION]
 * @param T type of element
 * @param P type of element's parent
 * @param C type for children of elements
 * @param serializersModule module from which the [SerializersModule] for classes [T], [P] and [C] are taken
 * @param fieldNameInParent element's name in parent
 * @param block function for setup [CollectionEncodingElementBuilder]
 */
public inline fun <reified T, reified P, reified C> collectionContextualElement(
    fieldNameInParent: String,
    serializersModule: SerializersModule = EmptySerializersModule(),
    noinline block: CollectionEncodingElementBuilder.() -> Unit,
): ComplexEncodedElement =
    serializersModule.serialDescriptor<P>().let { parent ->
        DefaultComplexEncodedElementFactory(
            descriptor = serializersModule.serialDescriptor<T>(),
            descriptorForChildren = serializersModule.serialDescriptor<C>(),
            parentDescriptor = parent,
            indexInParent = parent.getElementIndex(fieldNameInParent),
        )
    }.collection(block)
