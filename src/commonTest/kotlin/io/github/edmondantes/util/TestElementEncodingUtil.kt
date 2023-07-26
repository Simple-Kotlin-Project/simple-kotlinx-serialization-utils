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

import io.github.edmondantes.serialization.encoding.ConstructEncoder
import io.github.edmondantes.serialization.encoding.element.EncodingElement
import io.github.edmondantes.serialization.encoding.element.factory.CallbackElementFactory
import io.github.edmondantes.serialization.encoding.element.factory.ElementFactory
import kotlinx.serialization.descriptors.serialDescriptor
import kotlin.test.assertTrue

inline fun <reified T> assertEquals(
    encoder: ConstructEncoder<EncodingElement<*>>,
    expected: ElementFactory.() -> Unit,
) {
    CallbackElementFactory(serialDescriptor<T>(), null, null, null) {
        assertEquals(it, encoder.finishConstruct())
    }.expected()
}

fun assertEquals(expected: EncodingElement<*>, actual: EncodingElement<*>) {
    logExpectedAndActual(expected, actual)

    assertTrue { expected == actual }
}

fun logExpectedAndActual(expected: EncodingElement<*>, actual: EncodingElement<*>) {
    log {
        appendLine("Expected:")
        withIdent {
            expected.print(it)
        }
        appendLine()
        appendLine("Actual:")
        withIdent {
            actual.print(it)
        }
    }
}
