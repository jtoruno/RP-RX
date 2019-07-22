package com.zimplifica.redipuntos.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.appcompat.widget.AppCompatEditText
import android.util.AttributeSet
import android.util.Log
import android.widget.EditText
import com.zimplifica.redipuntos.R

class CardEditText : AppCompatEditText {
    private var currentDraw : Drawable= resources.getDrawable(R.drawable.creditcard)

    internal fun Char.isPlaceHolder() : Boolean = this == '#'

    enum class Issuer {
        AMEX, MASTERCARD, VISA, DISCOVER, UNKNOWN
    }

    private var issuer = Issuer.UNKNOWN
    private var selfChange : Boolean = false

    constructor(context: Context) : super(context){

    }
    constructor(context: Context, attributeSet: AttributeSet) : super(context,attributeSet){

    }

    constructor(context: Context,attributeSet: AttributeSet,defStyleAttr : Int) : super(context,attributeSet,defStyleAttr){

    }
    /*
    constructor(context: Context,attributeSet: AttributeSet,defStyleAttr : Int,defStyleRes: Int) :
            super(context,attributeSet,defStyleAttr,defStyleRes)*/

    private fun init(){
        var issuer = Issuer.UNKNOWN
        currentDraw = resources.getDrawable(R.drawable.creditcard)
        val value = text.toString()
        if(value.startsWith("4")){
            issuer = Issuer.VISA
            currentDraw = resources.getDrawable(R.drawable.visa)
        }
        if((value.startsWith("34"))|| (value.startsWith("37"))){
            issuer = Issuer.AMEX
            currentDraw = resources.getDrawable(R.drawable.amex)
        }
        if ((value.startsWith("51")) || (value.startsWith("52")) || (value.startsWith("53")) || (value.startsWith("54")) || (value.startsWith("55"))){
            issuer = Issuer.MASTERCARD
            currentDraw = resources.getDrawable(R.drawable.mastercard)
        }
        if((value.startsWith("65")) || (value.startsWith("644")) || (value.startsWith("6011"))){
            issuer = Issuer.DISCOVER
            currentDraw = resources.getDrawable(R.drawable.discover)
        }
        this.issuer = issuer
    }

    override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
        init()
        if(text.isNullOrEmpty() || selfChange) return

        format(text)

        setCursorPosition(start,lengthBefore,lengthAfter)
    }

    private fun format(source : CharSequence?){
        if (source.isNullOrEmpty()) return
        selfChange = true
        val text = source.toString().replace(" ","")
        val formatted = formatCardNumber(text)
        setText(formatted)
        selfChange = false

    }

    private fun formatCardNumber(cardNumber : String) : String{
        when(issuer){
            Issuer.AMEX -> {
                return formatAmex(cardNumber)
            }
            Issuer.MASTERCARD, Issuer.VISA -> {
                return formatVisaOrMasterCard(cardNumber)
            }
            else->{
                return cardNumber
            }
        }
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

    private fun setCursorPosition(start: Int, lengthBefore: Int, lengthAfter: Int) {
        if (text.isNullOrEmpty()) return

        val end = text!!.length
        //Log.e("OnCHange",start.toString() +" ENd :"+end.toString() +" Before :"+lengthBefore.toString()+"  Afer :"+lengthAfter.toString())
        val cursor = when {
            lengthBefore > lengthAfter -> start
            lengthAfter > 1 -> end
            //start < end -> findNextPlaceHolderPosition(start, end)
            start < end -> end
            else -> end
        }
        setSelection(end)
    }

    private fun findNextPlaceHolderPosition(start: Int, end: Int): Int {
        val mask = "#### ###### #####"
        mask.let {
            for (i in start until end) {
                val m = it[i]
                val c = text!!.get(i)
                if (m.isPlaceHolder() && c.isLetterOrDigit()) {
                    return i + 1
                }
            }
        }
        return start + 1
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        var rightOffset = 0
        if (error != null && error.isNotEmpty()) {
            rightOffset = resources.displayMetrics.density.toInt() * 32
        }

        val right = width - paddingRight - rightOffset

        val top = paddingTop
        val bottom = height - paddingBottom
        val ratio = currentDraw.intrinsicWidth.toFloat() / currentDraw.intrinsicHeight.toFloat()
        //int left = right - mCurrentDrawable.getIntrinsicWidth(); //If images are correct size.
        val left = (right - (bottom - top) * ratio).toInt() //scale image depeding on height available.
        currentDraw.setBounds(left, top, right, bottom)

        currentDraw.draw(canvas)
    }
}