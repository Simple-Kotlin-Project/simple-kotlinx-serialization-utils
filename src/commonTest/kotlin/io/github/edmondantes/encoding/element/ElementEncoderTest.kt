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
package io.github.edmondantes.encoding.element

import io.github.edmondantes.entity.TestEntityWithEnum
import io.github.edmondantes.entity.TestEntityWithNested
import io.github.edmondantes.entity.TestEnum
import io.github.edmondantes.entity.TestSimpleEntity
import io.github.edmondantes.serialization.encoding.element.ElementEncoder
import io.github.edmondantes.serialization.encoding.element.factory.collectionElement
import io.github.edmondantes.serialization.encoding.element.factory.element
import io.github.edmondantes.serialization.encoding.element.factory.structureElement
import io.github.edmondantes.util.assertEquals
import io.github.edmondantes.util.serializeWithLog
import kotlin.test.Test

class ElementEncoderTest {

    @Test
    fun testSimpleEntity() {
        val encoder = ElementEncoder()

        TestSimpleEntity("id", "name", 1, listOf("0", "56"))
            .serializeWithLog(encoder)

        assertEquals<TestSimpleEntity>(encoder) {
            structure {
                element("id", "id")
                element("name", "name")
                element("index", 1)
                collectionElement("collection") {
                    element("0")
                    element("56")
                }
            }
        }
    }

    @Test
    fun testCollection() {
        val encoder = ElementEncoder()

        ArrayList<String>().apply {
            add("1")
            add("2")
        }
            .serializeWithLog(encoder)

        assertEquals<ArrayList<String>>(encoder) {
            collection {
                element("1")
                element("2")
            }
        }
    }

    @Test
    fun testEntityWithNested() {
        val encoder = ElementEncoder("id1")

        TestEntityWithNested(
            "id1",
            TestSimpleEntity(
                "id",
                "name",
                1,
                listOf("0", "56"),
            ),
        ).serializeWithLog(encoder)

        assertEquals<TestEntityWithNested>(encoder) {
            structure {
                element("id", "id1")
                structureElement("nested") {
                    element("id", "id")
                    element("name", "name")
                    element("index", 1)
                    collectionElement("collection") {
                        element("0")
                        element("56")
                    }
                }
            }
        }
    }

    @Test
    fun testEnum() {
        val encoder = ElementEncoder()

        TestEntityWithEnum("id", TestEnum.A)
            .serializeWithLog(encoder)

        assertEquals<TestEntityWithEnum>(encoder) {
            structure {
                element("id", "id")
                element("enum", TestEnum.A)
            }
        }
    }

    @Test
    fun testNull() {
        val encoder = ElementEncoder()

        val value: String? = null

        value.serializeWithLog(encoder)

        assertEquals<String>(encoder) {
            value(null)
        }
    }
}
