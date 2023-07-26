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
import io.github.edmondantes.serialization.encoding.BroadcastEncoder
import io.github.edmondantes.serialization.encoding.LoggerEncoder
import io.github.edmondantes.serialization.util.AppendableWithIndent
import io.github.edmondantes.serialization.util.DelegateAppendableWithIndent
import io.github.edmondantes.serialization.util.serialize
import kotlinx.serialization.encoding.Encoder

fun loggerEncoder(): LoggerEncoder =
    if (Env.isEnableLogging) {
        LoggerEncoder()
    } else {
        LoggerEncoder({})
    }

fun log(msg: String) {
    if (Env.isEnableLogging) {
        println(msg)
    }
}

fun log(block: AppendableWithIndent.() -> Unit) {
    if (Env.isEnableLogging) {
        val builder = StringBuilder()
        DelegateAppendableWithIndent(builder).block()
        println(builder.toString())
    }
}

inline fun <reified T> T.serializeWithLog(vararg encoders: Encoder, block: BroadcastEncoder.() -> Encoder = { this }) =
    serializeWithLog(encoders.toList(), block)

inline fun <reified T> T.serializeWithLog(encoders: List<Encoder>, block: BroadcastEncoder.() -> Encoder = { this }) =
    serialize(encoders + loggerEncoder(), block)
