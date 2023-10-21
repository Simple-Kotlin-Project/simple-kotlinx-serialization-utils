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
import io.github.edmondantes.serialization.encoding.element.ElementEncoder
import io.github.edmondantes.serialization.encoding.filter.filterByIdentifier
import io.github.edmondantes.serialization.entity.TestFilterEntity
import io.github.edmondantes.util.assertEqualsStructure
import io.github.edmondantes.util.multiplySerializableTest
import io.github.edmondantes.util.serializeWithLog
import kotlin.test.Test

class FilterEncoderTest {
    @Test
    fun compileTimeFilter() =
        multiplySerializableTest(IntRange(1, 3).asSequence()) {
            configureEncoder { i, builder -> ElementEncoder(builder, "id$i") }

            serializationAll {
                TestFilterEntity(
                    ID,
                    NAME,
                    PASSWORD,
                    PERSONAL_DATA,
                    TRANSACTION_ID,
                ).serializeWithLog(it.filterByIdentifier())
            }

            check {
                assertEqualsStructure<TestFilterEntity> {
                    when (key) {
                        1 -> {
                            simple("id", ID)
                            simple("name", NAME)
                        }

                        2 -> {
                            simple("id", ID)
                            simple("name", NAME)
                            simple("password", PASSWORD)
                            simple("transactionId", TRANSACTION_ID)
                        }

                        3 -> {
                            simple("id", ID)
                            simple("name", NAME)
                            simple("personalData", PERSONAL_DATA)
                            simple("transactionId", TRANSACTION_ID)
                        }
                    }
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
