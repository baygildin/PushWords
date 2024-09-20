package com.sbaygildin.pushwords.wordlist

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sbaygildin.pushwords.wordlist.R

class WordlistFragment : Fragment() {

    companion object {
        fun newInstance() = WordlistFragment()
    }

    private val viewModel: WordlistViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_wordlist, container, false)
    }
}