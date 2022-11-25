package com.app.easyday.views

import android.content.Context
import android.util.AttributeSet
import com.app.easyday.screens.activities.main.more.notepad.CreateNoteFragment.Companion.numberIndex
import com.app.easyday.screens.activities.main.more.notepad.CreateNoteFragment.Companion.selectedFilter


class CustomEditText(context: Context, attrs: AttributeSet?) :
    androidx.appcompat.widget.AppCompatEditText(context, attrs) {


    override fun onTextChanged(
        text: CharSequence,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        var resultText = text

        when (selectedFilter) {
            "INDENT" -> {

            }
            "H1" -> {

            }
            "H2" -> {

            }
            "BULLET" -> {
                if (lengthAfter > lengthBefore) {
                    if (resultText.toString().length == 1) {
                        resultText = "• $resultText"
                        setText(resultText)
                        getText()?.length?.let { setSelection(it) }
                    }
                    if (resultText.toString().endsWith("\n")) {
                        resultText = resultText.toString().replaceAfterLast("\n", "• ")
                        resultText = resultText.toString().replace("• •", "•")
                        setText(resultText)
                        getText()?.length?.let { setSelection(it) }
                    }
                }
            }
            "NUMBER" -> {
                if (lengthAfter > lengthBefore) {
                    if (resultText.toString().length == 1) {
                        numberIndex++
                        resultText = "$numberIndex $resultText"
                        setText(resultText)
                        getText()?.length?.let { setSelection(it) }
                    }

                    if (resultText.toString().endsWith("\n")
                    ) {
                        numberIndex++
                        resultText = resultText.toString()
                            .replaceAfterLast("\n", "$numberIndex ")
                        resultText = resultText.toString()
                            .replace("$numberIndex ${numberIndex - 1}", "$numberIndex ")
                        setText(resultText)
                        getText()?.length?.let { setSelection(it) }
                    }
                }
            }
            "UNDERLINE" -> {

            }
        }

        super.onTextChanged(resultText, start, lengthBefore, lengthAfter)
    }
}