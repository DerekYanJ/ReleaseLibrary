package com.xinchan.edu.library.app

import android.content.Context

/**
 * @desc
 * @author derekyan
 * @date 2018/5/23
 */
class XcCore {

    companion object {
        fun init(context: Context): Configurator {
            Configurator.getInstance()
                    .getConfigs()[Configurator.ConfigKeys.APPCONTEXT] = context.applicationContext
            return Configurator.getInstance()
        }

        public fun getConfigurator(): Configurator {
            return Configurator.getInstance()
        }

        private fun <T> getConfiguration(key: Any): T {
            return getConfigurator().getConfiguration(key)
        }

        fun getApplicationContext(): Context{
            return getConfiguration(Configurator.ConfigKeys.APPCONTEXT)
        }

        fun getConfigs(key: Any) = getConfigurator().getConfigs()[key]

    }

}