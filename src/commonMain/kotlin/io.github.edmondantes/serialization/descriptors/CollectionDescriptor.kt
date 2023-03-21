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
package io.github.edmondantes.serialization.descriptors

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.StructureKind

@ExperimentalSerializationApi
public class CollectionDescriptor(override val serialName: String, private val elementDescriptor: SerialDescriptor) :
    SerialDescriptor {
    @ExperimentalSerializationApi
    override val elementsCount: Int = 1

    @ExperimentalSerializationApi
    override val kind: SerialKind = StructureKind.LIST

    @ExperimentalSerializationApi
    override fun getElementAnnotations(index: Int): List<Annotation> {
        require(index >= 0) { "Illegal index $index, $serialName expects only non-negative indices" }
        return emptyList()
    }

    @ExperimentalSerializationApi
    override fun getElementDescriptor(index: Int): SerialDescriptor {
        require(index >= 0) { "Illegal index $index, $serialName expects only non-negative indices" }
        return elementDescriptor
    }

    @ExperimentalSerializationApi
    override fun getElementIndex(name: String): Int =
        name.toIntOrNull() ?: throw IllegalArgumentException("$name is not a valid list index")

    @ExperimentalSerializationApi
    override fun getElementName(index: Int): String {
        require(index >= 0) { "Illegal index $index, $serialName expects only non-negative indices" }
        return index.toString()
    }

    @ExperimentalSerializationApi
    override fun isElementOptional(index: Int): Boolean {
        require(index >= 0) { "Illegal index $index, $serialName expects only non-negative indices" }
        return false
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CollectionDescriptor) return false
        if (elementDescriptor == other.elementDescriptor && serialName == other.serialName) return true
        return false
    }

    override fun hashCode(): Int {
        return elementDescriptor.hashCode() * 31 + serialName.hashCode()
    }

    override fun toString(): String = "$serialName($elementDescriptor)"
}
