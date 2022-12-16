package com.app.easyday.screens.dialogs.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.easyday.R
import com.app.easyday.app.sources.local.model.PhoneCodeModel
import com.app.easyday.utils.CountryCityUtils
import java.util.*


class CountryCodePickerAdapter (

    val context: Context,
    var countries: ArrayList<PhoneCodeModel>,
    private val countryPick: CountyrPick
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var countryFilterList = ArrayList<PhoneCodeModel>()
    init {
        countryFilterList = countries
    }
    var searchObject: ArrayList<PhoneCodeModel>? = null

    interface CountyrPick {

        fun pick(countries: PhoneCodeModel)

    }

    class Holder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {

        val countryName: TextView = itemView.findViewById(R.id.country_name_tv)
        val flagImv: TextView = itemView.findViewById(R.id.flag_imv)
        val codeTv: TextView = itemView.findViewById(R.id.code_tv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.country_code, parent, false)
        return Holder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {

        val holder = holder as Holder

        val ch = CountryCityUtils.firstTwo(countries[position].key.toString())

        holder.flagImv.text =
            CountryCityUtils.getFlagId(ch.lowercase(Locale.getDefault()).toString())

        holder.codeTv.text = "+" + countries[position].value

        holder.itemView.setOnClickListener {
            countryPick.pick(countries[position])
        }

        val loc = Locale("", ch)
        holder.countryName.text = loc.displayCountry

    }


    override fun getItemCount(): Int {
        return countries.size
    }
   /* override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    countryFilterList .addAll(countries)
                } else {
                    val resultList = ArrayList<PhoneCodeModel>()
                    for (row in countries) {
                        if (row.value?.toLowerCase()?.contains(constraint.toString().toLowerCase()) == true) {
                            resultList.add(row)
                        }
                    }
                    countryFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = countryFilterList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                countryFilterList = results?.values as ArrayList<PhoneCodeModel>
                notifyDataSetChanged()
            }
        }
    }*/


    fun firstTwo(str: String): String {
        return if (str.length < 2) str else str.substring(0, 2)
    }
    fun AddAll(phoneModelList: ArrayList<PhoneCodeModel>) {
        searchObject = ArrayList<PhoneCodeModel>()
        countries = ArrayList<PhoneCodeModel>()

        searchObject?.addAll(phoneModelList)
        countries.addAll(phoneModelList)
        notifyDataSetChanged()
    }
    fun filterList(filterlist: ArrayList<PhoneCodeModel>) {
        countries = filterlist
        notifyDataSetChanged()
    }

    /*override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    countries = countryFilterList
                } else {
                    val resultList = ArrayList<PhoneCodeModel>()
                    for (row in countryFilterList) {
                        if (row.value!!.lowercase(Locale.ROOT)
                                .contains(charSearch.lowercase(Locale.ROOT))
                        ) {
                            resultList.add(row)
                        }
                    }
                    countries = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = countries
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                countryFilterList = results?.values as ArrayList<PhoneCodeModel>
                notifyDataSetChanged()
            }

        }
    }*/
    /* override fun getFilter(): Filter {
         return object : Filter() {
             override fun performFiltering(constraint: CharSequence): FilterResults {
                 val charString = constraint.toString()
                 if (charString.isEmpty()) {
                     countries = searchObject!!
                 } else {
                     val filteredList1: ArrayList<PhoneCodeModel> = ArrayList<PhoneCodeModel>()
                     for (row in searchObject!!) {
                         if (row.key?.toLowerCase()?.contains(charString.lowercase(Locale.getDefault())) == true) {
                             filteredList1.add(row)
                         }
                     }
 //                    for (row in searchObject!!) {
 //                        if (row.value?.toLowerCase()
 //                                ?.contains(charString.lowercase(Locale.getDefault())) == true
 //                        ) {
 //                            filteredList1.add(row)
 //                            Toast.makeText(context, row.value, Toast.LENGTH_SHORT).show()
 //                        }
 //                    }
                     countries = filteredList1
                 }
                 val filterResults = FilterResults()
                 filterResults.values = countries
                 return filterResults
             }
 
             override fun publishResults(constraint: CharSequence, results: FilterResults) {
 //                filteredList = results.values as ArrayList<AlbumDetail?>
                 notifyDataSetChanged()
             }
         }
     }*/

}