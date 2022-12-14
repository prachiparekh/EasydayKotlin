package com.app.easyday.screens.activities.main.more.notepad

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.navigation.Navigation
import com.app.easyday.R
import com.app.easyday.screens.base.BaseFragment
import com.app.easyday.utils.notepad_utils.WYSIWYG
import com.app.easyday.views.CustomEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_create_note.*
import org.json.JSONObject
import java.util.*


@AndroidEntryPoint
class CreateNoteFragment : BaseFragment<CreateNoteViewModel>() {

    companion object {
        var selectedFilter: String? = "NORMAL"
        var numberIndex = 0
        var underlineStartIndex = 0

        //        var mHashmap: HashMap<String, String> = HashMap<String, String>()
        var mUnderlineHashMap: HashMap<Int, Int> =
            HashMap<Int, Int>()       //underlineStartIndex -> underlineEndIndex
    }

    var mEditText: CustomEditText? = null
    var obj: JSONObject = JSONObject()
    var previousText: String? = ""
    var previousFilter: String? = "NORMAL"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val wysiwygEditor = editor as WYSIWYG
        wysiwygEditor.setEditorHeight(200)
        wysiwygEditor.setEditorFontSize(16)
        wysiwygEditor.setPadding(10, 10, 10, 10)
        wysiwygEditor.setPlaceholder("Insert your notes here...")

//        val mPreview = preview as TextView
        wysiwygEditor.setOnTextChangeListener(object : WYSIWYG.OnTextChangeListener {
            override fun onTextChange(text: String?) {
                if (text != null) {
                    Log.e("text", text)
                }
            }
        })


        val month = Calendar.getInstance().get(Calendar.MONTH)
        val date = Calendar.getInstance().get(Calendar.DATE)
        val year = Calendar.getInstance().get(Calendar.YEAR)
        subject?.setText(
            requireContext().resources.getString(
                R.string.untitled_date,
                "$date/${month + 1}/$year"
            )
        )

        subject?.hint = requireContext().resources.getString(
            R.string.untitled_date,
            "$date/${month + 1}/$year"
        )

        back?.setOnClickListener {
            Log.e("ARRAY", " :: $obj")
            Navigation.findNavController(requireView()).popBackStack()
        }

        option?.setOnClickListener {
            showDialog()
        }

        mEditText = rtEditText

        toolbar_undo.setOnClickListener {
            wysiwygEditor.undo()
        }

        toolbar_redo.setOnClickListener {
            wysiwygEditor.redo()
        }

        toolbar_inc_indent.setOnClickListener {
            /*toolbar_inc_indent.isSelected = !toolbar_inc_indent.isSelected
            if (toolbar_inc_indent.isSelected) {
                selectedFilter = "INDENT"
                toolbar_fontsizeH1.isSelected = false
                toolbar_fontsizeH2.isSelected = false
                toolbar_bullet.isSelected = false
                toolbar_number.isSelected = false
                toolbar_underline.isSelected = false
            }*/

            wysiwygEditor.setIndent()
        }

        toolbar_fontsizeH1.setOnClickListener {
            wysiwygEditor.setHeading(
                1
            )
            /*toolbar_fontsizeH1.isSelected = !toolbar_fontsizeH1.isSelected
            if (toolbar_fontsizeH1.isSelected) {
                selectedFilter = "H1"
                toolbar_inc_indent.isSelected = false
                toolbar_fontsizeH2.isSelected = false
                toolbar_bullet.isSelected = false
                toolbar_number.isSelected = false
                toolbar_underline.isSelected = false
            }*/
        }
        toolbar_fontsizeH2.setOnClickListener {
            wysiwygEditor.setHeading(
                2
            )
            /*toolbar_fontsizeH1.isSelected = false
            toolbar_fontsizeH2.isSelected = !toolbar_fontsizeH2.isSelected

            if (toolbar_fontsizeH2.isSelected) {
                selectedFilter = "H2"
                toolbar_fontsizeH1.isSelected = false
                toolbar_inc_indent.isSelected = false
                toolbar_bullet.isSelected = false
                toolbar_number.isSelected = false
                toolbar_underline.isSelected = false
            }*/
        }

        toolbar_bullet.setOnClickListener {
            wysiwygEditor.setBullets()
            /*toolbar_bullet.isSelected = !toolbar_bullet.isSelected
            if (toolbar_bullet.isSelected) {
                selectedFilter = "BULLET"
//                selectedFilter?.let { mHashmap.put(it, "") }
                toolbar_fontsizeH1.isSelected = false
                toolbar_fontsizeH2.isSelected = false
                toolbar_inc_indent.isSelected = false
                toolbar_number.isSelected = false
                toolbar_underline.isSelected = false
            }*/
        }

        toolbar_number.setOnClickListener {
            wysiwygEditor.setNumbers()
            /* if (selectedFilter == "UNDERLINE") {

                 mEditText?.length()?.let { it1 -> mUnderlineHashMap[underlineStartIndex] = it1 }
                 Log.e("mUnderlineHashMap", mUnderlineHashMap.toString())
             }
             toolbar_number.isSelected = !toolbar_number.isSelected
             if (toolbar_number.isSelected) {
                 selectedFilter = "NUMBER"
                 numberIndex = 0
 //                selectedFilter?.let { mHashmap.put(it, "") }
                 toolbar_fontsizeH1.isSelected = false
                 toolbar_fontsizeH2.isSelected = false
                 toolbar_inc_indent.isSelected = false
                 toolbar_bullet.isSelected = false
                 toolbar_underline.isSelected = false
             }*/
        }

        toolbar_underline.setOnClickListener {
            wysiwygEditor.setUnderline()
            /*toolbar_underline.isSelected = !toolbar_underline.isSelected
            if (toolbar_underline.isSelected) {
                selectedFilter = "UNDERLINE"
                underlineStartIndex = mEditText?.length() ?: 0
                mUnderlineHashMap[underlineStartIndex] = underlineStartIndex
                Log.e("mUnderlineHashMap", mUnderlineHashMap.toString())
                toolbar_fontsizeH1.isSelected = false
                toolbar_fontsizeH2.isSelected = false
                toolbar_inc_indent.isSelected = false
                toolbar_bullet.isSelected = false
                toolbar_number.isSelected = false
            }*/
        }

        /*mEditText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if (p0?.endsWith("\n")!!) {
                    Log.e("previousText", " :: ${previousText.toString()}")
                    Log.e("previousFilter", " :: ${previousFilter.toString()}")
                    try {
                        obj.put(selectedFilter, p0.toString().replace(previousText.toString(), ""))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    Log.e("obj", " :: $obj")
                    if (previousFilter != selectedFilter)
                        previousText = p0.toString()
                    previousFilter = selectedFilter
                }
                for (key in mUnderlineHashMap.keys) {
                    mUnderlineHashMap[key]?.let {
                        p0.setSpan(
                            UnderlineSpan(), key, it,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                }
            }
        })*/

    }

    private fun showDialog() {
        val popupView: View =
            View.inflate(requireContext(), R.layout.create_note_popup_window, null)

        val width: Int = LinearLayout.LayoutParams.WRAP_CONTENT
        val height: Int = LinearLayout.LayoutParams.WRAP_CONTENT
        val focusable = true

        val popupWindow = PopupWindow(popupView, width, height, focusable)
        popupWindow.showAsDropDown(option, 0, 0, Gravity.END)
        val convert = popupView.findViewById<TextView>(R.id.convertTV)
        val share = popupView.findViewById<TextView>(R.id.shareTV)
        val delete = popupView.findViewById<TextView>(R.id.deleteTV)

        convert?.setOnClickListener {
            popupWindow.dismiss()
        }
        share?.setOnClickListener {
            popupWindow.dismiss()
        }
        delete?.setOnClickListener {
            popupWindow.dismiss()
        }
    }

    override fun getContentView() = R.layout.fragment_create_note

    override fun initUi() {
        requireActivity().window?.statusBarColor = resources.getColor(R.color.navy_blue)
    }

    override fun setObservers() {

    }

}