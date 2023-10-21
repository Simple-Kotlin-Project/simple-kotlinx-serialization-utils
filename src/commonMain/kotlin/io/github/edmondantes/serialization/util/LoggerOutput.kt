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

import io.github.edmondantes.serialization.encoding.logger.LoggerEncoder
import kotlinx.serialization.descriptors.SerialDescriptor

/**
 * Functional interface that get information for logs from [LoggerEncoder]
 * @see LoggerEncoder
 * @see DefaultLoggerOutput
 */
public fun interface LoggerOutput {
    /**
     * This function produce a logger message
     * @param methodName Name of method which was called
     * @param nestedLevel Level of nested structure
     * @param descriptor [SerialDescriptor]'s name of encoding object or parent [SerialDescriptor]'s name if [index] is not null (optional)
     * @param index Index of encoding field (optional)
     * @param value Encoding value (optional)
     */
    public fun log(
        methodName: String,
        nestedLevel: Int,
        descriptor: SerialDescriptor?,
        index: Int?,
        value: Any?,
    )
}
