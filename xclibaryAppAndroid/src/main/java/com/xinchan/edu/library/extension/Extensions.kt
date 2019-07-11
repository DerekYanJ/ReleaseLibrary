package com.xinchan.edu.library.extension

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION_CODES.M
import android.telephony.TelephonyManager
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.*
import com.blankj.utilcode.utils.KeyboardUtils
import com.blankj.utilcode.utils.ScreenUtils
import com.blankj.utilcode.utils.SizeUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.xinchan.edu.library.HttpErrorCode
import com.xinchan.edu.library.R
import com.xinchan.edu.library.XcConstants
import com.xinchan.edu.library.XcErrorCode
import com.xinchan.edu.library.app.Configurator
import com.xinchan.edu.library.app.XcCore
import com.xinchan.edu.library.base.IBaseView
import com.xinchan.edu.library.http.ApiException
import com.xinchan.edu.library.http.ApiResponse
import com.xinchan.edu.library.http.XcError
import com.xinchan.edu.library.ui.CampusDataMarkerView
import com.xinchan.edu.library.ui.MyPieChart
import com.xinchan.edu.library.ui.glide.GlideCircleTransform
import com.xinchan.edu.library.ui.glide.GlideRoundTransform
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.jetbrains.anko.toast
import org.jetbrains.anko.windowManager
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.Map
import java.util.regex.Pattern

/**
 * @desc
 * @author derekyan
 * @date 2018/5/23
 */

fun <T> rxRequest(observable: Observable<ApiResponse<T>>, listener: (T) -> Unit,  view: IBaseView?,isShowProgress: Boolean = true) {
    if(isShowProgress) view?.showProgress()
    Observable.create<Int> {
        it.onNext(getNetworkType())
        it.onComplete()
    }.map { it ->
        if (it != 0) {
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        if(isShowProgress) view?.hideProgress()
//                        loge(it.toString())
                        listener(it.body)
                    }, {
                        if(isShowProgress) view?.hideProgress()
                        it.printStackTrace()
                        if (it is ApiException) {
//                            loge(it.toString())
                            if ((it.code == XcErrorCode.TOKEN_EXPIRED) and (view != null)) {
                                view?.tokenExpired("该账号已在其他手机登录，如非本人操作请重新登录")
                            } else {
                                view?.showError(XcError(it.code, it.message.toString()))
                            }
                        } else {
                            view?.showError(it.message.toString())
                        }
                    }, {
//                        view?.hideProgress()
                    })
        } else {
            if(isShowProgress) view?.hideProgress()
            view?.showError(XcError(-2, "网络连接失败，请检查您的网络"))
        }
    }.subscribe()

}

fun <T,D> rxRequestTwo(observable: Observable<ApiResponse<T>>, funtion: io.reactivex.functions.Function<ApiResponse<T>, ObservableSource<ApiResponse<D>>>, listener: (D) -> Unit, view: IBaseView?) {
    Observable.create<Int> {
        it.onNext(getNetworkType())
        it.onComplete()
    }.map { it ->
        if (it != 0) {
            observable
                    .subscribeOn(Schedulers.io())
                    .flatMap(funtion)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        view?.hideProgress()
                        loge(it.toString())
                        listener(it.body)
                    }, {
                        view?.hideProgress()
                        if (it is ApiException) {
                            loge(it.toString())
                            if ((it.code == HttpErrorCode.TOKEN_EXPIRED) and (view != null)) {
                                view?.tokenExpired("登录信息过期，请您重新登录！")
                            } else {
                                view?.showError(XcError(it.code, it.message.toString()))
                            }
                        } else {
                            it.printStackTrace()
                            view?.showError(XcError(-1, it.message.toString()))
                        }
                    }, {
                        //view?.hideProgress()
                    })
        } else {
            view?.hideProgress()
            view?.showError(XcError(-2, "网络连接失败，请检查您的网络"))
        }
    }.subscribe()
}

fun Activity.setBackgroundAlpha(bgAlpha: Float ) {
    val lp = this.window.attributes
    lp.alpha = bgAlpha
    this.window.attributes = lp
}

fun Activity.hideSNBar() {
    if (Build.VERSION.SDK_INT >= 21) {
        val decorView = window.decorView
        val option = (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        decorView.systemUiVisibility = option
        window.navigationBarColor = Color.TRANSPARENT
        window.statusBarColor = Color.TRANSPARENT
    }
}

fun Activity.hideStatusBar(){
    if (Build.VERSION.SDK_INT >= 21) {
        val decorView = window.decorView
        val option = (
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        decorView.systemUiVisibility = option
        window.statusBarColor = Color.TRANSPARENT
    }
}

/**
 * 标题栏文字图标深色
 */
fun Activity.statusBarDark(){
    if(Build.VERSION.SDK_INT >= M){
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        window.statusBarColor = Color.TRANSPARENT
    }
}


/**
 * 标题栏文字图标白色
 */
fun Activity.statusBarLight(){
    if(Build.VERSION.SDK_INT >= 21){
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        window.statusBarColor = Color.TRANSPARENT
    }
}

fun androidx.fragment.app.Fragment.toastAtMiddle(str: String) {
    toastAtMiddle(this.context!!,str)
}

fun Activity.toastAtMiddle(str: String) {
    toastAtMiddle(this,str)
}

fun androidx.fragment.app.DialogFragment.toast(str: String){
    Toast.makeText(context, str, Toast.LENGTH_SHORT).show()
}

fun toastAtMiddle(context: Context, str: String){
    val makeText = Toast.makeText(context, str, Toast.LENGTH_SHORT)
    makeText.setGravity(Gravity.CENTER, 0, 0)
    makeText.show()
}

fun toast(context: Context?, str: String){
    val makeText = Toast.makeText(context, str, Toast.LENGTH_SHORT)
    makeText.show()
}

fun displayImage(context: Context?, url: String, imageView: ImageView) {
    displayImage(context,url,imageView,
            XcCore.getConfigs(Configurator.ConfigKeys.ERROR_RES) as Int,
            XcCore.getConfigs(Configurator.ConfigKeys.DEFAULT_RES) as Int)
}

fun displayImage(context: Context?, url: String, imageView: ImageView, errorRes: Int, placeHolder: Int) {
    Glide.with(context)
            .load(url)
            .placeholder(placeHolder)
            .dontAnimate()
            .error(errorRes)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imageView)
}

fun displayRoundCornerImage(context: Context?, url: String, radius: Int, imageView: ImageView, errorRes: Int) {
    Glide.with(context)
            .load(url)
            .placeholder(errorRes)
            .dontAnimate()
            .error(errorRes)
            .transform(GlideRoundTransform(context,radius))
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imageView)
}

fun displayRoundCornerImage(context: Context?, resId: Int, radius: Int, imageView: ImageView) {
    Glide.with(context)
            .load(resId)
            .placeholder(XcCore.getConfigs(Configurator.ConfigKeys.DEFAULT_CIRCLE_RES) as Int)
            .dontAnimate()
            .error( XcCore.getConfigs(Configurator.ConfigKeys.ERROR_RES) as Int)
            .transform(GlideRoundTransform(context,radius))
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imageView)
}

fun displayRoundCornerImage(context: Context?, url: String, radius: Int, imageView: ImageView) {
    displayRoundCornerImage(context, url, radius, imageView,
            XcCore.getConfigs(Configurator.ConfigKeys.ERROR_RES) as Int)
}

fun displayCircleImage(context: Context?, url: String, imageView: ImageView, errorRes: Int) {
    Glide.with(context)
            .load(url)
            .placeholder(errorRes)
            .dontAnimate()
            .error(errorRes)
            .transform(GlideCircleTransform(context))
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imageView)
}

fun displayCircleImage(context: Context?, url: String, imageView: ImageView) {
    displayCircleImage(context, url, imageView, XcCore.getConfigs(Configurator.ConfigKeys.DEFAULT_CIRCLE_RES) as Int)
}


var logTag = "xinChanLibrary"
fun loge(msg:Any){
    if(XcCore.getConfigs(Configurator.ConfigKeys.IS_SHOW_LOG) as Boolean)
        Log.e(logTag,msg.toString())
}


@SuppressLint("MissingPermission")
        /**
         * 没有网络-0：WIFI网络1：4G网络-4：3G网络-3：2G网络-2
         */
fun getNetworkType(): Int {
    var type = 0//默认无网络
    val connectManager = XcCore.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = connectManager.activeNetworkInfo ?: return type
    //否则 NetworkInfo对象不为空 则获取该networkInfo的类型
    val nType = networkInfo.type
    if (nType == ConnectivityManager.TYPE_WIFI) {
        //WIFI
        //TODO。2018.3.12添加获取IP地址
        type = 1
    } else if (nType == ConnectivityManager.TYPE_MOBILE) {
        val nSubType = networkInfo.subtype
        val telephonyManager = XcCore.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        //3G   联通的3G为UMTS或HSDPA 电信的3G为EVDO
        if ((nSubType == TelephonyManager.NETWORK_TYPE_LTE)
                and (!telephonyManager.isNetworkRoaming)) {
            type = 4
        } else if ((nSubType == TelephonyManager.NETWORK_TYPE_UMTS)
                or (nSubType == TelephonyManager.NETWORK_TYPE_HSDPA)
                or (nSubType == TelephonyManager.NETWORK_TYPE_EVDO_0)
                and (!telephonyManager.isNetworkRoaming)) {
            type = 3
            //2G 移动和联通的2G为GPRS或EGDE，电信的2G为CDMA
        } else if ((nSubType == TelephonyManager.NETWORK_TYPE_GPRS)
                or (nSubType == TelephonyManager.NETWORK_TYPE_EDGE)
                or (nSubType == TelephonyManager.NETWORK_TYPE_CDMA)
                and (!telephonyManager.isNetworkRoaming)) {
            type = 2
        } else {
            type = 2
        }
    }
    return type
}


fun nameImage(): String {
    val format = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.CHINA)
    return "img-" + format.format(Date()) + ".jpg"
}

fun nameAudio(): String {
    val format = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss")
    return "ado-" + format.format(Date()) + ".aac"
}

fun nameVideo(): String {
    val format = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss")
    return "vdo-" + format.format(Date()) + ".mp4"
}

fun newFile(filePath: String): File {
    val file = File(filePath)
    if (!file.parentFile.exists()) {
        file.parentFile.mkdirs()
    }
    return file
}

private fun handleImagesWithMatrix(files: MutableList<String>): MutableList<String> {
    val tempList = mutableListOf<String>()
    val start = System.currentTimeMillis()
    files.forEach {
        val file = File(it)
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = false//耗内存
        val source = BitmapFactory.decodeFile(it, options)//因为
        val width = options.outWidth
        val height = options.outHeight

        val referenceWidth = XcConstants.REFERENCE_WIDTH
        loge("file.length is ${file.length() / 1000}KB,w*h is $width*$height")

        if (width <= referenceWidth && height <= referenceWidth) {
            //不处理，使用原图
            loge("the width and height are both less than the reference，tempFile is not scaled")
            tempList.add(it)
        } else if (width > referenceWidth && height > referenceWidth) {//宽高都大于1280
            //较小设置为1280，较大等比压缩
            loge("the width and height are both greater than the reference,temp will be scaled")
            val min = Math.min(width, height)
            val scale = if (min == width) {
                referenceWidth / width.toFloat()
            } else {
                referenceWidth / height.toFloat()
            }
            loge("both greater scale is $scale")
            val tempFilePath = getScaledFile(scale, source, file)
            tempList.add(tempFilePath)
        } else {//一个大于1280，另外一个小于1280
            if (width / height > 2 || height / width > 2) {//宽高比大于2//改为大于2，目前不少全面屏截图均为2/1，但实际图片并不过于大。
                //不处理
                loge("either width or height is greater than the reference,and the ratio is more than two,don't scale it")
                tempList.add(it)
            } else {//宽高比小于2，设置最大边为1280，小边等比压缩
                //3.20增加判断，如果图片大小小于500K，则不必压缩。
                if (file.length() < 200 * 1024) {
                    loge("one of the height and width is greater than the reference,and the ratio is less than two," +
                            "but the size of file is less than 500KB,don't scale it.")
                    tempList.add(it)
                } else {
                    loge("one of the height and width is greater than the reference,and the ratio is less than two,scale it.")
                    val max = Math.max(width, height)
                    val scale = if (max == width) referenceWidth / width.toFloat() else referenceWidth / height.toFloat()
                    loge("one of scale = $scale")
                    val tempFilePath = getScaledFile(scale, source, file)
                    tempList.add(tempFilePath)
                }
            }
        }
    }
    loge("Matrix压缩耗时：${System.currentTimeMillis() - start}毫秒")
    return tempList
}

private fun getScaledFile(scale: Float, source: Bitmap, file: File): String {
    val matrix = Matrix()
    matrix.setScale(scale, scale)
    val bitmap = Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    val tempFilePath = XcConstants.APP_IMAGE_DIR + "/" + file.name + "-temp" + ".jpg"
    val tempNewFile = newFile(tempFilePath)
    val bos = BufferedOutputStream(FileOutputStream(tempNewFile))
    bitmap.compress(Bitmap.CompressFormat.JPEG, 70, bos)
//        loge("both greater tempFile.length = ${File(tempFilePath).length() / 1000}KB,and w*h is ${width * scale}*${height * scale}")
    bos.flush()
    bos.close()
    bitmap.recycle()
    return tempFilePath

}

fun getMultiBody(params: HashMap<String, Any>?,
                 imgFiles: MutableList<String>,
                 audioFile: String,
                 videoPath: String, coverPath: String,
                 uid: String, token: String): MultipartBody {

    if(params != null) loge(params.toString())
    loge("上传的文件：imgFiles:$imgFiles  audioFile:$audioFile  videoPath:$videoPath  coverPath:$coverPath")

    val builder = MultipartBody.Builder()
    val tempList = handleImagesWithMatrix(imgFiles)

    var index = 0

    //添加图片
    var totalLength: Long = 0
    for (i in tempList.indices) {
        index = i
        val path = tempList[i]
        val file = File(path)
        totalLength += file.length()
        val requestBody = RequestBody.create(MediaType.parse("image/*"), file)
        builder.addFormDataPart("file[$i]", file.name, requestBody)
    }

    //添加参数
    if (params != null) {
        val iter = params.entries.iterator()

        while (iter.hasNext()) {
            val entry = iter.next() as  kotlin.collections.Map.Entry<*, *>
//            val key = entry.key
            val `val` = entry.value
            builder.addFormDataPart("content", `val`.toString())
        }
    }

    if(audioFile.isNotEmpty()) {
        //音频流
        index++
        val audio = File(audioFile)
        totalLength += audio.length()
        val audioBody = RequestBody.create(MediaType.parse("audio/*"), audio)
        builder.addFormDataPart("file[$index]", audio.name, audioBody)
    }

    if(videoPath.isNotEmpty()){
        //视频
        index++
        val video = File(videoPath)
        totalLength += video.length()
        val videoBody = RequestBody.create(MediaType.parse("video/*"), video)
        builder.addFormDataPart("file[$index]", video.name, videoBody)

        //视频封面图
        index++
        val cover = handleImagesWithMatrix(arrayListOf(coverPath))[0]
        val coverFile = File(cover)
        totalLength += coverFile.length()
        val coverBody = RequestBody.create(MediaType.parse("image/*"), coverFile)
        builder.addFormDataPart("file[$index]", coverFile.name, coverBody)
    }
    loge("index:$index")

    builder.addFormDataPart("_uid", uid)
    builder.addFormDataPart("_token", token)
    builder.setType(MultipartBody.FORM)

    loge("totalLength = $totalLength")

    return builder.build()
}

fun getMultiBody(params: HashMap<String, Any>?, files: MutableList<String>,uid: String,token: String): MultipartBody {
//    loge(params?.toString()!!)
    val builder = MultipartBody.Builder()
    val tempList = handleImagesWithMatrix(files)

    var totalLength: Long = 0
    for (i in tempList.indices) {
        val path = tempList.get(i)
        val file = File(path)
        totalLength += file.length()
        val requestBody = RequestBody.create(MediaType.parse("image/*"), file)
        builder.addFormDataPart("content[$i]", file.name, requestBody)
    }
    loge("totalLength = $totalLength")
    if (params != null) {
        val iter = params.entries.iterator()
        while (iter.hasNext()) {
            val entry = iter.next() as Map.Entry<*, *>
            builder.addFormDataPart(entry.key.toString(), entry.value.toString())
        }
    }

    builder.addFormDataPart("_uid", uid)
    builder.addFormDataPart("_token", token)
    builder.setType(MultipartBody.FORM)

    return builder.build()
}

fun getMultiBody(params: HashMap<String, Any>?, imgFile: String,uid: String,token: String): MultipartBody {
//    loge(params?.toString()!!)
    val builder = MultipartBody.Builder()
    val tempList = handleImagesWithMatrix(mutableListOf(imgFile))

    var totalLength: Long = 0
    for (i in tempList.indices) {
        val path = tempList.get(i)
        val file = File(path)
        totalLength += file.length()
        val requestBody = RequestBody.create(MediaType.parse("image/*"), file)
        builder.addFormDataPart("file", file.name, requestBody)
    }
    loge("totalLength = $totalLength")
    if (params != null) {
        val iter = params.entries.iterator()
        while (iter.hasNext()) {
            val entry = iter.next() as Map.Entry<*, *>
            builder.addFormDataPart(entry.key.toString(), entry.value.toString())
        }
    }

    builder.addFormDataPart("_uid", uid)
    builder.addFormDataPart("_token", token)
    builder.setType(MultipartBody.FORM)

    return builder.build()
}

fun getMultiBody(map: Array<Pair<String, Any>>?, files: MutableList<String>, audioFile: String,uid: String,token: String): MultipartBody {
//    loge(map?.toString()!!)
    val builder = MultipartBody.Builder()
    //图片
    val tempList = handleImagesWithMatrix(files)

    var totalLength = 0L
    for (i in 0 until tempList.size) {
        val file = File(tempList[i])
        totalLength += file.length()
        loge("scaled temp file size = ${file.length()/1000}KB")
        loge("file == "+file.absolutePath)
        val requestBody = RequestBody.create(MediaType.parse("image/*"), file)
        builder.addFormDataPart("content[$i]", file.name, requestBody)
    }
    //音频流
    val audio = File(audioFile)
    totalLength += audio.length()
    val audioBody = RequestBody.create(MediaType.parse("audio/*"), audio)
    builder.addFormDataPart("descript", audio.name, audioBody)
    loge("totalLength = $totalLength")

    map?.forEach {
        builder.addFormDataPart(it.first, it.second.toString())
    }
    builder.addFormDataPart("_uid", uid)
    builder.addFormDataPart("_token", token)
    builder.setType(MultipartBody.FORM)

    return builder.build()
}

fun getMultiBody(map: Array<Pair<String, Any>>?, videoPath: String, coverPath: String,uid: String,token: String): MultipartBody {
//    loge(map?.toString()!!)
    val builder = MultipartBody.Builder()
    //视频
    val video = File(videoPath)
    val videoBody = RequestBody.create(MediaType.parse("video/*"), video)
    builder.addFormDataPart("content", video.name, videoBody)

    val cover = handleImagesWithMatrix(arrayListOf(coverPath))[0]
    val coverFile = File(cover)
    val coverBody = RequestBody.create(MediaType.parse("image/*"), coverFile)
    builder.addFormDataPart("cover_pic", coverFile.name, coverBody)

    map?.forEach {
        builder.addFormDataPart(it.first, it.second.toString())
    }
    builder.addFormDataPart("_uid", uid)
    builder.addFormDataPart("_token", token)
    builder.setType(MultipartBody.FORM)

    return builder.build()
}

fun Activity.getScreenSize(): Array<Int> {
    var screenWidth: Int
    var screenHeight: Int
    if (Build.VERSION.SDK_INT >= 17) {
        var metrics = DisplayMetrics()
        windowManager.defaultDisplay.getRealMetrics(metrics)
        screenWidth = metrics.widthPixels
        screenHeight = metrics.heightPixels
    } else {
        screenWidth = resources.displayMetrics.widthPixels
        screenHeight = resources.displayMetrics.heightPixels
    }
    return arrayOf(screenWidth, screenHeight)
}

fun Context.getScreenSize(): Array<Int> {
    val screenWidth: Int
    val screenHeight: Int
    if (Build.VERSION.SDK_INT >= 17) {
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getRealMetrics(metrics)
        screenWidth = metrics.widthPixels
        screenHeight = metrics.heightPixels
    } else {
        screenWidth = resources.displayMetrics.widthPixels
        screenHeight = resources.displayMetrics.heightPixels
    }
    return arrayOf(screenWidth, screenHeight)
}

fun Activity.callPhone(phone: String?) {
    if (phone.isNullOrEmpty()){
        return
    }
    if (isMobile(phone!!)) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    } else {
        toast("手机号码有误！")
    }
}

fun androidx.fragment.app.Fragment.callPhone(phone: String?) {
    if (phone.isNullOrEmpty()){
        return
    }
    if (isMobile(phone!!)) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    } else {
        toast(context!!,"手机号码有误！")
    }
}

fun isMobile(phone: String): Boolean = Pattern.matches("^[1][3456789][0-9]{9}\$", phone)

/**
 * 隐藏软键盘
 */
fun Activity.hideSoftInput1(){
    KeyboardUtils.hideSoftInput(this)
}


/**
 * 家长关系：1，父亲；2，母亲；3，爷爷；4，奶奶；5，外公；6，外婆；
 */
fun getRelationByType(type: String) = when(type){
    "1" -> "父亲"
    "2" -> "母亲"
    "3" -> "爷爷"
    "4" -> "奶奶"
    "5" -> "外公"
    "6" -> "外婆"
    else -> "未知"
}

fun getSpecialPhoneNumber(str: String) = str.replaceRange(3,7,"****")

/**
 * webview设置
 */
fun initWebView(mWebView: WebView){
    val webSettings: WebSettings = mWebView.settings

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        webSettings.safeBrowsingEnabled = false
    }

    webSettings.javaScriptEnabled = true
    webSettings.javaScriptCanOpenWindowsAutomatically = true
    webSettings.useWideViewPort = true //将图片调整到适合webView的大小
    webSettings.loadWithOverviewMode = true // 缩放至屏幕的大小
    webSettings.setSupportZoom(true)
    webSettings.builtInZoomControls = true
    webSettings.domStorageEnabled = true

    webSettings.setAppCacheEnabled(false) //启用应用缓存
    webSettings.databaseEnabled = false


    webSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
    //设置编码
//    webSettings.defaultTextEncodingName = "utf-8"

    //解决webview对于http和https安全机制问题
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        mWebView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
    }
}

fun clearWebView(mWebView: WebView){
    mWebView.webChromeClient = null
    mWebView.webViewClient = null
    mWebView.settings.javaScriptEnabled = false
    mWebView.clearCache(true)
    mWebView.destroy()
}

/**
 * 初始化设置饼图
 */
fun initPieChart(mPieChart: MyPieChart, noDataDesc: String = "暂无数据"){

    mPieChart.setNoDataText(noDataDesc)//无数据时显示文字 居中
    mPieChart.setNoDataTextColor(Color.parseColor("#999999"))

    mPieChart.setUsePercentValues(true)    // 表内数据用百分比替代，而不是原先的值。并且ValueFormatter中提供的值也是该百分比的。默认false
    mPieChart.description.isEnabled = false
    mPieChart.isDrawHoleEnabled = false //不画中间的圆
    mPieChart.maxAngle = 360f // 设置整个饼形图的角度，默认是360°即一个整圆，也可以设置为弧，这样现实的值也会重新计算
    mPieChart.setDrawEntryLabels(false) //不画饼图中Label 只显示百分比
    mPieChart.isRotationEnabled = false //禁止滑动
    mPieChart.isHighlightPerTapEnabled = false //禁止高亮显示 点击后对应扇形区域不高亮

    val l = mPieChart.legend
    l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
    l.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
    l.orientation = Legend.LegendOrientation.HORIZONTAL
    l.setDrawInside(false)
    l.xEntrySpace = 14f //横线图例间距
    l.yEntrySpace = 0f
    l.yOffset = 0f
    l.xOffset = 0f //x轴偏移量  默认18f
    l.textSize = 14f //图例文字大小
    l.textColor = Color.parseColor("#999999") //图例文字颜色
    l.isWordWrapEnabled = true //自动换行
}

fun setPieChartData(mPieChart: MyPieChart, pieEntries: List<PieEntry>, colors: List<Int>){
    val iPieDataSet = PieDataSet(pieEntries, "")
    iPieDataSet.colors = colors //每一块儿扇形区域的颜色
    iPieDataSet.setValueTextColors(mutableListOf(Color.WHITE))
    iPieDataSet.valueTextSize = 14f
    iPieDataSet.sliceSpace = 0f   // 每块之间的距离

    val pieData = PieData(iPieDataSet)
    pieData.setValueFormatter { value, _, _, _ ->
        if(value != 0f)
            "${String.format("%.0f",value)}%"  //不保留小数
        else ""
    }
    mPieChart.data = pieData //填充数据

    mPieChart.invalidate()

}

/**
 * 初始化LineChart设置
 */
fun initLineChart(mLineChart: LineChart, context: Context, noDataDesc: String = "暂无数据", isShowPercentage: Boolean = true){
    mLineChart.setNoDataText(noDataDesc) //没有数据时样式
    mLineChart.setNoDataTextColor(Color.parseColor("#999999"))

    val markerView = CampusDataMarkerView(context,R.layout.layout_campus_data_marker_view) //选中悬浮窗
    markerView.chartView = mLineChart //绑定折线图和markerview
    mLineChart.marker = markerView //设置选中悬浮窗
    mLineChart.setBackgroundColor(Color.WHITE) // 整个Chart的背景色
    mLineChart.description.isEnabled = false
    mLineChart.setDrawGridBackground(false) //绘制区域的背景（默认是一个灰色矩形背景）将绘制，默认false
    mLineChart.setDrawBorders(false)    // 绘制区域边框绘制，默认false
    mLineChart.setScaleEnabled(false)   // 两个轴上的缩放,X,Y分别默认为true
    mLineChart.isScaleYEnabled =  false
    mLineChart.isScaleXEnabled =  true
    mLineChart.isDoubleTapToZoomEnabled = false // 双击缩放,默认true
//    mLineChart.axisRight.isEnabled = false   // 不绘制右侧的轴线
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        mLineChart.isNestedScrollingEnabled = false
    }
    mLineChart.onChartGestureListener = object: OnChartGestureListener {
        override fun onChartGestureEnd(me: MotionEvent?, lastPerformedGesture: ChartTouchListener.ChartGesture?) {
            if (lastPerformedGesture != ChartTouchListener.ChartGesture.SINGLE_TAP)
                mLineChart.highlightValues(null) // or highlightTouch(null) for callback to onNothingSelected(...)
        }

        override fun onChartFling(me1: MotionEvent?, me2: MotionEvent?, velocityX: Float, velocityY: Float) {
        }

        override fun onChartSingleTapped(me: MotionEvent?) {
        }

        override fun onChartGestureStart(me: MotionEvent?, lastPerformedGesture: ChartTouchListener.ChartGesture?) {
        }

        override fun onChartScale(me: MotionEvent?, scaleX: Float, scaleY: Float) {
        }

        override fun onChartLongPressed(me: MotionEvent?) {
        }

        override fun onChartDoubleTapped(me: MotionEvent?) {
        }

        override fun onChartTranslate(me: MotionEvent?, dX: Float, dY: Float) {
        }

    }

    val xAxis = mLineChart.xAxis    // 获取X轴
    xAxis.setDrawGridLines(true)// 是否绘制网格线，默认true
    xAxis.setDrawAxisLine(true)// 是否绘制坐标轴,默认true
    xAxis.setDrawLabels(true)  // 是否绘制标签,默认true
    xAxis.position = XAxis.XAxisPosition.BOTTOM  // X轴绘制位置，默认是顶部
    xAxis.resetAxisMaximum()   // 撤销最大值；
    xAxis.textColor = Color.parseColor("#9FA9BA") //x轴文字颜色
    xAxis.gridColor = Color.parseColor("#EBEDF8") //x轴网格线颜色

    val yAxis = mLineChart.axisLeft // 获取Y轴,mLineChart.getAxis(YAxis.AxisDependency.LEFT);也可以获取Y轴
    yAxis.setDrawZeroLine(false)
    if(isShowPercentage)
        yAxis.axisMaximum = 100f
    yAxis.axisMinimum = 0f
    yAxis.setLabelCount(6, false) // 纵轴上标签显示的数量,数字不固定。如果force = true,将会画出明确数量，但是可能值导致不均匀，默认（6，false）
    yAxis.textSize = 12f
    yAxis.textColor = Color.parseColor("#9FA9BA") //y轴文字颜色
    yAxis.gridColor = Color.parseColor("#EBEDF8") //x轴网格线颜色
    yAxis.axisLineColor = Color.TRANSPARENT
    
    val rightAxis = mLineChart.axisRight
    rightAxis.setDrawAxisLine(true)
    rightAxis.setDrawGridLines(false)
    rightAxis.textColor = Color.TRANSPARENT
    rightAxis.axisLineColor = Color.TRANSPARENT
    rightAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
    rightAxis.xOffset = 4f

    // 轴值转换显示
    yAxis.valueFormatter = IAxisValueFormatter { value, _ ->
        // 与上面值转换一样，这里就是转换出轴上label的显示。也有几个默认的，不多说了。
        if(isShowPercentage)
        "${value.toInt()}%"
        else value.toInt().toString()
    }

    val legend = mLineChart.legend // 获取图例，但是在数据设置给chart之前是不可获取的
    legend.isEnabled = false    // 是否绘制图例
}

/**
 * 设置校园数据详情页适配布局和数据
 */
fun setCampusDataDetailAdapterItemViewAndData(view: ViewGroup, list: List<String>, iv: ImageView, isShowArrow: Boolean = false){
    for (i in 0 until view.childCount - 1){
        val tv = view.getChildAt(i) as TextView
        if(list.size > i){
            tv.text = list[i]
            tv.visibility = View.VISIBLE
        }else{
            tv.visibility = View.GONE
        }
    }
    iv.visibility = if(isShowArrow) View.VISIBLE else View.GONE
}

fun Activity.postponeEnterTransition(timeout: Long) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        postponeEnterTransition()

        window.decorView.postDelayed({
            startPostponedEnterTransition()
        },timeout)
    }
}


/**
 * 创建直播预览加载view
 * @return
 */
fun initVideoProgressBar(context: Context?): View {
    val relativeLayout = RelativeLayout(context)
    relativeLayout.setBackgroundColor(Color.parseColor("#000000"))
    val lp = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
    relativeLayout.layoutParams = lp
    val rlp = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
    rlp.addRule(RelativeLayout.CENTER_IN_PARENT)//addRule参数对应RelativeLayout XML布局的属性
    val mProgressBar = ProgressBar(context)
    relativeLayout.addView(mProgressBar, rlp)
    return relativeLayout
}

/**
 * adapter中只有一张图片时 获取宽度最大
 */
fun getAdapterOneImgWidth(context: Context?): Int {
    return (ScreenUtils.getScreenWidth(context) - SizeUtils.dp2px(context,30F))
}

/**
 * adapter中只有一张图片时 固定高度
 */
fun getAdapterOneImgHeight(context: Context?): Int {
    return SizeUtils.dp2px(context, 210F)
}

/**
 * adapter中有多个图片时 按照4列计算图片宽度高度
 * 两张图片间隔15dp
 * 15 * 4 + 15
 */
fun getAdapterDuoImgWidth(context: Context?): Int {
    return (ScreenUtils.getScreenWidth(context) - SizeUtils.dp2px(context,75F))/4
}


val checkInStatusStrList = arrayListOf("正常", "请假", "缺勤或未绑定考勤卡", "迟到或早退", "补勤")