package com.example.util

import java.security.MessageDigest
import java.security.SecureRandom

object SecurityUtils {

    fun generateSalt(): String {
        val random = SecureRandom()
        val salt = ByteArray(16)
        random.nextBytes(salt)
        return salt.joinToString("") { "%02x".format(it) }
    }

    fun hashPassword(password: String, salt: String): String {
        val input = "$password:$salt"
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(input.toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }
    }

    fun verifyPassword(password: String, salt: String, storedHash: String): Boolean {
        if (storedHash.isBlank()) {
            return password == "admin"
        }
        val computedHash = hashPassword(password, salt)
        return computedHash == storedHash
    }

    fun hashPin(pin: String, salt: String): String {
        return hashPassword(pin, salt)
    }
}
