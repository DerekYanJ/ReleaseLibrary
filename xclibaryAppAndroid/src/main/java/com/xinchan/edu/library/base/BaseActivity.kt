package com.xinchan.edu.library.base

import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.xinchan.edu.library.R
import com.xinchan.edu.library.app.Configurator
import com.xinchan.edu.library.app.XcCore
import com.xinchan.edu.library.http.XcError
import com.xinchan.edu.library.ui.loader.LoaderDialog
import com.xinchan.edu.library.util.ToolBarHelper
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.toast

/**
 * @desc
 * @author derekyan
 * @date 2018/5/23
 */
abstract class BaseActivity: AbstractActivity(), View.OnClickListener,IBaseView {

    lateinit var mToolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentViewBefore()
        val contentViewId = getContentViewId()
        if(contentViewId == -1)
            throw RuntimeException("请设置 contentViewId")

        if(isShowToolbar())
            setContentViewWithToolBar(contentViewId)
        else
            setContentView(contentViewId)

        initView()
        initData()
    }

    fun setEmptyDataView(desc: String, noDataResId: Int, clickListener: () -> Unit? = {}){
        findViewById<TextView>(R.id.tv_no_data)?.text = desc
        findViewById<ImageView>(R.id.iv_no_data)?.imageResource = noDataResId
        findViewById<TextView>(R.id.tv_no_data)?.setOnClickListener {
            clickListener.invoke()
        }
    }

    /**
     * 设置布局id之前
     */
    override fun setContentViewBefore() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    override fun onClickBack() {
        finish()
    }


    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //拦截系统返回键单击事件
            onClickBack()
        }
        return super.onKeyDown(keyCode, event)
    }

    /**
     * 默认展示toolbar
     */
    override fun isShowToolbar(): Boolean = true

    private fun setContentViewWithToolBar(layoutResID: Int) {
        val mToolBarHelper = ToolBarHelper(
                this,
                layoutResID,
                XcCore.getConfigs(Configurator.ConfigKeys.TOOLBAR_BACK_RES) as Int,
                XcCore.getConfigs(Configurator.ConfigKeys.TOOLBAR_BACKGROUND_RES))
        mToolbar = mToolBarHelper.toolBar
        setContentView(mToolBarHelper.contentView) /*把 toolbar 设置到Activity 中*/
        setSupportActionBar(mToolbar)
        onCreateCustomToolBar(mToolbar)
        mToolbar.setNavigationOnClickListener {
            onClickBack()
        }

    }

    /**
     * 设置标题
     * @param title
     */
    protected fun setToolBarCenterTitle(title: String) {
        val actionBar = supportActionBar
        actionBar?.title = ""
        val view: View? = mToolbar.findViewById(R.id.tv_toolbar_title)
        (view as TextView).text = title
    }

    /**
     * 设置右侧文字
     * @param str
     */
    protected fun setToolBarRightText(str: String,listener:View.OnClickListener) {
        val view = mToolbar.rootView.findViewById<View>(R.id.tv_toolbar_right)
        view.visibility = View.VISIBLE
        view.setOnClickListener(listener)
        (view as TextView).text = str
    }

    /**
     * 设置右侧文字
     * @param str
     */
    protected fun setToolBarRightText(str: String) {
        val view = mToolbar.rootView.findViewById<View>(R.id.tv_toolbar_right)
        view.visibility = View.VISIBLE
        (view as TextView).text = str
    }

    protected fun getToolBarRightView(): TextView {
        return mToolbar.findViewById(R.id.tv_toolbar_right)
    }

    private fun onCreateCustomToolBar(toolbar: Toolbar) {
        toolbar.setContentInsetsRelative(0, 0)
    }


    override fun showProgress() {
        LoaderDialog.showLoading(this)
    }

    override fun hideProgress() {
        LoaderDialog.stopLoading()
    }

    override fun showError(error: String) {
        toast(error)
    }

    override fun showError(xcError: XcError) {
        showError(xcError.msg)
    }

    override fun tokenExpired() {
        (XcCore.getApplicationContext() as BaseApplication).login(this,"")
    }

    override fun tokenExpired(msg: String) {
        (XcCore.getApplicationContext() as BaseApplication).login(this,msg)
    }

    override fun onClick(v: View?) {

    }
}