package com.app.easyday.screens.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.app.easyday.R
import com.app.easyday.app.sources.local.interfaces.AddAttributeInterface
import com.app.easyday.app.sources.local.interfaces.FilterCloseInterface
import com.app.easyday.app.sources.local.interfaces.AttributeSelectionInterface
import com.app.easyday.app.sources.remote.model.AttributeResponse
import com.app.easyday.databinding.AddSpaceZoneLayoutBinding
import com.app.easyday.screens.dialogs.adapters.SpaceZoneAdapter
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddSpaceZoneBottomSheetDialog (
    var mContext: Context,
    var attrList: ArrayList<AttributeResponse>,
    private var selectedAttrList: java.util.ArrayList<Int>,
    var attrInterface: AttributeSelectionInterface,
    var closeInterface: FilterCloseInterface,
    var attributeType:Int,
    var addAttributeInterface: AddAttributeInterface
) :
    BottomSheetDialogFragment() {
    var binding: AddSpaceZoneLayoutBinding? = null
    var adapter: SpaceZoneAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.add_space_zone_layout, container, false)
        isCancelable = false

        binding?.tagRV?.layoutManager= FlexboxLayoutManager(requireContext())
        adapter=SpaceZoneAdapter(mContext,attrList,selectedAttrList)
        binding?.tagRV?.adapter= adapter

        if(attributeType==1){
            binding?.mTitle?.text=requireContext().resources.getString(R.string.f_zone)
        }else{
            binding?.mTitle?.text=requireContext().resources.getString(R.string.f_space)
        }

        binding?.close?.setOnClickListener {
            closeInterface.onCloseClick()
            dismiss()
        }

        binding?.smallTag?.setOnClickListener {
            addAttributeInterface.addAttribute(attributeType,binding?.tagET?.text.toString())
        }

        binding?.cta?.setOnClickListener {
            dismiss()
            adapter?.getSelectedList()
                ?.let { it1 -> attrInterface.onClickAttribute(it1,attributeType) }
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

    fun clearAttrList() {
        this.attrList.clear()
        adapter?.clearList()
    }

    fun setAttrListData(tag: AttributeResponse) {
        this.attrList.add(tag)
        adapter?.notifyItemInserted(attrList.size-1)
    }

}
