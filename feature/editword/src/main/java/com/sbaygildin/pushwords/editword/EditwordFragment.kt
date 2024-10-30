package com.sbaygildin.pushwords.editword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.sbaygildin.pushwords.data.model.DifficultyLevel
import com.sbaygildin.pushwords.data.model.WordTranslation
import com.sbaygildin.pushwords.editword.databinding.FragmentEditwordBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditwordFragment : Fragment() {
    private val viewModel: EditwordViewModel by viewModels()
    private val args: EditwordFragmentArgs by navArgs()
    private var _binding: FragmentEditwordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditwordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val receivedId = args.id.toLongOrNull()
        if (receivedId != null) {
            loadWordData(receivedId)
        } else {
            Toast.makeText(
                requireContext(),
                getString(com.sbaygildin.pushwords.common.R.string.invalid_id),
                Toast.LENGTH_SHORT
            ).show()
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        val difficultyLevels = DifficultyLevel.values().map { it.name }
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, difficultyLevels)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.difficultyLevelSpinner.adapter = adapter
        binding.difficultyLevelSpinner.setSelection(difficultyLevels.indexOf(DifficultyLevel.MEDIUM.name))
        binding.cancelButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        binding.saveButton.setOnClickListener {
            saveWord(receivedId)
        }
    }

    private fun loadWordData(wordId: Long) {
        viewModel.getWordTranslationById(wordId) { wordTranslation ->
            wordTranslation?.let { populateFields(it) }
                ?: run {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.word_not_found),
                        Toast.LENGTH_SHORT
                    ).show()
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
        }
    }

    private fun populateFields(word: WordTranslation) {
        binding.originalWordEditText.setText(word.originalWord)
        binding.translatedWordEditText.setText(word.translatedWord)
        binding.isLearnedCheckBox.isChecked = word.isLearned
        val difficultyLevels = DifficultyLevel.values().map { it.name }
        binding.difficultyLevelSpinner.setSelection(difficultyLevels.indexOf(word.difficultyLevel.name))
    }

    private fun saveWord(receivedId: Long?) {
        val originalWord = binding.originalWordEditText.text.toString().trim()
        val translatedWord = binding.translatedWordEditText.text.toString().trim()
        val isLearned = binding.isLearnedCheckBox.isChecked
        val difficultyLevel =
            DifficultyLevel.valueOf(binding.difficultyLevelSpinner.selectedItem.toString())
        if (validateFields(originalWord, translatedWord)) {
            receivedId?.let {
                viewModel.getWordTranslationById(it) { wordTranslation ->
                    wordTranslation?.let { word ->
                        viewModel.updateWordTranslation(
                            requireContext(),
                            word,
                            originalWord,
                            translatedWord,
                            isLearned,
                            difficultyLevel,
                            onSuccess = { requireActivity().onBackPressedDispatcher.onBackPressed() },
                            onFailure = { /* handle failure */ }
                        )
                    }
                }
            }
        }
    }

    private fun validateFields(originalWord: String, translatedWord: String): Boolean {
        var isValid = true
        if (originalWord.isEmpty()) {
            binding.originalWordInputLayout.error =
                getString(com.sbaygildin.pushwords.common.R.string.tv_this_field_is_required)
            isValid = false
        } else {
            binding.originalWordInputLayout.error = null
        }
        if (translatedWord.isEmpty()) {
            binding.translatedWordInputLayout.error =
                getString(com.sbaygildin.pushwords.common.R.string.tv_this_field_is_required)
            isValid = false
        } else {
            binding.translatedWordInputLayout.error = null
        }
        return isValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
