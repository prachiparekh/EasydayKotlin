package com.app.easyday.screens.activities.main.more.feedback

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.navigation.Navigation
import com.app.easyday.R
import com.app.easyday.app.sources.local.interfaces.FeedBackTagInterfaceClick
import com.app.easyday.screens.base.BaseFragment
import com.app.easyday.screens.dialogs.BackTosettingDialog
import com.google.android.flexbox.FlexboxLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_feedback.*
import kotlinx.android.synthetic.main.fragment_profile.*

@AndroidEntryPoint
class FeedbackFragment : BaseFragment<FeedbackViewModel>(), FeedBackTagInterfaceClick {

    val list: ArrayList<String> = ArrayList()
    val taglist: ArrayList<String> = ArrayList()
    override fun getContentView() = R.layout.fragment_feedback
    var layoutManager: FlexboxLayoutManager? = null
    private var adapter: FeedTagsAdapter? = null

    @SuppressLint("ResourceAsColor")
    override fun initUi() {

        back.setOnClickListener {
            Navigation.findNavController(requireView()).popBackStack()
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
        val feedText = inputET.text

        val mrating = rating.rating.toString()
//        if (rating.rating != 0.0F) {
//            submit_btnRL.isEnabled = true
//            submit_btnRL.alpha = 1F
//        } else {
//            submit_btnRL.isEnabled = false
//            submit_btnRL.alpha = 0.5F
//        }

        submit_btnRL.setOnClickListener {

            viewModel.submitFeedback(
                mrating,
                taglist.toString(),
                feedText.toString()
            )

        }

        list.addAll(
            listOf(
                "Customer Support",
                "Transparency",
                "Too Slow",
                "Speed & Efficiency",
                "Improve Bugs"
            )
        )

        tags_RecV?.layoutManager = FlexboxLayoutManager(requireContext())
        adapter = FeedTagsAdapter(requireContext(), list, this)
        tags_RecV?.adapter = adapter

    }


    override fun setObservers() {
        viewModel.userFeedbackData.observe(viewLifecycleOwner) { response ->
            val str_rating = rating.rating.toString()

            if (response != null) {

                val dialog = BackTosettingDialog()
                if (rating.rating != 0.0F && !dialog.isAdded) {
                    dialog.show(childFragmentManager, "back")

                }
            }
        }
    }

    override fun onTagClick(position: Int, title_bg: RelativeLayout) {
        if (title_bg.isSelected) {
            when (position) {
                0 -> taglist.add("Customer Support")
                1 -> taglist.add("Transparency")
                2 -> taglist.add("Too Slow")
                3 -> taglist.add("Speed & Efficiency")
                4 -> taglist.add("Improve Bugs")

            }
        } else {
            when (position) {
                0 -> taglist.remove("Customer Support")
                1 -> taglist.remove("Transparency")
                2 -> taglist.remove("Too Slow")
                3 -> taglist.remove("Speed & Efficiency")
                4 -> taglist.remove("Improve Bugs")

            }
        }

    }


}