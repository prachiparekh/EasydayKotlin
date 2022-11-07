package com.app.easyday.screens.activities.main.more.notifications

import android.annotation.SuppressLint
import android.graphics.Paint
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.TextView
import androidx.navigation.Navigation
import com.app.easyday.R
import com.app.easyday.screens.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_notifications.*


class NotificationsFragment : BaseFragment<NotificationsViewModel>() {

    override fun getContentView() = R.layout.fragment_notifications

    @SuppressLint("StringFormatMatches")
    override fun initUi() {
        val dot = requireContext().resources.getString(R.string.dot)
        time_1?.text = requireContext().resources.getString(R.string.activity_time, dot)
        time_2?.text = requireContext().resources.getString(R.string.activity_time, dot)
        time_3?.text = requireContext().resources.getString(R.string.activity_time, dot)
        time_4?.text = requireContext().resources.getString(R.string.activity_time, dot)

        val fullText: String = requireContext().resources.getString(R.string.full_peter_assigned)
        val normalText: String = requireContext().resources.getString(R.string.peter_assigned_you)
        val boldText: String = requireContext().resources.getString(R.string.door_fix_on)

        MarkAll.paintFlags = MarkAll.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        val spanStringbold = SpannableString(boldText)
        val spanString = SpannableString(fullText)
        spanStringbold.setSpan(UnderlineSpan(), 0, spanStringbold.length, 0)
        spanStringbold.setSpan(StyleSpan(Typeface.BOLD), 0, spanStringbold.length, 0)

        setHighLightedText(fst_comment, requireContext().resources.getString(R.string.door_fix_on))
        setHighLightedText(sec_comment, requireContext().resources.getString(R.string.julia_okina))
        setHighLightedText(trd_comment, requireContext().resources.getString(R.string.door_fix_on))
        setHighLightedText(four_comment, requireContext().resources.getString(R.string.julia_okina))

        back.setOnClickListener {
            Navigation.findNavController(requireView()).popBackStack()
        }
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

    override fun setObservers() {

    }


}