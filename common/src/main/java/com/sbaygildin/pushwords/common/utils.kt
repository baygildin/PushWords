package com.sbaygildin.pushwords.common

fun validateInput(originalWord: String, translatedWord: String): Boolean {
    return originalWord.isNotEmpty() && translatedWord.isNotEmpty()
}