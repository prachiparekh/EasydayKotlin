package com.app.easyday.screens.activities.main.more.feedback

import android.annotation.SuppressLint
import androidx.navigation.Navigation
import com.app.easyday.R
import com.app.easyday.screens.base.BaseFragment
import com.app.easyday.screens.dialogs.BackTosettingDialog
import kotlinx.android.synthetic.main.fragment_feedback.*


class FeedbackFragment : BaseFragment<FeedbackViewModel>() {


    override fun getContentView() = R.layout.fragment_feedback

    @SuppressLint("ResourceAsColor")
    override fun initUi() {

        submit_btnRL.setOnClickListener {
            val dialog = BackTosettingDialog()
            if (!dialog.isAdded) {
                dialog.show(childFragmentManager, "back")
            }
        }

        back.setOnClickListener {
            Navigation.findNavController(requireView()).popBackStack()
        }
    }

    override fun setObservers() {

    }

}