package com.xinchan.edu.library.ui

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent



/**
 * Created by weicxu on 2017/12/18
 */
class ViewPagerCompat: androidx.viewpager.widget.ViewPager {

    private var isCanScroll = true

    constructor(context:Context):super(context){

    }

    constructor(context:Context,attrs:AttributeSet):super(context,attrs){

    }

    fun setScanScroll(isCanScroll: Boolean) {
        this.isCanScroll = isCanScroll
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return isCanScroll && super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return isCanScroll && super.onTouchEvent(ev)

    }
}