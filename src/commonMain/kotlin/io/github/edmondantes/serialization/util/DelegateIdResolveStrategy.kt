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

import io.github.edmondantes.serialization.encoding.UniqueCompositeEncoder
import io.github.edmondantes.serialization.encoding.UniqueEncoder
import io.github.edmondantes.serialization.encoding.delegate.DelegateCompositeEncoder
import io.github.edmondantes.serialization.encoding.delegate.DelegateEncoder
import io.github.edmondantes.serialization.util.DelegateIdResolveStrategy.DELEGATE

/**
 * Resolve id strategy for [DelegateEncoder] and [DelegateCompositeEncoder]
 *
 * Strategy change priority of these elements:
 * * Delegate's id (it must implement [UniqueEncoder] or [UniqueCompositeEncoder])
 * * Id from constructor of [DelegateEncoder] or [DelegateCompositeEncoder]
 * * Default id ([DelegateEncoder.DEFAULT_ID] or [DelegateCompositeEncoder.DEFAULT_ID])
 *
 * Default strategy is [DELEGATE]
 */
public enum class DelegateIdResolveStrategy(private vararg val order: IdClass) {
    /**
     * This strategy try to set id in this order:
     * 1. Delegate's id
     * 2. Id from constructor
     * 3. Default id
     */
    DELEGATE(IdClass.DELEGATE, IdClass.CONSTRUCTOR, IdClass.DEFAULT),

    /**
     * This strategy try to set id in this order:
     * 1. Id from constructor
     * 2. Delegate's id
     * 3. Default id
     */
    CURRENT(IdClass.CONSTRUCTOR, IdClass.DELEGATE, IdClass.DEFAULT),
    ;

    public fun resolveId(
        delegateId: () -> String?,
        constructId: () -> String?,
        defaultId: String,
    ): String {
        order.forEach {
            val value =
                when (it) {
                    IdClass.DELEGATE -> delegateId()
                    IdClass.CONSTRUCTOR -> constructId()
                    IdClass.DEFAULT -> defaultId
                }
            if (value != null) {
                return value
            }
        }

        return defaultId
    }

    private enum class IdClass {
        DELEGATE,
        CONSTRUCTOR,
        DEFAULT,
    }
}
