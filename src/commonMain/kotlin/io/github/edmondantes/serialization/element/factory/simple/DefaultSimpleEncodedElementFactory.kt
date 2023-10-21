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
package io.github.edmondantes.serialization.element.factory.simple

import io.github.edmondantes.serialization.element.DefaultEncodedElement
import io.github.edmondantes.serialization.element.EncodedElement
import io.github.edmondantes.serialization.element.EncodedElementType
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer

public typealias SimpleEncodedElementFactoryCallback<T> = (EncodedElement<T>) -> Unit

/**
 * Default implementation of [SimpleEncodedElementFactory]
 * @param descriptor element's [SerialDescriptor]
 * @param parentType [EncodedElementType] of element's parent
 * @param parentDescriptor [SerialDescriptor] of element's parent
 * @param indexInParent element's index in parent
 * @param callback function that will be called after build element
 */
public class DefaultSimpleEncodedElementFactory<T>(
    private val descriptor: SerialDescriptor?,
    private val parentType: EncodedElementType? = null,
    private val parentDescriptor: SerialDescriptor? = null,
    private val indexInParent: Int? = null,
    private val callback: SimpleEncodedElementFactoryCallback<T>? = null,
) : SimpleEncodedElementFactory<T> {
    override fun value(value: T): EncodedElement<T> =
        DefaultEncodedElement.Builder<T>()
            .descriptorName(descriptor)
            .name(parentDescriptor, indexInParent)
            .type(value == null, parentType)
            .value(value)
            .build()
            .also { element -> callback?.let { it(element) } }
}

/**
 * Create simple [EncodedElement]
 * @param descriptor element's descriptor
 * @param value element's value
 * @param parentType [EncodedElementType] of element's parent
 * @param parentDescriptor parent's [SerialDescriptor]
 * @param indexInParent element's index in parent's [SerialDescriptor]
 */
public fun <T> simpleElement(
    descriptor: SerialDescriptor?,
    value: T,
    parentType: EncodedElementType? = null,
    parentDescriptor: SerialDescriptor? = null,
    indexInParent: Int? = null,
): EncodedElement<T> = DefaultSimpleEncodedElementFactory<T>(descriptor, parentType, parentDescriptor, indexInParent, null).value(value)

/**
 * Create simple [EncodedElement]
 * @param value element's value
 * @param parentType [EncodedElementType] of element's parent
 * @param parentDescriptor parent's [SerialDescriptor]
 * @param indexInParent element's index in parent's [SerialDescriptor]
 */
public inline fun <reified T> simpleElement(
    value: T,
    parentType: EncodedElementType? = null,
    parentDescriptor: SerialDescriptor? = null,
    indexInParent: Int? = null,
    serializableModule: SerializersModule = EmptySerializersModule(),
): EncodedElement<T> =
    simpleElement(
        serializableModule.serializer<T>().descriptor,
        value,
        parentType,
        parentDescriptor,
        indexInParent,
    )
