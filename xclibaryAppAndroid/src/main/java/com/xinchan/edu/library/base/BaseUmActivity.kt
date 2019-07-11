package com.xinchan.edu.library.base

import androidx.appcompat.app.AppCompatActivity
import com.umeng.analytics.MobclickAgent

/**
 * @desc
 * @author derekyan
 * @date 2018/11/1
 */
abstract class BaseUmActivity: AppCompatActivity() {

    override fun onResume() {
        super.onResume()
        MobclickAgent.onResume(this)
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPause(this)
    }
}