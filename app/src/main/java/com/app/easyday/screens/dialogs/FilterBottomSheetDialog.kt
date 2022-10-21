package com.app.easyday.screens.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import com.app.easyday.R
import com.app.easyday.app.sources.local.interfaces.TaskFilterApplyInterface
import com.app.easyday.databinding.FilterBottomsheetBinding
import com.app.easyday.screens.activities.main.home.filter.FilterFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FilterBottomSheetDialog(val anInterface: TaskFilterApplyInterface) : BottomSheetDialogFragment() {
    var binding: FilterBottomsheetBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.filter_bottomsheet, container, false)
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.mapContainer, FilterFragment(anInterface = anInterface)).commit()
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
                setupFullHeight(it1)
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED

                behaviour.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(@NonNull bottomSheet: View, newState: Int) {
                        if (newState == BottomSheetBehavior.STATE_COLLAPSED || newState == BottomSheetBehavior.STATE_DRAGGING) {
                            behaviour.state = BottomSheetBehavior.STATE_EXPANDED
                        }
                    }

                    override fun onSlide(@NonNull bottomSheet: View, slideOffset: Float) {
                        // React to dragging events
                    }
                })
            }
        }

        return dialog
    }

    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }

}
