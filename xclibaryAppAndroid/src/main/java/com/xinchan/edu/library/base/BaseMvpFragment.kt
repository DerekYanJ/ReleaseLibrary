package com.xinchan.edu.library.base

import android.os.Bundle

/**
 * @desc
 * @author derekyan
 * @date 2018/6/20
 */
@Suppress("UNCHECKED_CAST")
abstract class BaseMvpFragment<V: IBaseView,T: BasePresenter<V>>: BaseFragment() {
    var mPresenter: T? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPresenter = initPresenter()
        mPresenter!!.attachView(this as V)
    }

    protected abstract fun initPresenter(): T

    override fun onDestroy() {
        super.onDestroy()
        if(mPresenter != null) {
            mPresenter!!.dettach()
            mPresenter = null
        }
    }
}