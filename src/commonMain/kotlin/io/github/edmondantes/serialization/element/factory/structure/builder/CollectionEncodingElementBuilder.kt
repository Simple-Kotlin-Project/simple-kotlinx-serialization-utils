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
import io.github.edmondantes.serialization.element.EncodedElement
import io.github.edmondantes.serialization.element.EncodedElementType
import io.github.edmondantes.serialization.element.factory.simple.SimpleEncodedElementFactory
import io.github.edmondantes.serialization.element.factory.structure.ComplexEncodedElementFactory
import io.github.edmondantes.serialization.util.serialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlin.jvm.JvmName

/**
 * Builder interface for construct [ComplexEncodedElement] with type [EncodedElementType.COLLECTION]
 */
public interface CollectionEncodingElementBuilder {
    /**
     * Adds simple element with [elementDescriptor] to collection
     * @param elementDescriptor [SerialDescriptor] of new element
     * @return [SimpleEncodedElementFactory] for construct simple [EncodedElement]
     */
    public fun <T> simple(elementDescriptor: SerialDescriptor? = null): SimpleEncodedElementFactory<T>

    /**
     * Adds simple element with [elementDescriptor] to collection
     * @param elementDescriptor [SerialDescriptor] of new element
     * @param block function that receive [SimpleEncodedElementFactory] for construct simple [EncodedElement]
     */
    public fun <T> simple(
        elementDescriptor: SerialDescriptor? = null,
        block: SimpleEncodedElementFactory<T>.() -> EncodedElement<T>,
    ): CollectionEncodingElementBuilder =
        apply {
            simple<T>(elementDescriptor).block()
        }

    /**
     * Adds complex element with [elementDescriptor] to structure
     * @param elementDescriptor [SerialDescriptor] of new element
     * @return [ComplexEncodedElementFactory] for construct [ComplexEncodedElement]
     */
    public fun complex(elementDescriptor: SerialDescriptor? = null): ComplexEncodedElementFactory

    /**
     * Adds complex element with [elementDescriptor] to collection
     * @param elementDescriptor [SerialDescriptor] of new element
     * @param block function that receive [ComplexEncodedElementFactory] for construct [ComplexEncodedElement]
     */
    public fun complex(
        elementDescriptor: SerialDescriptor? = null,
        block: ComplexEncodedElementFactory.() -> ComplexEncodedElement,
    ): CollectionEncodingElementBuilder =
        apply {
            complex(elementDescriptor).block()
        }

    /**
     * Adds complex element with [elementDescriptor] to collection
     * @param elementDescriptor [SerialDescriptor] of new element
     * @param childDescriptor [SerialDescriptor] for element's children
     * @return [ComplexEncodedElementFactory] for construct [ComplexEncodedElement]
     */
    public fun contextual(
        elementDescriptor: SerialDescriptor? = null,
        childDescriptor: SerialDescriptor? = null,
    ): ComplexEncodedElementFactory

    /**
     * Adds complex element with [elementDescriptor] to collection
     * @param elementDescriptor [SerialDescriptor] of new element
     * @param childDescriptor [SerialDescriptor] for element's children
     * @param block function that receive [ComplexEncodedElementFactory] for construct [ComplexEncodedElement]
     */
    public fun contextual(
        elementDescriptor: SerialDescriptor? = null,
        childDescriptor: SerialDescriptor? = null,
        block: ComplexEncodedElementFactory.() -> ComplexEncodedElement,
    ): CollectionEncodingElementBuilder

    /**
     * Switch [SerialDescriptor] for children to [descriptor] in [block]
     * @param descriptor new [SerialDescriptor] for children
     * @param block function that receive [CollectionEncodingElementBuilder] with new [SerialDescriptor] for children
     */
    public fun switch(
        descriptor: SerialDescriptor,
        block: CollectionEncodingElementBuilder.() -> Unit,
    ): CollectionEncodingElementBuilder

    /**
     * Finish construct [ComplexEncodedElement]
     */
    public fun build(): ComplexEncodedElement
}

public inline fun <reified T> CollectionEncodingElementBuilder.simple(
    value: T,
    serializersModule: SerializersModule = EmptySerializersModule(),
): CollectionEncodingElementBuilder =
    apply {
        simple<T>(elementDescriptor = serializersModule.serialDescriptor<T>()).value(value = value)
    }

public inline fun <reified T : Any> CollectionEncodingElementBuilder.simpleNullable(value: T?): CollectionEncodingElementBuilder =
    simple<T?>(value)

/**
 * Adds [Byte] element with [value] to collection
 * @param value element's value
 */
public fun CollectionEncodingElementBuilder.byte(value: Number): CollectionEncodingElementBuilder = simple(value = value.toByte())

/**
 * Adds nullable [Byte] element with [value] to collection
 * @param value element's value
 */
public fun CollectionEncodingElementBuilder.nullableByte(value: Number?): CollectionEncodingElementBuilder = simple(value = value?.toByte())

/**
 * Adds [Char] element with [value] to collection
 * @param value element's value
 */
public fun CollectionEncodingElementBuilder.char(value: Number): CollectionEncodingElementBuilder = simple(value = value.toInt().toChar())

/**
 * Adds nullable [Char] element with [value] to collection
 * @param value element's value
 */
public fun CollectionEncodingElementBuilder.nullableChar(value: Number?): CollectionEncodingElementBuilder =
    simple(value = value?.toInt()?.toChar())

/**
 * Adds [Short] element with [value] to collection
 * @param value element's value
 */
public fun CollectionEncodingElementBuilder.short(value: Number): CollectionEncodingElementBuilder = simple(value = value.toShort())

/**
 * Adds nullable [Short] element with [value] to collection
 * @param value element's value
 */
public fun CollectionEncodingElementBuilder.nullableShort(value: Number?): CollectionEncodingElementBuilder =
    simple(value = value?.toShort())

/**
 * Adds [Int] element with [value] to collection
 * @param value element's value
 */
public fun CollectionEncodingElementBuilder.int(value: Number): CollectionEncodingElementBuilder = simple(value = value.toInt())

/**
 * Adds nullable [Int] element with [value] to collection
 * @param value element's value
 */
public fun CollectionEncodingElementBuilder.nullableInt(value: Number?): CollectionEncodingElementBuilder = simple(value = value?.toInt())

/**
 * Adds [Long] element with [value] to collection
 * @param value element's value
 */
public fun CollectionEncodingElementBuilder.long(value: Number): CollectionEncodingElementBuilder = simple(value = value.toLong())

/**
 * Adds nullable [Long] element with [value] to collection
 * @param value element's value
 */
public fun CollectionEncodingElementBuilder.nullableLong(value: Number?): CollectionEncodingElementBuilder = simple(value = value?.toLong())

/**
 * Adds [Float] element with [value] to collection
 * @param value element's value
 */
public fun CollectionEncodingElementBuilder.float(value: Number): CollectionEncodingElementBuilder = simple(value = value.toFloat())

/**
 * Adds nullable [Float] element with [value] to collection
 * @param value element's value
 */
public fun CollectionEncodingElementBuilder.nullableFloat(value: Number?): CollectionEncodingElementBuilder =
    simple(value = value?.toFloat())

/**
 * Adds [Double] element with [value] to collection
 * @param value element's value
 */
public fun CollectionEncodingElementBuilder.double(value: Number): CollectionEncodingElementBuilder = simple(value = value.toDouble())

/**
 * Adds nullable [Double] element with [value] to collection
 * @param value element's value
 */
public fun CollectionEncodingElementBuilder.nullableDouble(value: Number?): CollectionEncodingElementBuilder =
    simple(value = value?.toDouble())

/**
 * Adds structure element to collection
 *
 * Element's [SerialDescriptor] is null\
 *
 * @param block function that setup [StructureEncodingElementBuilder]
 */
public fun CollectionEncodingElementBuilder.structure(block: StructureEncodingElementBuilder.() -> Unit): CollectionEncodingElementBuilder =
    apply {
        complex(elementDescriptor = null).structure(block)
    }

/**
 * Adds structure element to collection
 *
 * @param T type of element
 * @param serializersModule module from which the [SerializersModule] for class [T] is taken
 * @param block function that setup [StructureEncodingElementBuilder]
 */
@JvmName("structureWithType")
public inline fun <reified T> CollectionEncodingElementBuilder.structure(
    serializersModule: SerializersModule = EmptySerializersModule(),
    noinline block: StructureEncodingElementBuilder.() -> Unit,
): CollectionEncodingElementBuilder =
    apply {
        complex(elementDescriptor = serializersModule.serialDescriptor<T>()).structure(block)
    }

/**
 * Adds structure element to collection
 *
 * Element's [SerialDescriptor] is null
 *
 * @param C type for children
 * @param serializersModule module from which the [SerializersModule] for class [C] is taken
 * @param block function that setup [StructureEncodingElementBuilder]
 */
public inline fun <reified C> CollectionEncodingElementBuilder.structureContextual(
    serializersModule: SerializersModule = EmptySerializersModule(),
    noinline block: StructureEncodingElementBuilder.() -> Unit,
): CollectionEncodingElementBuilder =
    apply {
        contextual(elementDescriptor = null, childDescriptor = serializersModule.serialDescriptor<C>()).structure(block)
    }

/**
 * Adds structure element to collection
 *
 * @param T type of element
 * @param C type for children
 * @param serializersModule module from which the [SerializersModule] for classes [T] and [C] are taken
 * @param block function that setup [StructureEncodingElementBuilder]
 */
public inline fun <reified T, reified C> CollectionEncodingElementBuilder.structureFullContextual(
    serializersModule: SerializersModule = EmptySerializersModule(),
    noinline block: StructureEncodingElementBuilder.() -> Unit,
): CollectionEncodingElementBuilder =
    apply {
        contextual(
            elementDescriptor = serializersModule.serialDescriptor<T>(),
            childDescriptor = serializersModule.serialDescriptor<C>(),
        ).structure(block)
    }

/**
 * Adds collection element to collection
 *
 * Element's [SerialDescriptor] is null
 *
 * @param block function that setup [StructureEncodingElementBuilder]
 */
public fun CollectionEncodingElementBuilder.collection(
    block: CollectionEncodingElementBuilder.() -> Unit,
): CollectionEncodingElementBuilder =
    apply {
        complex(elementDescriptor = null).collection(block)
    }

/**
 * Adds collection element to collection
 *
 * @param T type of element
 * @param serializersModule module from which the [SerializersModule] for class [T] is taken
 * @param block function that setup [StructureEncodingElementBuilder]
 */
@JvmName("collectionWithType")
public inline fun <reified T> CollectionEncodingElementBuilder.collection(
    serializersModule: SerializersModule = EmptySerializersModule(),
    noinline block: CollectionEncodingElementBuilder.() -> Unit,
): CollectionEncodingElementBuilder =
    apply {
        complex(elementDescriptor = serializersModule.serialDescriptor<T>()).collection(block)
    }

/**
 * Adds collection element to collection
 *
 * Element's [SerialDescriptor] is null
 *
 * @param C type for children
 * @param serializersModule module from which the [SerializersModule] for class [C] is taken
 * @param block function that setup [StructureEncodingElementBuilder]
 */
public inline fun <reified C> CollectionEncodingElementBuilder.collectionContextual(
    serializersModule: SerializersModule = EmptySerializersModule(),
    noinline block: CollectionEncodingElementBuilder.() -> Unit,
): CollectionEncodingElementBuilder =
    apply {
        contextual(elementDescriptor = null, childDescriptor = serializersModule.serialDescriptor<C>()).collection(block)
    }

/**
 * Adds structure element to collection
 *
 * @param T type of element
 * @param C type for children
 * @param serializersModule module from which the [SerializersModule] for classes [T] and [C] are taken
 * @param block function that setup [StructureEncodingElementBuilder]
 */
public inline fun <reified T, reified C> CollectionEncodingElementBuilder.collectionFullContextual(
    serializersModule: SerializersModule = EmptySerializersModule(),
    noinline block: CollectionEncodingElementBuilder.() -> Unit,
): CollectionEncodingElementBuilder =
    apply {
        contextual(
            elementDescriptor = serializersModule.serialDescriptor<T>(),
            childDescriptor = serializersModule.serialDescriptor<C>(),
        ).collection(block)
    }

/**
 * Switch [SerialDescriptor] type for children to [T] in [block]
 *
 * @param T new type for children
 * @param serializersModule module from which the [SerializersModule] for class [T] is taken
 * @param block function that receive [CollectionEncodingElementBuilder] with new [SerialDescriptor] for children
 */
public inline fun <reified T> CollectionEncodingElementBuilder.switch(
    serializersModule: SerializersModule = EmptySerializersModule(),
    noinline block: CollectionEncodingElementBuilder.() -> Unit,
): CollectionEncodingElementBuilder = switch(serializersModule.serialDescriptor<T>(), block)
