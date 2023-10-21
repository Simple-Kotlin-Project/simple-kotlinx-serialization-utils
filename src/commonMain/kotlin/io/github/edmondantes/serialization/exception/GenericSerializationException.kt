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
package io.github.edmondantes.serialization.exception

import kotlinx.serialization.SerializationException

/**
 * A generic exception indicating the problem in serialization or deserialization process.
 */
public open class GenericSerializationException : SerializationException {
    /**
     * Creates an instance of [GenericSerializationException] without any details.
     */
    public constructor()

    /**
     * Creates an instance of [GenericSerializationException] with the specified detail [message].
     */
    public constructor(message: String?) : super(message)

    /**
     * Creates an instance of [GenericSerializationException] with the specified detail [message], and the given [cause].
     */
    public constructor(message: String?, cause: Throwable?) : super(message, cause)

    /**
     * Creates an instance of [GenericSerializationException] with the specified [cause].
     */
    public constructor(cause: Throwable?) : super(cause)
}
