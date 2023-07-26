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

import io.github.edmondantes.serialization.encoding.element.CollectionEncodingElement
import io.github.edmondantes.serialization.encoding.element.EncodingElement
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlin.jvm.JvmName

public interface CollectionEncodingElementBuilder {

    public fun element(elementDescriptor: SerialDescriptor? = null): ElementFactory

    public fun contextual(
        elementDescriptor: SerialDescriptor? = null,
        childDescriptor: SerialDescriptor? = null,
    ): ElementFactory

    public fun element(
        elementDescriptor: SerialDescriptor? = null,
        block: ElementFactory.() -> EncodingElement<*>,
    ): CollectionEncodingElementBuilder

    public fun contextual(
        elementDescriptor: SerialDescriptor? = null,
        childDescriptor: SerialDescriptor? = null,
        block: ElementFactory.() -> EncodingElement<*>,
    ): CollectionEncodingElementBuilder

    public fun build(): CollectionEncodingElement
}

public fun CollectionEncodingElementBuilder.element(value: Any?): CollectionEncodingElementBuilder = apply {
    element().value(value)
}

public fun CollectionEncodingElementBuilder.elementByte(value: Number?): CollectionEncodingElementBuilder = apply {
    element().byte(value)
}

public fun CollectionEncodingElementBuilder.elementChar(value: Number?): CollectionEncodingElementBuilder = apply {
    element().char(value)
}

public fun CollectionEncodingElementBuilder.elementShort(value: Number?): CollectionEncodingElementBuilder = apply {
    element().short(value)
}

public fun CollectionEncodingElementBuilder.elementInt(value: Number?): CollectionEncodingElementBuilder = apply {
    element().int(value)
}

public fun CollectionEncodingElementBuilder.elementLong(value: Number?): CollectionEncodingElementBuilder = apply {
    element().long(value)
}

public fun CollectionEncodingElementBuilder.elementFloat(value: Number?): CollectionEncodingElementBuilder = apply {
    element().float(value)
}

public fun CollectionEncodingElementBuilder.elementDouble(value: Number?): CollectionEncodingElementBuilder = apply {
    element().double(value)
}

@JvmName("elementWithType")
public inline fun <reified T> CollectionEncodingElementBuilder.element(value: Any?): CollectionEncodingElementBuilder =
    apply {
        element(elementDescriptor = serialDescriptor<T>()).value(value)
    }

public fun CollectionEncodingElementBuilder.structureElement(block: StructureEncodingElementBuilder.() -> Unit): CollectionEncodingElementBuilder =
    apply {
        element().structure(block)
    }

@JvmName("structureElementWithType")
public inline fun <reified T> CollectionEncodingElementBuilder.structureElement(noinline block: StructureEncodingElementBuilder.() -> Unit): CollectionEncodingElementBuilder =
    apply {
        element(elementDescriptor = serialDescriptor<T>()).structure(block)
    }

public inline fun <reified C> CollectionEncodingElementBuilder.structureContextual(
    noinline block: StructureEncodingElementBuilder.() -> Unit,
): CollectionEncodingElementBuilder = apply {
    contextual(elementDescriptor = null, childDescriptor = serialDescriptor<C>()).structure(block)
}

public inline fun <reified T, reified C> CollectionEncodingElementBuilder.structureFullContextual(
    noinline block: StructureEncodingElementBuilder.() -> Unit,
): CollectionEncodingElementBuilder = apply {
    contextual(
        elementDescriptor = serialDescriptor<T>(),
        childDescriptor = serialDescriptor<C>(),
    ).structure(block)
}

public fun CollectionEncodingElementBuilder.collectionElement(block: CollectionEncodingElementBuilder.() -> Unit): CollectionEncodingElementBuilder =
    apply {
        element().collection(block)
    }

@JvmName("collectionElementWithType")
public inline fun <reified T> CollectionEncodingElementBuilder.collectionElement(noinline block: CollectionEncodingElementBuilder.() -> Unit): CollectionEncodingElementBuilder =
    apply {
        element(elementDescriptor = serialDescriptor<T>()).collection(block)
    }

public inline fun <reified C> CollectionEncodingElementBuilder.collectionContextual(
    noinline block: CollectionEncodingElementBuilder.() -> Unit,
): CollectionEncodingElementBuilder = apply {
    contextual(elementDescriptor = null, childDescriptor = serialDescriptor<C>()).collection(block)
}

public inline fun <reified T, reified C> CollectionEncodingElementBuilder.collectionFullContextual(
    noinline block: CollectionEncodingElementBuilder.() -> Unit,
): CollectionEncodingElementBuilder = apply {
    contextual(elementDescriptor = serialDescriptor<T>(), childDescriptor = serialDescriptor<C>()).collection(block)
}
