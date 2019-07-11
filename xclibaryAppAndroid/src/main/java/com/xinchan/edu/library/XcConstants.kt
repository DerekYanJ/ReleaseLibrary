package com.xinchan.edu.library

import com.xinchan.edu.library.app.Configurator
import com.xinchan.edu.library.app.XcCore

/**
 * @desc
 * @author derekyan
 * @date 2018/5/23
 */

object XcErrorCode {
    val TOKEN_EXPIRED = 2038//token过期

}

object XcConstants{
    val APP_DIR = XcCore.getConfigs(Configurator.ConfigKeys.APP_DIR)
    val APP_IMAGE_DIR = "$APP_DIR/image"
    val APP_VIDEO_DIR = "$APP_DIR/video"
    val APP_AUDIO_DIR = "$APP_DIR/audio"

    val REFERENCE_WIDTH = 1280

    /**
     * 列表 每页多少条
     */
    val LIST_ROWS = 20

    /**
     * 最多上传9张图片
     */
    val MAX_IMG_COUNT = 9

    val WEB_VIEW_URL = "http://family-h5.xinchanedu.com/"
}

object XcRequestCode{

    val REQUEST_CAMERA_CODE = 100
    val REQUEST_ALBUM_CODE = 101
}

object XcSp {
    /**
     * 第一次进入
     */
    val FIRST_INSTALL = "first_install"

    /**
     * 全局存储userID
     */
    val USER_ID = "user_id"

    val USER_PHONE = "user_phone"

    /**
     * 用户登录token
     */
    val TOKEN = "token"
    /**
     * 校区id
     */
    val CAMPUSID = "campusId"
    /**
     * 用户登录token
     */
    val SHARE_URL = "shareUrl"
    /**
     * 用户imei
     */
    val IMEI = "imei"
    /**
     * 推送注册ID
     */
    val REGISTRATION_ID = "registration_id"

    /**
     * 请求存储权限的次数
     */
    val STORAGE_PERMISSION = "storage_permission_times"
    /**
     * 请求摄像头权限的次数
     */
    val CAMERA_PERMISSION = "camera_permission_times"
    /**
     * 请求录音权限的次数
     */
    val AUDIO_PERMISSION = "audio_permission_times"
    /**
     * 请求电话权限的次数
     */
    val PHONE_PERMISSION = "phone_permission_times"

    /**
     * 校园简介链接地址
     */
    val CAMPUS_HOME_URL = "campus_home_url"
}

object HttpErrorCode{
    val TOKEN_EXPIRED = 2038//token过期
    val CODE_NEED_NOT_SHOW_LEAVE = 2090//token过期

    val ILLEGAL_PARAMS = 2000 //非法参数
    val ILLEGAL_REQUEST = 2001//非法请求
    val ERROR_REQUEST = 2002//请求方式错误


    val USER_ISNOT_EXIST = 2030//用户不存在
    val USER_REGISTER_FAILURE = 2031//注册失败
    val USER_EXIST = 2032//用户已存在
    val ERROR_USERNAME = 2033//用户名错误
    val ERROR_PASSWORD = 2034//密码错误
    val ERROR_VERIFY_CODE = 2035//验证码错误
    val ERROR_PHONE_NUMBER = 2036//手机号错误
    val ERROR_PRIMARY_ID = 2037//自增ID错误
    val ERROR_NO_BABY = 2039//没有宝宝
    val ERROR_NO_CLASS = 2040//宝宝没有关联班级
    val ERROR_TEACHER_INFO = 2041//教师信息不全
}