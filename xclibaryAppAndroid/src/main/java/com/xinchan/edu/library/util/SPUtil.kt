package com.xinchan.edu.library.util

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.text.TextUtils
import com.xinchan.edu.library.app.Configurator
import com.xinchan.edu.library.app.XcCore

/**
 * Created by yanqy on 2017/4/18.
 */

class SPUtil private constructor(name: String, context: Context?) {
    private val sp: SharedPreferences

    init {
        if (TextUtils.isEmpty(name)) {
            throw IllegalArgumentException("Sp Name不能为空")
        }
        if (context == null) {
            throw IllegalArgumentException("context不能为空")
        }
        sp = context.getSharedPreferences(name, MODE_PRIVATE)
    }

    fun putString(key: String, value: String?) : SPUtil{
        val editor = sp.edit()
        editor.putString(key, value)
        editor.apply()
        return this
    }

    fun putInt(key: String, value: Int) {
        val editor = sp.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun putBoolean(key: String, value: Boolean) {
        val editor = sp.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    /**
     * 删除key
     *
     * @param key
     */
    fun remove(key: String) {
        val editor = sp.edit()
        editor.remove(key)
        editor.apply()
    }

    fun getInt(key: String, defaultValue: Int): Int {
        return sp.getInt(key, defaultValue)
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return sp.getBoolean(key, defaultValue)
    }

    fun getString(key: String, defaultValue: String): String? {
        return sp.getString(key, defaultValue)
    }

    fun getString(key: String): String {
        return sp.getString(key, "")
    }

    companion object {
        private var instance: SPUtil? = null

        fun getInstance(): SPUtil {
            if (instance == null) {
                synchronized(SPUtil::class.java) {
                    if (instance == null)
                        instance = SPUtil(XcCore.getConfigs(Configurator.ConfigKeys.SP_NAME) as String, XcCore.getApplicationContext())
                }
            }
            return instance!!
        }
    }
}
