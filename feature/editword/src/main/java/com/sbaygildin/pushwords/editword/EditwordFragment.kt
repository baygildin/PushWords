package com.sbaygildin.pushwords.editword

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditwordFragment : Fragment() {
    private val viewModel: EditwordViewModel by viewModels()
    private val args: EditwordFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val receivedId = args.id.toLongOrNull()
        if (receivedId == null) {
            Toast.makeText(
                requireContext(),
                getString(com.sbaygildin.pushwords.common.R.string.invalid_id),
                Toast.LENGTH_SHORT
            ).show()
            requireActivity().onBackPressedDispatcher.onBackPressed()
            return View(requireContext())
        }
        Log.d("IDDDDDD", "$receivedId")

        return ComposeView(requireContext()).apply {
            setContent {
                EditWordScreen(
                    viewModel = viewModel,
                    wordId = receivedId,
                    onBack = {
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }
                )
            }
        }
    }
}
