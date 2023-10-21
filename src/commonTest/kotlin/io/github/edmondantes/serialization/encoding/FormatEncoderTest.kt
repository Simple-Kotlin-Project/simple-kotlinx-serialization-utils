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
package io.github.edmondantes.serialization.encoding

import io.github.edmondantes.serialization.element.factory.structure.builder.byte
import io.github.edmondantes.serialization.element.factory.structure.builder.collectionContextual
import io.github.edmondantes.serialization.element.factory.structure.builder.collectionFullContextual
import io.github.edmondantes.serialization.element.factory.structure.builder.simple
import io.github.edmondantes.serialization.element.factory.structure.builder.simpleNullable
import io.github.edmondantes.serialization.element.factory.structure.builder.structure
import io.github.edmondantes.serialization.encoding.element.ElementEncoder
import io.github.edmondantes.serialization.encoding.format.supportBinaryFormats
import io.github.edmondantes.serialization.encoding.format.supportFormat
import io.github.edmondantes.serialization.encoding.format.supportStringFormats
import io.github.edmondantes.serialization.entity.TestBinaryFormat
import io.github.edmondantes.serialization.entity.TestEntityWithFormatProperties
import io.github.edmondantes.serialization.entity.TestEntityWithNestedEntityWithFormatProperties
import io.github.edmondantes.serialization.entity.TestSimpleEntity
import io.github.edmondantes.serialization.entity.TestStringFormat
import io.github.edmondantes.util.assertEqualsStructure
import io.github.edmondantes.util.serializableTest
import io.github.edmondantes.util.serializableTestTemplate
import io.github.edmondantes.util.serializeWithLog
import kotlinx.serialization.descriptors.serialDescriptor
import kotlin.test.Test

class FormatEncoderTest {
    @Test
    fun simplePropertyStringTest() =
        TEMPLATE.run {
            serialization {
                TestEntityWithFormatProperties("id", null, "test").serializeWithLog(it) {
                    supportFormat("test2", TestStringFormat())
                }
            }
            check {
                assertEqualsStructure<TestEntityWithFormatProperties> {
                    simple("id", "id")
                    simple("nested", null as TestSimpleEntity?)
                    simpleNullable("test", "test")
                }
            }
        }

    @Test
    fun complexPropertyStringTest() =
        TEMPLATE.run {
            val nested = TestSimpleEntity("id0", "name", 0, emptyList())

            serialization {
                TestEntityWithFormatProperties("id", nested)
                    .serializeWithLog(it) { supportFormat("test", TestStringFormat()) }
            }

            check {
                assertEqualsStructure<TestEntityWithFormatProperties> {
                    simple("id", "id")
                    simple<String>("nested", serialDescriptor<TestSimpleEntity?>()).value(nested.toString())
                    simpleNullable<String>("test", null)
                }
            }
        }

    @Test
    fun simpleComplexPropertyStringTest() =
        TEMPLATE.run {
            val nested = TestSimpleEntity("id0", "name", 0, emptyList())

            serialization {
                TestEntityWithFormatProperties(
                    "id",
                    nested,
                    "test",
                ).serializeWithLog(it) {
                    supportStringFormats(
                        "test" to TestStringFormat(),
                        "test2" to TestStringFormat(),
                    )
                }
            }

            check {
                assertEqualsStructure<TestEntityWithFormatProperties> {
                    simple("id", "id")
                    simple<String>("nested", serialDescriptor<TestSimpleEntity?>()).value(nested.toString())
                    simpleNullable("test", "test")
                }
            }
        }

    @Test
    fun simplePropertyBinaryTest() =
        TEMPLATE.run {
            serialization {
                TestEntityWithFormatProperties("id", null, "test")
                    .serializeWithLog(it) {
                        supportFormat("test2", TestBinaryFormat())
                    }
            }

            check {
                assertEqualsStructure<TestEntityWithFormatProperties> {
                    simple("id", "id")
                    simpleNullable<TestSimpleEntity>("nested", null)
                    collectionContextual<ByteArray>("test") {
                        byte(0)
                        byte(0)
                        byte(0)
                        byte(1)
                    }
                }
            }
        }

    @Test
    fun complexPropertyBinaryTest() =
        TEMPLATE.run {
            serialization {
                TestEntityWithFormatProperties("id", TestSimpleEntity("id0", "name", 0, emptyList()))
                    .serializeWithLog(it) { supportFormat("test", TestBinaryFormat()) }
            }

            check {
                assertEqualsStructure<TestEntityWithFormatProperties> {
                    simple("id", "id")
                    collectionFullContextual<TestSimpleEntity?, ByteArray>("nested") {
                        byte(0)
                        byte(0)
                        byte(0)
                        byte(1)
                    }
                    simpleNullable<String>("test", null)
                }
            }
        }

    @Test
    fun simpleComplexPropertyBinaryTest() =
        TEMPLATE.run {
            serialization {
                TestEntityWithFormatProperties("id", TestSimpleEntity("id0", "name", 0, emptyList()), "test")
                    .serializeWithLog(it) {
                        supportBinaryFormats("test" to TestBinaryFormat(), "test2" to TestBinaryFormat())
                    }
            }

            check {
                assertEqualsStructure<TestEntityWithFormatProperties> {
                    simple("id", "id")
                    collectionFullContextual<TestSimpleEntity?, ByteArray>("nested") {
                        byte(0)
                        byte(0)
                        byte(0)
                        byte(1)
                    }
                    collectionFullContextual<String?, ByteArray>("test") {
                        byte(0)
                        byte(0)
                        byte(0)
                        byte(1)
                    }
                }
            }
        }

    @Test
    fun nestedSimplePropertyStringTest() =
        TEMPLATE.run {
            serialization {
                TestEntityWithNestedEntityWithFormatProperties(
                    "id1",
                    TestEntityWithFormatProperties("id", null, "test"),
                ).serializeWithLog(it) { supportFormat("test2", TestStringFormat()) }
            }

            check {
                assertEqualsStructure<TestEntityWithNestedEntityWithFormatProperties> {
                    simple("id", "id1")
                    structure("nested") {
                        simple("id", "id")
                        simpleNullable<TestSimpleEntity>("nested", null)
                        simpleNullable("test", "test")
                    }
                }
            }
        }

    @Test
    fun nestedSimplePropertyBinaryTest() =
        serializableTest {
            serialization {
                TestEntityWithNestedEntityWithFormatProperties(
                    "id1",
                    TestEntityWithFormatProperties("id", null, "test"),
                ).serializeWithLog(it) {
                    supportFormat("test2", TestBinaryFormat())
                }
            }

            check {
                assertEqualsStructure<TestEntityWithNestedEntityWithFormatProperties> {
                    simple("id", "id1")
                    structure("nested") {
                        simple("id", "id")
                        simpleNullable<TestSimpleEntity>("nested", null)
                        collectionContextual<ByteArray>("test") {
                            byte(0)
                            byte(0)
                            byte(0)
                            byte(1)
                        }
                    }
                }
            }
        }

    companion object {
        private val TEMPLATE =
            serializableTestTemplate {
                configureEncoder { ElementEncoder(it, "test") }
            }
    }
}
