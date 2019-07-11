package com.xinchan.edu.library.util

import android.content.Context
import com.xinchan.edu.library.app.Configurator
import com.xinchan.edu.library.app.XcCore
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * @desc
 * @author derekyan
 * @date 2018/5/23
 */

class Preference<T>(private val context: Context?, private val name: String, private val default: T) : ReadWriteProperty<Any?, T> {

    val prefs by lazy { context!!.getSharedPreferences(XcCore.getConfigs(Configurator.ConfigKeys.SP_NAME) as String, Context.MODE_PRIVATE) }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T = get(name, default)

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        put(name, value)
    }

    private fun <U> get(name: String, default: U): U = with(prefs) {
        val res: Any = when (default) {
            is Long -> getLong(name, default)
            is String -> getString(name, default)
            is Int -> getInt(name, default)
            is Boolean -> getBoolean(name, default)
            is Float -> getFloat(name, default)
            else -> throw IllegalArgumentException("This type can be saved into Preferences")
        }
        res as U
    }

    private fun <U> put(name: String, value: U) = with(prefs.edit()) {
        when (value) {
            is Long -> putLong(name, value)
            is String -> putString(name, value)
            is Int -> putInt(name, value)
            is Boolean -> putBoolean(name, value)
            is Float -> putFloat(name, value)
            else -> throw IllegalArgumentException("This type can be saved into Preferences")
        }.apply()
    }

}