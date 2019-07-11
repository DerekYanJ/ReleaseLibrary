package com.xinchan.edu.library.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.GridView

/**
 * 不可滑动 高度最大化
 * Created by DerekYan on 2017/5/15.
 */

class NoScrollGridView : GridView {
    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE shr 2, View.MeasureSpec.AT_MOST)
        super.onMeasure(widthMeasureSpec, expandSpec)
    }
}
