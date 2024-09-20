package com.sbaygildin.pushwords.addword

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs

class AddwordFragment : Fragment() {

    companion object {
        fun newInstance() = AddwordFragment()
    }
//    private val args: AddwordFragmentArgs by navArgs()

    private val viewModel: AddwordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_addword, container, false)
    }
}