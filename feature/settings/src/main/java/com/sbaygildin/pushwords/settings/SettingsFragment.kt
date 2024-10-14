package com.sbaygildin.pushwords.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.sbaygildin.pushwords.settings.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

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
                    viewModel.notifications.collectLatest { isEnabled ->
                        binding.switchNotifications.isChecked = isEnabled
                    }
                }
                launch {
                    viewModel.notificationsInterval.collectLatest { interval ->
                        val intervalIndex = when (interval) {
                            TimeUnit.MINUTES.toMillis(15) -> 0
                            TimeUnit.MINUTES.toMillis(30) -> 1
                            TimeUnit.HOURS.toMillis(1) -> 2
                            TimeUnit.HOURS.toMillis(6) -> 3
                            TimeUnit.DAYS.toMillis(1) -> 4
                            else -> 4
                        }
                        binding.spinnerNotificationsFrequency.setSelection(intervalIndex)
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
                        binding.tvUserNameDisplay.text =
                            getString(R.string.txt_your_name_display, name)
                    }
                }
                launch {
                    viewModel.languageForRiddles.collectLatest { languageForRiddles ->
                        when (languageForRiddles) {
                            "originalLanguage" -> binding.radioOptionOriginalLanguage.isChecked =
                                true

                            "translationLanguage" -> binding.radioOptionTranslationLanguage.isChecked =
                                true

                            else -> binding.radioOptionRandomLanguage.isChecked = true
                        }
                    }
                }
                launch {
                    viewModel.isQuietModeEnabled.collectLatest { isQuietModeEnabled ->
                        binding.checkboxQuietMode.isChecked = isQuietModeEnabled
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        binding.saveNameButton.setOnClickListener {
            val currentName = binding.edittextUserName.text.toString().trim()
            if (currentName != viewModel.userName.value) {
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
        binding.spinnerNotificationsFrequency.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val intervals = resources.getStringArray(R.array.notification_frequencies)
                    val selectedIntervals = when (position) {
                        0 -> TimeUnit.MINUTES.toMillis(15)
                        1 -> TimeUnit.MINUTES.toMillis(30)
                        2 -> TimeUnit.HOURS.toMillis(1)
                        3 -> TimeUnit.HOURS.toMillis(6)
                        4 -> TimeUnit.DAYS.toMillis(1)
                        else -> TimeUnit.DAYS.toMillis(1)
                    }
                    viewModel.setNotificationInterval(selectedIntervals)
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }
        binding.isDarkModeEnabled.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setDarkMode(isChecked)
            val notificationController = activity as? NotificationController
            notificationController?.updateTheme(isChecked)
        }
        binding.checkboxQuietMode.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setQuietModeEnabled(isChecked)
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





