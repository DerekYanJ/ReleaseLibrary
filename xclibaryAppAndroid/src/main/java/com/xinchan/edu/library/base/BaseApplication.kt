package com.xinchan.edu.library.base

import android.app.Activity
import android.app.Application

/**
 * @desc
 * @author derekyan
 * @date 2018/6/20
 */
abstract class BaseApplication : Application(){
    abstract fun login(activity: Activity?, msg: String)
}