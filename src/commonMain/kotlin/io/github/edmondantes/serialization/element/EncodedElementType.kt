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
package io.github.edmondantes.serialization.element

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.StructureKind

/**
 * Represents type of encoded elements
 * @see EncodedElement
 */
public enum class EncodedElementType {
    /**
     * Represents structure encoded element
     * @see ComplexEncodedElement
     */
    STRUCTURE,

    /**
     * Represents collections
     * @see ComplexEncodedElement
     */
    COLLECTION,

    /**
     * Represents part of structure
     */
    PROPERTY,

    /**
     * Represents any encoded elements
     */
    ELEMENT,

    /**
     * Represents null value in encoded element
     */
    NULL,

    ;

    /**
     * Check if [EncodedElementType] is [STRUCTURE] or [COLLECTION]
     * @see ComplexEncodedElement
     */
    public fun isComplex(): Boolean = this == STRUCTURE || this == COLLECTION

    /**
     * Check if [EncodedElementType] is [PROPERTY] or [ELEMENT]
     */
    public fun isSimple(): Boolean = this == PROPERTY || this == ELEMENT

    /**
     * Check if [EncodedElementType] is [NULL]
     */
    public fun isNull(): Boolean = this == NULL

    /**
     * Check if [EncodedElementType] is [STRUCTURE] or [COLLECTION] or [NULL]
     * @see ComplexEncodedElement
     */
    public fun isComplexOrNull(): Boolean = isComplex() || isNull()

    /**
     * Check if [EncodedElementType] is [PROPERTY] or [ELEMENT] or [NULL]
     */
    public fun isSimpleOrNull(): Boolean = isSimple() || isNull()

    public companion object {
        /**
         * Get elements type for simple element
         * @param parentType [EncodedElementType] of element's parent
         * @return [PROPERTY] or [ELEMENT]
         */
        public fun getSimpleElementEncodedType(parentType: EncodedElementType?): EncodedElementType =
            when (parentType) {
                STRUCTURE -> PROPERTY
                else -> ELEMENT
            }
    }
}

/**
 * Transform [SerialKind] to [EncodedElementType]
 * @param parentType [EncodedElementType] of element's parent
 */
@OptIn(ExperimentalSerializationApi::class)
public fun SerialKind.toEncodedElementType(parentType: EncodedElementType?): EncodedElementType =
    when (this) {
        StructureKind.LIST -> EncodedElementType.COLLECTION
        is StructureKind.CLASS -> EncodedElementType.STRUCTURE
        else -> EncodedElementType.getSimpleElementEncodedType(parentType)
    }
