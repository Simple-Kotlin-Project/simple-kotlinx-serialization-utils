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
import io.github.edmondantes.serialization.encoding.element.ElementEncoder
import io.github.edmondantes.serialization.encoding.element.factory.element
import io.github.edmondantes.serialization.encoding.element.factory.elementNull
import io.github.edmondantes.serialization.encoding.filterByIdentifier
import io.github.edmondantes.util.assertEquals
import io.github.edmondantes.util.serializeWithLog
import kotlin.test.Test

class FilterEncoderTest {

    @Test
    fun compileTimeFilter() {
        val encoders = listOf(ElementEncoder("id1"), ElementEncoder("id2"), ElementEncoder("id3"))

        TestFilterEntity(
            ID,
            NAME,
            PASSWORD,
            PERSONAL_DATA,
            TRANSACTION_ID,
            null,
        ).serializeWithLog(encoders.map { it.filterByIdentifier() })

        assertEquals<TestFilterEntity>(encoders[0]) {
            structure {
                element("id", ID)
                element("name", NAME)
                elementNull("nestedContextualFilteredEntity")
            }
        }

        assertEquals<TestFilterEntity>(encoders[1]) {
            structure {
                element("id", ID)
                element("name", NAME)
                element("password", PASSWORD)
                element("transactionId", TRANSACTION_ID)
                elementNull("nestedContextualFilteredEntity")
            }
        }

        assertEquals<TestFilterEntity>(encoders[2]) {
            structure {
                element("id", ID)
                element("name", NAME)
                element("personalData", PERSONAL_DATA)
                element("transactionId", TRANSACTION_ID)
                elementNull("nestedContextualFilteredEntity")
            }
        }
    }

    @Test
    fun contextRuntimeFilter() {
        val encoders = listOf(ElementEncoder("id1"), ElementEncoder("id8"))

        TestFilterEntity(
            ID,
            NAME,
            PASSWORD,
            PERSONAL_DATA,
            TRANSACTION_ID,
            ContextualFilterEntity("id8", "test"),
        ).serializeWithLog(encoders.map { it.filterByIdentifier() })

        assertEquals<TestFilterEntity>(encoders[0]) {
            structure {
                element("id", ID)
                element("name", NAME)
                element("nestedContextualFilteredEntity", "test")
            }
        }

        assertEquals<TestFilterEntity>(encoders[1]) {
            structure {
                element("id", ID)
                element("name", NAME)
                element("transactionId", TRANSACTION_ID)
                element("nestedContextualFilteredEntity", "test")
            }
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
