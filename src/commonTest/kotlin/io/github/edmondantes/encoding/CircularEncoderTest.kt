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

import io.github.edmondantes.entity.TestCircleEntity
import io.github.edmondantes.entity.TestCircleEntityWithEquals
import io.github.edmondantes.entity.TestDataCircleEntity
import io.github.edmondantes.serialization.encoding.BroadcastEncoder
import io.github.edmondantes.util.TestEncoder
import io.github.edmondantes.util.beginStructure
import io.github.edmondantes.util.encodeNullableSerializableElement
import io.github.edmondantes.util.encodeStringElement
import io.github.edmondantes.util.expected
import io.github.edmondantes.util.loggerEncoder
import kotlinx.serialization.serializer
import kotlin.test.Test
import kotlin.test.assertTrue

class CircularEncoderTest {

    @Test
    fun testCircularWithoutHashCodeAndWithRefEquality() {
        val testEncoder = TestEncoder("id1")
        val encoder = BroadcastEncoder(testEncoder, loggerEncoder())
            .supportCircular(byHashCode = false, useRefEquality = true)

        val value = TestCircleEntity("id0", null).also { value ->
            value.nested = TestCircleEntity("id1", null).apply {
                nested = TestCircleEntity("id2", value)
            }
        }

        val expected = expected<TestCircleEntity> {
            beginStructure {
                encodeStringElement("id", "id0")
                encodeNullableSerializableElement("nested") {
                    beginStructure<TestCircleEntity> {
                        encodeStringElement("id", "id1")
                        encodeNullableSerializableElement("nested") {
                            beginStructure<TestCircleEntity> {
                                encodeStringElement("id", "id2")
                                encodeNullableSerializableElement("nested") {
                                    beginStructure<TestCircleEntity> {
                                        encodeStringElement("id", "id0")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        serializer<TestCircleEntity>().serialize(encoder, value)

        assertTrue(expected.equals(testEncoder))
    }

    @Test
    fun testCircularWithoutHashCodeAndWithoutRefEquality() {
        val testEncoder = TestEncoder("id1")
        val encoder = BroadcastEncoder(testEncoder, loggerEncoder())
            .supportCircular(byHashCode = false, useRefEquality = false)

        val value = TestCircleEntityWithEquals("id0", null).also { value ->
            value.nested = TestCircleEntityWithEquals("id1", null).apply {
                nested = TestCircleEntityWithEquals("id1", value)
            }
        }

        val expected = expected<TestCircleEntityWithEquals> {
            beginStructure {
                encodeStringElement("id", "id0")
                encodeNullableSerializableElement("nested") {
                    beginStructure<TestCircleEntityWithEquals> {
                        encodeStringElement("id", "id1")
                    }
                }
            }
        }

        serializer<TestCircleEntityWithEquals>().serialize(encoder, value)

        assertTrue(expected.equals(testEncoder))
    }

    @Test
    fun testCircularWithHashCodeAndWithRefEquality() {
        val testEncoder = TestEncoder("id1")
        val encoder = BroadcastEncoder(testEncoder, loggerEncoder())
            .supportCircular(byHashCode = true, useRefEquality = true)

        val value = TestDataCircleEntity("id0", null).also { value ->
            value.nested = TestDataCircleEntity("id1", null).apply {
                nested = TestDataCircleEntity("id2", value)
            }
        }

        val expected = expected<TestDataCircleEntity> {
            beginStructure {
                encodeStringElement("id", "id0")
                encodeNullableSerializableElement("nested") {
                    beginStructure<TestDataCircleEntity> {
                        encodeStringElement("id", "id1")
                        encodeNullableSerializableElement("nested") {
                            beginStructure<TestDataCircleEntity> {
                                encodeStringElement("id", "id2")
                                encodeNullableSerializableElement("nested") {
                                    beginStructure<TestDataCircleEntity> {
                                        encodeStringElement("id", "id0")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        serializer<TestDataCircleEntity>().serialize(encoder, value)

        assertTrue(expected.equals(testEncoder))
    }

    @Test
    fun testCircularWithHashCodeAndWithoutRefEquality() {
        val testEncoder = TestEncoder("id1")
        val encoder = BroadcastEncoder(testEncoder, loggerEncoder())
            .supportCircular(byHashCode = true, useRefEquality = false)

        val value = TestDataCircleEntity("id0", null).also { value ->
            value.nested = TestDataCircleEntity("id1", null).apply {
                nested = TestDataCircleEntity("id1", value)
            }
        }

        val expected = expected<TestDataCircleEntity> {
            beginStructure {
                encodeStringElement("id", "id0")
                encodeNullableSerializableElement("nested") {
                    beginStructure<TestDataCircleEntity> {
                        encodeStringElement("id", "id1")
                    }
                }
            }
        }

        serializer<TestDataCircleEntity>().serialize(encoder, value)

        assertTrue(expected.equals(testEncoder))
    }

    @Test
    fun testCircularWithAddedFirstObj() {
        val value = TestCircleEntity("id0", null).also { value ->
            value.nested = TestCircleEntity("id1", null).apply {
                nested = TestCircleEntity("id2", value)
            }
        }

        val testEncoder = TestEncoder("id1")
        val encoder = BroadcastEncoder(testEncoder, loggerEncoder())
            .supportCircular(value, byHashCode = false, useRefEquality = true)

        val expected = expected<TestCircleEntity> {
            beginStructure {
                encodeStringElement("id", "id0")
                encodeNullableSerializableElement("nested") {
                    beginStructure<TestCircleEntity> {
                        encodeStringElement("id", "id1")
                        encodeNullableSerializableElement("nested") {
                            beginStructure<TestCircleEntity> {
                                encodeStringElement("id", "id2")
                            }
                        }
                    }
                }
            }
        }

        serializer<TestCircleEntity>().serialize(encoder, value)

        assertTrue(expected.equals(testEncoder))
    }
}
