package com.app.easyday.screens.activities.main.more.notepad

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.navigation.Navigation
import com.app.easyday.R
import com.app.easyday.screens.base.BaseFragment
import com.onegravity.rteditor.RTManager
import com.onegravity.rteditor.api.RTApi
import com.onegravity.rteditor.api.RTMediaFactoryImpl
import com.onegravity.rteditor.api.RTProxyImpl
import com.onegravity.rteditor.effects.Effects
import com.onegravity.rteditor.utils.Helper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_create_note.*
import java.util.*


@AndroidEntryPoint
class CreateNoteFragment : BaseFragment<CreateNoteViewModel>() {

    override fun onGetLayoutInflater(savedInstanceState: Bundle?): LayoutInflater {
        val inflater = super.onGetLayoutInflater(savedInstanceState)
        val contextThemeWrapper: Context =
            ContextThemeWrapper(requireContext(), com.onegravity.rteditor.R.style.RTE_ThemeLight)

        return inflater.cloneInContext(contextThemeWrapper)
    }


    private var mRTManager: RTManager? = null


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mRTManager?.onSaveInstanceState(outState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rtApi = RTApi(
            requireContext(),
            RTProxyImpl(requireActivity()),
            RTMediaFactoryImpl(requireContext(), true)
        )
        mRTManager = RTManager(rtApi, savedInstanceState)

        mRTManager?.registerToolbar(
            rte_toolbar_container as ViewGroup,
            rte_toolbar_character
        )

        mRTManager?.registerEditor(rtEditText, true)
        rtEditText?.setRichTextEditing(
            true, ""
        )

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

        toolbar_fontsizeH1?.setOnClickListener {
            val size = Helper.convertPxToSp(30)
            rtEditText?.applyEffect(Effects.FONTSIZE, size)

        }

        toolbar_fontsizeH2?.setOnClickListener {
            val size = Helper.convertPxToSp(26)
            rtEditText?.applyEffect(Effects.FONTSIZE, size)
        }


        back?.setOnClickListener {
            Navigation.findNavController(requireView()).popBackStack()
        }

        option?.setOnClickListener {
            showDialog()
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


    override fun onDestroy() {
        super.onDestroy()
        mRTManager?.onDestroy(true)
    }

    override fun getContentView() = R.layout.fragment_create_note

    override fun initUi() {
        requireActivity().window?.statusBarColor = resources.getColor(R.color.navy_blue)
    }

    override fun setObservers() {

    }

}