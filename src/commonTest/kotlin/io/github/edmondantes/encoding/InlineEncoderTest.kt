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

import io.github.edmondantes.entity.TestEntityWithInlineClass
import io.github.edmondantes.entity.TestEntityWithInlineProperty
import io.github.edmondantes.entity.TestEntityWithInlinePropertyWithSameName
import io.github.edmondantes.entity.TestInlineEntity
import io.github.edmondantes.entity.TestSimpleEntity
import io.github.edmondantes.serialization.encoding.element.ElementEncoder
import io.github.edmondantes.serialization.encoding.element.factory.collectionElement
import io.github.edmondantes.serialization.encoding.element.factory.element
import io.github.edmondantes.serialization.encoding.element.factory.switch
import io.github.edmondantes.serialization.encoding.inline.supportInline
import io.github.edmondantes.util.assertEquals
import io.github.edmondantes.util.serializeWithLog
import kotlin.test.Test

class InlineEncoderTest {

    @Test
    fun testInline() {
        val encoder = ElementEncoder("id0")

        TestEntityWithInlineProperty(
            "id0",
            TestSimpleEntity(
                "id1",
                "name",
                10,
                listOf("one", "two"),
            ),
        ).serializeWithLog(encoder) { supportInline() }

        assertEquals<TestEntityWithInlineProperty>(encoder) {
            structure {
                element("notInline", "id0")
                switch<TestSimpleEntity> {
                    element("id", "id1")
                    element("name", "name")
                    element("index", 10)
                    collectionElement("collection") {
                        element("one")
                        element("two")
                    }
                }
            }
        }
    }

    @Test
    fun testInlineClass() {
        val encoder = ElementEncoder("id0")

        TestEntityWithInlineClass(
            "id0",
            TestInlineEntity(
                "id1",
                "name",
            ),
        ).serializeWithLog(encoder) { supportInline() }

        assertEquals<TestEntityWithInlineClass>(encoder) {
            structure {
                element("notInline", "id0")
                switch<TestInlineEntity> {
                    element("id", "id1")
                    element("name", "name")
                }
            }
        }
    }

    @Test
    fun testInlinePropertyWithSameName() {
        val encoder = ElementEncoder("id0")

        TestEntityWithInlinePropertyWithSameName(
            TestSimpleEntity(
                "id0",
                "name",
                10,
                listOf("one", "two"),
            ),
        ).serializeWithLog(encoder) { supportInline() }

        assertEquals<TestEntityWithInlinePropertyWithSameName>(encoder) {
            structure {
                switch<TestSimpleEntity> {
                    element("id", "id0")
                    element("name", "name")
                    element("index", 10)
                    collectionElement("collection") {
                        element("one")
                        element("two")
                    }
                }
            }
        }
    }
}
