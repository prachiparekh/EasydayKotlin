package com.app.easyday.screens.activities.main.more.notepad

import android.annotation.TargetApi
import android.content.ContentResolver
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.app.easyday.R
import com.app.easyday.databinding.FragmentNotepadListBinding
import kotlinx.android.synthetic.main.fragment_notepad_list.*
import java.net.URLEncoder

class NotepadListFragment : Fragment() {

var binding: FragmentNotepadListBinding?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_notepad_list, container, false)

        binding?.createNote?.setOnClickListener {
            val direction = NotepadListFragmentDirections.notepadListToCreateNote()
            Navigation.findNavController(requireView()).navigate(direction)
        }


        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window?.statusBarColor = resources.getColor(R.color.navy_blue)
    }


}