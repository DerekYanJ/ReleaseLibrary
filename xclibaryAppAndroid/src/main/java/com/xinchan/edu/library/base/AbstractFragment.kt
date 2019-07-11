package com.xinchan.edu.library.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View

/**
 * @desc
 * @author derekyan
 * @date 2018/6/20
 */
abstract class AbstractFragment: androidx.fragment.app.Fragment() {

    protected abstract fun getContentViewId(): Int

    /**
     * 是否展示toolbar
     */
    protected abstract fun isShowToolbar(): Boolean

    /**
     * 初始化view
     */
    protected abstract fun initView(savedInstanceState: Bundle?, view: View)

    /**
     * 初始化数据
     */
    abstract fun initData()
}