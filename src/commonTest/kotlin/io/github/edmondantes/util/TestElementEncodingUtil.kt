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
package io.github.edmondantes.util

import io.github.edmondantes.serialization.element.AnyEncodedElement
import io.github.edmondantes.serialization.element.EncodedElement
import io.github.edmondantes.serialization.element.factory.simple.DefaultSimpleEncodedElementFactory
import io.github.edmondantes.serialization.element.factory.simple.SimpleEncodedElementFactory
import io.github.edmondantes.serialization.element.factory.structure.ComplexEncodedElementFactory
import io.github.edmondantes.serialization.element.factory.structure.DefaultComplexEncodedElementFactory
import io.github.edmondantes.serialization.element.factory.structure.builder.StructureEncodingElementBuilder
import io.github.edmondantes.serialization.element.takeIfComplex
import kotlinx.serialization.descriptors.serialDescriptor
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.fail

inline fun <reified T> assertEqualsComplex(
    actual: AnyEncodedElement,
    expected: ComplexEncodedElementFactory.() -> Unit,
) {
    val actualStructureElement = actual.takeIfComplex()
    assertNotNull(actualStructureElement, "Actual element is not structure element")

    DefaultComplexEncodedElementFactory(serialDescriptor<T>(), null, null, null) { it ->
        assertEquals(it, actualStructureElement)
    }.expected()
}

inline fun <reified T> CheckContext<EncodedElement<*>>.assertEqualsComplex(expected: ComplexEncodedElementFactory.() -> Unit) =
    assertEqualsComplex<T>(actual, expected)

inline fun <reified T> assertEqualsStructure(
    element: AnyEncodedElement,
    crossinline expected: StructureEncodingElementBuilder.() -> Unit,
) = assertEqualsComplex<T>(element) { structure { this.expected() } }

inline fun <reified T> CheckContext<EncodedElement<*>>.assertEqualsStructure(
    crossinline expected: StructureEncodingElementBuilder.() -> Unit,
) = assertEqualsStructure<T>(actual, expected)

inline fun <reified T> assertEqualsSimple(
    element: AnyEncodedElement,
    expected: SimpleEncodedElementFactory<T>.() -> Unit,
) {
    assertNull(element.takeIfComplex(), "Actual element is complex element")
    DefaultSimpleEncodedElementFactory<T>(serialDescriptor<T>(), null, null, null) {
        assertEquals(it, element)
    }.expected()
}

inline fun <reified T> CheckContext<EncodedElement<*>>.assertEqualsSimple(crossinline expected: SimpleEncodedElementFactory<T>.() -> Unit) =
    assertEqualsSimple<T>(actual, expected)

fun assertEquals(
    e: EncodedElement<*>?,
    a: EncodedElement<*>?,
) {
    if (e === a) {
        return
    }

    compareNullability(
        e,
        a,
        expectedNull = { fail("Assertion error: expected is null") },
        actualNull = { fail("Assertion error: actual is null") },
        notNull = { expected, actual ->
            kotlin.test.assertEquals(
                expected.type,
                actual.type,
                "Assertion error: elements types in not equals",
            )

            kotlin.test.assertEquals(
                expected.descriptorName,
                actual.descriptorName,
                "Assertion error: elements descriptors names is not equals",
            )

            kotlin.test.assertEquals(
                expected.name,
                actual.name,
                "Assertion error: elements names is not equals",
            )

            compareNullability(
                expected.takeIfComplex(),
                actual.takeIfComplex(),
                everyNull = {
                    kotlin.test.assertEquals(
                        expected.value,
                        actual.value,
                        "Assertion error: simple elements values is not equals",
                    )
                },
                actualNull = { fail("Assertion error: expected element is complex, but actual is simple") },
                expectedNull = { fail("Assertion error: expected element is simple, but actual is complex") },
                notNull = { expectedComplex, actualComplex ->
                    try {
                        expectedComplex.value.value.forEachIndexed { index, expectedElement ->
                            try {
                                assertEquals(expectedElement, actualComplex.value.value[index])
                            } catch (e: AssertionError) {
                                fail("Assertion error: element by index: $index is not equals: [${e.message}]")
                            }
                        }
                    } catch (e: AssertionError) {
                        fail("Assertion error: complex elements values is not equals: [${e.message}]")
                    }
                },
            )
        },
    )
}

fun <E : Any, A : Any> compareNullability(
    expected: E?,
    actual: A?,
    everyNull: () -> Unit = {},
    expectedNull: (A) -> Unit = {},
    actualNull: (E) -> Unit = {},
    notNull: (E, A) -> Unit = { _, _ -> },
) {
    if (expected == null && actual == null) {
        everyNull()
    } else if (expected == null && actual != null) {
        expectedNull(actual)
    } else if (expected != null && actual == null) {
        actualNull(expected)
    } else {
        notNull(expected!!, actual!!)
    }
}
