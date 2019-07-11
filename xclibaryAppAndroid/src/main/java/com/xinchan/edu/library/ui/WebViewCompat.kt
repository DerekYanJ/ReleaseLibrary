package com.xinchan.edu.library.ui

import android.content.Context
import android.view.MotionEvent
import android.webkit.WebView


/**
 * Created by weicxu on 2018/1/3
 */
class WebViewCompat(context: Context?) : WebView(context) {
    private var isTop = false

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> isTop = false
            MotionEvent.ACTION_UP -> isTop = false
        }
        return super.onTouchEvent(event)
    }

    override fun overScrollBy(deltaX: Int, deltaY: Int, scrollX: Int, scrollY: Int, scrollRangeX: Int, scrollRangeY: Int, maxOverScrollX: Int, maxOverScrollY: Int, isTouchEvent: Boolean): Boolean {
        if (scrollY <= 0) {
            if (!isTop && deltaY < 0) {
                isTop = true
            }
        }
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent)
    }

    override fun onOverScrolled(scrollX: Int, scrollY: Int, clampedX: Boolean, clampedY: Boolean) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY)
    }

    fun isTop(): Boolean {
        return isTop
    }

}