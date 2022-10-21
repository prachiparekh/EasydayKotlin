package com.app.easyday.screens.activities.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.app.easyday.R
import com.app.easyday.databinding.FragmentTermsPolicyBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TermsPolicyFragment : Fragment() {

    var binding:FragmentTermsPolicyBinding?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        requireActivity().window?.statusBarColor = resources.getColor(R.color.navy_blue)
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_terms_policy, container, false)

        binding?.toolBar?.title = arguments?.getString("title")
        arguments?.getString("url")?.let { binding?.webView?.loadUrl(it) }

        binding?.toolBar?.setNavigationOnClickListener {
            Navigation.findNavController(requireView()).popBackStack()
        }
        return binding?.root
    }


}