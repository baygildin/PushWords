package com.sbaygildin.pushwords.settings

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.sbaygildin.pushwords.settings.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModels()
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeSettings()
        setupListeners()
    }

    private fun observeSettings() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.darkMode.collectLatest { isDarkMode ->
                        binding.isDarkModeEnabled.isChecked = isDarkMode
                    }
                }
                launch {
                    viewModel.language.collectLatest { selectedLanguage ->
                        val languages = resources.getStringArray(R.array.languages)
                        val index = languages.indexOf(selectedLanguage)
                        if (index >= 0) {
                            binding.spinnerLanguage.setSelection(index)
                        }
                    }
                }
                launch {
                    viewModel.notifications.collectLatest { isEnabled ->
                        binding.switchNotifications.isChecked = isEnabled
                    }
                }
                launch {
                    viewModel.volume.collectLatest { volume ->
                        val progress = (volume * 100).toInt()
                        binding.seekbarVolume.progress = progress
                    }
                }
                launch {
                    viewModel.userName.collectLatest { name ->
                        binding.edittextUserName.setText(name)
                        binding.tvUserNameDisplay.text = getString(R.string.txt_your_name_display, name)
                    }
                }
                launch {
                    viewModel.languageForRiddles.collectLatest { languageForRiddles ->
                        when (languageForRiddles) {
                            "originalLanguage" -> binding.radioOptionOriginalLanguage.isChecked = true
                            "translationLanguage" -> binding.radioOptionTranslationLanguage.isChecked = true
                            else -> binding.radioOptionRandomLanguage.isChecked = true
                        }
                    }
                }

            }
        }
    }

    private fun setupListeners() {
        binding.spinnerLanguage.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedLanguage = resources.getStringArray(R.array.languages)[position]
                    viewModel.setLanguage(selectedLanguage)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }

        binding.spinnerLanguage.adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.languages,
            android.R.layout.simple_spinner_item
        )
            .apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
        binding.saveNameButton.setOnClickListener {

            val currentName = binding.edittextUserName.text.toString().trim()
            if (currentName != viewModel.userName.value ) {
                viewModel.setUserName(currentName)
                binding.edittextUserName.setText("")
            }
        }
        binding.radioGroupLanguageForRiddles.setOnCheckedChangeListener { _, checkId ->
            val selectedLanguage = when (checkId) {
                R.id.radio_option_original_language -> "originalLanguage"
                R.id.radio_option_translation_language -> "translationLanguage"
                else -> "random"
            }
            viewModel.setLanguageForRiddles(selectedLanguage)
        }





        binding.isDarkModeEnabled.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setDarkMode(isChecked)
            val notificationController = activity as? NotificationController
            notificationController?.updateTheme(isChecked)
        }

        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            val notificationController = activity as? NotificationController
            viewModel.setNotifications(isChecked)
            if (isChecked) {
                notificationController?.scheduleQuizNotification()
            } else {
                notificationController?.cancelQuizNotification()
            }
        }

        binding.seekbarVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekbar: SeekBar?, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val finalVolume = seekBar?.progress?.div(100f) ?: 0.5f
                viewModel.setVolume(finalVolume)

            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}





