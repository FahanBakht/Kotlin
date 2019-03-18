package com.farhan.moviepocket.utils

import android.content.Context
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet

//Custom Image view which adjust it's Height accordingly to image height
class DynamicHeightImageView : AppCompatImageView {
    private var mAspectRatio = 1.5f

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    fun setAspectRatio(aspectRatio: Float) {
        mAspectRatio = aspectRatio
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val measuredWidth = measuredWidth
        setMeasuredDimension(measuredWidth, (measuredWidth / mAspectRatio).toInt())
    }
}