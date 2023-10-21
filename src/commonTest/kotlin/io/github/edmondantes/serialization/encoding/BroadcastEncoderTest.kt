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

import io.github.edmondantes.serialization.element.DefaultEncodedElement
import io.github.edmondantes.serialization.encoding.broadcast.BroadcastEncoder
import io.github.edmondantes.serialization.encoding.circular.sequenceSupportCircular
import io.github.edmondantes.serialization.encoding.element.ElementEncoder
import io.github.edmondantes.serialization.entity.TestCircleEntity
import io.github.edmondantes.serialization.entity.TestEntityWithNested
import io.github.edmondantes.serialization.entity.TestSimpleEntity
import io.github.edmondantes.serialization.util.serialize
import io.github.edmondantes.util.assertEquals
import io.github.edmondantes.util.loggerEncoder
import io.github.edmondantes.util.serializeWithLog
import kotlin.test.Test

class BroadcastEncoderTest {
    @Test
    fun testSimpleEntity() {
        val builders = buildList { repeat(2) { add(DefaultEncodedElement.Builder<Any?>()) } }
        val encoders = builders.mapIndexed { i, builder -> ElementEncoder(builder, "id${i + 1}") }

        TestSimpleEntity(
            "id",
            "name",
            10,
            listOf("one", "two"),
        ).serializeWithLog(encoders)

        val results = builders.map { it.build() }
        assertEquals(results[0], results[1])
    }

    @Test
    fun testNestedEntity() {
        val builders = buildList { repeat(2) { add(DefaultEncodedElement.Builder<Any?>()) } }
        val encoders = builders.mapIndexed { i, builder -> ElementEncoder(builder, "id${i + 1}") }

        TestEntityWithNested(
            "id123",
            TestSimpleEntity(
                "id",
                "name",
                10,
                listOf("one", "two"),
            ),
        ).serializeWithLog(encoders)

        val result = builders.map { it.build() }

        assertEquals(result[0], result[1])
    }

    @Test
    fun testCircularEntity() {
        val builders = buildList { repeat(2) { add(DefaultEncodedElement.Builder<Any?>()) } }
        val encoders = builders.mapIndexed { i, builder -> ElementEncoder(builder, "id${i + 1}") }
        val encoder = BroadcastEncoder(encoders + loggerEncoder()).sequenceSupportCircular(false)

        val entity0 = TestCircleEntity("id1", null)
        val entity1 = TestCircleEntity("id2", null).also { entity0.nested = it }
        TestCircleEntity("id3", entity0).also { entity1.nested = it }

        entity0.serialize(encoder)

        val result = builders.map { it.build() }

        assertEquals(result[0], result[1])
    }
}
