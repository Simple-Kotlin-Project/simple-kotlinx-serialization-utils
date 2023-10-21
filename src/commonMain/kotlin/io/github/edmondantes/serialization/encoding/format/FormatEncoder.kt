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
package io.github.edmondantes.serialization.encoding.format

import io.github.edmondantes.serialization.annotation.SerializationFormat
import io.github.edmondantes.serialization.encoding.delegate.DelegateEncoder
import io.github.edmondantes.serialization.util.DelegateIdResolveStrategy
import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.SerialFormat
import kotlinx.serialization.StringFormat
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

/**
 * This [Encoder] helps to serializer properties as another formats.
 * For example, you can start serialization of json, and serialize one property as xml string.
 * @param delegate original format [Encoder]
 * @param formats [Map] when keys is format's ids and values is formats.
 * @see SerializationFormat
 */
public class FormatEncoder public constructor(
    delegate: Encoder,
    private val formats: Map<String, SerialFormat>,
    serializersModule: SerializersModule = EmptySerializersModule(),
) : DelegateEncoder(
        delegate = delegate,
        currentId = DEFAULT_ID,
        idResolveStrategy = DelegateIdResolveStrategy.DELEGATE,
        serializersModule = serializersModule,
    ) {
    init {
        if (formats.containsKey("")) {
            error("Empty string can not be id for format")
        }
    }

    public constructor(delegate: Encoder, formats: List<Pair<String, SerialFormat>>) : this(
        delegate,
        formats.associate { it },
    )

    public constructor(delegate: Encoder, vararg formats: Pair<String, SerialFormat>) : this(
        delegate,
        formats.associate { it },
    )

    public constructor(delegate: Encoder, formatId: String, format: StringFormat) : this(
        delegate,
        mapOf(formatId to format),
    )

    public constructor(delegate: Encoder, formatId: String, format: BinaryFormat) : this(
        delegate,
        mapOf(formatId to format),
    )

    override fun transformerEncoder(encoder: Encoder): Encoder = FormatEncoder(encoder, formats, serializersModule)

    override fun transformerCompositeEncoder(encoder: CompositeEncoder): CompositeEncoder =
        FormatCompositeEncoder(encoder, formats, serializersModule)

    public companion object {
        public const val DEFAULT_ID: String = "io.github.edmondantes.serialization.encoding.format.FormatEncoder"
    }
}

/**
 * Add [formats] supports to [Encoder]
 */
public fun Encoder.supportFormats(formats: Map<String, SerialFormat>): Encoder = FormatEncoder(this, formats)

/**
 * Add [formats] supports to [Encoder]
 */
public fun Encoder.supportFormats(formats: List<Pair<String, SerialFormat>>): Encoder = FormatEncoder(this, formats)

/**
 * Add [formats] supports to [Encoder]
 */
public fun Encoder.supportFormats(vararg formats: Pair<String, SerialFormat>): Encoder = FormatEncoder(this, *formats)

/**
 * Add string [formats] supports to [Encoder]
 */
public fun Encoder.supportStringFormats(vararg formats: Pair<String, StringFormat>): Encoder =
    FormatEncoder(this, formats.map { it.first to it.second })

/**
 * Add binary [formats] supports to [Encoder]
 */
public fun Encoder.supportBinaryFormats(vararg formats: Pair<String, BinaryFormat>): Encoder =
    FormatEncoder(this, formats.map { it.first to it.second })

/**
 * Add [format] supports to [Encoder]
 * @param formatId id of supports [format]
 */
public fun Encoder.supportFormat(
    formatId: String,
    format: StringFormat,
): Encoder = FormatEncoder(this, formatId, format)

/**
 * Add [format] supports to [Encoder]
 * @param formatId id of supports [format]
 */
public fun Encoder.supportFormat(
    formatId: String,
    format: BinaryFormat,
): Encoder = FormatEncoder(this, formatId, format)
