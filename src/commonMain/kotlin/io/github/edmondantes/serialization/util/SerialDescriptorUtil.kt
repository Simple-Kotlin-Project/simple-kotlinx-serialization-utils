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

package io.github.edmondantes.serialization.util

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer

public inline fun <reified T> SerializersModule.serialDescriptor(): SerialDescriptor = serializer<T>().descriptor

/**
 * Retrieves all annotations associated with the specified element at the given index.
 *
 * @param index The index of the element to retrieve annotations from.
 * @return A list of annotations associated with the element.
 * @see SerialDescriptor
 * @see SerialDescriptor.annotations
 * @see SerialDescriptor.getElementAnnotations
 */
@OptIn(ExperimentalSerializationApi::class)
public fun SerialDescriptor.getElementAllAnnotation(index: Int): List<Annotation> =
    getElementAnnotations(index) + getElementDescriptor(index).annotations

/**
 * Tries to get the element info at the specified index in the [SerialDescriptor].
 * If the [SerialDescriptor] or the [index] is null, null will be returned.
 *
 * @param index The index of the element in the [SerialDescriptor].
 * @param block The function to retrieve the element info.
 * @return The element info at the specified index, or null if the [SerialDescriptor] or [index] is null.
 */
public inline fun <T> SerialDescriptor?.tryGetElementInfo(
    index: Int?,
    block: (SerialDescriptor, Int) -> T,
): T? = this?.let { descriptor -> index?.let { block(descriptor, index) } }

/**
 * Tries to get the [SerialDescriptor] of the element at the specified index in the [SerialDescriptor].
 * If the [SerialDescriptor] or the [index] is null, null will be returned.
 *
 * @param index The index of the element in the [SerialDescriptor].
 * @return The [SerialDescriptor] of the element at the specified index, or null if the [SerialDescriptor] or [index] is null.
 */
public fun SerialDescriptor?.tryGetElementDescriptor(index: Int?): SerialDescriptor? =
    tryGetElementInfo(index) { descriptor, elementIndex -> descriptor.getElementDescriptor(elementIndex) }

/**
 * Tries to get the name of the element at the specified index in the [SerialDescriptor].
 * If the [SerialDescriptor] or the [index] is null, null will be returned.
 *
 * @param index The index of the element in the [SerialDescriptor].
 * @return The name of the element at the specified index, or null if the [SerialDescriptor] or [index] is null.
 */
public fun SerialDescriptor?.tryGetElementName(index: Int?): String? =
    tryGetElementInfo(index) { descriptor, elementIndex -> descriptor.getElementName(elementIndex) }
