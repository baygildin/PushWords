package com.sbaygildin.pushwords.home

import com.sbaygildin.pushwords.data.di.AppPreferencesManager
import com.sbaygildin.pushwords.data.di.ProgressRepository
import com.sbaygildin.pushwords.data.di.WordTranslationDao
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class HomeViewModelTest {

    private lateinit var viewModel: HomeViewModel
    private val mockWordTranslationDao: WordTranslationDao = mockk()
    private val mockProgressRepository: ProgressRepository = mockk()
    private val mockPreferencesManager: AppPreferencesManager = mockk()

    @Before
    fun setup() {
        // Мок для getVolumeFlow()
        every { mockPreferencesManager.volumeFlow } returns flowOf(1.0f)

        // Мок для getLanguageForRiddlesFlow()
        every { mockPreferencesManager.languageForRiddlesFlow } returns flowOf("originalLanguage")

        // Мок для getAllWordTranslations()
        every { mockWordTranslationDao.getAllWordTranslations() } returns flowOf(emptyList())

        viewModel =
            HomeViewModel(mockWordTranslationDao, mockProgressRepository, mockPreferencesManager)
    }

    @Test
    fun `test correct and wrong answers are updated correctly`() = runTest {
        viewModel.correctAnswer = 5
        viewModel.wrongAnswer = 3
        viewModel.correctAnswer += 1
        viewModel.wrongAnswer += 1

        assertEquals(6, viewModel.correctAnswer)
        assertEquals(4, viewModel.wrongAnswer)
    }
}
