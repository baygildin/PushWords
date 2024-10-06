package com.sbaygildin.pushwords.settings

interface NotificationController {
    fun scheduleQuizNotification()
    fun cancelQuizNotification()
    fun updateTheme(isDarkMode: Boolean)
//    fun updateUserName(name: String)
}