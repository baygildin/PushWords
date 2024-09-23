package com.sbaygildin.pushwords.home

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sbaygildin.pushwords.home.R
import com.sbaygildin.pushwords.home.databinding.FragmentHomeBinding
import com.sbaygildin.pushwords.navigation.Navigator



class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("HomeFragment", "before btn")
        binding.btnAddNewWord.setOnClickListener {
            val navController = findNavController()
            (activity as Navigator).navigateHomeToSettingsArgs("from new homeHomeFragment")
//          (activity as Navigator).navigateHomeToSettingsWoArgs()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


