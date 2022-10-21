package com.app.easyday.screens.activities.main.more.activityLog

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.app.easyday.screens.base.BaseFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.app.easyday.R
import kotlinx.android.synthetic.main.fragment_activity_log.*


class ActivityLogFragment : BaseFragment<ActivityLogViewModel>() {



    override fun getContentView() = R.layout.fragment_activity_log

    override fun initUi() {

        val dot = requireContext().resources.getString(R.string.dot)
        time?.text = requireContext().resources.getString(R.string.hyderabad, dot)
        time2?.text = requireContext().resources.getString(R.string.hyderabad, dot)
        time3?.text = requireContext().resources.getString(R.string.hyderabad, dot)
        time4?.text = requireContext().resources.getString(R.string.hyderabad, dot)
        time5?.text = requireContext().resources.getString(R.string.hyderabad, dot)
        time6?.text = requireContext().resources.getString(R.string.hyderabad, dot)
        time7?.text = requireContext().resources.getString(R.string.hyderabad, dot)

        back.setOnClickListener {
            Navigation.findNavController(requireView()).popBackStack()
        }
    }

    override fun setObservers() {

    }

}