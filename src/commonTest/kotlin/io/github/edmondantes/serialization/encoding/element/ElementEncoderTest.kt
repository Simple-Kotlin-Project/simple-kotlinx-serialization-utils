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
package io.github.edmondantes.serialization.encoding.element

import io.github.edmondantes.serialization.element.factory.structure.builder.collection
import io.github.edmondantes.serialization.element.factory.structure.builder.simple
import io.github.edmondantes.serialization.element.factory.structure.builder.structure
import io.github.edmondantes.serialization.entity.TestEntityJvmInline
import io.github.edmondantes.serialization.entity.TestEntityWithEnum
import io.github.edmondantes.serialization.entity.TestEntityWithJvmInlineEntity
import io.github.edmondantes.serialization.entity.TestEntityWithNested
import io.github.edmondantes.serialization.entity.TestEnum
import io.github.edmondantes.serialization.entity.TestSimpleEntity
import io.github.edmondantes.util.assertEqualsComplex
import io.github.edmondantes.util.assertEqualsSimple
import io.github.edmondantes.util.multiplySerializableTest
import io.github.edmondantes.util.serializableTest
import io.github.edmondantes.util.serializeWithLog
import kotlinx.serialization.descriptors.serialDescriptor
import kotlin.test.Test

class ElementEncoderTest {
    @Test
    fun testSimpleEntity() =
        serializableTest {
            serialization {
                TestSimpleEntity("id", "name", 1, listOf("0", "56"))
                    .serializeWithLog(it)
            }

            check {
                assertEqualsComplex<TestSimpleEntity> {
                    structure {
                        simple("id", "id")
                        simple("name", "name")
                        simple("index", 1)
                        collection("collection") {
                            simple("0")
                            simple("56")
                        }
                    }
                }
            }
        }

    @Test
    fun testCollection() =
        serializableTest {
            serialization { arrayListOf("1", "2").serializeWithLog(it) }

            check {
                assertEqualsComplex<ArrayList<String>> {
                    collection {
                        simple("1")
                        simple("2")
                    }
                }
            }
        }

    @Test
    fun testEntityWithNested() =
        serializableTest {
            serialization {
                TestEntityWithNested(
                    "id1",
                    TestSimpleEntity(
                        "id",
                        "name",
                        1,
                        listOf("0", "56"),
                    ),
                ).serializeWithLog(it)
            }

            check {
                assertEqualsComplex<TestEntityWithNested> {
                    structure {
                        simple("id", "id1")
                        structure("nested") {
                            simple("id", "id")
                            simple("name", "name")
                            simple("index", 1)
                            collection("collection") {
                                simple("0")
                                simple("56")
                            }
                        }
                    }
                }
            }
        }

    @Test
    fun testEnum() =
        multiplySerializableTest(TestEnum.entries.asSequence()) {
            serialization { enumElement, builder ->
                TestEntityWithEnum("id", enumElement).serializeWithLog(builder)
            }

            check {
                assertEqualsComplex<TestEntityWithEnum> {
                    structure {
                        simple("id", "id")
                        simple("enum", key)
                    }
                }
            }
        }

    @Test
    fun testNull() =
        serializableTest {
            configureEncoder { ElementEncoder(it, null, serialDescriptor<String?>()) }
            serialization {
                val value: String? = null
                value.serializeWithLog(it)
            }

            check {
                assertEqualsSimple<String?> {
                    value(null)
                }
            }
        }

    @Test
    fun testJvmInline() =
        serializableTest {
            serialization {
                TestEntityWithJvmInlineEntity(TestEntityJvmInline("test"))
                    .serializeWithLog(it)
            }

            check {
                assertEqualsComplex<TestEntityWithJvmInlineEntity> {
                    structure {
                        simple<String>("inline", serialDescriptor<TestEntityJvmInline>()).value("test")
                    }
                }
            }
        }
}
