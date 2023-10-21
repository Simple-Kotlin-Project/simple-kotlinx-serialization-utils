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
import io.github.edmondantes.serialization.element.EncodedElementType
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlin.test.Test
import kotlin.test.assertEquals

class TestDefaultSimpleEncodedElementFactory {
    @Test
    fun simpleTest() {
        val element = simpleElement("string")
        assertEquals(
            DefaultEncodedElement(
                EncodedElementType.ELEMENT,
                "kotlin.String",
                null,
                "string",
            ),
            element,
        )
    }

    @Test
    @OptIn(ExperimentalSerializationApi::class)
    fun testWithParent() {
        val parentDescriptor =
            buildClassSerialDescriptor("parent") {
                element<String>("testString")
            }
        val element =
            simpleElement(
                "string",
                parentType = EncodedElementType.STRUCTURE,
                parentDescriptor = parentDescriptor,
                indexInParent = parentDescriptor.getElementIndex("testString"),
            )

        assertEquals(
            DefaultEncodedElement(
                EncodedElementType.PROPERTY,
                "kotlin.String",
                "testString",
                "string",
            ),
            element,
        )
    }
}
