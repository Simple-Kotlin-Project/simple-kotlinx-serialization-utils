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

public class DelegateAppendableWithIndent(
    private val delegate: Appendable,
    private val indent: CharSequence = "    ",
) : AppendableWithIndent {

    private var indentCount: Int = 0
    private var nowIsIndent: Boolean = true

    override fun append(value: Char): AppendableWithIndent = apply {
        if (value == '\n' && indentCount > 0) {
            val builder = StringBuilder()
            builder.append(value)
            repeat(indentCount) {
                builder.append(indent)
            }
            delegate.append(builder)
        } else {
            delegate.append(value)
        }
    }

    override fun append(value: CharSequence?): AppendableWithIndent = apply {
        append(value, 0, value?.length ?: 0)
    }

    override fun append(value: CharSequence?, startIndex: Int, endIndex: Int): AppendableWithIndent {
        if (value == null || indentCount < 1 || endIndex - startIndex < 1) {
            delegate.append(value, startIndex, endIndex)
            return this
        }

        var builder: StringBuilder? = null
        var prevLine: Int = -1

        for (i in startIndex until endIndex) {
            val ch = value[i]

            if (ch != '\n') {
                continue
            }

            if (builder == null) {
                builder = StringBuilder()
            }

            builder.append(value.substring(prevLine + 1, i + 1))

            repeat(indentCount) {
                builder.append(indent)
            }

            prevLine = i
        }

        if (builder == null) {
            delegate.append(value, startIndex, endIndex)
        } else {
            builder.append(value.substring(prevLine + 1))
            delegate.append(builder)
        }

        return this
    }

    override fun addIdent(): AppendableWithIndent = apply {
        indentCount++
        if (nowIsIndent) {
            delegate.append(indent)
        }
    }

    override fun removeIdent(): AppendableWithIndent = apply {
        indentCount--
    }

    override fun clearIdent(): AppendableWithIndent = apply {
        indentCount = 0
    }
}
