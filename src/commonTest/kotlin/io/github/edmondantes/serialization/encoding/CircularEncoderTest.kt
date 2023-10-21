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

import io.github.edmondantes.serialization.element.factory.structure.builder.simple
import io.github.edmondantes.serialization.element.factory.structure.builder.structure
import io.github.edmondantes.serialization.encoding.circular.supportCircular
import io.github.edmondantes.serialization.encoding.element.ElementEncoder
import io.github.edmondantes.serialization.entity.TestCircleEntity
import io.github.edmondantes.serialization.entity.TestCircleEntityWithEquals
import io.github.edmondantes.serialization.entity.TestDataCircleEntity
import io.github.edmondantes.util.assertEqualsStructure
import io.github.edmondantes.util.serializableTestTemplate
import io.github.edmondantes.util.serializeWithLog
import kotlin.test.Test

class CircularEncoderTest {
    @Test
    fun testCircularWithoutHashCodeAndWithRefEquality() =
        ENCODER_WITH_ID.run {
            serialization {
                TestCircleEntity("id0", null).also { value ->
                    value.nested =
                        TestCircleEntity("id1", null).apply {
                            nested = TestCircleEntity("id2", value)
                        }
                }.serializeWithLog(it) { supportCircular(byHashCode = false, useRefEquality = false) }
            }

            check {
                assertEqualsStructure<TestCircleEntity> {
                    simple("id", "id0")
                    structure("nested") {
                        simple("id", "id1")
                        structure("nested") {
                            simple("id", "id2")
                            structure("nested") {
                                simple("id", "id0")
                            }
                        }
                    }
                }
            }
        }

    @Test
    fun testCircularWithoutHashCodeAndWithoutRefEquality() =
        ENCODER_WITH_ID.run {
            serialization {
                TestCircleEntityWithEquals("id0", null).also { value ->
                    value.nested =
                        TestCircleEntityWithEquals("id1", null).apply {
                            nested = TestCircleEntityWithEquals("id1", value)
                        }
                }.serializeWithLog(it) { supportCircular(byHashCode = false, useRefEquality = false) }
            }

            check {
                assertEqualsStructure<TestCircleEntityWithEquals> {
                    simple("id", "id0")
                    structure("nested") {
                        simple("id", "id1")
                    }
                }
            }
        }

    @Test
    fun testCircularWithHashCodeAndWithRefEquality() =
        ENCODER_WITH_ID.run {
            serialization {
                TestDataCircleEntity("id0", null).also { value ->
                    value.nested =
                        TestDataCircleEntity("id1", null).apply {
                            nested = TestDataCircleEntity("id2", value)
                        }
                }.serializeWithLog(it) { supportCircular(byHashCode = true, useRefEquality = true) }
            }

            check {
                assertEqualsStructure<TestDataCircleEntity> {
                    simple("id", "id0")
                    structure("nested") {
                        simple("id", "id1")
                        structure("nested") {
                            simple("id", "id2")
                            structure("nested") {
                                simple("id", "id0")
                            }
                        }
                    }
                }
            }
        }

    @Test
    fun testCircularWithHashCodeAndWithoutRefEquality() =
        ENCODER_WITH_ID.run {
            serialization {
                TestDataCircleEntity("id0", null).also { value ->
                    value.nested =
                        TestDataCircleEntity("id1", null).apply {
                            nested = TestDataCircleEntity("id1", value)
                        }
                }.serializeWithLog(it) { supportCircular(byHashCode = true, useRefEquality = false) }
            }

            check {
                assertEqualsStructure<TestDataCircleEntity> {
                    simple("id", "id0")
                    structure("nested") {
                        simple("id", "id1")
                    }
                }
            }
        }

    @Test
    fun testCircularWithAddedFirstObj() =
        ENCODER_WITH_ID.run {
            serialization {
                val value =
                    TestCircleEntity("id0", null).also { value ->
                        value.nested =
                            TestCircleEntity("id1", null).apply {
                                nested = TestCircleEntity("id2", value)
                            }
                    }

                value.serializeWithLog(it) {
                    supportCircular(
                        objForSerialization = value,
                        byHashCode = false,
                        useRefEquality = true,
                    )
                }
            }

            check {
                assertEqualsStructure<TestCircleEntity> {
                    simple("id", "id0")
                    structure("nested") {
                        simple("id", "id1")
                        structure("nested") {
                            simple("id", "id2")
                        }
                    }
                }
            }
        }

    private companion object {
        val ENCODER_WITH_ID =
            serializableTestTemplate {
                configureEncoder { ElementEncoder(it, "id1") }
            }
    }
}
