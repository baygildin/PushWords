package com.sbaygildin.pushwords.wordlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sbaygildin.pushwords.data.di.WordTranslationDao
import com.sbaygildin.pushwords.data.model.WordTranslation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WordlistViewModel @Inject constructor(
    private val wordTranslationDao: WordTranslationDao
) : ViewModel() {

    private val _wordList = MutableStateFlow<List<WordTranslation>>(emptyList())
    val wordList: StateFlow<List<WordTranslation>> = _wordList

    init {
        viewModelScope.launch {
            wordTranslationDao.getAllWordTranslations().collect { words ->
                _wordList.value = words
            }
        }
    }

    suspend fun deleteWord(wordId: Long): Int {
        return wordTranslationDao.deleteWordTranslationById(wordId)
    }
}
