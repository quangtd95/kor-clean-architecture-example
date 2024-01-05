package io.qtd.fungpt.auth.core.repositories

interface PasswordCheckerPort {
    fun validatePassword(attempt: String, userPassword: String): Boolean
    fun encryptPassword(password: String): String
    fun generatePasswordWithDefault(): String
    fun validateFeasiblePassword(password: String): Boolean
}