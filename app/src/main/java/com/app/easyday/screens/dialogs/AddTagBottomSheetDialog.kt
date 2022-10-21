package com.app.easyday.screens.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.app.easyday.R
import com.app.easyday.app.sources.local.interfaces.AddAttributeInterface
import com.app.easyday.app.sources.local.interfaces.AttributeSelectionInterface
import com.app.easyday.app.sources.local.interfaces.FilterCloseInterface
import com.app.easyday.app.sources.remote.model.AttributeResponse
import com.app.easyday.databinding.AddTagLayoutBinding
import com.app.easyday.screens.dialogs.adapters.TagsAdapter
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class AddTagBottomSheetDialog(
    var mContext: Context,
    private var selectedTagList: java.util.ArrayList<Int>,
    var tagInterface: AttributeSelectionInterface,
    var closeInterface: FilterCloseInterface,
    var addAttributeInterface: AddAttributeInterface
) :
    BottomSheetDialogFragment() {
    var binding: AddTagLayoutBinding? = null
    var adapter: TagsAdapter? = null
    private var tagList = ArrayList<AttributeResponse>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.add_tag_layout, container, false)
        isCancelable = false

        binding?.tagRV?.layoutManager = FlexboxLayoutManager(requireContext())
        adapter = TagsAdapter(mContext, tagList, selectedTagList)
        binding?.tagRV?.adapter = adapter

        binding?.close?.setOnClickListener {
            closeInterface.onCloseClick()
            dismiss()
        }

        binding?.smallTag?.setOnClickListener {
            addAttributeInterface.addAttribute(0, binding?.tagET?.text.toString())
        }

        binding?.cta?.setOnClickListener {
            dismiss()
            adapter?.getSelectedTagList()?.let { it1 -> tagInterface.onClickAttribute(it1, 0) }
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

    fun clearTagList() {
        this.tagList.clear()
        adapter?.clearList()
    }

    fun setTagListData(tag: AttributeResponse) {
        this.tagList.add(tag)
        adapter?.notifyItemInserted(tagList.size-1)
    }


}
