package com.app.easyday.screens.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import com.app.easyday.R
import com.app.easyday.app.sources.local.interfaces.FilterCloseInterface
import com.app.easyday.app.sources.local.interfaces.AssigneeInterface
import com.app.easyday.app.sources.local.model.ContactModel
import com.app.easyday.databinding.AddAssigneeLayoutBinding
import com.app.easyday.screens.activities.main.home.project.AddParticipantsFragment
import com.app.easyday.screens.activities.main.home.project.adapter.ParticipentAdapter
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AsigneeSelectionBottomSheetDialog(
    var mContext: Context,
    var userList: ArrayList<ContactModel>,
    var closeInterface: FilterCloseInterface,
    var assigneeInterface:AssigneeInterface
) :
    BottomSheetDialogFragment() {
    var binding: AddAssigneeLayoutBinding? = null
    var adapter: ParticipentAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.add_assignee_layout, container, false)
        isCancelable = false

        binding?.tagRV?.layoutManager = FlexboxLayoutManager(requireContext())
        adapter = ParticipentAdapter(requireContext(), userList)
        binding?.tagRV?.adapter = adapter

        binding?.close?.setOnClickListener {
            closeInterface.onCloseClick()
            dismiss()
        }

        binding?.mSearch?.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isEmpty())
                    binding?.searchImageView?.visibility = View.VISIBLE
                else
                    binding?.searchImageView?.visibility = View.INVISIBLE
                adapter?.filter?.filter(newText)
                return true
            }
        })

        binding?.cta?.setOnClickListener {
            val assigneeList=ArrayList<Int>()
            val mainList=adapter?.getList()

            mainList?.indices?.forEach { i ->
                mainList[i].id?.toInt()?.let { it1 -> assigneeList.add(it1)

                }
            }


            assigneeInterface.onSelestAssignee(assigneeList)
            dismiss()
        }

        binding?.skip?.setOnClickListener {
            assigneeInterface.onSkipAssignee()
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
