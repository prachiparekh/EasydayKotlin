package com.app.easyday.screens.activities.boarding

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.easyday.R
import com.app.easyday.app.sources.local.model.OnboardingItem

class OnBoardingAdapter(
    private var items: List<OnboardingItem> = arrayListOf()

) : RecyclerView.Adapter<OnBoardingAdapter.OnBoardingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = OnBoardingViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_onboarding_pager, parent, false)
    )

    override fun onBindViewHolder(holder: OnBoardingViewHolder, position: Int) {
        val item = items[position]
        val context = holder.itemView.context

        holder.image.setImageDrawable(ContextCompat.getDrawable(context, item.imageResId))

    }

    override fun getItemCount() = items.size

    class OnBoardingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.img)

    }


}