package com.sbaygildin.pushwords.addword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class HelpDialogFragment: BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
        ) : View? {
        return inflater.inflate(R.layout.fragment_help_dialog, container, false)
    }
}