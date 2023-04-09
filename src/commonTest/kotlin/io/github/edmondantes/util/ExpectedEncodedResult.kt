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

import kotlinx.serialization.ExperimentalSerializationApi

class ExpectedEncodedResult<T>(private val _elements: MutableList<TestEncodingElements> = ArrayList()) :
    List<TestEncodingElements> by _elements {

    @Suppress("UNUSED")
    val elements: List<TestEncodingElements>
        get() = _elements

    fun add(element: TestEncodingElements) {
        _elements.add(element)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other || other is ExpectedEncodedResult<*> && this._elements == other._elements) {
            return true
        }

        if (other is List<*>) {
            if (other.size != _elements.size) {
                return false
            }
            for (i in other.indices) {
                val elem0 = other[i]
                val elem1 = _elements[i]

                if (elem0 != elem1) {
                    return false
                }
            }
            return true
        }

        return false
    }

    override fun hashCode(): Int =
        _elements.hashCode()

    override fun toString(): String =
        _elements.toString()
}

inline fun <reified T> expected(block: ExpectedEncodedResult<T>.() -> Unit): ExpectedEncodedResult<T> =
    ExpectedEncodedResult<T>().also(block)

@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T> ExpectedEncodedResult<T>.beginStructure(
    elementName: String? = null,
    structure: ExpectedEncodedResult<T>.() -> Unit,
) {
    val nested = ExpectedEncodedResult<T>()
    structure(nested)
    nested.add(TestEncodingElements("endStructure", descriptor<T>().serialName, null, null, null))
    add(
        TestEncodingElements(
            "beginStructure",
            descriptor<T>().serialName,
            elementName?.let { descriptor<T>().getElementIndex(it) },
            elementName,
            nested,
        ),
    )
}

@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T> ExpectedEncodedResult<T>.beginCollection(
    elementName: String? = null,
    structure: ExpectedEncodedResult<T>.() -> Unit,
) {
    val nested = ExpectedEncodedResult<T>()
    structure(nested)
    nested.add(TestEncodingElements("endStructure", descriptor<T>().serialName, null, null, null))
    add(
        TestEncodingElements(
            "beginCollection",
            descriptor<T>().serialName,
            elementName?.let { descriptor<T>().getElementIndex(it) },
            elementName,
            nested,
        ),
    )
}

@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T> ExpectedEncodedResult<T>.encodeByteElement(elementName: String?, elementValue: Byte?) {
    add(
        TestEncodingElements(
            "encodeByteElement",
            descriptor<T>().serialName,
            elementName?.let { descriptor<T>().getElementIndex(it) },
            elementName,
            elementValue,
        ),
    )
}

@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T> ExpectedEncodedResult<T>.encodeStringElement(elementName: String?, elementValue: Any?) {
    add(
        TestEncodingElements(
            "encodeStringElement",
            descriptor<T>().serialName,
            elementName?.let { descriptor<T>().getElementIndex(it) },
            elementName,
            elementValue,
        ),
    )
}

@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T> ExpectedEncodedResult<T>.encodeIntElement(elementName: String?, elementValue: Any?) {
    add(
        TestEncodingElements(
            "encodeIntElement",
            descriptor<T>().serialName,
            elementName?.let { descriptor<T>().getElementIndex(it) },
            elementName,
            elementValue,
        ),
    )
}

fun <T> ExpectedEncodedResult<T>.encodeString(elementValue: Any?) {
    add(
        TestEncodingElements(
            "encodeString",
            null,
            null,
            null,
            elementValue,
        ),
    )
}

@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T, reified R> ExpectedEncodedResult<T>.encodeNullableSerializableElement(
    elementName: String,
    structure: ExpectedEncodedResult<R>.() -> Unit,
) {
    val nested = ExpectedEncodedResult<R>().also(structure)
    val descriptor = descriptor<T>()
    add(
        TestEncodingElements(
            "encodeNullableSerializableElement",
            descriptor.serialName,
            descriptor.getElementIndex(elementName),
            elementName,
            nested,
        ),
    )
}

@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T, reified R> ExpectedEncodedResult<T>.encodeSerializableElement(
    elementName: String,
    structure: ExpectedEncodedResult<R>.() -> Unit,
) {
    val nested = ExpectedEncodedResult<R>().also(structure)
    add(
        TestEncodingElements(
            "encodeSerializableElement",
            descriptor<T>().serialName,
            descriptor<T>().getElementIndex(elementName),
            elementName,
            nested,
        ),
    )
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T, reified R> ExpectedEncodedResult<T>.anotherDescriptor(structure: ExpectedEncodedResult<R>.() -> Unit) {
    (this as ExpectedEncodedResult<R>).also(structure)
}

inline fun <reified T> ExpectedEncodedResult<T>.encodeNull() {
    add(
        TestEncodingElements(
            "encodeNull",
            null,
            null,
            null,
            null,
        ),
    )
}
