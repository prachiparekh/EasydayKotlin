package com.app.easyday.screens.activities.main.more.activityLog

import android.annotation.SuppressLint
import android.view.View
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.app.easyday.R
import com.app.easyday.app.sources.local.model.ListItem
import com.app.easyday.app.sources.local.model.ListSection
import com.app.easyday.app.sources.remote.model.UserActivityResponse
import com.app.easyday.screens.activities.main.home.HomeFragment.Companion.selectedProjectID
import com.app.easyday.screens.base.BaseFragment
import com.app.easyday.utils.DateTimeUtils
import com.app.easyday.utils.DateTimeUtils.getNowSeconds
import com.app.easyday.utils.DateTimeUtils.getTitleDate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_activity_log.*
import java.text.SimpleDateFormat

@AndroidEntryPoint
class ActivityLogFragment : BaseFragment<ActivityLogViewModel>() {

    var userActivityList: ArrayList<UserActivityResponse> = ArrayList()
    var layoutManager: LinearLayoutManager? = null
    private var adapter: ActivityLogAdapter? = null

    private val listItems = ArrayList<ListItem>()
    private val invisibleListItems = ArrayList<ListItem>()
    private val dateItems = ArrayList<String>()

    override fun getContentView() = R.layout.fragment_activity_log

    override fun initUi() {

        back.setOnClickListener {
            Navigation.findNavController(requireView()).popBackStack()
        }

        selectedProjectID?.let { viewModel.getUserActivityDetails(it) }

        layoutManager = LinearLayoutManager(context)
        activityRV.layoutManager = layoutManager

    }

    override fun setObservers() {

        viewModel.userActivityData.observe(viewLifecycleOwner) { userActivityResp ->
            userActivityList.clear()
            if (userActivityResp != null) {
                userActivityList = userActivityResp
            }
            if (userActivityResp.isNullOrEmpty()) {
                activityRV.visibility = View.GONE
            } else {
                activityRV.visibility = View.VISIBLE


                adapter = ActivityLogAdapter(
                    requireContext(), arrayListOf()
                )
                activityRV.adapter = adapter
                sortList(userActivityList)
            }
        }
    }


    @SuppressLint("SimpleDateFormat")
    private fun sortList(reminderList: ArrayList<UserActivityResponse>) {
        reminderList.sortWith { o1, o2 ->
            val date1 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(o1.createdAt)
            val date2 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(o2.createdAt)
            if (o1.createdAt != null && o2.createdAt != null) {
                val dateStr1 = DateTimeUtils.addStringTimeToDate(date1)
                val dateStr2 = DateTimeUtils.addStringTimeToDate(date2)
                dateStr1.compareTo(dateStr2)
            } else {
                date1.compareTo(date2)
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
                    val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(it.createdAt)
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
            adapter?.setItems(listItems, invisibleListItems)
            snapToCurrentActivity()
        }
    }

    private fun snapToCurrentActivity() {
        val smoothScroller: RecyclerView.SmoothScroller = object : LinearSmoothScroller(context) {
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_START
            }
        }
        smoothScroller.targetPosition = getPositionOfTheClosestTimePeriod()
        activityRV.layoutManager?.startSmoothScroll(smoothScroller)
    }

    private fun getPositionOfTheClosestTimePeriod(): Int {
        var position = 0
        listItems.forEachIndexed { index, listItem ->
            if (listItem is ListSection && listItem.title == "This Month") {
                position = index
                return position
            }
        }
        return position
    }

}