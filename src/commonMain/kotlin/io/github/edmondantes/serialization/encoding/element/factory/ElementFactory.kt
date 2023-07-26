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
package io.github.edmondantes.serialization.encoding.element.factory

import io.github.edmondantes.serialization.encoding.element.EncodingElement

public interface ElementFactory {

    public fun structure(block: StructureEncodingElementBuilder.() -> Unit): EncodingElement<*>

    public fun collection(block: CollectionEncodingElementBuilder.() -> Unit): EncodingElement<*>

    public fun value(value: Any?): EncodingElement<*>
}

public fun ElementFactory.byte(number: Number?): EncodingElement<*> =
    value(number?.toByte())

public fun ElementFactory.char(number: Number?): EncodingElement<*> =
    value(number?.toChar())

public fun ElementFactory.short(number: Number?): EncodingElement<*> =
    value(number?.toShort())

public fun ElementFactory.int(number: Number?): EncodingElement<*> =
    value(number?.toInt())

public fun ElementFactory.long(number: Number?): EncodingElement<*> =
    value(number?.toLong())

public fun ElementFactory.float(number: Number?): EncodingElement<*> =
    value(number?.toFloat())

public fun ElementFactory.double(number: Number?): EncodingElement<*> =
    value(number?.toDouble())
