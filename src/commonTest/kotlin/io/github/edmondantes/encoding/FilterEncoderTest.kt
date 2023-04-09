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

import io.github.edmondantes.entity.ContextualFilterEntity
import io.github.edmondantes.entity.TestFilterEntity
import io.github.edmondantes.serialization.encoding.BroadcastEncoder
import io.github.edmondantes.serialization.encoding.filterByIdentifier
import io.github.edmondantes.util.TestEncoder
import io.github.edmondantes.util.beginStructure
import io.github.edmondantes.util.encodeNull
import io.github.edmondantes.util.encodeNullableSerializableElement
import io.github.edmondantes.util.encodeString
import io.github.edmondantes.util.encodeStringElement
import io.github.edmondantes.util.expected
import io.github.edmondantes.util.loggerEncoder
import kotlinx.serialization.serializer
import kotlin.test.Test
import kotlin.test.assertTrue

class FilterEncoderTest {

    @Test
    fun compileTimeFilter() {
        val encoders = listOf(TestEncoder("id1"), TestEncoder("id2"), TestEncoder("id3"))
        val encoder = BroadcastEncoder(encoders.map { it.filterByIdentifier() } + loggerEncoder())

        val expected = listOf(
            expected<TestFilterEntity> {
                beginStructure {
                    encodeStringElement("id", ID)
                    encodeStringElement("name", NAME)
                    encodeNullableSerializableElement("nestedContextualFilteredEntity") {
                        encodeNull<ContextualFilterEntity>()
                    }
                }
            },
            expected<TestFilterEntity> {
                beginStructure {
                    encodeStringElement("id", ID)
                    encodeStringElement("name", NAME)
                    encodeStringElement("password", PASSWORD)
                    encodeStringElement("transactionId", TRANSACTION_ID)
                    encodeNullableSerializableElement("nestedContextualFilteredEntity") {
                        encodeNull<ContextualFilterEntity>()
                    }
                }
            },
            expected<TestFilterEntity> {
                beginStructure {
                    encodeStringElement("id", ID)
                    encodeStringElement("name", NAME)
                    encodeStringElement("personalData", PERSONAL_DATA)
                    encodeStringElement("transactionId", TRANSACTION_ID)
                    encodeNullableSerializableElement("nestedContextualFilteredEntity") {
                        encodeNull<ContextualFilterEntity>()
                    }
                }
            },
        )

        val value = TestFilterEntity(
            ID,
            NAME,
            PASSWORD,
            PERSONAL_DATA,
            TRANSACTION_ID,
            null,
        )

        serializer<TestFilterEntity>().serialize(encoder, value)

        for (i in 0 until 2) {
            assertTrue(expected[i].equals(encoders[i]))
        }
    }

    @Test
    fun contextRuntimeFilter() {
        val encoders = listOf(TestEncoder("id1"), TestEncoder("id8"))
        val encoder = BroadcastEncoder(encoders.map { it.filterByIdentifier() } + loggerEncoder())

        val expected = listOf(
            expected<TestFilterEntity> {
                beginStructure {
                    encodeStringElement("id", ID)
                    encodeStringElement("name", NAME)
                    encodeNullableSerializableElement<TestFilterEntity, ContextualFilterEntity>("nestedContextualFilteredEntity") {
                        encodeString("test")
                    }
                }
            },
            expected<TestFilterEntity> {
                beginStructure {
                    encodeStringElement("id", ID)
                    encodeStringElement("name", NAME)
                    encodeStringElement("transactionId", TRANSACTION_ID)
                    encodeNullableSerializableElement<TestFilterEntity, ContextualFilterEntity>("nestedContextualFilteredEntity") {
                        encodeString("test")
                    }
                }
            },
        )

        val value = TestFilterEntity(
            ID,
            NAME,
            PASSWORD,
            PERSONAL_DATA,
            TRANSACTION_ID,
            ContextualFilterEntity("id8", "test"),
        )

        serializer<TestFilterEntity>().serialize(encoder, value)

        for (i in 0 until 2) {
            assertTrue(expected[i].equals(encoders[i]))
        }
    }

    private companion object {
        const val ID = "id"
        const val NAME = "name"
        const val PASSWORD = "pass"
        const val PERSONAL_DATA = "first name, last name, address"
        const val TRANSACTION_ID = "randomUUID"
    }
}
