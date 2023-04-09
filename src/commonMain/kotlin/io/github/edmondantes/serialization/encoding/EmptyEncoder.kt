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
package io.github.edmondantes.serialization.encoding

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

/**
 * [Encoder] which doing nothing
 * @see Encoder
 * @see UniqueEncoder
 */
public object EmptyEncoder : UniqueEncoder {

    override val id: String = "io.github.edmondantes.serialization.encoding.EmptyEncoder"

    override val serializersModule: SerializersModule = EmptySerializersModule()

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder = EmptyCompositeEncoder
    override fun encodeBoolean(value: Boolean) {}
    override fun encodeByte(value: Byte) {}
    override fun encodeChar(value: Char) {}
    override fun encodeDouble(value: Double) {}
    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) {}
    override fun encodeFloat(value: Float) {}
    override fun encodeInline(descriptor: SerialDescriptor): Encoder = EmptyEncoder
    override fun encodeInt(value: Int) {}
    override fun encodeLong(value: Long) {}
    override fun encodeShort(value: Short) {}
    override fun encodeString(value: String) {}

    @ExperimentalSerializationApi
    override fun encodeNull() {}
}
