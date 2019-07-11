package com.xinchan.edu.library.base

import java.lang.ref.Reference
import java.lang.ref.WeakReference

/**
 * @desc
 * @author derekyan
 * @date 2018/5/23
 *
 *  T  IBaseView
 */
abstract class BasePresenter<T: IBaseView> : IPresenter{
    private var mViewRef: Reference<T>? = null

    var mView: T? = null

    fun attachView(mView: T) {
        this.mView = mView
        mViewRef = WeakReference(mView)
    }

    fun dettach() {
        if (mViewRef != null) {
            mViewRef!!.clear()
            mViewRef = null
        }
    }

}