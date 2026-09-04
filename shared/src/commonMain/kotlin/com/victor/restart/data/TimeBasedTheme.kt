package com.victor.restart.data

import kotlinx.serialization.Serializable

@Serializable
data class TimeBasedTheme(
    val hourStart: Int,
    val hourEnd: Int,
    val timeStart: Int,
    val timeEnd: Int,
)
