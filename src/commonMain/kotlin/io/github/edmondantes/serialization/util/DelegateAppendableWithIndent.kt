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

/**
 * A class that implements the [AppendableWithIndent] interface to delegates append with intents to classic [Appendable]
 *
 * @property delegate The delegate classic [Appendable] instance.
 * @property indent The indentation characters to be used. Defaults to four spaces.
 */
public class DelegateAppendableWithIndent(
    private val delegate: Appendable,
    private val indent: CharSequence = "    ",
) : AppendableWithIndent {
    private var indentCount: Int = 0
    private var needIndent: Boolean = true

    override fun append(value: Char): AppendableWithIndent =
        apply {
            if (value == '\n' && indentCount > 0) {
                delegate.append('\n')
                needIndent = true
            } else {
                if (needIndent) {
                    repeat(indentCount) {
                        delegate.append(indent)
                    }
                }
                delegate.append(value)
                needIndent = false
            }
        }

    override fun append(value: CharSequence?): AppendableWithIndent =
        apply {
            append(value, 0, value?.length ?: 0)
        }

    override fun append(
        value: CharSequence?,
        startIndex: Int,
        endIndex: Int,
    ): AppendableWithIndent =
        apply {
            if (value == null || endIndex - startIndex < 1) {
                delegate.append(value, startIndex, endIndex)
                return this
            }

            value.substring(startIndex, endIndex).forEach {
                append(it)
            }
        }

    override fun addIdent(): AppendableWithIndent =
        apply {
            indentCount++
        }

    override fun removeIdent(): AppendableWithIndent =
        apply {
            indentCount = (indentCount - 1).coerceAtLeast(0)
            if (indentCount == 0) {
                needIndent = false
            }
        }

    override fun clearIdent(): AppendableWithIndent =
        apply {
            indentCount = 0
            needIndent = false
        }
}
