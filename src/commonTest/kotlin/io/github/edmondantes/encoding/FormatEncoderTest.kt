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
package io.github.edmondantes.encoding

import io.github.edmondantes.entity.TestBinaryFormat
import io.github.edmondantes.entity.TestEntityWithFormatProperties
import io.github.edmondantes.entity.TestEntityWithNestedEntityWithFormatProperties
import io.github.edmondantes.entity.TestSimpleEntity
import io.github.edmondantes.entity.TestStringFormat
import io.github.edmondantes.serialization.encoding.element.ElementEncoder
import io.github.edmondantes.serialization.encoding.element.factory.collectionContextual
import io.github.edmondantes.serialization.encoding.element.factory.collectionFullContextual
import io.github.edmondantes.serialization.encoding.element.factory.element
import io.github.edmondantes.serialization.encoding.element.factory.elementByte
import io.github.edmondantes.serialization.encoding.element.factory.elementNull
import io.github.edmondantes.serialization.encoding.element.factory.structureElement
import io.github.edmondantes.serialization.encoding.format.supportBinaryFormats
import io.github.edmondantes.serialization.encoding.format.supportFormat
import io.github.edmondantes.serialization.encoding.format.supportStringFormats
import io.github.edmondantes.util.assertEquals
import io.github.edmondantes.util.serializeWithLog
import kotlin.test.Test

class FormatEncoderTest {

    @Test
    fun simplePropertyStringTest() {
        val encoder = ElementEncoder("test")

        TestEntityWithFormatProperties("id", null, "test")
            .serializeWithLog(encoder) {
                supportFormat(
                    "test2",
                    TestStringFormat(),
                )
            }

        assertEquals<TestEntityWithFormatProperties>(encoder) {
            structure {
                element("id", "id")
                element("test", "test")

                elementNull("nested")
            }
        }
    }

    @Test
    fun complexPropertyStringTest() {
        val encoder = ElementEncoder("test")

        val nested = TestSimpleEntity("id0", "name", 0, emptyList())

        TestEntityWithFormatProperties("id", nested)
            .serializeWithLog(encoder) { supportFormat("test", TestStringFormat()) }

        assertEquals<TestEntityWithFormatProperties>(encoder) {
            structure {
                element("id", "id")
                element("nested", nested.toString())

                elementNull("test")
            }
        }
    }

    @Test
    fun simpleComplexPropertyStringTest() {
        val encoder = ElementEncoder("test")

        val nested = TestSimpleEntity("id0", "name", 0, emptyList())
        TestEntityWithFormatProperties(
            "id",
            nested,
            "test",
        ).serializeWithLog(encoder) {
            supportStringFormats(
                "test" to TestStringFormat(),
                "test2" to TestStringFormat(),
            )
        }

        assertEquals<TestEntityWithFormatProperties>(encoder) {
            structure {
                element("id", "id")
                element("nested", nested.toString())
                element("test", "test")
            }
        }
    }

    @Test
    fun simplePropertyBinaryTest() {
        val encoder = ElementEncoder("test")

        TestEntityWithFormatProperties("id", null, "test")
            .serializeWithLog(encoder) {
                supportFormat(
                    "test2",
                    TestBinaryFormat(),
                )
            }

        assertEquals<TestEntityWithFormatProperties>(encoder) {
            structure {
                element("id", "id")
                elementNull("nested")
                collectionContextual<ByteArray>("test") {
                    elementByte(0)
                    elementByte(0)
                    elementByte(0)
                    elementByte(1)
                }
            }
        }
    }

    @Test
    fun complexPropertyBinaryTest() {
        val encoder = ElementEncoder("test")

        TestEntityWithFormatProperties("id", TestSimpleEntity("id0", "name", 0, emptyList()))
            .serializeWithLog(encoder) {
                supportFormat("test", TestBinaryFormat())
            }

        assertEquals<TestEntityWithFormatProperties>(encoder) {
            structure {
                element("id", "id")
                collectionFullContextual<TestSimpleEntity?, ByteArray>("nested") {
                    elementByte(0)
                    elementByte(0)
                    elementByte(0)
                    elementByte(1)
                }
                elementNull("test")
            }
        }
    }

    @Test
    fun simpleComplexPropertyBinaryTest() {
        val encoder = ElementEncoder("test")

        TestEntityWithFormatProperties("id", TestSimpleEntity("id0", "name", 0, emptyList()), "test")
            .serializeWithLog(encoder) {
                supportBinaryFormats("test" to TestBinaryFormat(), "test2" to TestBinaryFormat())
            }

        assertEquals<TestEntityWithFormatProperties>(encoder) {
            structure {
                element("id", "id")
                collectionFullContextual<TestSimpleEntity?, ByteArray>("nested") {
                    elementByte(0)
                    elementByte(0)
                    elementByte(0)
                    elementByte(1)
                }
                collectionFullContextual<String?, ByteArray>("test") {
                    elementByte(0)
                    elementByte(0)
                    elementByte(0)
                    elementByte(1)
                }
            }
        }
    }

    @Test
    fun nestedSimplePropertyStringTest() {
        val encoder = ElementEncoder("test")

        TestEntityWithNestedEntityWithFormatProperties(
            "id1",
            TestEntityWithFormatProperties("id", null, "test"),
        ).serializeWithLog(encoder) {
            supportFormat("test2", TestStringFormat())
        }

        assertEquals<TestEntityWithNestedEntityWithFormatProperties>(encoder) {
            structure {
                element("id", "id1")
                structureElement("nested") {
                    element("id", "id")
                    elementNull("nested")
                    element("test", "test")
                }
            }
        }
    }

    @Test
    fun nestedSimplePropertyBinaryTest() {
        val encoder = ElementEncoder()

        TestEntityWithNestedEntityWithFormatProperties(
            "id1",
            TestEntityWithFormatProperties("id", null, "test"),
        ).serializeWithLog(encoder) {
            supportFormat("test2", TestBinaryFormat())
        }

        assertEquals<TestEntityWithNestedEntityWithFormatProperties>(encoder) {
            structure {
                element("id", "id1")
                structureElement("nested") {
                    element("id", "id")
                    elementNull("nested")
                    collectionContextual<ByteArray>("test") {
                        elementByte(0)
                        elementByte(0)
                        elementByte(0)
                        elementByte(1)
                    }
                }
            }
        }
    }
}
