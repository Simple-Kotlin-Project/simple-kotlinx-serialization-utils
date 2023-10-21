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
@file:Suppress("UNCHECKED_CAST")

package io.github.edmondantes.serialization.element

import io.github.edmondantes.serialization.util.asInstance
import io.github.edmondantes.serialization.util.map
import io.github.edmondantes.serialization.util.or

public typealias ComplexElement = List<AnyEncodedElement>

/**
 * Complex [EncodedElement] with type [EncodedElementType.STRUCTURE] or [EncodedElementType.COLLECTION]
 * and [List] of [AnyEncodedElement] as value
 */
public typealias ComplexEncodedElement = EncodedElement<ComplexElement>

public typealias ComplexElementBuilder = MutableList<AnyEncodedElement>

/**
 * Complex [EncodedElement.Builder] with type [EncodedElementType.STRUCTURE] or [EncodedElementType.COLLECTION]
 * and [List] of [AnyEncodedElement] as value
 */
public typealias ComplexEncodedElementBuilder = EncodedElementBuilder<ComplexElementBuilder>

/**
 * Check it is [ComplexEncodedElement]
 * @return true if it is [ComplexEncodedElement], else false
 */
public fun AnyEncodedElement.isComplex(): Boolean =
    type.isComplex() &&
        value.asInstance<List<*>>().map { it.getOrNull(0) is AnyEncodedElement }.value

/**
 * Check it is [ComplexEncodedElementBuilder]
 * @return true if it is [ComplexEncodedElementBuilder], else false
 */
public fun AnyEncodedElementBuilder.isComplex(): Boolean =
    value.isEmpty ||
        value.asInstance<List<*>>().map { it.getOrNull(0) is AnyEncodedElement }.or(true)

/**
 * Try to cast to [ComplexEncodedElement]
 * @return [ComplexEncodedElement] if it is one, else returns null
 */
public fun AnyEncodedElement.takeIfComplex(): ComplexEncodedElement? = if (isComplex()) this as ComplexEncodedElement else null

/**
 * Try to cast to [ComplexEncodedElementBuilder]
 * @return [ComplexEncodedElementBuilder] if it is one, else returns null
 */
public fun AnyEncodedElementBuilder.takeIfComplex(): ComplexEncodedElementBuilder? =
    if (isComplex()) this as ComplexEncodedElementBuilder else null

/**
 * Add element to [ComplexEncodedElementBuilder]
 */
public fun ComplexEncodedElementBuilder.add(element: AnyEncodedElement) {
    if (value.isEmpty) {
        value(mutableListOf(element))
    } else {
        value.value.add(element)
    }
}
