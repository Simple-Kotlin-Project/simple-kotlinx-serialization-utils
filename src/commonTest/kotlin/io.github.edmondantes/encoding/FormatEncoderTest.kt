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
import io.github.edmondantes.serialization.encoding.BroadcastEncoder
import io.github.edmondantes.serialization.encoding.LoggerEncoder
import io.github.edmondantes.serialization.encoding.format.supportBinaryFormats
import io.github.edmondantes.serialization.encoding.format.supportFormat
import io.github.edmondantes.serialization.encoding.format.supportStringFormats
import io.github.edmondantes.util.TestEncoder
import io.github.edmondantes.util.beginCollection
import io.github.edmondantes.util.beginStructure
import io.github.edmondantes.util.encodeByteElement
import io.github.edmondantes.util.encodeNull
import io.github.edmondantes.util.encodeNullableSerializableElement
import io.github.edmondantes.util.encodeSerializableElement
import io.github.edmondantes.util.encodeString
import io.github.edmondantes.util.encodeStringElement
import io.github.edmondantes.util.expected
import kotlinx.serialization.serializer
import kotlin.test.Test
import kotlin.test.assertTrue

class FormatEncoderTest {

    @Test
    fun simplePropertyStringTest() {
        val encoders = listOf(TestEncoder("test"))
        val encoder = BroadcastEncoder(encoders + LoggerEncoder())
            .supportFormat("test2", TestStringFormat())

        val entity = TestEntityWithFormatProperties("id", null, "test")


        serializer<TestEntityWithFormatProperties>().serialize(encoder, entity)

        val expected = expected<TestEntityWithFormatProperties> {
            beginStructure {
                encodeStringElement("id", "id")
                encodeNullableSerializableElement("nested") {
                    encodeNull<TestSimpleEntity>()
                }
                encodeSerializableElement<TestEntityWithFormatProperties, String>("test") {
                    encodeString("test")
                }
            }
        }

        assertTrue(expected.equals(encoders[0]))
    }

    @Test
    fun complexPropertyStringTest() {
        val encoders = listOf(TestEncoder("test"))
        val encoder = BroadcastEncoder(encoders + LoggerEncoder())
            .supportFormat("test", TestStringFormat())

        val nested = TestSimpleEntity("id0", "name", 0, emptyList())
        val entity = TestEntityWithFormatProperties("id", nested)

        serializer<TestEntityWithFormatProperties>().serialize(encoder, entity)

        val expected = expected<TestEntityWithFormatProperties> {
            beginStructure {
                encodeStringElement("id", "id")
                encodeSerializableElement<TestEntityWithFormatProperties, String>("nested") {
                    encodeString(nested.toString())
                }
                encodeNullableSerializableElement<TestEntityWithFormatProperties, String>("test") {
                    encodeNull()
                }
            }
        }

        assertTrue(expected.equals(encoders[0]))
    }

    @Test
    fun simpleComplexPropertyStringTest() {
        val encoders = listOf(TestEncoder("test"))
        val encoder = BroadcastEncoder(encoders + LoggerEncoder())
            .supportStringFormats("test" to TestStringFormat(), "test2" to TestStringFormat())

        val nested = TestSimpleEntity("id0", "name", 0, emptyList())
        val entity = TestEntityWithFormatProperties("id", nested, "test")


        serializer<TestEntityWithFormatProperties>().serialize(encoder, entity)

        val expected = expected<TestEntityWithFormatProperties> {
            beginStructure {
                encodeStringElement("id", "id")
                encodeSerializableElement<TestEntityWithFormatProperties, String>("nested") {
                    encodeString(nested.toString())
                }
                encodeSerializableElement<TestEntityWithFormatProperties, String>("test") {
                    encodeString("test")
                }
            }
        }

        assertTrue(expected.equals(encoders[0]))
    }

    @Test
    fun simplePropertyBinaryTest() {
        val encoders = listOf(TestEncoder("test"))
        val encoder = BroadcastEncoder(encoders + LoggerEncoder())
            .supportFormat("test2", TestBinaryFormat())

        val entity = TestEntityWithFormatProperties("id", null, "test")


        serializer<TestEntityWithFormatProperties>().serialize(encoder, entity)

        val expected = expected<TestEntityWithFormatProperties> {
            beginStructure {
                encodeStringElement("id", "id")
                encodeNullableSerializableElement("nested") {
                    encodeNull<TestSimpleEntity>()
                }
                encodeSerializableElement<TestEntityWithFormatProperties, ByteArray>("test") {
                    beginCollection {
                        encodeByteElement("0", 0)
                        encodeByteElement("1", 0)
                        encodeByteElement("2", 0)
                        encodeByteElement("3", 1)
                    }
                }
            }
        }

        assertTrue(expected.equals(encoders[0]))
    }

    @Test
    fun complexPropertyBinaryTest() {
        val encoders = listOf(TestEncoder("test"))
        val encoder = BroadcastEncoder(encoders + LoggerEncoder())
            .supportFormat("test", TestBinaryFormat())

        val nested = TestSimpleEntity("id0", "name", 0, emptyList())
        val entity = TestEntityWithFormatProperties("id", nested)

        serializer<TestEntityWithFormatProperties>().serialize(encoder, entity)

        val expected = expected<TestEntityWithFormatProperties> {
            beginStructure {
                encodeStringElement("id", "id")
                encodeSerializableElement<TestEntityWithFormatProperties, ByteArray>("nested") {
                    beginCollection {
                        encodeByteElement("0", 0)
                        encodeByteElement("1", 0)
                        encodeByteElement("2", 0)
                        encodeByteElement("3", 1)
                    }
                }
                encodeNullableSerializableElement<TestEntityWithFormatProperties, String>("test") {
                    encodeNull()
                }
            }
        }

        assertTrue(expected.equals(encoders[0]))
    }

    @Test
    fun simpleComplexPropertyBinaryTest() {
        val encoders = listOf(TestEncoder("test"))
        val encoder = BroadcastEncoder(encoders + LoggerEncoder())
            .supportBinaryFormats("test" to TestBinaryFormat(), "test2" to TestBinaryFormat())

        val nested = TestSimpleEntity("id0", "name", 0, emptyList())
        val entity = TestEntityWithFormatProperties("id", nested, "test")


        serializer<TestEntityWithFormatProperties>().serialize(encoder, entity)

        val expected = expected<TestEntityWithFormatProperties> {
            beginStructure {
                encodeStringElement("id", "id")
                encodeSerializableElement<TestEntityWithFormatProperties, ByteArray>("nested") {
                    beginCollection {
                        encodeByteElement("0", 0)
                        encodeByteElement("1", 0)
                        encodeByteElement("2", 0)
                        encodeByteElement("3", 1)
                    }
                }
                encodeSerializableElement<TestEntityWithFormatProperties, ByteArray>("test") {
                    beginCollection {
                        encodeByteElement("0", 0)
                        encodeByteElement("1", 0)
                        encodeByteElement("2", 0)
                        encodeByteElement("3", 1)
                    }
                }
            }
        }

        assertTrue(expected.equals(encoders[0]))
    }

    @Test
    fun nestedSimplePropertyStringTest() {
        val encoders = listOf(TestEncoder("test"))
        val encoder = BroadcastEncoder(encoders + LoggerEncoder())
            .supportFormat("test2", TestStringFormat())

        val entity = TestEntityWithNestedEntityWithFormatProperties(
            "id1",
            TestEntityWithFormatProperties("id", null, "test")
        )

        serializer<TestEntityWithNestedEntityWithFormatProperties>().serialize(encoder, entity)

        val expected = expected<TestEntityWithNestedEntityWithFormatProperties> {
            beginStructure {
                encodeStringElement("id", "id1")
                encodeSerializableElement("nested") {
                    beginStructure<TestEntityWithFormatProperties> {
                        encodeStringElement("id", "id")
                        encodeNullableSerializableElement("nested") {
                            encodeNull<TestSimpleEntity>()
                        }
                        encodeSerializableElement<TestEntityWithFormatProperties, String>("test") {
                            encodeString("test")
                        }
                    }
                }
            }
        }

        assertTrue(expected.equals(encoders[0]))
    }

    @Test
    fun nestedSimplePropertyBinaryTest() {
        val encoders = listOf(TestEncoder("test"))
        val encoder = BroadcastEncoder(encoders + LoggerEncoder())
            .supportFormat("test2", TestBinaryFormat())

        val entity = TestEntityWithNestedEntityWithFormatProperties(
            "id1",
            TestEntityWithFormatProperties("id", null, "test")
        )

        serializer<TestEntityWithNestedEntityWithFormatProperties>().serialize(encoder, entity)

        val expected = expected<TestEntityWithNestedEntityWithFormatProperties> {
            beginStructure {
                encodeStringElement("id", "id1")
                encodeSerializableElement("nested") {
                    beginStructure<TestEntityWithFormatProperties> {
                        encodeStringElement("id", "id")
                        encodeNullableSerializableElement("nested") {
                            encodeNull<TestSimpleEntity>()
                        }
                        encodeSerializableElement<TestEntityWithFormatProperties, ByteArray>("test") {
                            beginCollection {
                                encodeByteElement("0", 0)
                                encodeByteElement("1", 0)
                                encodeByteElement("2", 0)
                                encodeByteElement("3", 1)
                            }
                        }
                    }
                }
            }
        }

        assertTrue(expected.equals(encoders[0]))
    }

}