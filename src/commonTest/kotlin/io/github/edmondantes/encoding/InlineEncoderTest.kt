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

class InlineEncoderTest {

//    @Test
//    fun testInlineProperty() {
//        val encoder = ElementEncoder("id0")
//
//        TestEntityWithInlineProperty(
//            "id0",
//            TestSimpleEntity(
//                "id1",
//                "name",
//                10,
//                listOf("one", "two"),
//            ),
//        ).serializeWithLog(encoder) {
//            supportInline()
//        }
//
//        println(encoder.finishConstruct())
//
//        val expected = expected<TestEntityWithInlineProperty> {
//            beginStructure {
//                encodeStringElement("notInline", "id0")
//                encodeSerializableElement<TestEntityWithInlineProperty, TestSimpleEntity>("inlineProperty") {}
//                anotherDescriptor<TestEntityWithInlineProperty, TestSimpleEntity> {
//                    encodeStringElement("id", "id1")
//                    encodeStringElement("name", "name")
//                    encodeIntElement("index", 10)
//
//                    encodeSerializableElement("collection") {
//                        beginCollection<ArrayList<String>> {
//                            encodeSerializableElement("0") {
//                                encodeString<ArrayList<String>>("one")
//                            }
//                            encodeSerializableElement("1") {
//                                encodeString<ArrayList<String>>("two")
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    @Test
//    @Ignore
//    fun testInlineClass() {
//        val encoders = listOf(TestEncoder("id0"))
//        val encoder = BroadcastEncoder(encoders + loggerEncoder()).supportInline()
//
//        val value = TestEntityWithInlineClass(
//            "id0",
//            TestInlineEntity(
//                "id1",
//                "name",
//            ),
//        )
//
//        serializer<TestEntityWithInlineClass>().serialize(encoder, value)
//
//        val expected = expected<TestEntityWithInlineClass> {
//            beginStructure {
//                encodeStringElement("notInline", "id0")
//                encodeSerializableElement<TestEntityWithInlineClass, TestInlineEntity>("inlineClass") {}
//                anotherDescriptor<TestEntityWithInlineClass, TestInlineEntity> {
//                    encodeStringElement("id", "id1")
//                    encodeStringElement("name", "name")
//                }
//            }
//        }
//
//        assertTrue(expected.equals(encoders[0]))
//    }
//
//    @Test
//    @Ignore
//    fun testInlinePropertyWithSameName() {
//        val encoders = listOf(TestEncoder("id0"))
//        val encoder = BroadcastEncoder(encoders + loggerEncoder()).supportInline()
//
//        val value = TestEntityWithInlinePropertyWithSameName(
//            TestSimpleEntity(
//                "id0",
//                "name",
//                10,
//                listOf("one", "two"),
//            ),
//        )
//
//        serializer<TestEntityWithInlinePropertyWithSameName>().serialize(encoder, value)
//
// //        val expected = expected<TestEntityWithInlinePropertyWithSameName> {
// //            beginStructure {
// //                encodeSerializableElement<TestEntityWithInlinePropertyWithSameName, TestSimpleEntity>("id") {}
// //                anotherDescriptor<TestEntityWithInlinePropertyWithSameName, TestSimpleEntity> {
// //                    encodeStringElement("id", "id0")
// //                    encodeStringElement("name", "name")
// //                    encodeIntElement("index", 10)
// //
// //                    encodeSerializableElement("collection") {
// //                        beginCollection<ArrayList<String>> {
// //                            encodeSerializableElement("0") {
// //                                encodeString<ArrayList<String>>("one")
// //                            }
// //                            encodeSerializableElement("1") {
// //                                encodeString<ArrayList<String>>("two")
// //                            }
// //                        }
// //                    }
// //                }
// //            }
// //        }
// //
// //        assertTrue(expected.equals(encoders[0]))
//
//        println(encoders[0])
//    }
}
