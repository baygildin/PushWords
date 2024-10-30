package com.sbaygildin.pushwords.wordlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.sbaygildin.pushwords.navigation.Navigator
import com.sbaygildin.pushwords.wordlist.databinding.FragmentWordlistBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WordlistFragment : Fragment(R.layout.fragment_wordlist) {
    private val viewModel: WordlistViewModel by viewModels()
    private var _binding: FragmentWordlistBinding? = null
    private val binding get() = _binding!!

    private val wordlistAdapter = WordlistAdapter(
        onEditClick = { word ->
            (activity as Navigator).navigateWordlistToEdit(word.id.toString())
        },
        onDeleteClick = { word ->
            lifecycleScope.launch {
                val rowsDeleted = viewModel.deleteWord(word.id)
                if (rowsDeleted > 0) {
                    Toast.makeText(
                        requireContext(),
                        getString(
                            R.string.word_deleted_message,
                            word.originalWord,
                            word.translatedWord
                        ),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.toast_delete_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWordlistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        lifecycleScope.launch {
            viewModel.wordList.collect { words ->
                wordlistAdapter.setWords(words)
            }
        }
        binding.addWordButton.setOnClickListener {
            (activity as Navigator).navigateWordlistToAddword()
        }
    }

    private fun setupRecyclerView() {
        binding.wordListRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = wordlistAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
