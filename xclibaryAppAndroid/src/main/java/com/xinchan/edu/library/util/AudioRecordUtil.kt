package com.xinchan.edu.library.util

import android.media.MediaRecorder
import android.os.Handler
import com.xinchan.edu.library.XcConstants
import com.xinchan.edu.library.extension.loge
import com.xinchan.edu.library.extension.nameAudio
import java.io.File
import kotlin.math.log10

/**
 * Created by weicxu on 2018/2/28
 */
class AudioRecordUtil {

    private var filePath = ""
    private var dirPath = ""

    private var mMediaRecorder: MediaRecorder?
    private val MAX_DURATION = 1000 * 60

    private var startTime: Long = 0L
    private var endTime: Long = 0L

    private lateinit var listener: (Int) -> Unit

    constructor() : this(XcConstants.APP_AUDIO_DIR)

    constructor(dirPath: String) {
        this.dirPath = dirPath
        mMediaRecorder = MediaRecorder()
        loge("dirPath = $dirPath")
        val file = File(dirPath)
        if (!file.exists()) {
            file.mkdirs()
        }
    }


    fun startRecord(listener: (Int) -> Unit) {
        this.listener = listener
        if (mMediaRecorder == null)
            mMediaRecorder = MediaRecorder()
        //设置麦克风
        mMediaRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        //设置编码
        mMediaRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
        //设置输出格式 3.28,增加使用aac编码，兼容iOS
        mMediaRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        filePath = dirPath + "/" + nameAudio()

        mMediaRecorder!!.setOutputFile(filePath)
        mMediaRecorder!!.setMaxDuration(MAX_DURATION)//没用啊
        mMediaRecorder!!.prepare()
        mMediaRecorder!!.start()

        startTime = System.currentTimeMillis()
        updateMicStatus()
    }

    fun stopRecord(): MutableList<Any> {
        if (mMediaRecorder == null)
            return mutableListOf()
        endTime = System.currentTimeMillis()
        handler.removeCallbacks { updateMicStatus() }
        mMediaRecorder!!.stop()
        mMediaRecorder!!.reset()
        mMediaRecorder!!.release()
        mMediaRecorder = null
        val list = mutableListOf<Any>()
        list.add(endTime - startTime)
        list.add(filePath)
        return list
    }

    private val BASE: Double = 1.0
    private val SPACE = 100L

    private val handler = Handler()

    private fun updateMicStatus() {
        if (mMediaRecorder != null) {
//            if (System.currentTimeMillis()-startTime>=MAX_DURATION){
//                listener(-100)//假设-100约定为超时处理
////                stopRecord()
//            }else {
            val radio: Double = mMediaRecorder!!.maxAmplitude / BASE
            var db: Double
            if (radio > 1) {
                db = 20 * log10(radio)
                listener(db.toInt())
            }
            handler.postDelayed({ updateMicStatus() }, SPACE)
//            }

        }
    }

    fun cancelRecord() {
        stopRecord()
        val file = File(filePath)
        file.delete()

    }


}