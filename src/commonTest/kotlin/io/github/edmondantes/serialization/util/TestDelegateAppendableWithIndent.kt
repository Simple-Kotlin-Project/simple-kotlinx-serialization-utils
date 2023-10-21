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
package io.github.edmondantes.serialization.util

import kotlin.test.Test
import kotlin.test.assertEquals

class TestDelegateAppendableWithIndent {
    val appendable = StringBuilder()
    val indent = "    "
    val delegate = DelegateAppendableWithIndent(appendable, indent)

    @Test
    fun testWithoutIndent() {
        delegate.append('1')
        delegate.append("Hello")
        delegate.append("NotWorldYet", 3, 8)

        assertEquals("1HelloWorld", appendable.toString())
    }

    @Test
    fun testWithIndentsSimple() {
        delegate.append("1")
        delegate.withIdent {
            delegate.newLine()
            delegate.append("12")
        }

        assertEquals("1\n${indent}12", appendable.toString())
    }

    @Test
    fun testWithIndents() {
        delegate.addIdent()
        delegate.append('1')
        delegate.withIdent {
            it.append("23")
            it.newLine()
            it.append('H')
            it.append("elloWorld")
            delegate.newLine()
        }
        delegate.append("67")

        assertEquals("${indent}123\n${indent}${indent}HelloWorld\n${indent}67", appendable.toString())
    }

    @Test
    fun testClearIndents() {
        delegate.append("1")
        delegate.addIdent()
        delegate.newLine()
        delegate.append("7368")
        delegate.newLine()
        delegate.addIdent()
        delegate.append("Hello")
        delegate.clearIdent()
        delegate.newLine()
        delegate.append("world")

        assertEquals("1\n${indent}7368\n${indent}${indent}Hello\nworld", appendable.toString())
    }

    @Test
    fun testMultilineWithIndents() {
        delegate.append('1')
        delegate.addIdent()
        delegate.append("23\nHelloWorld")

        assertEquals("123\n${indent}HelloWorld", appendable.toString())
    }

    @Test
    fun testNullable() {
        delegate.append('1')
        delegate.append(null)
        delegate.append("45", 0, 0)

        assertEquals("1", appendable.toString())
    }
}
