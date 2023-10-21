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
package io.github.edmondantes.serialization.element.factory.structure.builder

import io.github.edmondantes.serialization.element.AnyEncodedElement
import io.github.edmondantes.serialization.element.ComplexElementBuilder
import io.github.edmondantes.serialization.element.ComplexEncodedElement
import io.github.edmondantes.serialization.element.DefaultEncodedElement
import io.github.edmondantes.serialization.element.EncodedElement
import io.github.edmondantes.serialization.element.EncodedElementType
import io.github.edmondantes.serialization.element.factory.simple.DefaultSimpleEncodedElementFactory
import io.github.edmondantes.serialization.element.factory.simple.SimpleEncodedElementFactory
import io.github.edmondantes.serialization.element.factory.structure.ComplexEncodedElementFactory
import io.github.edmondantes.serialization.element.factory.structure.DefaultComplexEncodedElementFactory
import io.github.edmondantes.serialization.util.tryGetElementDescriptor
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor

public typealias IndexSupplier = (SerialDescriptor) -> Int

/**
 * Abstract base implementation for [DefaultCollectionEncodingElementBuilder] and [DefaultStructureEncodingElementBuilder]
 * @see DefaultCollectionEncodingElementBuilder
 * @see DefaultStructureEncodingElementBuilder
 */
@OptIn(ExperimentalSerializationApi::class)
public abstract class AbstractComplexEncodingElementBuilder(
    protected val descriptor: SerialDescriptor,
    protected val typeForChildren: EncodedElementType,
    protected val descriptorForChildren: SerialDescriptor? = null,
    protected val parent: SerialDescriptor? = null,
    protected val indexInParent: Int? = null,
) {
    protected val structureValue: ComplexElementBuilder = mutableListOf()
    protected val builder: DefaultEncodedElement.Builder<ComplexElementBuilder> =
        DefaultEncodedElement.Builder<ComplexElementBuilder>()
            .type(typeForChildren)
            .descriptorName(descriptor)
            .name(parent, indexInParent)
            .value(structureValue)

    protected fun <T> simpleInternal(
        elementDescriptor: SerialDescriptor?,
        indexSupplier: IndexSupplier,
    ): SimpleEncodedElementFactory<T> =
        prepare(indexSupplier) { childDescriptor, index ->
            DefaultSimpleEncodedElementFactory(
                elementDescriptor ?: childDescriptor,
                typeForChildren,
                descriptorForChildren ?: descriptor,
                index,
            ) {
                structureValue.add(it)
            }
        }

    protected fun <T> simpleInternal(
        elementDescriptor: SerialDescriptor?,
        block: SimpleEncodedElementFactory<T>.() -> EncodedElement<T>,
        indexSupplier: IndexSupplier,
    ): Unit =
        prepare(indexSupplier) { childDescriptor, index ->
            val factory =
                DefaultSimpleEncodedElementFactory<T>(
                    elementDescriptor ?: childDescriptor,
                    typeForChildren,
                    descriptorForChildren ?: descriptor,
                    index,
                )
            structureValue.add(factory.block() as AnyEncodedElement)
        }

    protected fun complexInternal(
        elementDescriptor: SerialDescriptor?,
        indexSupplier: IndexSupplier,
    ): ComplexEncodedElementFactory =
        prepareComplex(elementDescriptor, indexSupplier) { childDescriptor, index ->
            DefaultComplexEncodedElementFactory(
                elementDescriptor ?: childDescriptor,
                null,
                descriptorForChildren ?: descriptor,
                index,
            ) {
                structureValue.add(it as AnyEncodedElement)
            }
        }

    protected fun complexInternal(
        elementDescriptor: SerialDescriptor?,
        block: ComplexEncodedElementFactory.() -> ComplexEncodedElement,
        indexSupplier: IndexSupplier,
    ): Unit =
        prepareComplex(elementDescriptor, indexSupplier) { childDescriptor, index ->
            val factory =
                DefaultComplexEncodedElementFactory(
                    elementDescriptor ?: childDescriptor,
                    null,
                    descriptorForChildren ?: descriptor,
                    index,
                )
            structureValue.add(factory.block() as AnyEncodedElement)
        }

    protected fun contextualInternal(
        elementDescriptor: SerialDescriptor?,
        elementDescriptorForChildren: SerialDescriptor?,
        indexSupplier: IndexSupplier,
    ): ComplexEncodedElementFactory =
        prepareComplex(elementDescriptor, indexSupplier) { childDescriptor, index ->
            DefaultComplexEncodedElementFactory(
                elementDescriptor ?: childDescriptor,
                elementDescriptorForChildren,
                descriptorForChildren ?: descriptor,
                index,
            ) {
                structureValue.add(it as AnyEncodedElement)
            }
        }

    protected fun contextualInternal(
        elementDescriptor: SerialDescriptor?,
        elementDescriptorForChildren: SerialDescriptor?,
        block: ComplexEncodedElementFactory.() -> ComplexEncodedElement,
        indexSupplier: IndexSupplier,
    ): Unit =
        prepareComplex(elementDescriptor, indexSupplier) { childDescriptor, index ->
            val factory =
                DefaultComplexEncodedElementFactory(
                    elementDescriptor ?: childDescriptor,
                    elementDescriptorForChildren,
                    descriptorForChildren ?: descriptor,
                    index,
                )
            structureValue.add(factory.block() as AnyEncodedElement)
        }

    protected inline fun <T> prepare(
        indexSupplier: IndexSupplier,
        block: (SerialDescriptor?, Int) -> T,
    ): T {
        val currDescriptorForChildren = descriptorForChildren ?: descriptor
        val index = indexSupplier(currDescriptorForChildren)
        return currDescriptorForChildren.run {
            val childDescriptor = tryGetElementDescriptor(index)
            block(childDescriptor, index)
        }
    }

    protected inline fun <T> prepareComplex(
        elementDescriptor: SerialDescriptor?,
        indexSupplier: IndexSupplier,
        block: (SerialDescriptor, Int) -> T,
    ): T =
        prepare(indexSupplier) { childDescriptor, index ->
            block(
                elementDescriptor ?: childDescriptor ?: error("Can not find descriptor for element with index: $index"),
                index,
            )
        }
}
