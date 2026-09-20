package com.victor.restart.core.enums


enum class LanguageConfig(
    val locale: String?,
    val languageName: String,
) {
    DEFAULT(
        locale = null,
        languageName = "System (Default)",
    ),

    ENGLISH(
        locale = "en",
        languageName = "English",
    ),

    FRENCH(
        locale = "fr",
        languageName = "French",
    );

    companion object {
        fun fromString(languageName: String): LanguageConfig {
            return entries.find {
                it.languageName.equals(languageName, ignoreCase = true)
            } ?: DEFAULT
        }
    }
}