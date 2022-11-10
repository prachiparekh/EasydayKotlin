package com.app.easyday.screens.activities.main.more.feedback

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.RelativeLayout
import androidx.navigation.Navigation
import com.app.easyday.R
import com.app.easyday.app.sources.local.interfaces.FeedBackTagInterfaceClick
import com.app.easyday.screens.base.BaseFragment
import com.app.easyday.screens.dialogs.BackTosettingDialog
import com.google.android.flexbox.FlexboxLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_feedback.*

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

        /* cs_Tv.setOnClickListener {
             cs_Tv.isSelected = !cs_Tv.isSelected
             if (cs_Tv.isSelected) {
                 list.add("Customer Support")
                 cs_Tv.isSelected = true
             } else {
                 cs_Tv.isSelected = false
                 list.remove("Customer Support")
             }

         }
         tr_Tv.setOnClickListener {
             tr_Tv.isSelected = !tr_Tv.isSelected
             if (tr_Tv.isSelected) {
                 list.add("Transparency")
                 tr_Tv.isSelected = true
             } else {
                 tr_Tv.isSelected = false
                 list.remove("Transparency")
             }
         }
         tslo_Tv.setOnClickListener {
             tslo_Tv.isSelected = !tslo_Tv.isSelected
             if (tslo_Tv.isSelected) {
                 list.add("Too Slow")
                 tslo_Tv.isSelected = true
             } else {
                 tslo_Tv.isSelected = false
                 list.remove("Too Slow")
             }
         }
         se_Tv.setOnClickListener {
             se_Tv.isSelected = !se_Tv.isSelected
             if (se_Tv.isSelected) {
                 list.add("Speed & Efficiency")
                 se_Tv.isSelected = true
             } else {
                 se_Tv.isSelected = false
                 list.remove("Speed & Efficiency")
             }
         }
         ibg_Tv.setOnClickListener {
             ibg_Tv.isSelected = !ibg_Tv.isSelected
             if (ibg_Tv.isSelected) {
                 list.add("Improve Bugs")
                 ibg_Tv.isSelected = true
             } else {
                 ibg_Tv.isSelected = false
                 list.remove("Improve Bugs")
             }
         }*/
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

        submit_btnRL.setOnClickListener {
            val rating = rating.rating.toString()
            viewModel.submitFeedback(
                rating,
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

            Log.e("res", response.toString())
            if (response != null) {

                val dialog = BackTosettingDialog()
                if (!dialog.isAdded) {
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