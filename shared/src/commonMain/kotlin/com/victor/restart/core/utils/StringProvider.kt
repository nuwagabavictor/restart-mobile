package com.victor.restart.core.utils

import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString

interface StringProvider {
    suspend fun get(resource: StringResource, vararg formatArgs: Any = emptyArray()): String
}

class DefaultStringProvider : StringProvider {
    override suspend fun get(resource: StringResource, vararg formatArgs: Any): String {
        return getString(resource, *formatArgs)
    }
}