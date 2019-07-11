package com.xinchan.edu.library.ui.loader

import android.content.Context
import androidx.appcompat.app.AppCompatDialog
import com.xinchan.edu.library.R
import com.xinchan.edu.library.extension.getScreenSize
import com.xinchan.edu.library.extension.loge


/**
 * Created by weicxu on 2017/12/18
 */
object LoaderDialog {
    private val LOADER_SIZE_SCALE = 8
    private val LOADER_OFFSET_SCALE = 10

    private val LOADERS = ArrayList<AppCompatDialog>()

    private val DEFAULT_LOADER = LoaderStyle.BallClipRotatePulseIndicator.name
    private val DEFAULT_INDICATOR = LoaderStyle.BallClipRotatePulseIndicator

    fun showLoading(context: Context, type: Enum<LoaderStyle>) {
        showLoading(context, type.name)
    }

    private fun showLoading(context: Context, type: String) {
        try {
            if (LOADERS.size > 0) {
                loge("dialog---loaders.size = ${LOADERS.size}")
                val dialog = LOADERS[0]
                dialog.show()
            } else {
                loge("dialog---新建dialog")
                val dialog = AppCompatDialog(context, R.style.dialog)

                val avLoadingIndicatorView = LoaderCreator.create(type, context)
                dialog.setContentView(avLoadingIndicatorView)

                val deviceWidth = context.getScreenSize()[0]
                val deviceHeight = context.getScreenSize()[1]

                val dialogWindow = dialog.window

                if (dialogWindow != null) {
                    val lp = dialogWindow.attributes
                    lp.width = deviceWidth / LOADER_SIZE_SCALE
                    lp.height = lp.width
                }
                dialog.setCancelable(false)
                LOADERS.add(dialog)
                dialog.show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showLoading(context: Context) {
//        loge("111       showProgress .")
        showLoading(context, DEFAULT_LOADER)
    }

    fun stopLoading() {
        LOADERS
                .filter { it != null && it.isShowing }
                .forEach {
                    it.cancel()
//                    loge("hideProgress .")
                }
        clearLoaders()
    }

    fun clearLoaders() {
        LOADERS.clear()
    }
}