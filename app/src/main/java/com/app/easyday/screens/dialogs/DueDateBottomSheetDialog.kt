package com.app.easyday.screens.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView.OnDateChangeListener
import androidx.databinding.DataBindingUtil
import com.app.easyday.R
import com.app.easyday.app.sources.local.interfaces.FilterCloseInterface
import com.app.easyday.databinding.DueDateBottomLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.*


class DueDateBottomSheetDialog(
    var mContext: Context,
    val dateStr: String?,
    var closeInterface: FilterCloseInterface
) :
    BottomSheetDialogFragment() {
    var binding: DueDateBottomLayoutBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.due_date_bottom_layout, container, false)
        isCancelable = false

        binding?.close?.setOnClickListener {
            closeInterface.onCloseClick()
            dismiss()
        }

        if (dateStr != mContext.resources.getString(R.string.f_due_date) && dateStr != null) {
            val parts = dateStr.split("/").toTypedArray()

            val day = parts[0].toInt()
            val month = parts[1].toInt()-1
            val year = parts[2].toInt()

            val calendar: Calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)

            binding?.calenderView?.setDate(calendar.timeInMillis, true, true)
        }

        binding?.calenderView?.setOnDateChangeListener(OnDateChangeListener { calendarView, i, i1, i2 ->

            closeInterface.onDateClick("$i2/${(i1 + 1)}/$i")
            dismiss()
        })

        return binding?.root
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.setOnShowListener {

            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { it1 ->
                val behaviour = BottomSheetBehavior.from(it1)
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        return dialog
    }

}
