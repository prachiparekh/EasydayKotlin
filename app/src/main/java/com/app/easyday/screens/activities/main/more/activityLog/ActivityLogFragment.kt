package com.app.easyday.screens.activities.main.more.activityLog

import android.annotation.SuppressLint
import android.view.View
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
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
import java.util.*

@AndroidEntryPoint
class ActivityLogFragment : BaseFragment<ActivityLogViewModel>() {

    var userActivityList: ArrayList<UserActivityResponse> = ArrayList()
    var layoutManager: LinearLayoutManager? = null
    private var adapter: ActivityLogAdapter? = null

    private val listItems = ArrayList<ListItem>()

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
    private fun sortList(logList: ArrayList<UserActivityResponse>) {

        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        format.timeZone = TimeZone.getTimeZone("UTC")

        logList.sortWith { o1, o2 ->
            val date1 = format.parse(o1.createdAt)
            val date2 = format.parse(o2.createdAt)
            if (o1.createdAt != null && o2.createdAt != null) {
                val dateStr1 = DateTimeUtils.addStringTimeToDate(date1)
                val dateStr2 = DateTimeUtils.addStringTimeToDate(date2)
                dateStr2.compareTo(dateStr1)
            } else {
                date2.compareTo(date1)
            }
        }

        logList.sortByDescending { it.createdAt }

        if (adapter != null) {
            adapter?.clearAll()
            listItems.clear()
            var prevCode = ""
            val now = getNowSeconds()
            val today = getTitleDate(now, requireContext(), true)
            logList.forEach {
                if (it.createdAt != null) {
                    val date =
                        format.parse(
                            it.createdAt
                        )
                    val code = getTitleDate(date.time, requireContext(), true)

                    if (code != prevCode) {
                        val titleItem = getTitleDate(date.time, requireContext(), true)
                        val day = getTitleDate(date.time, requireContext(), true)
                        val isToday = day == today

                        val listSection =
                            ListSection(titleItem, code, isToday, !isToday && date.time < now)
                        listSection.sectionName = code
                        listItems.add(listSection)
                        prevCode = code
                    }
                    it.sectionName = prevCode
                    listItems.add(it)
                }
            }
            adapter?.setItems(listItems)
        }
    }
}