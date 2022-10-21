package com.app.easyday.screens.activities.main.home.project.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.easyday.R
import com.app.easyday.app.sources.local.interfaces.ColorInterface

class ColorAdapter(
    private val context: Context,
    private var colorList: IntArray,
    val colorInterface: ColorInterface
) : RecyclerView.Adapter<ColorAdapter.ViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    var selectedColorNo = 0

    override fun getItemCount(): Int = colorList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.item_color, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val colorItem = itemView.findViewById<ImageView>(R.id.colorItem)

        @SuppressLint("NewApi")
        fun bind(position: Int) {

            colorItem.setColorFilter(ContextCompat.getColor(context, colorList[position]), android.graphics.PorterDuff.Mode.SRC_IN);

            if (selectedColorNo == position) {
                colorItem.background = context.resources.getDrawable(R.drawable.ic_ellipse)
            } else {
                colorItem.background = null
            }

            itemView.setOnClickListener {
                selectedColorNo = position
                notifyDataSetChanged()
                colorList[position].let { it1 -> colorInterface.onColorClick(it1) }
            }
        }
    }
}