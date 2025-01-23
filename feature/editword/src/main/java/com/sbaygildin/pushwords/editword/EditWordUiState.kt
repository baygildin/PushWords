package com.sbaygildin.pushwords.editword

import com.sbaygildin.pushwords.data.model.WordTranslation


sealed class EditWordUiState {
    object Loading : EditWordUiState()
    data class Success(val word: WordTranslation) : EditWordUiState()
    object Error : EditWordUiState()
}
