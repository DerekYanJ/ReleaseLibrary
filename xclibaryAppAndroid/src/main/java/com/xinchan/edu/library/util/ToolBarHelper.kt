package com.xinchan.edu.library.util

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar
import com.blankj.utilcode.utils.BarUtils
import com.xinchan.edu.library.R

/**
 * @author derekyan
 * @desc ToolBar辅助类
 * @date 2016/12/6
 */

class ToolBarHelper(private val mContext: Activity, layoutId: Int, val navigationIconRes: Int,  val toolBarBackgroundRes: Any?) {
    lateinit var contentView: FrameLayout
    lateinit var toolBar: Toolbar
    lateinit var ll_toolbar: LinearLayout
    private val mInflater: LayoutInflater = LayoutInflater.from(mContext)

    init {
        /*初始化整个内容*/
        initContentView()
        /*初始化用户定义的布局*/
        initUserView(layoutId)
        /*初始化toolbar*/
        initToolBar()
    }

    private fun initContentView() {
        /*直接创建一个帧布局，作为视图容器的父容器*/
        contentView = FrameLayout(mContext)
        val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
        contentView.layoutParams = params
    }

    private fun initToolBar() {
        /*通过inflater获取toolbar的布局文件*/
        val toolbar = mInflater.inflate(R.layout.layout_toolbar_base, contentView)
        toolBar = toolbar.findViewById(R.id.toolbar)
        ll_toolbar = toolbar.findViewById(R.id.ll_toolbar)

        if(toolBarBackgroundRes != null)
            ll_toolbar.setBackgroundResource(toolBarBackgroundRes as Int)

        //设置返回按钮图片
        toolBar.setNavigationIcon(navigationIconRes)

        //状态栏高度
        if (Build.VERSION.SDK_INT >= 21) {
            val decorView = mContext.window.decorView
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            mContext.window.statusBarColor = Color.TRANSPARENT

            val lp = toolBar.layoutParams as LinearLayout.LayoutParams
            lp.topMargin = BarUtils.getStatusBarHeight(mContext)
            toolBar.layoutParams = lp
        }
    }


    @SuppressLint("ResourceType")
    private fun initUserView(id: Int) {
        val mUserView = mInflater.inflate(id, null)
        val params = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        val typedArray = mContext.theme.obtainStyledAttributes(ATTRS)
        /*获取主题中定义的悬浮标志*/
        val overly = typedArray.getBoolean(0, false)
        /*获取主题中定义的toolbar的高度 + 状态栏高度*/
        val toolBarSize = typedArray.getDimension(1, mContext.resources.getDimension(R.dimen.libray_toolbar_height).toInt().toFloat()).toInt() + if (Build.VERSION.SDK_INT >= 21) BarUtils.getStatusBarHeight(mContext) else 0
        typedArray.recycle()
        /*如果是悬浮状态，则不需要设置间距*/
        params.topMargin = if (overly) 0 else toolBarSize

        contentView.addView(mUserView, params)
    }

    companion object {
        /*
        * 两个属性
        * 1、toolbar是否悬浮在窗口之上
        * 2、toolbar的高度获取
        * */
        private val ATTRS = intArrayOf(R.attr.windowActionBarOverlay, R.dimen.libray_toolbar_height)
    }

}
