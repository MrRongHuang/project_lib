package com.koi.projectlib

object Constant {
    const val VIDEO_SELECT_MAX_NUMBER = 1
    const val PHOTO_SELECT_MAX_NUMBER = 9

    const val PHOTO_SHOP_LOGO = 1
    const val PHOTO_SHOP_BANNER = 1

    const val PHOTO_SELECT_MAX_NUMBER_THINK_TANK = 10
    const val VIDEO_SELECT_MAX_NUMBER_THINK_TANK = 3

    const val PHOTO_ENQUIRY_MAX_NUMBER = 1
    // 会员优惠金额提现上传 pdf 文件数量
    const val PHOTO_CIP_WITHDRAWAL_MAX_NUMBER = 6

    const val PHOTO_CLOUD_DEVICE_MAX_NUM = 5
//    const val PHOTO_CLOUD_PATENT_MAX_NUM = 5

    const val PAGE_SIZE = 10
    const val PAGE_SIZE_20 = 20
    const val PAGE_SIZE_MAX = 99999

    const val REQUEST_IMAGE_CAPTURE = 201
    const val REQUEST_VIDEO_CAPTURE = 202

    //选择相册类型 1照片  2视频
    const val ALBUM_TYPE_PHOTO = 1
    const val ALBUM_TYPE_VIDEO = 2

    const val UPGRADE_TIME_PROMPT_INTERVALS = 3 * 24 * 60 * 60 * 1000  // 3天内不在提示升级

    const val CROSS_APP_HOST = "jsb://cn.jinsubao/seller?"  // 跨应用Uri Host
    const val SP_FILE_NAME = "cn.jinsubao.app.shared_preferences"  // SP存储文件名
    const val INT_DEFAULT = -1  // int存储默认值

    const val WECHAT_APP_ID = "wxee131a7af0f259a7"
    const val WX_APP_SECRET = "877baa06e39721a5f079305978c5ced9"
    const val QQ_APP_ID = "1112195704"

    /* 权限申请提示 Start */
    const val READ_MEDIA_IMAGES_AND_CAMERA_STR = "为了拍照并上传图片，请授权金塑宝使用您的相机和存储权限"
    const val READ_MEDIA_IMAGES_AND_CAMERA_STR_TIPS = "相机或存储权限已拒绝，该功能可能无法正常使用哦"
    const val READ_MEDIA_IMAGES_AND_CAMERA_DENIED_STR = "该功能所需相机或存储权限被拒绝且不再询问，无法正常使用，是否前往授予权限？"

    const val READ_MEDIA_CAMERA_STR = "为了调用您的相机进行扫码，请授权金塑宝使用您的相机权限"
    const val READ_MEDIA_CAMERA_STR_TIPS = "相机权限已拒绝，该功能可能无法正常使用哦"
    const val READ_MEDIA_CAMERA_DENIED_STR = "该功能所需相机被拒绝且不再询问，无法正常使用，是否前往授予权限？"

    const val CALL_PHONE_AND_RECORD_AUDIO_STR = "为了能够联系商家，请授权金塑宝使用您的拨打电话、录音权限"
    const val CALL_PHONE_AND_RECORD_AUDIO_STR_TIPS = "电话或录音权限已拒绝，该功能可能无法正常使用哦"
    const val CALL_PHONE_AND_RECORD_AUDIO_DENIED_STR = "该功能所需电话或录音权限被拒绝且不再询问，无法正常使用，是否前往授予权限？"

    const val READ_MEDIA_IMAGES_STR = "为了上传您本机内的图片或视频信息，请授权金塑宝使用您的存储权限"
    const val READ_MEDIA_DOWN_LOAD_STR = "为了下载对应内容，请授权金塑宝使用您的存储权限"
    const val READ_MEDIA_IMAGES_STR_TIPS = "为了上传您本机内的图片信息，请授权金塑宝使用您的存储权限"
    const val READ_MEDIA_DOWN_LOAD_STR_TIPS = "为了下载对应内容，请授权金塑宝使用您的存储权限"
    const val READ_MEDIA_IMAGES_DENIED_STR = "该功能所需存储权限被拒绝且不再询问，无法正常使用，是否前往授予权限？"

    const val READ_MEDIA_LOCATION_STR = "为了调用您的精确定位权限，请授权金塑宝使用您的精确定位权限"
    const val READ_MEDIA_LOCATION_STR_TIPS = "未获取到精确定位权限，该功能可能无法正常使用哦"
    const val READ_MEDIA_LOCATION_DENIED_STR = "该功能所需精确定位权限未授权，无法正常使用，是否前往授予权限？"
    /* 权限申请提示 End */

}