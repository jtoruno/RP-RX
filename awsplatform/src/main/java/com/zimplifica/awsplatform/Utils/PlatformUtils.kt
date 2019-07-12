package com.zimplifica.awsplatform.Utils

import android.util.Base64
import android.util.Log
import com.zimplifica.awsplatform.BuildConfig
import java.security.*
import java.security.spec.RSAPublicKeySpec
import javax.crypto.Cipher

object PlatformUtils {

    //Implementation without PKCS! limiter, delete delimiter or replace if want to use this method
    //-----BEGIN RSA PUBLIC KEY-----
    //-----END RSA PUBLIC KEY-----
    fun encrypt(data : String) : String?{
        val key = BuildConfig.PUBLIC_KEY
        Log.e("key",key)
        val encoding = Base64.decode(key,Base64.DEFAULT)
        val generatePublic = getKey(key)
        //val generatePublic = getKey(key)

        val cipher = Cipher.getInstance("RSA/NONE/PKCS1Padding","BC")

        cipher.init(Cipher.ENCRYPT_MODE,generatePublic)
        val encryptedBytes = cipher.doFinal(data.toByteArray())
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }
    private fun getKey(data : String): PublicKey{
        val decoded = Base64.decode(data,Base64.DEFAULT)
        val pkcs1PublicKey = org.bouncycastle.asn1.pkcs.RSAPublicKey.getInstance(decoded)
        val modulus = pkcs1PublicKey.modulus
        val publicExponent = pkcs1PublicKey.publicExponent
        val keySpec = RSAPublicKeySpec(modulus,publicExponent)
        val kf = KeyFactory.getInstance("RSA")
        val generatedPublic = kf.generatePublic(keySpec)
        return generatedPublic
    }
}





