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
import io.github.edmondantes.serialization.encoding.element.ElementEncoder
import io.github.edmondantes.serialization.encoding.element.factory.element
import io.github.edmondantes.serialization.encoding.element.factory.structureElement
import io.github.edmondantes.util.assertEquals
import io.github.edmondantes.util.serializeWithLog
import kotlin.test.Test

class CircularEncoderTest {

    @Test
    fun testCircularWithoutHashCodeAndWithRefEquality() {
        val encoder = ElementEncoder("id1")

        TestCircleEntity("id0", null).also { value ->
            value.nested = TestCircleEntity("id1", null).apply {
                nested = TestCircleEntity("id2", value)
            }
        }.serializeWithLog(encoder) { supportCircular(byHashCode = false, useRefEquality = false) }

        assertEquals<TestCircleEntity>(encoder) {
            structure {
                element("id", "id0")
                structureElement("nested") {
                    element("id", "id1")
                    structureElement("nested") {
                        element("id", "id2")
                        structureElement("nested") {
                            element("id", "id0")
                        }
                    }
                }
            }
        }
    }

    @Test
    fun testCircularWithoutHashCodeAndWithoutRefEquality() {
        val encoder = ElementEncoder("id1")

        TestCircleEntityWithEquals("id0", null).also { value ->
            value.nested = TestCircleEntityWithEquals("id1", null).apply {
                nested = TestCircleEntityWithEquals("id1", value)
            }
        }.serializeWithLog(encoder) { supportCircular(byHashCode = false, useRefEquality = false) }

        assertEquals<TestCircleEntityWithEquals>(encoder) {
            structure {
                element("id", "id0")
                structureElement("nested") {
                    element("id", "id1")
                }
            }
        }
    }

    @Test
    fun testCircularWithHashCodeAndWithRefEquality() {
        val encoder = ElementEncoder("id1")

        TestDataCircleEntity("id0", null).also { value ->
            value.nested = TestDataCircleEntity("id1", null).apply {
                nested = TestDataCircleEntity("id2", value)
            }
        }.serializeWithLog(encoder) { supportCircular(byHashCode = true, useRefEquality = true) }

        assertEquals<TestDataCircleEntity>(encoder) {
            structure {
                element("id", "id0")
                structureElement("nested") {
                    element("id", "id1")
                    structureElement("nested") {
                        element("id", "id2")
                        structureElement("nested") {
                            element("id", "id0")
                        }
                    }
                }
            }
        }
    }

    @Test
    fun testCircularWithHashCodeAndWithoutRefEquality() {
        val encoder = ElementEncoder("id1")

        TestDataCircleEntity("id0", null).also { value ->
            value.nested = TestDataCircleEntity("id1", null).apply {
                nested = TestDataCircleEntity("id1", value)
            }
        }.serializeWithLog(encoder) { supportCircular(byHashCode = true, useRefEquality = false) }

        assertEquals<TestDataCircleEntity>(encoder) {
            structure {
                element("id", "id0")
                structureElement("nested") {
                    element("id", "id1")
                }
            }
        }
    }

    @Test
    fun testCircularWithAddedFirstObj() {
        val value = TestCircleEntity("id0", null).also { value ->
            value.nested = TestCircleEntity("id1", null).apply {
                nested = TestCircleEntity("id2", value)
            }
        }

        val encoder = ElementEncoder("id1")
        value.serializeWithLog(encoder) {
            supportCircular(
                objForSerialization = value,
                byHashCode = false,
                useRefEquality = true,
            )
        }

        assertEquals<TestCircleEntity>(encoder) {
            structure {
                element("id", "id0")
                structureElement("nested") {
                    element("id", "id1")
                    structureElement("nested") {
                        element("id", "id2")
                    }
                }
            }
        }
    }
}
