package com.app.easyday.screens.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.app.easyday.R
import com.app.easyday.app.sources.local.interfaces.DeleteLogoutProfileInterface
import com.app.easyday.databinding.DeleteLogoutLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DeleteUserDialog(val action: String, val anInterface: DeleteLogoutProfileInterface) :
    BottomSheetDialogFragment() {

    var binding: DeleteLogoutLayoutBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.delete_logout_layout, container, false)
        isCancelable = true

        binding?.cancel?.setOnClickListener {
            dismiss()
        }

        if (action == "DELETE") {
            binding?.mImage?.setImageDrawable(requireContext().resources.getDrawable(R.drawable.ic_delete_btn))
            binding?.text1?.text =
                requireContext().resources.getString(R.string.delete_account_text)
            binding?.text2?.text =
                requireContext().resources.getString(R.string.delete_account_text1)
            binding?.cta?.text = requireContext().resources.getString(R.string.yes_delete)
        } else {
            binding?.mImage?.setImageDrawable(requireContext().resources.getDrawable(R.drawable.ic_logout_btn))
            binding?.text1?.text =
                requireContext().resources.getString(R.string.logout_account_text)
            binding?.text2?.text =
                requireContext().resources.getString(R.string.logout_account_text1)
            binding?.cta?.text = requireContext().resources.getString(R.string.yes_logout)
        }

        binding?.cta?.setOnClickListener {
            if (action == "DELETE")
                anInterface.OnDeleteClick()
            else
                anInterface.OnLogoutClick()
            dismiss()
        }

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