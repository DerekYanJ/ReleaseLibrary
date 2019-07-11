package com.xinchan.edu.library.base

import android.os.Bundle

/**
 * @desc
 * @author derekyan
 * @date 2018/5/23
 */
@Suppress("UNCHECKED_CAST")
abstract class BaseMvpActivity<V: IBaseView,T: BasePresenter<V>> : BaseActivity(){
    protected var mPresenter: T? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        mPresenter = initPresenter()
        mPresenter!!.attachView(this as V)
        super.onCreate(savedInstanceState)
    }

    protected abstract fun initPresenter(): T

    override fun onDestroy() {
        if(mPresenter != null) {
            mPresenter!!.dettach()
            mPresenter = null
        }
        super.onDestroy()
    }
}