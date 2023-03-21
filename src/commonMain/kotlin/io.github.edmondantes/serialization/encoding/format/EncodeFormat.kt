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

import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.StringFormat

public sealed interface EncodeFormat {
    public val stringFormat: StringFormat?
    public val binaryFormat: BinaryFormat?
}

public class StringEncodeFormat(override val stringFormat: StringFormat) : EncodeFormat {
    override val binaryFormat: BinaryFormat? = null
}

public class BinaryEncodeFormat(override val binaryFormat: BinaryFormat) : EncodeFormat {
    override val stringFormat: StringFormat? = null
}

public fun stringFormat(stringFormat: StringFormat): EncodeFormat = StringEncodeFormat(stringFormat)
public fun binaryFormat(binaryFormat: BinaryFormat): EncodeFormat = BinaryEncodeFormat(binaryFormat)
