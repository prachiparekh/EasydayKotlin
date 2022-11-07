package com.app.easyday.screens.activities.main.more.feedback

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
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
        var button_background : Int = 1
        cs_Tv.setOnClickListener {

            cs_Tv.isSelected = !cs_Tv.isSelected

        }
        tr_Tv.setOnClickListener {
            tr_Tv.isSelected = !tr_Tv.isSelected
        }
        tslo_Tv.setOnClickListener {
            tslo_Tv.isSelected = !tslo_Tv.isSelected
        }
        se_Tv.setOnClickListener {
            se_Tv.isSelected = !se_Tv.isSelected
        }
        ibg_Tv.setOnClickListener {
            ibg_Tv.isSelected = !ibg_Tv.isSelected
        }
        inputET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                if (p0 != null) {
                    inputed_length_Tv.text = "${p0.length}/500"
                }
            }

        })
    }

    override fun setObservers() {

    }

}