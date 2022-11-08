package com.app.easyday.screens.activities.main.more.activityLog

import android.annotation.SuppressLint
import android.view.View
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.easyday.R
import com.app.easyday.app.sources.remote.model.UserActivityResponse
import com.app.easyday.screens.activities.main.home.HomeFragment.Companion.selectedProjectID
import com.app.easyday.screens.base.BaseFragment
import com.app.easyday.utils.DateTimeUtils
import com.app.easyday.utils.DateTimeUtils.getNowSeconds
import com.app.easyday.utils.DateTimeUtils.getTitleDate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_activity_log.*
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class ActivityLogFragment : BaseFragment<ActivityLogViewModel>() {

    var userActivityList: ArrayList<UserActivityResponse> = ArrayList()
    var userActivityModel: UserActivityResponse? = null
    var layoutManager: LinearLayoutManager? = null
    private var adapter: ActivityLogAdapter? = null

    private val listItems = java.util.ArrayList<ListItem>()
    private val invisibleListItems = java.util.ArrayList<ListItem>()
    private val dateItems = ArrayList<String>()

    override fun getContentView() = R.layout.fragment_activity_log

    override fun initUi() {

        back.setOnClickListener {
            Navigation.findNavController(requireView()).popBackStack()
        }

        selectedProjectID?.let { viewModel.getUserActivityDetails(it) }

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

                Collections.reverse(userActivityList)

//                sortList(userActivityList)

            }

//            Log.e("userActivityList", userActivityList.toString())

        }
    }


    /*@SuppressLint("SimpleDateFormat", "NewApi")
    private fun sortList(userActivityList: ArrayList<UserActivityResponse>) {

        val listDate : UserActivityResponse? = null
        val odt = OffsetDateTime.parse(listDate?.createdAt)
        val dtf = DateTimeFormatter.ofPattern("MMM dd,yyyy", Locale.ENGLISH)
        val dtf1 = DateTimeFormatter.ofPattern("HH:MMa", Locale.ENGLISH)

        val date: Date? = dtf.format(odt)

        userActivityList.sortWith { o1, o2 ->
            if (date != null && date != null) {
                val date1 = DateTimeUtils.addStringTimeToDate(date)
                val date2 = DateTimeUtils.addStringTimeToDate(date)
                date1.compareTo(date2)
            } else {
                date!!.compareTo(date)
            }

        }
        if (adapter != null) {
//            adapter?.clearAll()
            listItems.clear()
            invisibleListItems.clear()
            var prevCode = ""
            val now = getNowSeconds()
            val today = getTitleDate(now, requireContext(), true)
            var reachedCurrentMonth = false
            userActivityList.forEach {
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
//            adapter?.setItems(listItems, allFilterList, invisibleListItems)
//            snapToCurrentReminder()
        }
    }*/
    /*private fun snapToCurrentReminder() {
        val smoothScroller: RecyclerView.SmoothScroller = object : LinearSmoothScroller(context) {
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_START
            }
        }
        smoothScroller.targetPosition = getPositionOfTheClosestTimePeriod()
        Act_log_Recy.layoutManager?.startSmoothScroll(smoothScroller)
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
    }*/

}