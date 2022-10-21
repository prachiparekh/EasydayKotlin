package com.app.easyday.screens.activities.main.reports

import com.app.easyday.R
import com.app.easyday.screens.base.BaseFragment
import com.google.android.flexbox.FlexboxLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_line.*

@AndroidEntryPoint
class LineFragment : BaseFragment<LineViewModel>() {

    companion object
    {
        val tag="Line"
    }

    val tagList= arrayListOf<String>()

    override fun getContentView() = R.layout.fragment_line

    override fun initUi() {
        tagList.add("• Door work")
        tagList.add("• Electric")
        tagList.add("• Paint")
        tagList.add("• Repairing")
        tagList.add("• Drilling")
        tagRV.adapter=TagAdapter(requireContext(),tagList)
        tagRV.layoutManager=FlexboxLayoutManager(requireContext())
    }

    override fun setObservers() {
    }


}