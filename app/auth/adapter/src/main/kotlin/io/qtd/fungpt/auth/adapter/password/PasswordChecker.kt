package io.qtd.fungpt.auth.adapter.password

import io.qtd.fungpt.auth.core.repositories.PasswordCheckerPort
import io.qtd.fungpt.common.core.exception.WrongRequestException
import io.qtd.fungpt.common.core.extension.unless
import org.mindrot.jbcrypt.BCrypt
import java.security.SecureRandom
import kotlin.random.asKotlinRandom


object PasswordChecker : PasswordCheckerPort {
    private val letters = 'a'..'z'
    private val uppercaseLetters = 'A'..'Z'
    private val numbers = '0'..'9'
    private const val specials: String = "@#=+!£\$%&?"

    override fun validatePassword(attempt: String, userPassword: String): Boolean {
        return BCrypt.checkpw(attempt, userPassword)
    }

    override fun encryptPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }

    override fun validateFeasiblePassword(password: String): Boolean {
        val hasLetter = password.any { it in letters }
        val hasUppercase = password.any { it in uppercaseLetters }
        val hasNumber = password.any { it in numbers }
        val hasSpecial = password.any { it in specials }

        unless(hasLetter) {
            throw WrongRequestException("Password must contain at least one letter")
        }
        unless(hasUppercase) {
            throw WrongRequestException("Password must contain at least one uppercase letter")
        }
        unless(hasNumber) {
            throw WrongRequestException("Password must contain at least one number")
        }
        unless(hasSpecial) {
            throw WrongRequestException("Password must contain at least one special character")
        }
        if (password.length < 6) {
            throw WrongRequestException("Password must be at least 6 characters long")
        }
        return true
    }

    override fun generatePasswordWithDefault() = generatePassword()

    fun generatePassword(
        isWithLetters: Boolean = true,
        isWithUppercase: Boolean = true,
        isWithNumbers: Boolean = true,
        isWithSpecial: Boolean = true,
        length: Int = 6
    ): String {
        var chars = ""

        if (isWithLetters) {
            chars += letters
        }
        if (isWithUppercase) {
            chars += uppercaseLetters
        }
        if (isWithNumbers) {
            chars += numbers
        }
        if (isWithSpecial) {
            chars += specials
        }

        val rnd = SecureRandom.getInstance("SHA1PRNG").asKotlinRandom()
        return List(length) { chars.random(rnd) }.joinToString("")
    }

}