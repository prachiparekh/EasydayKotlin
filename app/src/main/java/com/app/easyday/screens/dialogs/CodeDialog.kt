package com.app.easyday.screens.dialogs

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.easyday.R
import com.app.easyday.app.sources.local.model.PhoneCodeModel
import com.app.easyday.screens.dialogs.adapters.CountryCodePickerAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CodeDialog(
    val context: Activity,
    private val phoneModelList: ArrayList<PhoneCodeModel>,
    val mListner: CountyPickerItems
) :
    BottomSheetDialogFragment() {

    private lateinit var bottomSheet: ViewGroup
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>

    interface CountyPickerItems {
        fun pickCountry(countries: PhoneCodeModel)
    }

    override fun onStart() {
        super.onStart()
        bottomSheet =
            dialog?.findViewById(com.google.android.material.R.id.design_bottom_sheet) as ViewGroup // notice the R root package
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.peekHeight = BottomSheetBehavior.PEEK_HEIGHT_AUTO
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(view: View, i: Int) {
                if (BottomSheetBehavior.STATE_HIDDEN == i) {
                    dismiss()
                }
            }

            override fun onSlide(view: View, v: Float) {}
        })

//        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewItems: View =
            inflater.inflate(R.layout.country_code_dialog, container, false)

        val recyclerView = viewItems.findViewById<RecyclerView>(R.id.country_dialog_lv)
        val close = viewItems.findViewById<ImageView>(R.id.mClose)

        recyclerView?.layoutManager = LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false)
        recyclerView.adapter = CountryCodePickerAdapter(context, phoneModelList,

            countryPick = object : CountryCodePickerAdapter.CountyrPick {
                override fun pick(countries: PhoneCodeModel) {
                    mListner.pickCountry(countries)
                    dismiss()
                }
            })

        close.setOnClickListener { dismiss() }

        return viewItems

    }


}