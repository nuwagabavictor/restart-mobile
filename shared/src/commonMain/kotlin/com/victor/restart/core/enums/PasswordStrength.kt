package com.victor.restart.core.enums

enum class PasswordStrength(val value: Int) {
    WEAK(1),
    MEDIUM(2),
    STRONG(3);

    companion object{
        fun fromInt(value: Int): PasswordStrength{
            return entries.find { it.value == value } ?: WEAK;
        }
        fun calculatePasswordStrength(password: String): PasswordStrength {
            if (password.length < 8) return PasswordStrength.WEAK

            var score = 0
            if (password.any { it.isDigit() }) score++
            if (password.any { it.isUpperCase() }) score++
            if (password.any { it.isLowerCase() }) score++
            if (password.any { !it.isLetterOrDigit() }) score++

            return when {
                score >= 3 && password.length >= 12 -> PasswordStrength.STRONG
                score >= 2 -> PasswordStrength.MEDIUM
                else -> PasswordStrength.WEAK
            }
        }
    }
}