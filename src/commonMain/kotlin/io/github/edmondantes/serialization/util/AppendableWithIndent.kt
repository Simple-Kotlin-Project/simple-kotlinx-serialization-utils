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
 * An interface that extends the [Appendable] interface to provide methods for appending text with indentation.
 */
public interface AppendableWithIndent : Appendable {
    override fun append(value: Char): AppendableWithIndent

    override fun append(value: CharSequence?): AppendableWithIndent

    override fun append(
        value: CharSequence?,
        startIndex: Int,
        endIndex: Int,
    ): AppendableWithIndent

    /**
     * Adds one to intends level counter to the current [AppendableWithIndent] instance.
     */
    public fun addIdent(): AppendableWithIndent

    /**
     * Remove one to intends level counter to the current [AppendableWithIndent] instance.
     */
    public fun removeIdent(): AppendableWithIndent

    /**
     * Clears intends level counter from the current [AppendableWithIndent] instance.
     */
    public fun clearIdent(): AppendableWithIndent

    /**
     * This method adds one level of indentation before executing the [block] and removes it after the block is executed.
     *
     * @param block function with indent in this [AppendableWithIndent]
     */
    public fun withIdent(block: (AppendableWithIndent) -> Unit) {
        addIdent()
        block(this)
        removeIdent()
    }
}

/**
 * Appends a new line character ('\n') to [Appendable].
 */
public fun <T : Appendable> T.newLine(): T =
    apply {
        append('\n')
    }
