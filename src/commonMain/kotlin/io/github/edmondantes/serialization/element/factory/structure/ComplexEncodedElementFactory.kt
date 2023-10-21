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
package io.github.edmondantes.serialization.element.factory.structure

import io.github.edmondantes.serialization.element.ComplexEncodedElement
import io.github.edmondantes.serialization.element.EncodedElement
import io.github.edmondantes.serialization.element.EncodedElementType
import io.github.edmondantes.serialization.element.factory.structure.builder.CollectionEncodingElementBuilder
import io.github.edmondantes.serialization.element.factory.structure.builder.StructureEncodingElementBuilder

/**
 * Factory interface for create complex [EncodedElement]
 * @see ComplexEncodedElement
 */
public interface ComplexEncodedElementFactory {
    /**
     * Create [ComplexEncodedElement] with type [EncodedElementType.STRUCTURE]
     * @param block function for setup [StructureEncodingElementBuilder]
     */
    public fun structure(block: StructureEncodingElementBuilder.() -> Unit): ComplexEncodedElement

    /**
     * Create [ComplexEncodedElement] with type [EncodedElementType.COLLECTION]
     * @param block function for setup [CollectionEncodingElementBuilder]
     */
    public fun collection(block: CollectionEncodingElementBuilder.() -> Unit): ComplexEncodedElement
}
