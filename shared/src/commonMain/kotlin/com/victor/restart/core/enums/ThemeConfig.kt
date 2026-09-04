package com.victor.restart.core.enums

enum class ThemeConfig(val osValue: Int, val configName: String) {
    FOLLOW_SYSTEM(-1,"Follow System"),
    LIGHT(1,"Light"),
    DARK(2, "Dark"),
    BASED_ON_TIME(3,"Based on Time");

    companion object{
        fun fromString(configName: String): ThemeConfig{
            return entries.find { it.configName.equals(configName, ignoreCase = true) } ?: FOLLOW_SYSTEM
        }
    }
}