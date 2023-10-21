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

import io.github.edmondantes.serialization.element.ComplexEncodedElement
import io.github.edmondantes.serialization.element.EncodedElementType.COLLECTION
import io.github.edmondantes.serialization.element.factory.simple.SimpleEncodedElementFactory
import io.github.edmondantes.serialization.element.factory.structure.ComplexEncodedElementFactory
import kotlinx.serialization.descriptors.SerialDescriptor

public open class DefaultCollectionEncodingElementBuilder(
    descriptor: SerialDescriptor,
    childDescriptor: SerialDescriptor? = null,
    parent: SerialDescriptor? = null,
    indexInParent: Int? = null,
) : AbstractComplexEncodingElementBuilder(descriptor, COLLECTION, childDescriptor, parent, indexInParent),
    CollectionEncodingElementBuilder {
    private val index: Int
        get() = structureValue.size

    override fun <T> simple(elementDescriptor: SerialDescriptor?): SimpleEncodedElementFactory<T> =
        simpleInternal(elementDescriptor) { index }

    override fun complex(elementDescriptor: SerialDescriptor?): ComplexEncodedElementFactory = complexInternal(elementDescriptor) { index }

    override fun contextual(
        elementDescriptor: SerialDescriptor?,
        childDescriptor: SerialDescriptor?,
    ): ComplexEncodedElementFactory = contextualInternal(elementDescriptor, childDescriptor) { index }

    override fun contextual(
        elementDescriptor: SerialDescriptor?,
        childDescriptor: SerialDescriptor?,
        block: ComplexEncodedElementFactory.() -> ComplexEncodedElement,
    ): CollectionEncodingElementBuilder =
        apply {
            contextualInternal(elementDescriptor, childDescriptor, block) { index }
        }

    override fun switch(
        descriptor: SerialDescriptor,
        block: CollectionEncodingElementBuilder.() -> Unit,
    ): CollectionEncodingElementBuilder {
        val switchBuilder = DefaultCollectionEncodingElementBuilder(descriptor, null, parent, indexInParent)
        switchBuilder.block()
        structureValue.addAll(switchBuilder.structureValue)
        return this
    }

    override fun build(): ComplexEncodedElement = builder.build()
}
