package com.app.easyday.screens.activities.main.more.activityLog

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import com.app.easyday.screens.base.BaseFragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.easyday.R
import com.app.easyday.app.sources.remote.model.UserActivityResponse
import com.app.easyday.screens.activities.main.home.HomeFragment.Companion.selectedProjectID
import com.app.easyday.utils.DateTimeUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_activity_log.*

@AndroidEntryPoint
class ActivityLogFragment : BaseFragment<ActivityLogViewModel>() {

    var userActivityList: ArrayList<UserActivityResponse> = ArrayList()
    var layoutManager: LinearLayoutManager? = null
    private var adapter: ActivityLogAdapter? = null

    private val listItems = java.util.ArrayList<ListItem>()
    private val invisibleListItems = java.util.ArrayList<ListItem>()

    override fun getContentView() = R.layout.fragment_activity_log

    override fun initUi() {

        val dot = requireContext().resources.getString(R.string.dot)

//        time4?.text = requireContext().resources.getString(R.string.hyderabad, dot)
//        time5?.text = requireContext().resources.getString(R.string.hyderabad, dot)
//        time6?.text = requireContext().resources.getString(R.string.hyderabad, dot)
//        time7?.text = requireContext().resources.getString(R.string.hyderabad, dot)

        back.setOnClickListener {
            Navigation.findNavController(requireView()).popBackStack()
        }

        selectedProjectID?.let { viewModel.getUserActivityDetails(it) }

//        comment_tv.text = userActivityModel?.activityText?.length.toString()
//        comment_tv.text = userActivityModel?.activityText?.toString()
//        comm.setImageURI()
        layoutManager = LinearLayoutManager(context)
        Act_log_Recy.layoutManager = layoutManager

    }

    override fun setObservers() {

        viewModel.userActivityData.observe(viewLifecycleOwner) { userActivityResp ->
            userActivityList.clear()
            if (userActivityResp != null) {
                userActivityList = userActivityResp
            }
            if (userActivityResp.isNullOrEmpty()) {
                Act_log_Recy.visibility = View.GONE
            } else {
                Act_log_Recy.visibility = View.VISIBLE


                adapter = ActivityLogAdapter(
                    requireContext(), userActivityList
                )
                Act_log_Recy.adapter = adapter

//                sortList(userActivityList)
            }

//            Log.e("userActivityList", userActivityList.toString())
        }
    }

    /*@SuppressLint("SimpleDateFormat")
    private fun sortList(reminderList: java.util.ArrayList<UserActivityResponse>) {
        reminderList.sortWith { o1, o2 ->
            if (o1.createdAt != null && o2.createdAt != null && o1.createdAt != null && o2.createdAt != null) {
                val date1 = DateTimeUtils.addStringTimeToDate(o1.createdAt, o1.createdAt)
                val date2 = DateTimeUtils.addStringTimeToDate(o2.createdAt, o2.createdAt)
                date1.compareTo(date2)
            } else {
                o1.createdAt!!.compareTo(o2.createdAt)
            }

        }
        if (adapter != null) {
            adapter?.clearAll()
            listItems.clear()
            invisibleListItems.clear()
            var prevCode = ""
            val now = getNowSeconds()
            val today = getTitleDate(now, requireContext(), true)
            var reachedCurrentMonth = false
            reminderList.forEach {
                if (it.createdAt != null) {
                    val date = it.createdAt
                    val code = getTitleDate(date.time, requireContext(), false)
                    if (code != prevCode) {
                        val titleItem = getTitleDate(date.time, requireContext(), false)
                        val day = getTitleDate(date.time, requireContext(), true)
                        val isToday = day == today
                        if (code == requireContext().resources.getString(R.string.this_month)) {
                            reachedCurrentMonth = true
                        }
                        val listSection =
                            ListSection(titleItem, code, isToday, !isToday && date.time < now)
                        listSection.isItemVisible = reachedCurrentMonth
                        listSection.sectionName = code
                        listItems.add(listSection)
                        prevCode = code
                    }
                    it.sectionName = prevCode
                    if (!reachedCurrentMonth) {
                        invisibleListItems.add(it)
                    } else {
                        listItems.add(it)
                    }
                }
            }
            adapter?.setItems(listItems, allFilterList, invisibleListItems)
            snapToCurrentReminder()
        }
    }*/

}