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

}
