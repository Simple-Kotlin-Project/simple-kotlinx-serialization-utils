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

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor

/**
 * [DefaultLoggerOutput] is an implementation of the [LoggerOutput] interface that logs messages using a provided [logger] function.
 *
 * @property logger The function used for logging messages. Defaults to [println] if not provided.
 */
@OptIn(ExperimentalSerializationApi::class)
public open class DefaultLoggerOutput(protected val logger: (String) -> Unit = ::println) : LoggerOutput {
    override fun log(
        methodName: String,
        nestedLevel: Int,
        descriptor: SerialDescriptor?,
        index: Int?,
        value: Any?,
    ) {
        logger(
            buildString {
                append("[LoggerEncoder][level=$nestedLevel] call method ").append(methodName)

                if (descriptor != null) {
                    append(" for descriptor ")
                    append(descriptor.serialName)
                    if (index != null) {
                        append("::").append(descriptor.getElementName(index))
                    }
                }

                if (value != null) {
                    append(" with value = ").append(value)
                }
            },
        )
    }
}
