package com.sbaygildin.pushwords.home

import android.media.MediaPlayer
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.sbaygildin.pushwords.data.model.WordTranslation
import com.sbaygildin.pushwords.home.databinding.FragmentHomeBinding
import com.sbaygildin.pushwords.navigation.Navigator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding
    private var mediaPlayer: MediaPlayer? = null
    private var roundCounter = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.startButton?.visibility = View.VISIBLE
        binding?.wordButtonsContainer?.visibility = View.GONE



        binding?.startButton?.setOnClickListener {
            startQuiz()
        }


        viewModel.resetCounters()
        Log.d("blinking", "onViewCreated")



        binding?.addWordButton?.setOnClickListener {
            (activity as Navigator).navigateHomeToAddword()
        }
    }
    private fun startQuiz() {
        roundCounter = 0
        binding?.startButton?.visibility = View.GONE
        binding?.wordButtonsContainer?.visibility = View.VISIBLE
        setupQuiz()
    }


    private fun setupQuiz() {
        Log.d("blinking", "private fun setupQuiz()")
        val languageForRiddles = listOf("originalLanguage", "translationLanguage").random()

        if (roundCounter == 20) {
            binding?.startButton?.visibility = View.VISIBLE
            binding?.startButton?.text = "Продолжить"
            binding?.wordButtonsContainer?.visibility = View.GONE
        } else {
            roundCounter++
            if (viewModel.getCachedWords().isEmpty()) {
                binding?.tvLetsStartQuiz?.text = "Нет доступных слов для повторения"
                return
            }

            if (viewModel.getCachedWords().size < 4) {
                binding?.tvLetsStartQuiz?.text =
                    "Добавьте еще слов или откройте файл со словами"
                return
            }

            val correctWord = viewModel.getCachedWords().random()
            val randomWords =
                viewModel.getCachedWords().filter { it != correctWord }.shuffled().take(3).toMutableList()
            randomWords.add(correctWord)
            randomWords.shuffle()

            val isOriginalToTranslate = languageForRiddles == "originalLanguage"
            binding?.tvWordToGuess?.text =
                if (isOriginalToTranslate) correctWord.originalWord else correctWord.translatedWord
            val correctTranslation =
                if (isOriginalToTranslate) correctWord.translatedWord else correctWord.originalWord

            val options = listOf(
                binding?.btnOption1,
                binding?.btnOption2,
                binding?.btnOption3,
                binding?.btnOption4
            )

            options.forEach { button ->
                button?.isEnabled = true
                button?.alpha = 1f
            }

            var firstAttempt = true

            options.forEachIndexed { index, button ->
                val word = randomWords[index]
                val displayText =
                    if (isOriginalToTranslate) word.translatedWord else word.originalWord
                button?.text = displayText
                button?.setOnClickListener {

                    lifecycleScope.launch {
                        if (displayText == correctTranslation) {
                            Log.d("blinking", "clicking right button")
                            options.forEach { it?.isEnabled = false }
                            viewModel.correctAnswer += 1
                            playCorrectAnswerSound()


                            if (firstAttempt) {
                                viewModel.guessedRightAway += 1

                                if (!correctWord.isLearned) {
                                    viewModel.learnedWords += 1
                                    //viewModel.updateWordAsLearned(correctWord.id)
                                }
                            }

                            viewModel.recordProgressData(
                                viewModel.correctAnswer,
                                viewModel.wrongAnswer,
                                viewModel.guessedRightAway,
                                viewModel.learnedWords
                            )

                            binding?.tvLetsStartQuiz?.text = "Правильно!\nВы молодец✨"
                            button?.animate()
                                ?.translationYBy(-600f)
                                ?.alpha(0f)
                                ?.setDuration(1000)
                                ?.withLayer()
                                ?.withEndAction {
                                    button.translationY = 0f
                                    button.alpha = 1f
                                    viewModel.resetCounters()
                                    Log.d("blinking", "before delay")

                                }
                                ?.start()
                            Log.d("blinking", "after animation")
                            lifecycleScope.launch {
                                delay(500)
                                Log.d("blinking", "after delay")
                                setupQuiz()
                                Log.d("blinking", "after delay and setupQuiz")
                            }


                        } else {
                            firstAttempt = false
                            Log.d("blinking", "clicking wrong button")
                            viewModel.wrongAnswer += 1
                            binding?.tvLetsStartQuiz?.text =
                                "Неправильный ответ⛔\nПопробуйте ещё раз"
                            button?.isEnabled = false
                            button?.animate()
                                ?.translationYBy(+300f)
                                ?.alpha(0f)
                                ?.rotation(360f)
                                ?.setDuration(1000)
                                ?.withLayer()
                                ?.withEndAction {
                                    button.translationY = 0f
                                    button.rotation = 0f
                                }
                                ?.start()
                        }
                    }
                }
            }
        }
    }


    private fun playCorrectAnswerSound() {
        lifecycleScope.launch {
            viewModel.volume.collectLatest { volume ->
                mediaPlayer = MediaPlayer.create(context, R.raw.correct_answer_sound)
                mediaPlayer?.setVolume(volume, volume)
                mediaPlayer?.start()
                mediaPlayer?.setOnCompletionListener {
                    it.release()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.release()
        _binding = null
    }
}
