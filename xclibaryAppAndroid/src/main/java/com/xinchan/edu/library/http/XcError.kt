package com.xinchan.edu.library.http

/**
 * @desc 用于发生错误后传递给UI解析
 * @author derekyan
 * @date 2018/5/23
 */
data class XcError(val code: Int, val msg: String)