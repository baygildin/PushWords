package com.sbaygildin.pushwords.progress

import androidx.lifecycle.ViewModel
import com.sbaygildin.pushwords.data.di.AppPreferencesManager
import com.sbaygildin.pushwords.data.di.ProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class ProgressViewModel @Inject constructor(
    preferencesManager: AppPreferencesManager,
    private val progressRepository: ProgressRepository,
) : ViewModel() {

    val userName: Flow<String> = preferencesManager.userNameFlow
}