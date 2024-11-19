package com.sbaygildin.pushwords.home

import android.content.Context
import android.media.MediaPlayer
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sbaygildin.pushwords.data.model.WordTranslation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onAddWordClicked: () -> Unit,
) {
    val context = LocalContext.current
    val volume by viewModel.volume.collectAsState(initial = 1f)
    var roundCounter by remember { mutableStateOf(0) }
    var firstAttempt by remember { mutableStateOf(true) }

    val words by viewModel.wordCache.collectAsState()
    val languageForRiddles by viewModel.languageForRiddles.collectAsState(initial = "originalLanguage")
    val coroutineScope = rememberCoroutineScope()
    val isQuizStarted = remember { mutableStateOf(false) }
    val showStats = remember { mutableStateOf(false) }
    val quizState = remember { mutableStateOf<QuizState?>(null) }
    val mediaPlayer = remember { mutableStateOf<MediaPlayer?>(null) }
    val quizMessage = remember { mutableStateOf(context.getString(R.string.txt_lets_start_quiz)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(bottom = 64.dp, top = 64.dp)
    ) {
        Text(
            text = stringResource(id = com.sbaygildin.pushwords.common.R.string.app_name),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 64.dp)
        )

        Spacer(modifier = Modifier.height(64.dp))
        Text(
            text = quizMessage.value,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (!isQuizStarted.value || showStats.value) {
            Button(
                onClick = {
                    isQuizStarted.value = true
                    showStats.value = false
                    roundCounter = 0
                    viewModel.resetCounters()
                    viewModel.updateCache()
                    quizMessage.value = ""
                    SetupQuiz(
                        context = context,
                        roundCounterState = { roundCounter },
                        updateRoundCounter = { roundCounter = it },
                        updateFirstAttempt = { firstAttempt = it },
                        quizState = quizState,
                        languageForRiddles = languageForRiddles,
                        quizMessageState = quizMessage,
                        words = words
                    )
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = if (showStats.value) stringResource(id = com.sbaygildin.pushwords.common.R.string.txt_continue) else stringResource(
                        id = R.string.txt_start
                    )
                )
            }
        } else {
            quizState.value?.let { state ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = state.wordToGuess,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    val options = state.options
                    val optionStates = remember(state) {
                        options.map { mutableStateOf(OptionState()) }
                    }

                    val optionChunks = options.zip(optionStates).chunked(2)

                    optionChunks.forEach { chunk ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            chunk.forEach { (option, optionState) ->
                                val defaultButtonColor = MaterialTheme.colorScheme.primaryContainer
                                val backgroundColor by animateColorAsState(
                                    targetValue = when {
                                        optionState.value.isCorrect -> Color.Green
                                        optionState.value.isWrong -> Color.Red
                                        else -> defaultButtonColor
                                    }
                                )

                                val scale = remember { Animatable(1f) }

                                LaunchedEffect(optionState.value.isCorrect) {
                                    if (optionState.value.isCorrect) {
                                        scale.animateTo(
                                            targetValue = 1.2f,
                                            animationSpec = tween(
                                                durationMillis = 200,
                                                easing = LinearOutSlowInEasing
                                            )
                                        )
                                        scale.animateTo(
                                            targetValue = 1f,
                                            animationSpec = tween(
                                                durationMillis = 200,
                                                easing = LinearOutSlowInEasing
                                            )
                                        )
                                    }
                                }

                                val shakeOffset = remember { Animatable(0f) }

                                LaunchedEffect(optionState.value.isWrong) {
                                    if (optionState.value.isWrong) {
                                        shakeOffset.animateTo(
                                            targetValue = 0f,
                                            animationSpec = keyframes {
                                                durationMillis = 200
                                                -10f at 0
                                                10f at 100
                                                -10f at 200
                                                10f at 300
                                                -10f at 400
                                                0f at 500
                                            }
                                        )
                                    }
                                }

                                Button(
                                    onClick = {
                                        coroutineScope.launch {
                                            if (option == state.correctTranslation) {
                                                optionState.value = optionState.value.copy(
                                                    isSelected = true,
                                                    isCorrect = true,
                                                    isEnabled = false
                                                )
                                                optionStates.forEach { otherOptionState ->
                                                    if (otherOptionState != optionState) {
                                                        otherOptionState.value =
                                                            otherOptionState.value.copy(isEnabled = false)
                                                    }
                                                }

                                                viewModel.correctAnswer += 1
                                                playCorrectAnswerSound(context, volume, mediaPlayer)

                                                var learnedWord = 0
                                                var guessedRightAwayCount = 0

                                                if (firstAttempt) {
                                                    viewModel.guessedRightAway += 1
                                                    guessedRightAwayCount = 1

                                                    if (!state.correctWord.isLearned) {
                                                        viewModel.learnedWords += 1
                                                        learnedWord = 1
                                                        viewModel.updateWordAsLearned(state.correctWord.id)
                                                    }
                                                }

                                                viewModel.recordProgressData(
                                                    correct = 1,
                                                    wrong = 0,
                                                    guessedRightAway = guessedRightAwayCount,
                                                    learnedWords = learnedWord
                                                )

                                                quizMessage.value =
                                                    context.getString(R.string.txt_correct_answer)
                                                delay(700)
                                                if (roundCounter >= 20) {
                                                    showStats.value = true
                                                    isQuizStarted.value = false
                                                    quizMessage.value = context.getString(
                                                        R.string.txt_word_to_guess_stats,
                                                        viewModel.correctAnswer,
                                                        viewModel.wrongAnswer,
                                                        viewModel.guessedRightAway,
                                                        viewModel.learnedWords
                                                    )
                                                } else {
                                                    SetupQuiz(
                                                        context = context,
                                                        roundCounterState = { roundCounter },
                                                        updateRoundCounter = { roundCounter = it },
                                                        updateFirstAttempt = { firstAttempt = it },
                                                        quizState = quizState,
                                                        languageForRiddles = languageForRiddles,
                                                        quizMessageState = quizMessage,
                                                        words = words
                                                    )
                                                }
                                            } else {
                                                optionState.value = optionState.value.copy(
                                                    isSelected = true,
                                                    isWrong = true,
                                                    isEnabled = false
                                                )
                                                firstAttempt = false
                                                viewModel.wrongAnswer += 1

                                                viewModel.recordProgressData(
                                                    correct = 0,
                                                    wrong = 1,
                                                    guessedRightAway = 0,
                                                    learnedWords = 0
                                                )

                                                quizMessage.value =
                                                    context.getString(R.string.txt_wrong_answer)
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(4.dp)
                                        .offset(x = shakeOffset.value.dp)
                                        .scale(scale.value),
                                    enabled = optionState.value.isEnabled,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = backgroundColor
                                    )
                                ) {
                                    Text(text = option)
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        FloatingActionButton(
            onClick = {
                onAddWordClicked()
            },
            modifier = Modifier
                .align(Alignment.End)
                .padding(bottom = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(id = com.sbaygildin.pushwords.common.R.string.add_word_button)
            )
        }
    }
}

fun SetupQuiz(
    context: Context,
    words: Array<WordTranslation>,
    roundCounterState: () -> Int,
    updateRoundCounter: (Int) -> Unit,
    updateFirstAttempt: (Boolean) -> Unit,
    quizState: MutableState<QuizState?>,
    languageForRiddles: String,
    quizMessageState: MutableState<String>,
) {
    val words = words
    if (words.isEmpty()) {
        quizMessageState.value =
            context.getString(com.sbaygildin.pushwords.common.R.string.no_words_for_repetition)
        return
    }

    if (words.size < 4) {
        quizMessageState.value =
            context.getString(com.sbaygildin.pushwords.common.R.string.add_more_words)
        return
    }

    val correctWord = words.random()
    val randomWords = words.filter { it != correctWord }.shuffled().take(3).toMutableList()
    randomWords.add(correctWord)
    randomWords.shuffle()

    val isOriginalToTranslate = languageForRiddles == "originalLanguage"
    val wordToGuess =
        if (isOriginalToTranslate) correctWord.originalWord else correctWord.translatedWord
    val correctTranslation =
        if (isOriginalToTranslate) correctWord.translatedWord else correctWord.originalWord

    val options = randomWords.map { word ->
        if (isOriginalToTranslate) word.translatedWord else word.originalWord
    }

    updateRoundCounter(roundCounterState() + 1)
    updateFirstAttempt(true)

    quizState.value = QuizState(
        wordToGuess = wordToGuess,
        correctTranslation = correctTranslation,
        options = options,
        correctWord = correctWord
    )

    quizMessageState.value = ""
}

data class QuizState(
    val wordToGuess: String,
    val correctTranslation: String,
    val options: List<String>,
    val correctWord: WordTranslation,
)

fun playCorrectAnswerSound(
    context: Context,
    volume: Float,
    mediaPlayerState: MutableState<MediaPlayer?>,
) {
    try {
        val mediaPlayer = MediaPlayer.create(context, R.raw.correct_answer_sound)
        mediaPlayer.setVolume(volume, volume)
        mediaPlayer.start()
        mediaPlayer.setOnCompletionListener {
            it.release()
            mediaPlayerState.value = null
        }
        mediaPlayerState.value = mediaPlayer
    } catch (e: Exception) {
        Toast.makeText(context, "Error playing sound: $e", Toast.LENGTH_SHORT).show()
    }
}


data class OptionState(
    val isSelected: Boolean = false,
    val isCorrect: Boolean = false,
    val isWrong: Boolean = false,
    val isEnabled: Boolean = true,
)