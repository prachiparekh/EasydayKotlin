package com.app.easyday.screens.activities.main.home.task_detail.discussion

import android.text.Editable
import android.text.TextWatcher
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import com.app.easyday.R
import com.app.easyday.screens.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_discussion.*

class DiscussionFragment : BaseFragment<DiscussionViewModel>() {

    override fun getContentView() = R.layout.fragment_discussion

    override fun initUi() {

        add_commentTV.setOnClickListener {
            add_commentTV.isVisible = false
            bottom_RL.isVisible = true
        }

        recordBtn.setOnClickListener {
            start_recordRL.isVisible = false
            stop_recordRL.isVisible = true
        }
        stop_recordTV.setOnClickListener {
            start_recordRL.isVisible = true
            stop_recordRL.isVisible = false
        }

        back.setOnClickListener {
            Navigation.findNavController(requireView()).popBackStack()
        }

        commentET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(newText: Editable?) {
                if (newText.isNullOrEmpty()) {
                    recordBtn.isVisible = true
                    cta.isVisible = false
                } else {
                    recordBtn.isVisible = false
                    cta.isVisible = true
                }
            }
        })

        cta.setOnClickListener {
//            viewModel.addComment()
        }

    }

    override fun setObservers() {

    }

}