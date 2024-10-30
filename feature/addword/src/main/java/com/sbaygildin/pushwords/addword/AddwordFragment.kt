package com.sbaygildin.pushwords.addword

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.sbaygildin.pushwords.addword.databinding.FragmentAddwordBinding
import com.sbaygildin.pushwords.data.model.DifficultyLevel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddwordFragment : Fragment() {
    private val viewModel: AddwordViewModel by viewModels()
    private var _binding: FragmentAddwordBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val REQUEST_CODE_OPEN_FILE = 1001
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddwordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val difficultyLevels = DifficultyLevel.values().map { it.name }
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, difficultyLevels)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.difficultyLevelSpinner.adapter = adapter
        binding.difficultyLevelSpinner.setSelection(difficultyLevels.indexOf(DifficultyLevel.MEDIUM.name))
        binding.cancelButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        binding.loadFromFileButton.setOnClickListener {
            openFilePicker()
        }
        binding.wordsLevelA1Button.setOnClickListener {
            viewModel.importWordsFromRaw(requireContext(), R.raw.a1level, DifficultyLevel.EASY)
        }
        binding.wordsLevelB1Button.setOnClickListener {
            viewModel.importWordsFromRaw(requireContext(), R.raw.b1level, DifficultyLevel.MEDIUM)
        }
        binding.wordsLevelA2Button.setOnClickListener {
            viewModel.importWordsFromRaw(requireContext(), R.raw.a2level, DifficultyLevel.EASY)
        }
        binding.wordsLevelB2Button.setOnClickListener {
            viewModel.importWordsFromRaw(requireContext(), R.raw.b2level, DifficultyLevel.MEDIUM)
        }
        binding.wordsLevelC1Button.setOnClickListener {
            viewModel.importWordsFromRaw(requireContext(), R.raw.c1level, DifficultyLevel.HARD)
        }
        binding.wordsLevelC2Button.setOnClickListener {
            viewModel.importWordsFromRaw(requireContext(), R.raw.c2level, DifficultyLevel.HARD)
        }
        binding.helpIcon.setOnClickListener {
            val helpDialog = HelpDialogFragment()
            helpDialog.show(parentFragmentManager, "HelpDialog")
        }

        binding.saveButton.setOnClickListener {
            val originalWord = binding.originalWordEditText.text.toString().trim()
            val translatedWord = binding.translatedWordEditText.text.toString().trim()
            val isLearned = binding.isLearnedCheckBox.isChecked
            val difficultyLevel =
                DifficultyLevel.valueOf(binding.difficultyLevelSpinner.selectedItem.toString())

            if (validateInput(originalWord, translatedWord)) {
                viewModel.saveWord(originalWord, translatedWord, isLearned, difficultyLevel)
                Toast.makeText(requireContext(), getString(R.string.word_added), Toast.LENGTH_SHORT)
                    .show()
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
        viewModel.importSuccess.observe(viewLifecycleOwner, Observer { success ->
            if (success) Toast.makeText(
                context,
                getString(R.string.words_imported),
                Toast.LENGTH_SHORT
            ).show()
        })
    }

    private fun validateInput(originalWord: String, translatedWord: String): Boolean {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_OPEN_FILE && resultCode == Activity.RESULT_OK) {
            data?.data?.also { uri ->
                viewModel.readWordsFromFile(requireContext(), uri)
            }
        }
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "text/plain"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(intent, REQUEST_CODE_OPEN_FILE)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}