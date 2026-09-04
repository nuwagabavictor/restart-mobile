package com.victor.restart.core.enums

enum class AppLanguage(val code: String, val displayName: String) {
    SYSTEM_LANGUAGE("System_Language", "System Language"),
    ENGLISH("en", "English");

    companion object {
        fun fromCode(code: String): AppLanguage {
            return entries.find { it.code.equals(code, ignoreCase = true) } ?: ENGLISH
        }
    }

    override fun toString(): String {
        return displayName
    }
}