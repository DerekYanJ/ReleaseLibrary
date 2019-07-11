package com.xinchan.edu.library.app

import java.util.*


/**
 * Created by weicxu on 2017/12/13
 */
class Configurator {
    companion object {
        private val CONFIGS = HashMap<Any, Any>()
        fun getInstance() = Holder.INSTANCE
    }

    private constructor() {
        CONFIGS[ConfigKeys.CONFIG_READY] = false
    }

    private object Holder {
        val INSTANCE = Configurator()
    }

    fun apply() {
        CONFIGS[ConfigKeys.CONFIG_READY] = true
    }

    fun setBaseUrl(host: String): Configurator {
        CONFIGS[ConfigKeys.BASE_URL] = host
        return this
    }

    fun setEBaseUrl(host: String): Configurator {
        CONFIGS[ConfigKeys.EBASE_URL] = host
        return this
    }

    fun setDefaultRes(resDefault: Int): Configurator {
        CONFIGS[ConfigKeys.DEFAULT_RES] = resDefault
        return this
    }

    fun setDefaultCricleRes(resCircleDefault: Int): Configurator {
        CONFIGS[ConfigKeys.DEFAULT_CIRCLE_RES] = resCircleDefault
        return this
    }

    fun setErrorRes(resError: Int): Configurator {
        CONFIGS[ConfigKeys.ERROR_RES] = resError
        return this
    }

    fun setToolbarBackRes(resBack: Int): Configurator {
        CONFIGS[ConfigKeys.TOOLBAR_BACK_RES] = resBack
        return this
    }

    fun setToolbarBackgroundRes(resBackground: Int): Configurator {
        CONFIGS[ConfigKeys.TOOLBAR_BACKGROUND_RES] = resBackground
        return this
    }

    fun setAppDir(appDir: String): Configurator {
        CONFIGS[ConfigKeys.APP_DIR] = appDir
        return this
    }

    fun setSpName(spName: String): Configurator {
        CONFIGS[ConfigKeys.SP_NAME] = spName
        return this
    }

    fun isShowLog(isShowLog: Boolean): Configurator {
        CONFIGS[ConfigKeys.IS_SHOW_LOG] = isShowLog
        return this
    }


    private fun checkConfiguration() {
        val isReady = CONFIGS[ConfigKeys.CONFIG_READY] as Boolean
        if (!isReady) {
            throw RuntimeException("Configuration is not ready,call configure")
        }
    }

    fun <T> getConfiguration(key: Any): T {
        checkConfiguration()
        CONFIGS[key] ?: throw NullPointerException(key.toString() + " IS NULL")
        return CONFIGS[key] as T
    }
    fun getConfigs(): HashMap<Any, Any> {
        return CONFIGS
    }

    enum class ConfigKeys {
        /**
         * 是否配置  必须配置  不配置抛异常
         */
        CONFIG_READY,
        /**
         * 服务器根接口
         */
        BASE_URL,
        EBASE_URL,
        /**
         * Application Context
         */
        APPCONTEXT,

        /**
         * Glide 默认图 和 获取图片错误图片
         */
        DEFAULT_RES,
        ERROR_RES,
        DEFAULT_CIRCLE_RES,

        /**
         * 标题栏返回按钮ResId
         */
        TOOLBAR_BACK_RES,
        TOOLBAR_BACKGROUND_RES,

        /**
         * 根存储路径
         */
        APP_DIR,

        /**
         * sp name
         */
        SP_NAME,

        /**
         * 是否展示log
         */
        IS_SHOW_LOG

        /**
         * 主题颜色值 用于首页BottomView
         */
    }

}