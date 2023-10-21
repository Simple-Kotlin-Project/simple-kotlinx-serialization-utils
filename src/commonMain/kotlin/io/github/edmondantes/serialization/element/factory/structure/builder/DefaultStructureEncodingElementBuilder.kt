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
import io.github.edmondantes.serialization.element.EncodedElementType.STRUCTURE
import io.github.edmondantes.serialization.element.factory.simple.SimpleEncodedElementFactory
import io.github.edmondantes.serialization.element.factory.structure.ComplexEncodedElementFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor

/**
 * Default implementation of [StructureEncodingElementBuilder]
 */
@OptIn(ExperimentalSerializationApi::class)
public open class DefaultStructureEncodingElementBuilder(
    descriptor: SerialDescriptor,
    descriptorForChildren: SerialDescriptor? = null,
    parent: SerialDescriptor? = null,
    indexInParent: Int? = null,
) : AbstractComplexEncodingElementBuilder(descriptor, STRUCTURE, descriptorForChildren, parent, indexInParent),
    StructureEncodingElementBuilder {
    override fun <T> simple(
        name: String,
        elementDescriptor: SerialDescriptor?,
    ): SimpleEncodedElementFactory<T> = simpleInternal(elementDescriptor) { it.getElementIndex(name) }

    override fun complex(
        name: String,
        elementDescriptor: SerialDescriptor?,
    ): ComplexEncodedElementFactory = complexInternal(elementDescriptor) { it.getElementIndex(name) }

    override fun contextual(
        name: String,
        elementDescriptor: SerialDescriptor?,
        childDescriptor: SerialDescriptor?,
    ): ComplexEncodedElementFactory = contextualInternal(elementDescriptor, childDescriptor) { it.getElementIndex(name) }

    override fun switch(
        descriptor: SerialDescriptor,
        block: StructureEncodingElementBuilder.() -> Unit,
    ): StructureEncodingElementBuilder {
        val switchBuilder = DefaultStructureEncodingElementBuilder(descriptor, null, parent, indexInParent)
        switchBuilder.block()
        structureValue.addAll(switchBuilder.structureValue)
        return this
    }

    override fun build(): ComplexEncodedElement = builder.build()
}
