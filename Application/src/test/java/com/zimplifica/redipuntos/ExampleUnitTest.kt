package com.zimplifica.redipuntos

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun n(){
        print(transformUserId("116500454"))
    }

    private fun transformUserId(userIdString : String) : String {
        var user = userIdString
        user = userIdString.substring(0,1) + "-" + userIdString.substring(1,5) + "-" + userIdString.substring(5,userIdString.length)
        return user
    }
}
