package com.sbaygildin.pushwords.navigation


interface Navigator {
    fun navigateHomeFragmentToSettingsFragment(s: String)
    fun navigateWordlistToEdit(id: String)
    fun navigateWordlistToAddword()
    fun navigateHomeToAddword()
}