package com.app.easyday.screens.activities.main.home.search_task

import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.app.easyday.R
import com.app.easyday.app.sources.local.interfaces.SearchHintInterface
import com.app.easyday.app.sources.local.interfaces.TaskInterfaceClick
import com.app.easyday.app.sources.local.prefrences.AppPreferencesDelegates
import com.app.easyday.screens.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_search.*

@AndroidEntryPoint
class SearchFragment : BaseFragment<SearchViewModel>() {

    override fun getContentView() = R.layout.fragment_search

    override fun initUi() {
        requireActivity().window?.statusBarColor = resources.getColor(R.color.navy_blue)

        mBack.setOnClickListener {
            Navigation.findNavController(requireView()).popBackStack()
        }

        val localList = AppPreferencesDelegates.get().searchList.toMutableList()

        val searchHintAdapter = SearchHintAdapter(
            requireContext(),
            localList as ArrayList<String>,
            object : SearchHintInterface {
                override fun onHintClick(title: String) {
                    mSearch.setText(title)
                    searchRV.isVisible = false
                    taskRV.isVisible = true
                }
            })
        searchRV.adapter = searchHintAdapter


        mSearch.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val setList = AppPreferencesDelegates.get().searchList.toMutableList()
                    setList.add(mSearch.text.toString())
                    AppPreferencesDelegates.get().searchList = setList.toSet()
                    searchHintAdapter.addItem(mSearch.text.toString())
                    return true
                }
                return false
            }
        })
    }

    override fun setObservers() {
        viewModel.taskList.observe(viewLifecycleOwner) {
            val adapter = it?.let { it1 ->
                SearchAdapter(requireContext(), it1, object :
                    TaskInterfaceClick {
                    override fun onTaskClick() {
                        val action = SearchFragmentDirections.searchToTaskDetails()
                        val nav: NavController = Navigation.findNavController(requireView())
                        if (nav.currentDestination != null && nav.currentDestination?.id == R.id.searchFragment) {
                            nav.navigate(action)
                        }
                    }

                    override fun onDiscussionClick() {
                    }

                    override fun onSearchResult(count: Int) {
                        resultCount.text =
                            requireContext().resources.getString(R.string.result_found, count)
                    }

                })
            }
            taskRV.adapter = adapter

            mSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun afterTextChanged(newText: Editable?) {
                    if (newText.isNullOrEmpty()) {
                        searchRV.isVisible = true
                        taskRV.isVisible = false
                        resultCount.text =
                            requireContext().resources.getString(R.string.recent_searches)
                    } else {
                        searchRV.isVisible = false
                        taskRV.isVisible = true
                        adapter?.filter?.filter(newText)
                    }
                }

            })

            taskRV.setOnClickListener {

            }
        }
    }


}