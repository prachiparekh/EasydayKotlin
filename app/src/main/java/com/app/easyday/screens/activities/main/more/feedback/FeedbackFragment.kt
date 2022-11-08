package com.app.easyday.screens.activities.main.more.feedback

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.navigation.Navigation
import com.app.easyday.R
import com.app.easyday.screens.activities.main.home.HomeFragment
import com.app.easyday.screens.base.BaseFragment
import com.app.easyday.screens.dialogs.BackTosettingDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_feedback.*

@AndroidEntryPoint
class FeedbackFragment : BaseFragment<FeedbackViewModel>() {


    override fun getContentView() = R.layout.fragment_feedback

    @SuppressLint("ResourceAsColor")
    override fun initUi() {

        back.setOnClickListener {
            Navigation.findNavController(requireView()).popBackStack()
        }
        val list: ArrayList<String> = ArrayList()
        cs_Tv.setOnClickListener {
            cs_Tv.isSelected = !cs_Tv.isSelected
           if (cs_Tv.isSelected){
               list.add("Customer Service")
           }

        }
        tr_Tv.setOnClickListener {
            tr_Tv.isSelected = !tr_Tv.isSelected
//            if (cs_Tv.isSelected){
//                list.add("Transparency")
//            }
        }
        tslo_Tv.setOnClickListener {
            tslo_Tv.isSelected = !tslo_Tv.isSelected
//            if (cs_Tv.isSelected){
//                list.add("Too Slow")
//            }
        }
        se_Tv.setOnClickListener {
            se_Tv.isSelected = !se_Tv.isSelected
//            if (cs_Tv.isSelected){
//                list.add("Speed & Efficiency")
//            }
        }
        ibg_Tv.setOnClickListener {
            ibg_Tv.isSelected = !ibg_Tv.isSelected
//            if (cs_Tv.isSelected){
//                list.add("Improve Bugs")
//            }
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

        if(cs_Tv.isSelected && tr_Tv.isSelected && tslo_Tv.isSelected && se_Tv.isSelected && ibg_Tv.isSelected ){
            list.addAll(listOf("Customer Support", "Transparency", "Too Slow", "Speed & Efficiency", "Improve Bugs"))
        }else if (cs_Tv.isSelected && !tr_Tv.isSelected && !tslo_Tv.isSelected && !se_Tv.isSelected && !ibg_Tv.isSelected ){
            list.add("Customer Support")
        }else if (cs_Tv.isSelected && tr_Tv.isSelected && !tslo_Tv.isSelected && !se_Tv.isSelected && !ibg_Tv.isSelected ){
//            list.add("Transparency")
                list.addAll(listOf("Customer Support", "Transparency"))
        }else if (tslo_Tv.isSelected && !cs_Tv.isSelected && !tr_Tv.isSelected && !se_Tv.isSelected && !ibg_Tv.isSelected){
            list.add("Too Slow")
        }else if (se_Tv.isSelected && !cs_Tv.isSelected && !tslo_Tv.isSelected && !tr_Tv.isSelected && !ibg_Tv.isSelected){
            list.add("Speed & Efficiency")
        }else if (ibg_Tv.isSelected && !cs_Tv.isSelected && !tslo_Tv.isSelected && !se_Tv.isSelected && !tr_Tv.isSelected){
            list.add("Improve Bugs")
            }
//        else if (cs_Tv.isSelected && tr_Tv.isSelected && tslo_Tv.isSelected && se_Tv.isSelected && ibg_Tv.isSelected){
//            list.addAll(listOf("Customer Support", "Transparency", "Too Slow", "Speed & Efficiency", "Improve Bugs"))
//        }else
            else{
                list.removeAll(listOf("Customer Service", "Transparency", "Too Slow", "Speed & Efficiency", "Improve Bugs"))
            }
//            else{
//            list.addAll(listOf("Customer Service", "Transparency", "Too Slow", "Speed & Efficiency", "Improve Bugs"))
////            list.removeAll(listOf("Customer Service", "Transparency", "Too Slow", "Speed & Efficiency", "Improve Bugs"))
//        }
        submit_btnRL.setOnClickListener {

//            viewModel.api.submitFeedback(rating, tags, feedback_text)
//                .subscribe { resp ->
//                if (resp.success) {

                    val dialog = BackTosettingDialog()
                    if (!dialog.isAdded) {
                        dialog.show(childFragmentManager, "back")
                    }

                    val rating = rating.rating.toString()
//            val tags = rating.rating.toString()
                    HomeFragment.selectedProjectID?.let { viewModel.submitFeedback(rating, list.toString(), feedText.toString()) }
                    Toast.makeText(context, list.toString(), Toast.LENGTH_SHORT).show()

//                }
//            }

        }

    }

    override fun setObservers() {

    }

}