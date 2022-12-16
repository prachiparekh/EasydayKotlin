package com.app.easyday.screens.dialogs

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
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
        val mSearch = viewItems.findViewById<SearchView>(R.id.mSearch)

        recyclerView?.layoutManager = LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false)
        recyclerView.adapter = CountryCodePickerAdapter(context, phoneModelList,

            countryPick = object : CountryCodePickerAdapter.CountyrPick {
                override fun pick(countries: PhoneCodeModel) {
                    mListner.pickCountry(countries)
                    dismiss()
                }
            })

        close.setOnClickListener { dismiss() }

        val adapter: CountryCodePickerAdapter? = null
/*        adapter?.AddAll(phoneModelList)

        mSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                adapter?.getFilter()?.filter(newText)

                return true
            }
        })*/

       /* mSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(msg: String): Boolean {
                filter(msg, adapter)
                return false
            }
        })*/
    /*    mSearch.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter?.getFilter()?.filter(newText)
                return false
            }

        })*/

       /* mSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                Log.d("onQueryTextChange", "query: " + query)
                adapter?.filter?.filter(query)
                return true
            }
        })*/

        return viewItems

    }

   /* private fun filter(text: String, adapter: CountryCodePickerAdapter?) {
        val filteredlist: ArrayList<PhoneCodeModel> = ArrayList()

        for (item in phoneModelList) {
            if (item.value!!.toLowerCase().contains(text.toLowerCase()) ) {
                filteredlist.add(item)
            }
        }
        if (filteredlist.isEmpty()) {
            Toast.makeText(context, "No Data Found..", Toast.LENGTH_SHORT).show()
        } else {
            adapter?.filterList(filteredlist)
        }
    }*/


}