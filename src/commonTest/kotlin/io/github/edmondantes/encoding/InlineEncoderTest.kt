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
@file:Suppress("RemoveExplicitTypeArguments")

package io.github.edmondantes.encoding

import io.github.edmondantes.entity.TestEntityWithInlineClass
import io.github.edmondantes.entity.TestEntityWithInlineProperty
import io.github.edmondantes.entity.TestInlineEntity
import io.github.edmondantes.entity.TestSimpleEntity
import io.github.edmondantes.serialization.encoding.BroadcastEncoder
import io.github.edmondantes.util.TestEncoder
import io.github.edmondantes.util.anotherDescriptor
import io.github.edmondantes.util.beginCollection
import io.github.edmondantes.util.beginStructure
import io.github.edmondantes.util.encodeIntElement
import io.github.edmondantes.util.encodeSerializableElement
import io.github.edmondantes.util.encodeString
import io.github.edmondantes.util.encodeStringElement
import io.github.edmondantes.util.expected
import io.github.edmondantes.util.loggerEncoder
import kotlinx.serialization.serializer
import kotlin.test.Test
import kotlin.test.assertTrue

class InlineEncoderTest {

    @Test
    fun testInlineProperty() {
        val encoders = listOf(TestEncoder("id0"))
        val encoder = BroadcastEncoder(encoders + loggerEncoder()).supportInline()

        val value = TestEntityWithInlineProperty(
            "id0",
            TestSimpleEntity(
                "id1",
                "name",
                10,
                listOf("one", "two"),
            ),
        )

        serializer<TestEntityWithInlineProperty>().serialize(encoder, value)

        val expected = expected<TestEntityWithInlineProperty> {
            beginStructure {
                encodeStringElement("notInline", "id0")
                encodeSerializableElement<TestEntityWithInlineProperty, TestSimpleEntity>("inlineProperty") {}
                anotherDescriptor<TestEntityWithInlineProperty, TestSimpleEntity> {
                    encodeStringElement("id", "id1")
                    encodeStringElement("name", "name")
                    encodeIntElement("index", 10)

                    encodeSerializableElement("collection") {
                        beginCollection<ArrayList<String>> {
                            encodeSerializableElement("0") {
                                encodeString<ArrayList<String>>("one")
                            }
                            encodeSerializableElement("1") {
                                encodeString<ArrayList<String>>("two")
                            }
                        }
                    }
                }
            }
        }

        assertTrue(expected.equals(encoders[0]))
    }

    @Test
    fun testInlineClass() {
        val encoders = listOf(TestEncoder("id0"))
        val encoder = BroadcastEncoder(encoders + loggerEncoder()).supportInline()

        val value = TestEntityWithInlineClass(
            "id0",
            TestInlineEntity(
                "id1",
                "name",
            ),
        )

        serializer<TestEntityWithInlineClass>().serialize(encoder, value)

        val expected = expected<TestEntityWithInlineClass> {
            beginStructure {
                encodeStringElement("notInline", "id0")
                encodeSerializableElement<TestEntityWithInlineClass, TestInlineEntity>("inlineClass") {}
                anotherDescriptor<TestEntityWithInlineClass, TestInlineEntity> {
                    encodeStringElement("id", "id1")
                    encodeStringElement("name", "name")
                }
            }
        }

        assertTrue(expected.equals(encoders[0]))
    }
}
