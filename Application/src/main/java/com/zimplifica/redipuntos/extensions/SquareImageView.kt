package com.zimplifica.redipuntos.extensions

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView



class SquareImageView : ImageView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(
        context: Context, attrs: AttributeSet,
        defStyle: Int
    ) : super(context, attrs, defStyle)

    override fun onMeasure(width: Int, height: Int) {
        super.onMeasure(width, height)
        /*
        val measuredWidth = measuredWidth
        val measuredHeight = measuredHeight
        if (measuredWidth > measuredHeight) {
            setMeasuredDimension(measuredHeight, measuredHeight)
        } else {
            setMeasuredDimension(measuredWidth, measuredWidth)

        }*/

        val width = measuredWidth
        setMeasuredDimension(width, width)

    }

}