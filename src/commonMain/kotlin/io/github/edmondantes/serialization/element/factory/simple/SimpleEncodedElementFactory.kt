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
package io.github.edmondantes.serialization.element.factory.simple

import io.github.edmondantes.serialization.element.EncodedElement

/**
 * Factory interface that take possibility create simple [EncodedElement]
 */
public interface SimpleEncodedElementFactory<T> {
    /**
     * Create simple [EncodedElement] with [value]
     */
    public fun value(value: T): EncodedElement<T>
}

/**
 * Create simple [EncodedElement] with [Byte] value
 */
public fun SimpleEncodedElementFactory<Byte>.byte(number: Number): EncodedElement<Byte> = value(number.toByte())

/**
 * Create simple [EncodedElement] with nullable [Byte] value
 */
public fun SimpleEncodedElementFactory<Byte?>.byteOrNull(number: Number?): EncodedElement<Byte?> = value(number?.toByte())

/**
 * Create simple [EncodedElement] with [Char] value
 */
public fun SimpleEncodedElementFactory<Char>.char(number: Number): EncodedElement<Char> = value(number.toInt().toChar())

/**
 * Create simple [EncodedElement] with nullable [Char] value
 */
public fun SimpleEncodedElementFactory<Char?>.charOrNull(number: Number?): EncodedElement<Char?> = value(number?.toInt()?.toChar())

/**
 * Create simple [EncodedElement] with [Short] value
 */
public fun SimpleEncodedElementFactory<Short>.short(number: Number): EncodedElement<Short> = value(number.toShort())

/**
 * Create simple [EncodedElement] with nullable [Short] value
 */
public fun SimpleEncodedElementFactory<Short?>.shortOrNull(number: Number?): EncodedElement<Short?> = value(number?.toShort())

/**
 * Create simple [EncodedElement] with [Int] value
 */
public fun SimpleEncodedElementFactory<Int>.int(number: Number): EncodedElement<Int> = value(number.toInt())

/**
 * Create simple [EncodedElement] with nullable [Int] value
 */
public fun SimpleEncodedElementFactory<Int?>.intOrNull(number: Number?): EncodedElement<Int?> = value(number?.toInt())

/**
 * Create simple [EncodedElement] with [Long] value
 */
public fun SimpleEncodedElementFactory<Long>.long(number: Number): EncodedElement<Long> = value(number.toLong())

/**
 * Create simple [EncodedElement] with nullable [Long] value
 */
public fun SimpleEncodedElementFactory<Long?>.longOrNull(number: Number?): EncodedElement<Long?> = value(number?.toLong())

/**
 * Create simple [EncodedElement] with [Float] value
 */
public fun SimpleEncodedElementFactory<Float>.float(number: Number): EncodedElement<Float> = value(number.toFloat())

/**
 * Create simple [EncodedElement] with nullable [Float] value
 */
public fun SimpleEncodedElementFactory<Float?>.floatOrNull(number: Number?): EncodedElement<Float?> = value(number?.toFloat())

/**
 * Create simple [EncodedElement] with [Double] value
 */
public fun SimpleEncodedElementFactory<Double>.double(number: Number): EncodedElement<Double> = value(number.toDouble())

/**
 * Create simple [EncodedElement] with nullable [Double] value
 */
public fun SimpleEncodedElementFactory<Double?>.doubleOrNull(number: Number?): EncodedElement<Double?> = value(number?.toDouble())
