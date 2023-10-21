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
package io.github.edmondantes.serialization.decoding.element

import io.github.edmondantes.serialization.element.factory.simple.simpleElement
import io.github.edmondantes.serialization.element.factory.structure.builder.StructureEncodingElementBuilder
import io.github.edmondantes.serialization.element.factory.structure.builder.collection
import io.github.edmondantes.serialization.element.factory.structure.builder.simple
import io.github.edmondantes.serialization.element.factory.structure.builder.structure
import io.github.edmondantes.serialization.element.factory.structure.collectionElement
import io.github.edmondantes.serialization.element.factory.structure.structureElement
import io.github.edmondantes.serialization.entity.TestEntityWithEnum
import io.github.edmondantes.serialization.entity.TestEntityWithNested
import io.github.edmondantes.serialization.entity.TestEnum
import io.github.edmondantes.serialization.entity.TestSimpleEntity
import io.github.edmondantes.util.deserializableTestTemplate
import io.github.edmondantes.util.withLog
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ElementDecoderTest {
    @Test
    fun testSimpleEntity() =
        TEMPLATE.run {
            deserialization {
                structureElement<TestSimpleEntity> {
                    simpleEntity()
                }
            }

            check {
                assertEquals(SIMPLE_ENTITY, actual)
            }
        }

    @Test
    fun testCollection() =
        TEMPLATE.run<List<String>> {
            deserialization {
                collectionElement<List<String>> {
                    simple("1")
                    simple("2")
                }
            }

            check {
                assertContentEquals(listOf("1", "2"), actual)
            }
        }

    @Test
    fun testEntityWithNested() =
        TEMPLATE.run<TestEntityWithNested> {
            deserialization {
                structureElement<TestEntityWithNested> {
                    entityWithNested()
                }
            }
            check {
                assertEquals(ENTITY_WITH_NESTED, actual)
            }
        }

    @Test
    fun testEnum() =
        TEMPLATE.run<TestEntityWithEnum> {
            deserialization {
                structureElement<TestEntityWithEnum> {
                    simple("id", "id")
                    simple("enum", TestEnum.A)
                }
            }

            check {
                assertEquals(TestEntityWithEnum("id", TestEnum.A), actual)
            }
        }

    @Test
    fun testNull() =
        TEMPLATE.run<String?> {
            deserialization {
                simpleElement<String?>(null)
            }

            check { assertNull(actual) }
        }

    companion object {
        val TEMPLATE =
            deserializableTestTemplate<Any?> {
                configureDecoder { ElementDecoder(it, null).withLog() }
            }

        val SIMPLE_ENTITY = TestSimpleEntity("id", "name", 1, listOf("0", "56"))

        fun StructureEncodingElementBuilder.simpleEntity() {
            simple("id", "id")
            simple("name", "name")
            simple("index", 1)
            collection("collection") {
                simple("0")
                simple("56")
            }
        }

        val ENTITY_WITH_NESTED = TestEntityWithNested("id1", SIMPLE_ENTITY)

        fun StructureEncodingElementBuilder.entityWithNested() {
            simple("id", "id1")
            structure("nested") {
                simpleEntity()
            }
        }
    }
}
