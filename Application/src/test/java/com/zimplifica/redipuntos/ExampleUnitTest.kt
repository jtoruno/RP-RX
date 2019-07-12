package com.zimplifica.redipuntos

import android.util.Base64
import org.bouncycastle.asn1.pkcs.RSAPublicKey
import org.junit.Test

import org.junit.Assert.*
import java.nio.charset.StandardCharsets
import java.security.Key
import java.security.KeyFactory
import java.security.NoSuchAlgorithmException
import java.security.PublicKey
import java.security.spec.InvalidKeySpecException
import java.security.spec.RSAPublicKeySpec
import java.security.spec.X509EncodedKeySpec


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun rsaKey(){
        val originalKey = "MIICCgKCAgEAot1iRTuueRnWRsMVjwbcmrsjnHTmc/miER4Wbr6+fSXynQ2jdyzZEyn6UW+Co6r9QL6W09rJZV6t5beYzslekPIh6UxbdLWKocQm3vjRbuKXJLZWVgaPwgJjwHjeTQvR2s0S6b5xql2tbBhSB0kKErllvGh+gR3t/GMH4t9hP6NbBBDhcBd/0l5/fhC5P9BCaXDpJvCBX0RaspUYMjO0Pn0k1yQv6IwaQZ5q8fGtToT33+8shh/5F12QJzp0UZnqeauBqWcaFeps2Z+2NFeEGjKKYFvArxTAVHDV040RaS8bybNo55xNpU5rbmM8DQOGiVbBRpaSqqil8I36Gp8ISDop18cjcnthfIjdqkjTIfoVzX/xmwKOyrLDDqgkyCJbO5ZtAu8n+irwY5eq38fONX8Fo6YSuX6004TMj8JbRP6ytm7yHizG6CEojF/hAjhSxdiuHhSzV2tuuJbpqOgpXEAp3+HoMsmKchsBmep87LKM/8rndZGZ16h+JaFVvUeiX6yNItiICeLkTsDhp5PObXQG1qui65mVe/QOx4M0FGRdiwiDb67nVP8naxvwRgwavZPPT/sMH9My/A/WuUH6cfQz0BzPhEBwQVr4FsN7AOFL16L58RmLtyCKyiKA9C/9X04bkflT1brLP6/uPqNMjVYOjVRGEe00F9TSTrZnLhUCAwEAAQ=="
        println(n(originalKey))
        println(getKey(originalKey))
    }

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun dateLong(){
        val example = "2019-06-13T00:00-06:00"
        val final = formatLongDate(example)
        println(final)

    }

    @Test
    fun testPartition(){
        val hour = "20:30"
        val partition = hour.split(":")
        assertEquals("20",partition.first())
    }

    @Test
    fun n(){
        print(transformUserId("116500454"))
        print("\n")
        print(formatAmex("37775"))
        print("\n")
        print(formatVisaOrMasterCard("4611310142137015"))
        print("\n")
        print(formatVisaOrMasterCard("4611310142"))
    }

    private fun transformUserId(userIdString : String) : String {
        var user = userIdString
        user = userIdString.substring(0,1) + "-" + userIdString.substring(1,5) + "-" + userIdString.substring(5,userIdString.length)
        return user
    }

    private fun formatAmex(creditCard : String): String{
        if(creditCard.isEmpty()){return ""}
        var number = creditCard
        if(number.length > 10){
            number = number.substring(0,4)+" "+number.substring(4,10)+" "+number.substring(10,number.length)
        }
        else if(number.length > 4){
            number = number.substring(0,4)+" "+number.substring(4,number.length)
        }
        return number

    }

    private fun formatVisaOrMasterCard(creditCard : String): String{
        if(creditCard.isEmpty()){return ""}
        var number = creditCard
        if(number.length > 12){
            number = number.substring(0,4)+" "+number.substring(4,8)+" "+number.substring(8,12)+" "+number.substring(12,number.length)
        }
        else if(number.length > 8){
            number = number.substring(0,4)+" "+number.substring(4,8)+" "+number.substring(8,number.length)
        }
        else if (number.length > 4){
            number = number.substring(0,4)+" "+number.substring(4,number.length)
        }
        return number
    }

    private fun formatLongDate(dateValue : String) : String{
        val hourString = dateValue.split("T").last()
        val date = dateValue.split("T").first()
        val final = date.replace("-","/")
        return "$final $hourString"
    }

    fun getKey(key: String): RSAPublicKey {
        return RSAPublicKey.getInstance(java.util.Base64.getDecoder().decode(key))
    }


    fun n(data : String): Key{
        val decoded = java.util.Base64.getDecoder().decode(data)
        val pkcs1PublicKey = RSAPublicKey.getInstance(decoded)
        val modulus = pkcs1PublicKey.modulus
        val publicExponent = pkcs1PublicKey.publicExponent
        val keySpec = RSAPublicKeySpec(modulus,publicExponent)
        val kf = KeyFactory.getInstance("RSA")
        val generatedPublic = kf.generatePublic(keySpec)
        return generatedPublic
    }

}
