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

import io.github.edmondantes.serialization.element.AnyEncodedElement
import io.github.edmondantes.serialization.element.DefaultEncodedElement
import io.github.edmondantes.serialization.element.EncodedElementType
import io.github.edmondantes.serialization.entity.TestFilterEntity
import io.github.edmondantes.serialization.entity.TestSimpleEntity
import kotlinx.serialization.descriptors.serialDescriptor
import kotlin.test.Test
import kotlin.test.assertEquals

class TestDefaultComplexEncodedElementFactory {
    @Test
    fun testSimpleStructure() {
        val element =
            structureElement<TestSimpleEntity> {
                simple<String>("id", serialDescriptor<String>()).value("Test")
            }

        val expected =
            DefaultEncodedElement<List<AnyEncodedElement>>(
                EncodedElementType.STRUCTURE,
                "io.github.edmondantes.serialization.entity.TestSimpleEntity",
                null,
                listOf(
                    DefaultEncodedElement(EncodedElementType.PROPERTY, "kotlin.String", "id", "Test"),
                ),
            )

        assertEquals(expected, element)
    }

    @Test
    fun testSimpleCollection() {
        val element =
            collectionElement<ArrayList<String>> {
                simple<String>().value("one")
                simple<String>().value("two")
            }

        val expected =
            DefaultEncodedElement<List<AnyEncodedElement>>(
                EncodedElementType.COLLECTION,
                "kotlin.collections.ArrayList",
                null,
                listOf(
                    DefaultEncodedElement(EncodedElementType.ELEMENT, "kotlin.String", "0", "one"),
                    DefaultEncodedElement(EncodedElementType.ELEMENT, "kotlin.String", "1", "two"),
                ),
            )

        assertEquals(expected, element)
    }

    @Test
    fun testContextualStructure() {
        val element =
            structureContextualElement<TestSimpleEntity, TestFilterEntity> {
                simple<String>("id").value("id12")
                simple<String>("password").value("name|index")
            }

        val expected =
            DefaultEncodedElement<List<AnyEncodedElement>>(
                EncodedElementType.STRUCTURE,
                "io.github.edmondantes.serialization.entity.TestSimpleEntity",
                null,
                listOf(
                    DefaultEncodedElement(EncodedElementType.PROPERTY, "kotlin.String", "id", "id12"),
                    DefaultEncodedElement(EncodedElementType.PROPERTY, "kotlin.String", "password", "name|index"),
                ),
            )

        assertEquals(expected, element)
    }
}
