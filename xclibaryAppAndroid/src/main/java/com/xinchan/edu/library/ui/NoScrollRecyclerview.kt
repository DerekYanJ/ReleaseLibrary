package com.xinchan.edu.library.ui

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import android.widget.ScrollView

/**
 * 不可滑动 高度最大化
 * Created by DerekYan on 2017/5/15.
 */

class NoScrollRecyclerview : androidx.recyclerview.widget.RecyclerView {

    private var parentScrollView: ScrollView? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE shr 2, View.MeasureSpec.AT_MOST)
        super.onMeasure(widthMeasureSpec, expandSpec)
    }

    fun setParentScrollView(parentScrollView: ScrollView) {
        this.parentScrollView = parentScrollView
    }

    /**
     * 是否允许scrollview滚动
     * @param flag
     */
    private fun setParentScrollAble(flag: Boolean) {

        parentScrollView!!.requestDisallowInterceptTouchEvent(!flag)
    }
}
