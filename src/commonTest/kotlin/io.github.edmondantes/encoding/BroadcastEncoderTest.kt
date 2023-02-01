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

import io.github.edmondantes.entity.SimpleTestEntity
import io.github.edmondantes.entity.TestCircleEntity
import io.github.edmondantes.entity.TestEntityWithNested
import io.github.edmondantes.serialization.encoding.BroadcastEncoder
import io.github.edmondantes.serialization.encoding.LoggerEncoder
import io.github.edmondantes.util.TestEncoder
import kotlinx.serialization.serializer
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class BroadcastEncoderTest {

    @BeforeTest
    fun init() {
        println()
    }

    @Test
    fun testSimpleEntity() {
        val encoders = listOf(TestEncoder("id1"), TestEncoder("id2"))
        val encoder = BroadcastEncoder(encoders + LoggerEncoder())

        val value = SimpleTestEntity(
            "id",
            "name",
            10,
            listOf("one", "two"),
        )

        serializer<SimpleTestEntity>().serialize(encoder, value)

        assertEquals(encoders[0], encoders[1])
    }

    @Test
    fun testNestedEntity() {
        val encoders = listOf(TestEncoder("id1"), TestEncoder("id2"))
        val encoder = BroadcastEncoder(encoders + LoggerEncoder())

        val value = TestEntityWithNested(
            "id123",
            SimpleTestEntity(
                "id",
                "name",
                10,
                listOf("one", "two"),
            ),
        )

        serializer<TestEntityWithNested>().serialize(encoder, value)

        assertEquals(encoders[0], encoders[1])
    }

    @Test
    fun testCircularEntity() {
        val encoders = listOf(TestEncoder("id1"), TestEncoder("id2"))
        val encoder = BroadcastEncoder(encoders + LoggerEncoder()).supportCircular(false)

        val entity0 = TestCircleEntity("id1", null)
        val entity1 = TestCircleEntity("id2", null).also { entity0.nested = it }
        TestCircleEntity("id3", entity0).also { entity1.nested = it }

        serializer<TestCircleEntity>().serialize(encoder, entity0)

        assertEquals(encoders[0], encoders[1])
    }
}
