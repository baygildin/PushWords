package com.sbaygildin.pushwords.home

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        binding.btnAddNewWord.setOnClickListener {
            (activity as Navigator).navigateHomeToAddwordWoArgs()
        }
        val correctTranslation = "Правильный перевод"
        val wrongTranslations = listOf("Неправильный 1", "Неправильный 2", "Неправильный 3")
        val allTranslations = (wrongTranslations + correctTranslation).shuffled()
        binding.btnOption1.text = allTranslations[0]
        binding.btnOption2.text = allTranslations[1]
        binding.btnOption3.text = allTranslations[2]
        binding.btnOption4.text = allTranslations[3]
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


