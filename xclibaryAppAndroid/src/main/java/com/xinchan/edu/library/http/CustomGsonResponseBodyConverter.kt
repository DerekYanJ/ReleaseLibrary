package com.xinchan.edu.library.http

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.xinchan.edu.library.extension.loge
import okhttp3.ResponseBody
import okhttp3.internal.Util.UTF_8
import retrofit2.Converter
import java.io.ByteArrayInputStream
import java.io.InputStreamReader


/**
 * 返回结果包装
 * Created by weicxu on 2017/12/22
 */

class  CustomGsonResponseBodyConverter<T>(private val gson: Gson, private val adapter: TypeAdapter<T>) : Converter<ResponseBody, T> {

    override fun convert(value: ResponseBody): T? {
        val response = value.string()

        //获取到返回的response字符串，并解析成APIResponse格式。如果code==0，则为正确返回，直接read,
        //如果code！=0，则是错误返回，此处应处理成通用错误数据。暂时直接抛异常看能否收到信息。
        loge("服务器返回：$response")
        var apiResponse: ApiResponse<*>? = null
        try {
            apiResponse = gson.fromJson(response, ApiResponse::class.java)
            //应对特殊返回数据格式：
            //{"code":500,"message":"系统开小差了,请稍等片刻","data":{}}
            //需要给 msg 字段赋有效值 message
            if(apiResponse?.msg.isNullOrEmpty() && apiResponse?.message != null) apiResponse.msg = apiResponse.message
        } catch (e: Exception) {
            throw ApiException(-3, "服务器解析错误！")
        } finally {
            if (apiResponse == null) {
                throw ApiException(-3, "服务器解析错误！")
            }
        }

        //接口请求正确返回
        if (apiResponse.code == 0) {
            try {
                try {
                    val contentType = value.contentType()
                    val charset = if (contentType != null) contentType.charset(UTF_8) else UTF_8
                    val inputStream = ByteArrayInputStream(response.toByteArray())
                    val reader = InputStreamReader(inputStream, charset)
                    val jsonReader = gson.newJsonReader(reader)
                    return adapter.read(jsonReader)
                } catch (e1: Exception) {
                    e1.printStackTrace()
                    throw ApiException(-2, "JSON解析失败${e1.message}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                if (e is ApiException) {
                    throw e
                } else {
                    throw ApiException(-2, "JSON解析失败${e.message}")
                }
            } finally {
                value.close()
            }
        } else {
            value.close()
            if (apiResponse.code == -1) {
                throw ApiException(-1, "服务器出现异常！")
            }
            throw ApiException(apiResponse.code, apiResponse.msg)
        }
    }
}
