package com.app.easyday.screens.activities.main.more.devices

import android.annotation.SuppressLint
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.navigation.Navigation
import com.app.easyday.R
import com.app.easyday.screens.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_devices.*
import kotlinx.android.synthetic.main.fragment_login.terms


class DevicesFragment : BaseFragment<DevicesViewModel>() {


    override fun getContentView() = R.layout.fragment_devices

    @SuppressLint("StringFormatInvalid")
    override fun initUi() {
        setHighLightedText(terms, requireContext().resources.getString(R.string.easy_day_desktop))
        setHighLightedText(terms, requireContext().resources.getString(R.string.easy_day_web))

        val dot = requireContext().resources.getString(R.string.dot)
        location?.text = requireContext().resources.getString(R.string.hyderabad, dot)
        location2?.text = requireContext().resources.getString(R.string.hyderabad, dot)
        location3?.text = requireContext().resources.getString(R.string.hyderabad, dot)

        back.setOnClickListener {
            Navigation.findNavController(requireView()).popBackStack()
        }
    }

    override fun setObservers() {

    }

    private fun setHighLightedText(tv: TextView, textToHighlight: String) {

        val tvt = tv.text.toString()
        var ofe = tvt.indexOf(textToHighlight, 0)
        val wordToSpan: Spannable = SpannableString(tv.text)
        var ofs = 0
        while (ofs < tvt.length && ofe != -1) {
            ofe = tvt.indexOf(textToHighlight, ofs)
            if (ofe == -1) break else {
                wordToSpan.setSpan(
                    object : ClickableSpan() {
                        override fun onClick(textView: View) {

                        }

                        override fun updateDrawState(ds: TextPaint) {
                            super.updateDrawState(ds)
                            ds.isUnderlineText = false
                        }
                    },
                    ofe,
                    ofe + textToHighlight.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                tv.movementMethod = LinkMovementMethod.getInstance()
                tv.setText(wordToSpan, TextView.BufferType.SPANNABLE)
            }
            ofs = ofe + 1
        }
    }

}