package com.xinchan.edu.library.http

import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.internal.Util
import okio.BufferedSink
import java.io.IOException
import java.nio.charset.Charset

/**
 * Created by weicxu on 2017/12/20
 */
abstract class RequestBodyCompat() : RequestBody() {
    private var content: String? = null

    private constructor(content: String) : this() {
        this.content = content
    }

    fun peekCotent() = content

    companion object {
        fun compatCreate(contentType: MediaType?, content: String): RequestBody {
            var charset: Charset? = Util.UTF_8
            var contentTypeTemp = contentType
            if (contentType != null) {
                charset = contentType.charset()
                if (charset == null) {
                    charset = Util.UTF_8
                    contentTypeTemp = MediaType.parse(contentType.toString() + "; charset=utf-8")
                }
            }
            val bytes = content.toByteArray(charset!!)
            val length = bytes.size
            Util.checkOffsetAndCount(length.toLong(), 0, length.toLong())
            return object : RequestBodyCompat(content) {
                override fun contentType(): MediaType? {
                    return contentTypeTemp
                }

                override fun contentLength(): Long {
                    return length.toLong()
                }

                @Throws(IOException::class)
                override fun writeTo(sink: BufferedSink) {
                    sink.write(bytes, 0, length)
                }
            }
        }
    }

}

