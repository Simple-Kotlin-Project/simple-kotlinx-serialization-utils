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

import env.Env
import io.github.edmondantes.serialization.decoding.logger.LoggerDecoder
import io.github.edmondantes.serialization.encoding.logger.LoggerEncoder
import io.github.edmondantes.serialization.util.AppendableWithIndent
import io.github.edmondantes.serialization.util.DelegateAppendableWithIndent
import io.github.edmondantes.serialization.util.EmptyLoggerOutput
import io.github.edmondantes.serialization.util.serialize
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

fun loggerEncoder(): LoggerEncoder =
    if (Env.isEnableLogging) {
        LoggerEncoder()
    } else {
        LoggerEncoder(EmptyLoggerOutput)
    }

fun Decoder.loggerDecoder(): LoggerDecoder =
    if (Env.isEnableLogging) {
        LoggerDecoder(this)
    } else {
        LoggerDecoder(this, EmptyLoggerOutput)
    }

fun log(msg: String) {
    if (Env.isEnableLogging) {
        println(msg)
    }
}

inline fun <reified T> T.serializeWithLog(
    vararg encoders: Encoder,
    block: Encoder.() -> Encoder = { this },
) = serializeWithLog(encoders.toList(), block)

inline fun <reified T> T.serializeWithLog(
    encoders: List<Encoder>,
    block: Encoder.() -> Encoder = { this },
) {
    log("\n------------------------")
    serialize(encoders + loggerEncoder(), block)
    log("------------------------\n")
}

fun Decoder.withLog(): Decoder = this.loggerDecoder()
