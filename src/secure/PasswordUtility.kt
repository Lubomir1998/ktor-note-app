package com.example.secure

import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils
import java.security.SecureRandom

fun getHashWithSalt(passwordToHash: String, saltLength: Int = 32): String {
    val salt = SecureRandom.getInstance("SHA1PRNG").generateSeed(saltLength)
    val saltAsHex = Hex.encodeHexString(salt)
    val hash = DigestUtils.sha256Hex("$saltAsHex$passwordToHash")
    return "$saltAsHex:$hash"
}

fun checkHashForPassword(password: String, hashWithSalt: String): Boolean {
    val hashAndSalt = hashWithSalt.split(":")
    val salt = hashAndSalt[0]
    val hash = hashAndSalt[1]
    val passwordHash = DigestUtils.sha256Hex("$salt$password")
    return hash == passwordHash
}