package com.xinchan.edu.library.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.xinchan.edu.library.http.XcError



/**
 * @desc
 * @author derekyan
 * @date 2018/6/20
 */
abstract class BaseFragment: AbstractFragment(), View.OnClickListener,IBaseView  {

    lateinit var rootView: View
    var isInited = false //是否已经初始化了

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(getContentViewId(), container, false)
        initView(savedInstanceState, rootView)
        isInited = true
        initData()
        return rootView
    }

    override fun isShowToolbar(): Boolean = false

    override fun onClick(v: View?) {
    }

    override fun showProgress() {
        if(activity != null && activity is BaseActivity){
            (activity as BaseActivity).showProgress()
        }
    }

    override fun hideProgress() {
        if(activity != null && activity is BaseActivity){
            (activity as BaseActivity).hideProgress()
        }
    }

    override fun showError(error: String) {
        if(activity != null && activity is BaseActivity){
            (activity as BaseActivity).showError(error)
        }
    }

    override fun showError(xcError: XcError) {
        showError(xcError.msg)
    }

    override fun tokenExpired() {
        if(activity != null && activity is BaseActivity){
            (activity as BaseActivity).tokenExpired()
        }
    }

    override fun tokenExpired(msg: String) {
        if(activity != null && activity is BaseActivity){
            (activity as BaseActivity).tokenExpired(msg)
        }
    }

    override fun onResume() {
        super.onResume()
        //友盟统计
//        MobclickAgent.onPageStart(this.javaClass.simpleName)
    }

    override fun onPause() {
        super.onPause()
        //友盟统计
//        MobclickAgent.onPageEnd(this.javaClass.simpleName)
    }
}