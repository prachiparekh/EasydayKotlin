package com.app.easyday.screens.activities.main.more.notepad

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.app.easyday.R
import com.app.easyday.databinding.FragmentCreateNoteBinding
import com.onegravity.rteditor.RTManager
import com.onegravity.rteditor.api.RTApi
import com.onegravity.rteditor.api.RTMediaFactoryImpl
import com.onegravity.rteditor.api.RTProxyImpl
import com.onegravity.rteditor.effects.Effects
import com.onegravity.rteditor.utils.Helper
import java.util.*


class CreateNoteFragment : Fragment() {

    override fun onGetLayoutInflater(savedInstanceState: Bundle?): LayoutInflater {
        val inflater = super.onGetLayoutInflater(savedInstanceState)
        val contextThemeWrapper: Context =
            ContextThemeWrapper(requireContext(), com.onegravity.rteditor.R.style.RTE_ThemeLight)

        return inflater.cloneInContext(contextThemeWrapper)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window?.statusBarColor = resources.getColor(R.color.navy_blue)
    }

    private var mRTManager: RTManager? = null
    var binding: FragmentCreateNoteBinding? = null

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mRTManager?.onSaveInstanceState(outState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_note, container, false)

        val rtApi = RTApi(
            requireContext(),
            RTProxyImpl(requireActivity()),
            RTMediaFactoryImpl(requireContext(), true)
        )
        mRTManager = RTManager(rtApi, savedInstanceState)

        mRTManager?.registerToolbar(
            binding?.rteToolbarContainer as ViewGroup,
            binding?.rteToolbarCharacter
        )

        mRTManager?.registerEditor(binding?.rtEditText, true)
        binding?.rtEditText?.setRichTextEditing(
            true, ""
        )

        val month = Calendar.getInstance().get(Calendar.MONTH)
        val date = Calendar.getInstance().get(Calendar.DATE)
        val year = Calendar.getInstance().get(Calendar.YEAR)
        binding?.subject?.setText(
            requireContext().resources.getString(
                R.string.untitled_date,
                "$date/${month + 1}/$year"
            )
        )

        binding?.toolbarFontsizeH1?.setOnClickListener {
            val size = Helper.convertPxToSp(30)
            binding?.rtEditText?.applyEffect(Effects.FONTSIZE, size)
        }

        binding?.back?.setOnClickListener {
            Navigation.findNavController(requireView()).popBackStack()
        }

        binding?.option?.setOnClickListener {
            showDialog()
        }


        return binding?.root
    }

    private fun showDialog() {
        val popupView: View =
            View.inflate(requireContext(), R.layout.create_note_popup_window, null)

        val width: Int = LinearLayout.LayoutParams.WRAP_CONTENT
        val height: Int = LinearLayout.LayoutParams.WRAP_CONTENT
        val focusable = true

        val popupWindow = PopupWindow(popupView, width, height, focusable)
        popupWindow.showAsDropDown(binding?.option, 0, 0, Gravity.END)
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


    override fun onDestroy() {
        super.onDestroy()
        mRTManager?.onDestroy(true)
    }

}