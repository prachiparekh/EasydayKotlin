package com.app.easyday.screens.activities.main.inbox

import android.content.Intent
import com.app.easyday.R
import com.app.easyday.screens.activities.main.home.HomeViewModel.Companion.userModel
import com.app.easyday.screens.base.BaseFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_inbox.*
import kotlinx.android.synthetic.main.single_item.*

@AndroidEntryPoint
class InboxFragment : BaseFragment<InboxViewModel>() {

    companion object {
        const val TAG = "InboxFragment"
    }

    override fun getContentView() = R.layout.fragment_inbox

    override fun initUi() {

    }

    override fun setObservers() {
        if (userModel?.profileImage != null) {
            val options = RequestOptions()
            profile.clipToOutline = true
            Glide.with(requireContext())
                .load(userModel?.profileImage)
                .apply(
                    options.centerCrop()
                        .skipMemoryCache(true)
                        .priority(Priority.HIGH)
                        .format(DecodeFormat.PREFER_ARGB_8888)
                )
                .into(profile)
        }
        inboxLL.setOnClickListener {
            startActivity(Intent(requireActivity(), ConversationActivity::class.java))
        }

    }


}