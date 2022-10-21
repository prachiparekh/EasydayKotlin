package com.app.easyday.screens.dialogs.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.easyday.R
import com.app.easyday.app.sources.local.model.PhoneCodeModel
import com.app.easyday.utils.CountryCityUtils
import java.util.*


class CountryCodePickerAdapter(

    val context: Context,
    val countries: ArrayList<PhoneCodeModel>,
    private val countryPick: CountyrPick
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


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


    fun firstTwo(str: String): String {
        return if (str.length < 2) str else str.substring(0, 2)
    }


}