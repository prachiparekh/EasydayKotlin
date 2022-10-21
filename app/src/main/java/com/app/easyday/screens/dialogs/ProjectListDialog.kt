package com.app.easyday.screens.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.easyday.R
import com.app.easyday.app.sources.local.interfaces.ProjectInterface
import com.app.easyday.app.sources.remote.model.ProjectRespModel
import com.app.easyday.databinding.ProjectListBdialogBinding
import com.app.easyday.screens.dialogs.adapters.ProjectAdapter
import com.app.easyday.views.Progressbar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ProjectListDialog(
    private val projectInterface: ProjectInterface,
    var projectList: ArrayList<ProjectRespModel>?,
    var selectedProjectPosition: Int?
) :
    BottomSheetDialogFragment() {
    var binding: ProjectListBdialogBinding? = null
    var progressbar: Progressbar? = null
    var adapter: ProjectAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.project_list_bdialog, container, false)
        isCancelable = false
        progressbar = Progressbar(requireContext())

        binding?.createNew?.setOnClickListener {
            projectInterface.onClickProject(-1)
        }

        binding?.projectRV?.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        adapter = projectList?.let {
            ProjectAdapter(
                requireContext(),
                projectList = it,
                selectedProjectPosition ?: 0
            )
        }
        binding?.projectRV?.adapter = adapter

        binding?.close?.setOnClickListener {
            dismiss()
        }

        if(projectList?.isNotEmpty() == true ) {
            if (projectList?.size == 1) {
                binding?.cta?.isVisible = false
            } else {
                binding?.cta?.isVisible = true
                binding?.cta?.setOnClickListener {
                    adapter?.selectedProjectPosition().let { it1 ->
                        if (it1 != null) {
                            projectInterface.onClickProject(it1)
                        }
                    }
                    dismiss()
                }
            }
        }

        binding?.projectRV
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
