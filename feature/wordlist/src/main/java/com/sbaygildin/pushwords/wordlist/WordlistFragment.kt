package com.sbaygildin.pushwords.wordlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.sbaygildin.pushwords.navigation.Navigator
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class WordlistFragment : Fragment() {
    private val viewModel: WordlistViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                WordlistScreen(
                    viewModel = viewModel,

                    onAddWordClick = {
                        (activity as Navigator).navigateWordlistToAddword()
                    },
                    onEditClick = { word ->
                        (activity as Navigator).navigateWordlistToEdit(word.id.toString())
                    },
                    onDeleteClick = { word ->
                        viewModel.deleteWord(word.id)
                    }
                )
            }
        }
    }
}

