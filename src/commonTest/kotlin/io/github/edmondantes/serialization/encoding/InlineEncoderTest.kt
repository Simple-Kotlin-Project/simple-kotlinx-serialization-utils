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

import io.github.edmondantes.serialization.element.factory.structure.builder.collection
import io.github.edmondantes.serialization.element.factory.structure.builder.simple
import io.github.edmondantes.serialization.element.factory.structure.builder.switch
import io.github.edmondantes.serialization.encoding.filter.filterByIdentifier
import io.github.edmondantes.serialization.encoding.inline.supportInline
import io.github.edmondantes.serialization.entity.TestEntityJvmInline
import io.github.edmondantes.serialization.entity.TestEntityWithInlineClass
import io.github.edmondantes.serialization.entity.TestEntityWithInlineProperty
import io.github.edmondantes.serialization.entity.TestEntityWithInlinePropertyWithSameName
import io.github.edmondantes.serialization.entity.TestEntityWithJvmInlineEntity
import io.github.edmondantes.serialization.entity.TestInlineEntity
import io.github.edmondantes.serialization.entity.TestSimpleEntity
import io.github.edmondantes.util.assertEqualsStructure
import io.github.edmondantes.util.serializableTest
import io.github.edmondantes.util.serializeWithLog
import kotlinx.serialization.descriptors.serialDescriptor
import kotlin.test.Test

class InlineEncoderTest {
    @Test
    fun testInline() =
        serializableTest {
            serialization {
                TestEntityWithInlineProperty(
                    "id0",
                    TestSimpleEntity(
                        "id1",
                        "name",
                        10,
                        listOf("one", "two"),
                    ),
                ).serializeWithLog(it) { supportInline() }
            }

            check {
                assertEqualsStructure<TestEntityWithInlineProperty> {
                    simple("notInline", "id0")
                    switch<TestSimpleEntity> {
                        simple("id", "id1")
                        simple("name", "name")
                        simple("index", 10)
                        collection("collection") {
                            simple("one")
                            simple("two")
                        }
                    }
                }
            }
        }

    @Test
    fun testInlineClass() =
        serializableTest {
            serialization {
                TestEntityWithInlineClass(
                    "id0",
                    TestInlineEntity(
                        "id1",
                        "name",
                    ),
                ).serializeWithLog(it) { supportInline() }
            }

            check {
                assertEqualsStructure<TestEntityWithInlineClass> {
                    simple("notInline", "id0")
                    switch<TestInlineEntity> {
                        simple("id", "id1")
                        simple("name", "name")
                    }
                }
            }
        }

    @Test
    fun testInlinePropertyWithSameName() =
        serializableTest {
            serialization {
                TestEntityWithInlinePropertyWithSameName(
                    TestSimpleEntity(
                        "id0",
                        "name",
                        10,
                        listOf("one", "two"),
                    ),
                ).serializeWithLog(it) { supportInline() }
            }

            check {
                assertEqualsStructure<TestEntityWithInlinePropertyWithSameName> {
                    switch<TestSimpleEntity> {
                        simple("id", "id0")
                        simple("name", "name")
                        simple("index", 10)
                        collection("collection") {
                            simple("one")
                            simple("two")
                        }
                    }
                }
            }
        }

    @Test
    fun testJvmInline() =
        serializableTest {
            serialization {
                TestEntityWithJvmInlineEntity(TestEntityJvmInline("test"))
                    .serializeWithLog(it.filterByIdentifier()) { supportInline() }
            }

            check {
                assertEqualsStructure<TestEntityWithJvmInlineEntity> {
                    simple<String>("inline", serialDescriptor<TestEntityJvmInline>()).value("test")
                }
            }
        }
}
