package com.sbaygildin.pushwords.settings

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.navigation.fragment.findNavController

import com.sbaygildin.pushwords.settings.databinding.FragmentSettingsBinding

import com.sbaygildin.pushwords.settings.R

import com.sbaygildin.pushwords.navigation.Navigator

class SettingsFragment : Fragment() {
    private val viewModel: SettingsViewModel by viewModels()
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val args: SettingsFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val exampleArg = args.exampleArg // Получаете переданный аргумент
        exampleArg.also { binding.tvSettingsTitle.text = it }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}