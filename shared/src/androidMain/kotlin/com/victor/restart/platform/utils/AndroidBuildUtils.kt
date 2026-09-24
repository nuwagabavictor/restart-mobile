package com.victor.restart.platform.utils

import android.os.Build

/**
 * Determines if the current device's Android SDK version is below a specified version.
 *
 * This utility function compares the current device's Android SDK version with the
 * provided version parameter. It is useful for implementing version-specific behavior
 * in Android applications.
 *
 * @param version The Android SDK version to compare against (e.g., Build.VERSION_CODES.TIRAMISU)
 * @return true if the current device's SDK version is below the specified version,
 *         false otherwise
 */
fun isBuildVersionBelow(version: Int): Boolean = version > Build.VERSION.SDK_INT