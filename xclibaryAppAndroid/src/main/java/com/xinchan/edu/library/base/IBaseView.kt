package com.xinchan.edu.library.base

import com.xinchan.edu.library.http.XcError

/**
 * @desc
 * @author derekyan
 * @date 2018/5/23
 */
interface IBaseView {

    fun showProgress()

    fun hideProgress()

    fun showError(error: String)

    fun showError(xcError: XcError)

    fun tokenExpired()

    fun tokenExpired(msg: String)
}