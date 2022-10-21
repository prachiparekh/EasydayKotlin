package com.app.easyday.screens.activities.main.home.task_detail.Discussion

import android.view.View
import androidx.navigation.Navigation
import com.app.easyday.R
import com.app.easyday.screens.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_discussion.*

class DiscussionFragment : BaseFragment<DiscussionViewModel>() {

    override fun getContentView() = R.layout.fragment_discussion

    override fun initUi() {

        add_commentTV.setOnClickListener {
            add_commentTV.visibility = View.GONE
            bottom_RL.visibility = View.VISIBLE
        }

        recordIV.setOnClickListener {
            start_recordRL.visibility = View.GONE
            stop_recordRL.visibility = View.VISIBLE
        }
        stop_recordTV.setOnClickListener {
            start_recordRL.visibility = View.VISIBLE
            stop_recordRL.visibility = View.GONE
        }

        back.setOnClickListener {
            Navigation.findNavController(requireView()).popBackStack()
        }

    }

    override fun setObservers() {

    }

}