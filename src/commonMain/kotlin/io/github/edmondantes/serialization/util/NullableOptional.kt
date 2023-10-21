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
 * Represents a nullable optional value of type [T].
 *
 * @param T The type of the value.
 * @property isPresent Indicates whether the optional value is present.
 * @property isEmpty Indicates whether the optional value is empty.
 * @property valueOrNull Returns the value if it is present, otherwise returns null.
 * @property value Returns the value if it is present, otherwise throws an error.
 */
public class NullableOptional<out T> {
    private val valueInternal: T?

    public val isPresent: Boolean
    public val isEmpty: Boolean
        get() = !isPresent

    public val valueOrNull: T?
        get() =
            if (isPresent) {
                valueInternal
            } else {
                null
            }

    @Suppress("UNCHECKED_CAST")
    public val value: T
        get() {
            if (isPresent) {
                return valueInternal as T
            } else {
                error("Value was not set")
            }
        }

    public constructor() {
        isPresent = false
        valueInternal = null
    }

    public constructor(value: T) {
        isPresent = true
        valueInternal = value
    }

    override fun toString(): String =
        if (isEmpty) {
            "empty{}"
        } else {
            "{" + valueInternal.toString() + "}"
        }

    override fun hashCode(): Int =
        if (isPresent) {
            valueInternal.hashCode()
        } else {
            0
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NullableOptional<*>) return false

        if (isPresent != other.isPresent) return false
        if (valueInternal != other.valueInternal) return false

        return true
    }
}

public fun <T> T.nullableOptional(): NullableOptional<T> = NullableOptional(this)

public fun <T> nullableEmpty(): NullableOptional<T> = NullableOptional()

public inline fun <reified T> NullableOptional<*>.isInstance(): Boolean = if (isEmpty) false else value is T

@Suppress("UNCHECKED_CAST")
public inline fun <reified T> NullableOptional<*>.asInstance(): NullableOptional<T> =
    if (isInstance<T>()) this as NullableOptional<T> else nullableEmpty()

public inline fun <I, O> NullableOptional<I>.map(func: (I) -> O): NullableOptional<O> =
    if (isPresent) func(value).nullableOptional() else nullableEmpty()

public fun <T> NullableOptional<T>.or(defaultValue: T): T = valueOrNull ?: defaultValue

public inline fun <T> NullableOptional<T>.orElse(func: () -> T): T = valueOrNull ?: func()
