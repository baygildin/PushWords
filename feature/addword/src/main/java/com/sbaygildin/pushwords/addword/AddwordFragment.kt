package com.sbaygildin.pushwords.addword

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContentProviderCompat
import androidx.lifecycle.Observer
import com.sbaygildin.pushwords.addword.databinding.FragmentAddwordBinding
import com.sbaygildin.pushwords.data.model.DifficultyLevel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddwordFragment : Fragment() {
    private val viewModel: AddwordViewModel by viewModels()
    private var _binding: FragmentAddwordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddwordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val difficultyLevels  = DifficultyLevel.values().map { it.name }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, difficultyLevels)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.difficultyLevelSpinner.adapter = adapter
        binding.difficultyLevelSpinner.setSelection(difficultyLevels.indexOf(DifficultyLevel.MEDIUM.name))

        binding.cancelButton.setOnClickListener{
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.loadFromFileButton.setOnClickListener {
            openFilePicker()
        }

        binding.saveButton.setOnClickListener {
            val originalWord = binding.originalWordEditText.text.toString().trim()
            val translatedWord = binding.translatedWordEditText.text.toString().trim()
            val isLearned = binding.isLearnedCheckBox.isChecked
            val difficultyLevel = DifficultyLevel.valueOf(binding.difficultyLevelSpinner.selectedItem.toString())

            if (validateInput(originalWord, translatedWord)) {
                viewModel.saveWord(originalWord, translatedWord, isLearned, difficultyLevel)
                Toast.makeText(requireContext(), "Word added!", Toast.LENGTH_SHORT).show()
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
        viewModel.importSuccess.observe(viewLifecycleOwner, Observer { success ->
            if (success) Toast.makeText(context, "Words imported!", Toast.LENGTH_SHORT).show()
        })
    }
    private fun validateInput(originalWord: String, translatedWord: String): Boolean {
        var isValid = true

        if (originalWord.isEmpty()) {
            binding.originalWordInputLayout.error = "This field is required"
            isValid = false
        } else {
            binding.originalWordInputLayout.error = null
        }

        if (translatedWord.isEmpty()) {
            binding.translatedWordInputLayout.error = "This field is required"
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

    companion object {
        const val REQUEST_CODE_OPEN_FILE = 1001
    }
}
