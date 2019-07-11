package com.xinchan.edu.library.http

/**
 * Created by weicxu on 2018/4/23
 */
data class ApiResponse<T>(val code: Int, var msg: String, val body: T, val message: String)