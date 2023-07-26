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
import io.github.edmondantes.entity.TestEntityWithNested
import io.github.edmondantes.entity.TestSimpleEntity
import io.github.edmondantes.serialization.encoding.BroadcastEncoder
import io.github.edmondantes.serialization.encoding.element.ElementEncoder
import io.github.edmondantes.serialization.util.serialize
import io.github.edmondantes.util.assertEquals
import io.github.edmondantes.util.loggerEncoder
import io.github.edmondantes.util.serializeWithLog
import kotlin.test.Test

class BroadcastEncoderTest {

    @Test
    fun testSimpleEntity() {
        val encoders = listOf(ElementEncoder(id = "id1"), ElementEncoder(id = "id2"))

        TestSimpleEntity(
            "id",
            "name",
            10,
            listOf("one", "two"),
        ).serializeWithLog(encoders)

        val results = encoders.map { it.finishConstruct() }

        assertEquals(results[0], results[1])
    }

    @Test
    fun testNestedEntity() {
        val encoders = listOf(ElementEncoder("id1"), ElementEncoder("id2"))

        TestEntityWithNested(
            "id123",
            TestSimpleEntity(
                "id",
                "name",
                10,
                listOf("one", "two"),
            ),
        ).serializeWithLog(encoders)

        val result = encoders.map { it.finishConstruct() }

        assertEquals(result[0], result[1])
    }

    @Test
    fun testCircularEntity() {
        val encoders = listOf(ElementEncoder("id1"), ElementEncoder("id2"))
        val encoder = BroadcastEncoder(encoders + loggerEncoder()).supportCircular(false)

        val entity0 = TestCircleEntity("id1", null)
        val entity1 = TestCircleEntity("id2", null).also { entity0.nested = it }
        TestCircleEntity("id3", entity0).also { entity1.nested = it }

        entity0.serialize(encoder)

        val result = encoders.map { it.finishConstruct() }

        assertEquals(result[0], result[1])
    }
}
