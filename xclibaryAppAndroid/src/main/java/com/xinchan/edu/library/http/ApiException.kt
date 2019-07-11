package com.xinchan.edu.library.http

import java.lang.RuntimeException

/**
 * 自定义异常
 * Created by weicxu on 2017/12/22
 */
class ApiException(val code: Int, errorMessage: String) : RuntimeException(errorMessage)
