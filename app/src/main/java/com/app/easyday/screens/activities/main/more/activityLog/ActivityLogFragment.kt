package com.app.easyday.screens.activities.main.more.activityLog

import android.util.Log
import com.app.easyday.screens.base.BaseFragment
import androidx.navigation.Navigation
import com.app.easyday.R
import com.app.easyday.app.sources.remote.model.UserActivityResponse
import com.app.easyday.screens.activities.main.home.HomeFragment.Companion.selectedProjectID
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_activity_log.*

@AndroidEntryPoint
class ActivityLogFragment : BaseFragment<ActivityLogViewModel>() {

    var userActivityList: ArrayList<UserActivityResponse>? = null
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

        viewModel.userActivityData.observe(viewLifecycleOwner) {
            this.userActivityList = it
            Log.e("userActivityList", userActivityList.toString())
        }
    }

}