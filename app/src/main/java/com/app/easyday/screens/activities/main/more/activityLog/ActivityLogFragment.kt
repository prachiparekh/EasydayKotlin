package com.app.easyday.screens.activities.main.more.activityLog

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.app.easyday.screens.base.BaseFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.app.easyday.R
import com.app.easyday.app.sources.remote.model.ProjectParticipantsModel
import com.app.easyday.app.sources.remote.model.UserActivityResponse
import com.app.easyday.app.sources.remote.model.UserModel
import com.app.easyday.screens.activities.main.home.HomeFragment
import com.app.easyday.screens.activities.main.home.HomeFragment.Companion.selectedProjectID
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_activity_log.*

@AndroidEntryPoint
class ActivityLogFragment : BaseFragment<ActivityLogViewModel>() {

    var projectparticipantList: ArrayList<UserActivityResponse>? = null
    var userActivityModel: UserActivityResponse? = null

    override fun getContentView() = R.layout.fragment_activity_log

    override fun initUi() {

        val dot = requireContext().resources.getString(R.string.dot)
        time?.text = requireContext().resources.getString(R.string.hyderabad, dot)
        time2?.text = requireContext().resources.getString(R.string.hyderabad, dot)
        time3?.text = requireContext().resources.getString(R.string.hyderabad, dot)
        time4?.text = requireContext().resources.getString(R.string.hyderabad, dot)
        time5?.text = requireContext().resources.getString(R.string.hyderabad, dot)
        time6?.text = requireContext().resources.getString(R.string.hyderabad, dot)
        time7?.text = requireContext().resources.getString(R.string.hyderabad, dot)

        back.setOnClickListener {
            Navigation.findNavController(requireView()).popBackStack()
        }

        selectedProjectID?.let { viewModel.getUserActivityDetails(it) }

//        comment_tv.text = userActivityModel?.activityText?.length.toString()
        comment_tv.text = userActivityModel?.activityText?.toString()
//        comm.setImageURI()
    }

    override fun setObservers() {

        viewModel.projectParticipantsData.observe(viewLifecycleOwner) {
            this.projectparticipantList = it
        }
    }

}