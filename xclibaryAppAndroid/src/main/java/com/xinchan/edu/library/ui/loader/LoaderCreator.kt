package com.xinchan.edu.library.ui.loader

import android.content.Context
import com.wang.avi.AVLoadingIndicatorView
import com.wang.avi.Indicator
import java.util.*


/**
 * Created by weicxu on 2017/12/18
 */
object LoaderCreator{
    private val LOADING_MAP = WeakHashMap<String,Indicator>()

    fun create(type: String, context: Context): AVLoadingIndicatorView {

        val avLoadingIndicatorView = AVLoadingIndicatorView(context)
        if (LOADING_MAP.get(type) == null) {
            val indicator = getIndicator(type)
            LOADING_MAP.put(type, indicator)
        }
        avLoadingIndicatorView.indicator = LOADING_MAP[type]
        return avLoadingIndicatorView
    }

    private fun getIndicator(name: String?): Indicator? {
        if (name == null || name.isEmpty()) {
            return null
        }
        val drawableClassName = StringBuilder()
        if (!name.contains(".")) {
            val defaultPackageName = AVLoadingIndicatorView::class.java.`package`.name
            drawableClassName.append(defaultPackageName)
                    .append(".indicators")
                    .append(".")
        }
        drawableClassName.append(name)
        try {
            val drawableClass = Class.forName(drawableClassName.toString())
            return drawableClass.newInstance() as Indicator
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

    }
}