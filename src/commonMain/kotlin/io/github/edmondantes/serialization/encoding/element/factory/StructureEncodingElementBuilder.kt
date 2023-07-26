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
import io.github.edmondantes.serialization.encoding.element.StructureEncodingElement
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlin.jvm.JvmName

public interface StructureEncodingElementBuilder {

    public fun element(name: String, elementDescriptor: SerialDescriptor? = null): ElementFactory

    public fun contextual(
        name: String,
        elementDescriptor: SerialDescriptor? = null,
        childDescriptor: SerialDescriptor? = null,
    ): ElementFactory

    public fun element(
        name: String,
        elementDescriptor: SerialDescriptor? = null,
        block: ElementFactory.() -> EncodingElement<*>,
    ): StructureEncodingElementBuilder

    public fun contextual(
        name: String,
        elementDescriptor: SerialDescriptor? = null,
        childDescriptor: SerialDescriptor? = null,
        block: ElementFactory.() -> EncodingElement<*>,
    ): StructureEncodingElementBuilder

    public fun build(): StructureEncodingElement
}

public fun StructureEncodingElementBuilder.elementNull(name: String): StructureEncodingElementBuilder =
    element(name, value = null)

public fun StructureEncodingElementBuilder.element(name: String, value: Any?): StructureEncodingElementBuilder =
    apply {
        element(name = name).value(value)
    }

@JvmName("elementWithType")
public inline fun <reified T> StructureEncodingElementBuilder.element(
    name: String,
    value: Any?,
): StructureEncodingElementBuilder = apply {
    element(
        name = name,
        elementDescriptor = serialDescriptor<T>(),
    ).value(value)
}

public fun StructureEncodingElementBuilder.structureElement(
    name: String,
    block: StructureEncodingElementBuilder.() -> Unit,
): StructureEncodingElementBuilder = apply {
    element(name = name).structure(block)
}

@JvmName("structureElementWithType")
public inline fun <reified T> StructureEncodingElementBuilder.structureElement(
    name: String,
    noinline block: StructureEncodingElementBuilder.() -> Unit,
): StructureEncodingElementBuilder = apply {
    element(name = name, elementDescriptor = serialDescriptor<T>()).structure(block)
}

public inline fun <reified C> StructureEncodingElementBuilder.structureContextual(
    name: String,
    noinline block: StructureEncodingElementBuilder.() -> Unit,
): StructureEncodingElementBuilder = apply {
    contextual(name = name, elementDescriptor = null, childDescriptor = serialDescriptor<C>()).structure(block)
}

public inline fun <reified T, reified C> StructureEncodingElementBuilder.structureFullContextual(
    name: String,
    noinline block: StructureEncodingElementBuilder.() -> Unit,
): StructureEncodingElementBuilder = apply {
    contextual(
        name = name,
        elementDescriptor = serialDescriptor<T>(),
        childDescriptor = serialDescriptor<C>(),
    ).structure(block)
}

public fun StructureEncodingElementBuilder.collectionElement(
    name: String,
    block: CollectionEncodingElementBuilder.() -> Unit,
): StructureEncodingElementBuilder = apply {
    element(name = name).collection(block)
}

@JvmName("collectionElementWithType")
public inline fun <reified T> StructureEncodingElementBuilder.collectionElement(
    name: String,
    noinline block: CollectionEncodingElementBuilder.() -> Unit,
): StructureEncodingElementBuilder = apply {
    element(name = name, elementDescriptor = serialDescriptor<T>()).collection(block)
}

public inline fun <reified C> StructureEncodingElementBuilder.collectionContextual(
    name: String,
    noinline block: CollectionEncodingElementBuilder.() -> Unit,
): StructureEncodingElementBuilder = apply {
    contextual(name = name, elementDescriptor = null, childDescriptor = serialDescriptor<C>()).collection(block)
}

public inline fun <reified T, reified C> StructureEncodingElementBuilder.collectionFullContextual(
    name: String,
    noinline block: CollectionEncodingElementBuilder.() -> Unit,
): StructureEncodingElementBuilder = apply {
    contextual(
        name = name,
        elementDescriptor = serialDescriptor<T>(),
        childDescriptor = serialDescriptor<C>(),
    ).collection(
        block,
    )
}
