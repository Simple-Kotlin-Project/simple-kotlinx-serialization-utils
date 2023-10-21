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
 * Builder interface for construct [ComplexEncodedElement] with type [EncodedElementType.STRUCTURE]
 */
public interface StructureEncodingElementBuilder {
    /**
     * Adds simple element with [name] and [elementDescriptor] to structure
     * @param name name of new element
     * @param elementDescriptor [SerialDescriptor] of new element
     * @return [SimpleEncodedElementFactory] for construct simple [EncodedElement]
     */
    public fun <T> simple(
        name: String,
        elementDescriptor: SerialDescriptor? = null,
    ): SimpleEncodedElementFactory<T>

    /**
     * Adds simple element with [name] and [elementDescriptor] to structure
     * @param name name of new element
     * @param elementDescriptor [SerialDescriptor] of new element
     * @param block function that receive [SimpleEncodedElementFactory] for construct simple [EncodedElement]
     */
    public fun <T> simple(
        name: String,
        elementDescriptor: SerialDescriptor? = null,
        block: SimpleEncodedElementFactory<T>.() -> EncodedElement<T>,
    ): StructureEncodingElementBuilder =
        apply {
            simple<T>(name, elementDescriptor).block()
        }

    /**
     * Adds complex element with [name] and [elementDescriptor] to structure
     * @param name of new element
     * @param elementDescriptor [SerialDescriptor] of new element
     * @return [ComplexEncodedElementFactory] for construct [ComplexEncodedElement]
     */
    public fun complex(
        name: String,
        elementDescriptor: SerialDescriptor? = null,
    ): ComplexEncodedElementFactory

    /**
     * Adds complex element with [name] and [elementDescriptor] to structure
     * @param name of new element
     * @param elementDescriptor [SerialDescriptor] of new element
     * @param block function that receive [ComplexEncodedElementFactory] for construct [ComplexEncodedElement]
     */
    public fun complex(
        name: String,
        elementDescriptor: SerialDescriptor? = null,
        block: ComplexEncodedElementFactory.() -> ComplexEncodedElement,
    ): StructureEncodingElementBuilder =
        apply {
            complex(name, elementDescriptor).block()
        }

    /**
     * Adds complex element with [name] and [elementDescriptor] to structure
     * @param name of new element
     * @param elementDescriptor [SerialDescriptor] of new element
     * @param childDescriptor [SerialDescriptor] for element's children
     * @return [ComplexEncodedElementFactory] for construct [ComplexEncodedElement]
     */
    public fun contextual(
        name: String,
        elementDescriptor: SerialDescriptor? = null,
        childDescriptor: SerialDescriptor? = null,
    ): ComplexEncodedElementFactory

    /**
     * Adds complex element with [name] and [elementDescriptor] to structure
     * @param name of new element
     * @param elementDescriptor [SerialDescriptor] of new element
     * @param childDescriptor [SerialDescriptor] for element's children
     * @param block function that receive [ComplexEncodedElementFactory] for construct [ComplexEncodedElement]
     */
    public fun contextual(
        name: String,
        elementDescriptor: SerialDescriptor? = null,
        childDescriptor: SerialDescriptor? = null,
        block: ComplexEncodedElementFactory.() -> ComplexEncodedElement,
    ): StructureEncodingElementBuilder =
        apply {
            contextual(name, elementDescriptor, childDescriptor).block()
        }

    /**
     * Switch [SerialDescriptor] for children to [descriptor] in [block]
     * @param descriptor new [SerialDescriptor] for children
     * @param block function that receive [StructureEncodingElementBuilder] with new [SerialDescriptor] for children
     */
    public fun switch(
        descriptor: SerialDescriptor,
        block: StructureEncodingElementBuilder.() -> Unit,
    ): StructureEncodingElementBuilder

    /**
     * Finish construct [ComplexEncodedElement]
     */
    public fun build(): ComplexEncodedElement
}

/**
 * Adds simple element with [name] and [value] to structure
 *
 * @param T simple element class
 * @param name name of new element
 * @param value element's value
 * @param serializersModule module from which the [SerializersModule] for class [T] is taken
 */
public inline fun <reified T> StructureEncodingElementBuilder.simple(
    name: String,
    value: T,
    serializersModule: SerializersModule = EmptySerializersModule(),
): StructureEncodingElementBuilder =
    apply {
        simple<T>(name = name, elementDescriptor = serializersModule.serialDescriptor<T>()).value(value = value)
    }

/**
 * Adds nullable simple element with [name] and [value] to structure
 * @param name name of new element
 * @param value element's value
 * @param serializersModule module from which the [SerializersModule] for class [T] is taken
 */
public inline fun <reified T : Any> StructureEncodingElementBuilder.simpleNullable(
    name: String,
    value: T?,
    serializersModule: SerializersModule = EmptySerializersModule(),
): StructureEncodingElementBuilder = simple<T?>(name, value, serializersModule)

/**
 * Adds [Byte] element with [name] and [value] to structure
 * @param name name of new element
 * @param value element's value
 * @param serializersModule module from which the [SerializersModule] for class [Byte] is taken
 */
public fun StructureEncodingElementBuilder.byte(
    name: String,
    value: Number,
    serializersModule: SerializersModule = EmptySerializersModule(),
): StructureEncodingElementBuilder = simple(name = name, value = value.toByte(), serializersModule = serializersModule)

/**
 * Adds nullable [Byte] element with [name] and [value] to structure
 * @param name name of new element
 * @param value element's value
 * @param serializersModule module from which the [SerializersModule] for class [Byte] is taken
 */
public fun StructureEncodingElementBuilder.nullableByte(
    name: String,
    value: Number?,
    serializersModule: SerializersModule = EmptySerializersModule(),
): StructureEncodingElementBuilder = simple(name = name, value = value?.toByte(), serializersModule = serializersModule)

/**
 * Adds [Char] element with [name] and [value] to structure
 * @param name name of new element
 * @param value element's value
 * @param serializersModule module from which the [SerializersModule] for class [Char] is taken
 */
public fun StructureEncodingElementBuilder.char(
    name: String,
    value: Number,
    serializersModule: SerializersModule = EmptySerializersModule(),
): StructureEncodingElementBuilder = simple(name = name, value = value.toInt().toChar(), serializersModule = serializersModule)

/**
 * Adds nullable [Char] element with [name] and [value] to structure
 * @param name name of new element
 * @param value element's value
 * @param serializersModule module from which the [SerializersModule] for class [Char] is taken
 */
public fun StructureEncodingElementBuilder.nullableChar(
    name: String,
    value: Number?,
    serializersModule: SerializersModule = EmptySerializersModule(),
): StructureEncodingElementBuilder = simple(name = name, value = value?.toInt()?.toChar(), serializersModule = serializersModule)

/**
 * Adds [Short] element with [name] and [value] to structure
 * @param name name of new element
 * @param value element's value
 * @param serializersModule module from which the [SerializersModule] for class [Short] is taken
 */
public fun StructureEncodingElementBuilder.short(
    name: String,
    value: Number,
    serializersModule: SerializersModule = EmptySerializersModule(),
): StructureEncodingElementBuilder = simple(name = name, value = value.toShort(), serializersModule = serializersModule)

/**
 * Adds nullable [Short] element with [name] and [value] to structure
 * @param name name of new element
 * @param value element's value
 * @param serializersModule module from which the [SerializersModule] for class [Short] is taken
 */
public fun StructureEncodingElementBuilder.nullableShort(
    name: String,
    value: Number?,
    serializersModule: SerializersModule = EmptySerializersModule(),
): StructureEncodingElementBuilder = simple(name = name, value = value?.toShort(), serializersModule = serializersModule)

/**
 * Adds [Int] element with [name] and [value] to structure
 * @param name name of new element
 * @param value element's value
 * @param serializersModule module from which the [SerializersModule] for class [Int] is taken
 */
public fun StructureEncodingElementBuilder.int(
    name: String,
    value: Number,
    serializersModule: SerializersModule = EmptySerializersModule(),
): StructureEncodingElementBuilder = simple(name = name, value = value.toInt(), serializersModule = serializersModule)

/**
 * Adds nullable [Int] element with [name] and [value] to structure
 * @param name name of new element
 * @param value element's value
 * @param serializersModule module from which the [SerializersModule] for class [Int] is taken
 */
public fun StructureEncodingElementBuilder.nullableInt(
    name: String,
    value: Number?,
    serializersModule: SerializersModule = EmptySerializersModule(),
): StructureEncodingElementBuilder = simple(name = name, value = value?.toInt(), serializersModule = serializersModule)

/**
 * Adds [Long] element with [name] and [value] to structure
 * @param name name of new element
 * @param value element's value
 * @param serializersModule module from which the [SerializersModule] for class [Long] is taken
 */
public fun StructureEncodingElementBuilder.long(
    name: String,
    value: Number,
    serializersModule: SerializersModule = EmptySerializersModule(),
): StructureEncodingElementBuilder = simple(name = name, value = value.toLong(), serializersModule = serializersModule)

/**
 * Adds nullable [Long] element with [name] and [value] to structure
 * @param name name of new element
 * @param value element's value
 * @param serializersModule module from which the [SerializersModule] for class [Long] is taken
 */
public fun StructureEncodingElementBuilder.nullableLong(
    name: String,
    value: Number?,
    serializersModule: SerializersModule = EmptySerializersModule(),
): StructureEncodingElementBuilder = simple(name = name, value = value?.toLong(), serializersModule = serializersModule)

/**
 * Adds [Float] element with [name] and [value] to structure
 * @param name name of new element
 * @param value element's value
 * @param serializersModule module from which the [SerializersModule] for class [Float] is taken
 */
public fun StructureEncodingElementBuilder.float(
    name: String,
    value: Number,
    serializersModule: SerializersModule = EmptySerializersModule(),
): StructureEncodingElementBuilder = simple(name = name, value = value.toFloat(), serializersModule = serializersModule)

/**
 * Adds nullable [Float] element with [name] and [value] to structure
 * @param name name of new element
 * @param value element's value
 * @param serializersModule module from which the [SerializersModule] for class [Float] is taken
 */
public fun StructureEncodingElementBuilder.nullableFloat(
    name: String,
    value: Number?,
    serializersModule: SerializersModule = EmptySerializersModule(),
): StructureEncodingElementBuilder = simple(name = name, value = value?.toFloat(), serializersModule = serializersModule)

/**
 * Adds [Double] element with [name] and [value] to structure
 * @param name name of new element
 * @param value element's value
 * @param serializersModule module from which the [SerializersModule] for class [Double] is taken
 */
public fun StructureEncodingElementBuilder.double(
    name: String,
    value: Number,
    serializersModule: SerializersModule = EmptySerializersModule(),
): StructureEncodingElementBuilder = simple(name = name, value = value.toDouble(), serializersModule = serializersModule)

/**
 * Adds nullable [Double] element with [name] and [value] to structure
 * @param name name of new element
 * @param value element's value
 * @param serializersModule module from which the [SerializersModule] for class [Double] is taken
 */
public fun StructureEncodingElementBuilder.nullableDouble(
    name: String,
    value: Number?,
    serializersModule: SerializersModule = EmptySerializersModule(),
): StructureEncodingElementBuilder = simple(name = name, value = value?.toDouble(), serializersModule = serializersModule)

/**
 * Adds structure element
 *
 * Element's [SerialDescriptor] is null
 *
 * @param name name of new element
 * @param block function that setup [StructureEncodingElementBuilder]
 */
public fun StructureEncodingElementBuilder.structure(
    name: String,
    block: StructureEncodingElementBuilder.() -> Unit,
): StructureEncodingElementBuilder =
    apply {
        complex(name = name).structure(block)
    }

/**
 * Adds structure element
 *
 * @param T type of element
 * @param name name of new element
 * @param serializersModule module from which the [SerializersModule] for class [T] is taken
 * @param block function that setup [StructureEncodingElementBuilder]
 */
@JvmName("structureWithType")
public inline fun <reified T> StructureEncodingElementBuilder.structure(
    name: String,
    serializersModule: SerializersModule = EmptySerializersModule(),
    noinline block: StructureEncodingElementBuilder.() -> Unit,
): StructureEncodingElementBuilder =
    apply {
        complex(name = name, elementDescriptor = serializersModule.serialDescriptor<T>()).structure(block)
    }

/**
 * Adds structure element
 *
 * Element's [SerialDescriptor] is null
 *
 * @param C type for children
 * @param name name of new element
 * @param serializersModule module from which the [SerializersModule] for class [C] is taken
 * @param block function that setup [StructureEncodingElementBuilder]
 */
public inline fun <reified C> StructureEncodingElementBuilder.structureContextual(
    name: String,
    serializersModule: SerializersModule = EmptySerializersModule(),
    noinline block: StructureEncodingElementBuilder.() -> Unit,
): StructureEncodingElementBuilder =
    apply {
        contextual(name = name, elementDescriptor = null, childDescriptor = serializersModule.serialDescriptor<C>()).structure(block)
    }

/**
 * Adds structure element
 * @param T type of element
 * @param C type for children
 * @param name name of new element
 * @param serializersModule module from which the [SerializersModule] for classes [T] and [C] are taken
 * @param block function that setup [StructureEncodingElementBuilder]
 */
public inline fun <reified T, reified C> StructureEncodingElementBuilder.structureFullContextual(
    name: String,
    serializersModule: SerializersModule = EmptySerializersModule(),
    noinline block: StructureEncodingElementBuilder.() -> Unit,
): StructureEncodingElementBuilder =
    apply {
        contextual(
            name = name,
            elementDescriptor = serializersModule.serialDescriptor<T>(),
            childDescriptor = serializersModule.serialDescriptor<C>(),
        ).structure(block)
    }

/**
 * Adds collection element
 *
 * Element's [SerialDescriptor] is null
 *
 * @param name name of new element
 * @param block function that setup [StructureEncodingElementBuilder]
 */
public fun StructureEncodingElementBuilder.collection(
    name: String,
    block: CollectionEncodingElementBuilder.() -> Unit,
): StructureEncodingElementBuilder =
    apply {
        complex(name = name).collection(block)
    }

/**
 * Adds collection element
 *
 * @param T type of element
 * @param name name of new element
 * @param serializersModule module from which the [SerializersModule] for class [T] is taken
 * @param block function that setup [StructureEncodingElementBuilder]
 */
@JvmName("collectionWithType")
public inline fun <reified T> StructureEncodingElementBuilder.collection(
    name: String,
    serializersModule: SerializersModule = EmptySerializersModule(),
    noinline block: CollectionEncodingElementBuilder.() -> Unit,
): StructureEncodingElementBuilder =
    apply {
        complex(name = name, elementDescriptor = serializersModule.serialDescriptor<T>()).collection(block)
    }

/**
 * Adds collection element
 *
 * Element's [SerialDescriptor] is null
 *
 * @param C type for children
 * @param name name of new element
 * @param serializersModule module from which the [SerializersModule] for class [C] is taken
 * @param block function that setup [StructureEncodingElementBuilder]
 */
public inline fun <reified C> StructureEncodingElementBuilder.collectionContextual(
    name: String,
    serializersModule: SerializersModule = EmptySerializersModule(),
    noinline block: CollectionEncodingElementBuilder.() -> Unit,
): StructureEncodingElementBuilder =
    apply {
        contextual(name = name, elementDescriptor = null, childDescriptor = serializersModule.serialDescriptor<C>()).collection(block)
    }

/**
 * Adds structure element
 *
 * @param T type of element
 * @param C type for children
 * @param name name of new element
 * @param serializersModule module from which the [SerializersModule] for classes [T] and [C] are taken
 * @param block function that setup [StructureEncodingElementBuilder]
 */
public inline fun <reified T, reified C> StructureEncodingElementBuilder.collectionFullContextual(
    name: String,
    serializersModule: SerializersModule = EmptySerializersModule(),
    noinline block: CollectionEncodingElementBuilder.() -> Unit,
): StructureEncodingElementBuilder =
    apply {
        contextual(
            name = name,
            elementDescriptor = serializersModule.serialDescriptor<T>(),
            childDescriptor = serializersModule.serialDescriptor<C>(),
        ).collection(
            block,
        )
    }

/**
 * Switch [SerialDescriptor] type for children to [T] in [block]
 *
 * @param T new type for children
 * @param serializersModule module from which the [SerializersModule] for class [T] is taken
 * @param block function that receive [StructureEncodingElementBuilder] with new [SerialDescriptor] for children
 */
public inline fun <reified T> StructureEncodingElementBuilder.switch(
    serializersModule: SerializersModule = EmptySerializersModule(),
    noinline block: StructureEncodingElementBuilder.() -> Unit,
): StructureEncodingElementBuilder = switch(serializersModule.serialDescriptor<T>(), block)
