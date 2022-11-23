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
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.richeditor.RichEditor
import kotlinx.android.synthetic.main.fragment_create_note.*
import java.util.*


@AndroidEntryPoint
class CreateNoteFragment : BaseFragment<CreateNoteViewModel>() {

    val mHashmap: HashMap<String, String>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


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
            Navigation.findNavController(requireView()).popBackStack()
        }

        option?.setOnClickListener {
            showDialog()
        }

        rtEditText.setEditorHeight(200)
        rtEditText?.setPadding(10, 10, 10, 10)
        rtEditText?.setPlaceholder(requireContext().resources.getString(R.string.type_here))

        rtEditText.setOnTextChangeListener(RichEditor.OnTextChangeListener { text ->
            Log.e(
                "Preview",
                text
            )

        })

        toolbar_undo.setOnClickListener {
            rtEditText?.undo()
        }

        toolbar_redo.setOnClickListener {
            rtEditText?.redo()
        }

        toolbar_inc_indent.setOnClickListener {
            toolbar_inc_indent.isSelected = !toolbar_inc_indent.isSelected
        }

        toolbar_fontsizeH1.setOnClickListener {
            toolbar_fontsizeH2.isSelected = false
            toolbar_fontsizeH1.isSelected = !toolbar_fontsizeH1.isSelected

            if (toolbar_fontsizeH1.isSelected) {
                rtEditText.setHeading(1)
            } else {
                rtEditText.setEditorHeight(200)
            }
        }
        toolbar_fontsizeH2.setOnClickListener {
            toolbar_fontsizeH1.isSelected = false
            toolbar_fontsizeH2.isSelected = !toolbar_fontsizeH2.isSelected

            if (toolbar_fontsizeH2.isSelected) {
                rtEditText.setHeading(2)
            } else {
                rtEditText.setEditorHeight(200)
            }
        }

        toolbar_bullet.setOnClickListener {
            toolbar_number.isSelected = false
            toolbar_bullet.isSelected = !toolbar_bullet.isSelected
            if (toolbar_bullet.isSelected)
                rtEditText.setBullets()
        }

        toolbar_number.setOnClickListener {
            toolbar_bullet.isSelected = false
            toolbar_number.isSelected = !toolbar_number.isSelected
            if (toolbar_number.isSelected)
                rtEditText.setNumbers()
        }

        toolbar_underline.setOnClickListener {
            toolbar_underline.isSelected = !toolbar_underline.isSelected
            rtEditText.setUnderline()
        }
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


    /*override fun onDestroy() {
        super.onDestroy()
        mRTManager?.onDestroy(true)
    }
*/
    override fun getContentView() = R.layout.fragment_create_note

    override fun initUi() {
        requireActivity().window?.statusBarColor = resources.getColor(R.color.navy_blue)
    }

    override fun setObservers() {

    }

}