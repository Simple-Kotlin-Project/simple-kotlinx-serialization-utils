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
@file:Suppress("unused")

package io.github.edmondantes.serialization.encoding.format

import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.StringFormat
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule

public class FormatEncoder public constructor(
    private val defaultEncoder: Encoder,
    private val formats: Map<String, EncodeFormat>,
) : Encoder by defaultEncoder {

    override val serializersModule: SerializersModule
        get() = defaultEncoder.serializersModule

    init {
        if (formats.containsKey("")) {
            error("Empty string can not be id for format")
        }
    }

    public constructor(defaultEncoder: Encoder, formats: List<Pair<String, EncodeFormat>>) : this(
        defaultEncoder,
        formats.associate { it },
    )

    public constructor(defaultEncoder: Encoder, vararg formats: Pair<String, EncodeFormat>) : this(
        defaultEncoder,
        formats.associate { it },
    )

    public constructor(defaultEncoder: Encoder, formatId: String, format: StringFormat) : this(
        defaultEncoder,
        mapOf(formatId to stringFormat(format)),
    )

    public constructor(defaultEncoder: Encoder, formatId: String, format: BinaryFormat) : this(
        defaultEncoder,
        mapOf(formatId to binaryFormat(format)),
    )

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        val defaultCompositeEncoder = defaultEncoder.beginStructure(descriptor)

        return FormatCompositeEncoder(defaultCompositeEncoder, formats)
    }

    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) {
        TODO("Not yet implemented")
    }

    override fun encodeInline(descriptor: SerialDescriptor): Encoder {
        TODO("Not yet implemented")
    }
}

public fun Encoder.supportFormats(formats: Map<String, EncodeFormat>): Encoder = FormatEncoder(this, formats)
public fun Encoder.supportFormats(formats: List<Pair<String, EncodeFormat>>): Encoder =
    FormatEncoder(this, formats)

public fun Encoder.supportFormats(vararg formats: Pair<String, EncodeFormat>): Encoder =
    FormatEncoder(this, *formats)

public fun Encoder.supportStringFormats(vararg formats: Pair<String, StringFormat>): Encoder =
    FormatEncoder(this, formats.map { it.first to stringFormat(it.second) })

public fun Encoder.supportBinaryFormats(vararg formats: Pair<String, BinaryFormat>): Encoder =
    FormatEncoder(this, formats.map { it.first to binaryFormat(it.second) })

public fun Encoder.supportFormat(formatId: String, format: StringFormat): Encoder =
    FormatEncoder(this, formatId, format)

public fun Encoder.supportFormat(formatId: String, format: BinaryFormat): Encoder =
    FormatEncoder(this, formatId, format)
