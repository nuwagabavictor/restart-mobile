package com.victor.restart.core.enums

enum class LanguageConfig(
    val localName: String?,
    val languageName: String,
) {
    DEFAULT(
        localName = null,
        languageName = "System (Default)",
    ),
    ENGLISH(
        localName = "en",
        languageName = "English (English)",
    );

    companion object{
        fun fromString(languageName: String): LanguageConfig{
            return entries.find { it.languageName.equals(languageName, ignoreCase = true) } ?:DEFAULT
        }
    }
}