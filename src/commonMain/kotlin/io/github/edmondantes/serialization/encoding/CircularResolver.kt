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

/**
 * This interface describe [Set]-like object that can add an object and check for its presence
 * @see Set
 * @see DefaultCircularResolver
 * @see CircularEncoder
 */
public interface CircularResolver {

    /**
     * Added [obj] to [CircularResolver]
     */
    public fun add(obj: Any)

    /**
     * Check for [obj] presence
     * @return True if [obj] was added to [CircularResolver], else false
     */
    public fun has(obj: Any): Boolean
}
