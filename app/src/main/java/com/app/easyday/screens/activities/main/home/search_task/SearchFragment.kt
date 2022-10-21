package com.app.easyday.screens.activities.main.home.search_task

import android.text.Editable
import android.text.TextWatcher
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.app.easyday.R
import com.app.easyday.screens.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_search.*

@AndroidEntryPoint
class SearchFragment : BaseFragment<SearchViewModel>(){


    interface TaskInterfaceClick{
        fun onTaskClick()
        fun onDiscussionClick()
    }

    override fun getContentView() = R.layout.fragment_search

    override fun initUi() {
        requireActivity().window?.statusBarColor = resources.getColor(R.color.navy_blue)

        mBack.setOnClickListener {
            Navigation.findNavController(requireView()).popBackStack()
        }
    }

    override fun setObservers() {
        viewModel.taskList.observe(viewLifecycleOwner) {
            val adapter = it?.let { it1 -> SearchAdapter(requireContext(), it1,object :TaskInterfaceClick {
                override fun onTaskClick() {
                    val action = SearchFragmentDirections.searchToTaskDetails()
                    val nav: NavController = Navigation.findNavController(requireView())
                    if (nav.currentDestination != null && nav.currentDestination?.id == R.id.searchFragment) {
                        nav.navigate(action)
                    }
                }

                override fun onDiscussionClick() {
                    TODO("Not yet implemented")
                }

            }) }
            taskRV.adapter = adapter

            mSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun afterTextChanged(newText: Editable?) {
                    adapter?.filter?.filter(newText)
                }

            })

            taskRV.setOnClickListener {

            }
        }
    }


}