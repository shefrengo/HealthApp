package com.shefrengo.health.utils.extentions

import android.graphics.Color
import android.text.Html
import android.text.Spanned
import android.text.TextUtils
import android.util.Base64
import com.shefrengo.health.Constants



import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.net.URLDecoder
import java.net.URLEncoder
import java.security.MessageDigest
import java.util.*
import java.util.regex.Pattern
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

fun String.checkIsEmpty(): Boolean = isNullOrEmpty() || "" == this || this.equals("null", ignoreCase = true)

fun String.isValidIPAddress(): Boolean {
    val compile = Pattern.compile("^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$")
    val pattern = compile
    return compile.matcher(this).find()
}

fun String.toColorInt(): Int = Color.parseColor(this)

fun String.toDate(): Date = Constants.DATE_FORMAT.parse(this)

fun String.getHtmlString(): Spanned = Html.fromHtml(this)

fun String.htmlEncode(): String = TextUtils.htmlEncode(this)

fun String.urlEncode(encoding: String = "UTF-8"): String = URLEncoder.encode(this, encoding)

fun String.urlDecode(encoding: String = "UTF-8"): String = URLDecoder.decode(this, encoding)

fun String.toCamelCase(): String {
    var stringBuilder = StringBuilder()
    try {
        val toLowerCase = this.toLowerCase()
        if (toLowerCase.isNotEmpty()) {
            for (toProperCase in toLowerCase.trim { it <= ' ' }.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
                stringBuilder.append(" ").append(toProperCase(toProperCase))
            }
        }
    } catch (e: NullPointerException) {
        stringBuilder = StringBuilder()
    }

    return stringBuilder.toString()
}

fun toProperCase(str: String): String {
    return try {
        if (str.isNotEmpty()) str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase() else ""
    } catch (e: NullPointerException) {
        ""
    }
}

fun String.equalsZero(): Boolean {
    return this == "0.00"
}


/**
 * returns the md5 of the String
 */
fun String.md5() = encrypt(this, "MD5")

/**
 * return SHA1 of the String
 */
fun String.sha1() = encrypt(this, "SHA-1")

/**
 * encode The String to Binary
 */
fun String.encodeToBinary(): String {
    val stringBuilder = StringBuilder()
    toCharArray().forEach {
        stringBuilder.append(Integer.toBinaryString(it.toInt()))
        stringBuilder.append(" ")
    }
    return stringBuilder.toString()
}

/**
 * Decode the String from binary
 */
fun String.deCodeToBinary(): String {
    val stringBuilder = StringBuilder()
    split(" ").forEach {
        stringBuilder.append(Integer.parseInt(it.replace(" ", ""), 2))
    }
    return stringBuilder.toString()
}

/**
 * Encrypt String to AES with the specific Key
 */
fun String.encryptAES(key: String): String {
    var crypted: ByteArray? = null
    try {
        val skey = SecretKeySpec(key.toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, skey)
        crypted = cipher.doFinal(toByteArray())
    } catch (e: Exception) {
        println(e.toString())
    }
    return String(Base64.encode(crypted, Base64.DEFAULT))
}

/**
 * Decrypt String to AES with the specific Key
 */
fun String.decryptAES(key: String): String {
    var output: ByteArray? = null
    try {
        val skey = SecretKeySpec(key.toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, skey)
        output = cipher.doFinal(Base64.decode(this, Base64.DEFAULT))
    } catch (e: Exception) {
        println(e.toString())
    }
    return output?.let { String(it) } ?: ""
}

private fun encrypt(string: String?, type: String): String = bytes2Hex(MessageDigest.getInstance(type).digest(string!!.toByteArray()))

internal fun bytes2Hex(bts: ByteArray): String {
    var des = ""
    var tmp: String
    for (i in bts.indices) {
        tmp = Integer.toHexString(bts[i].toInt() and 0xFF)
        if (tmp.length == 1) {
            des += "0"
        }
        des += tmp
    }
    return des
}

fun String.isJsonValid(): Boolean {
    try {
        JSONObject(this)
    } catch (ex: JSONException) {
        try {
            JSONArray(this)
        } catch (ex1: JSONException) {
            return false
        }
    }
    return true
}

fun String.isValidColor(): Boolean {
    return (contains("#") && length >= 6)
}
fun String.currencyFormat(code: String = "INR"): String {
    return if (this.checkIsEmpty()) "" else {
        "${getDefaultCurrency().getHtmlString()}$this"
    }
    /*return when (code) {
        "USD" -> "$$this"
        "INR" -> "₹$this"
        else -> "₹$this"
    }*/
}
