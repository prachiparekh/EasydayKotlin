package com.app.easyday.screens.activities.main.home.filter

import android.app.DatePickerDialog
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import com.app.easyday.R
import com.app.easyday.app.sources.local.interfaces.FilterTypeInterface
import com.app.easyday.app.sources.local.interfaces.TaskFilterApplyInterface
import com.app.easyday.app.sources.local.model.ContactModel
import com.app.easyday.app.sources.remote.model.AttributeResponse
import com.app.easyday.app.sources.remote.model.ProjectParticipantsModel
import com.app.easyday.screens.base.BaseFragment
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.date_range_filter_layout.view.*
import kotlinx.android.synthetic.main.fragment_filter.*
import kotlinx.android.synthetic.main.other_filter_layout.view.*
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class FilterFragment(val anInterface: TaskFilterApplyInterface) : BaseFragment<FilterViewModel>(),
    FilterTypeInterface {

    companion object {
        var selectedFilterPosition = 0
        var filterTagList = ArrayList<Int>()
        var filterZoneList = ArrayList<Int>()
        var filterSpaceList = ArrayList<Int>()
        var filterAssigneeList = ArrayList<Int>()
        var filterDateRangeList = ArrayList<String>()
        var filterPriority: Int? = null
        var filterTaskStatus: Int? = null
        var filterRedFlag: Int? = null
        var filterDueDate: Int? = null
    }

    private var filterTypeList = arrayListOf<String>()
    private var taskStatusList = arrayListOf<String>()
    private var tagList = ArrayList<AttributeResponse>()
    private var zoneList = ArrayList<AttributeResponse>()
    private var spaceList = ArrayList<AttributeResponse>()
    var priorityList = arrayListOf<String>()
    var redFlagList = arrayListOf<String>()
    var dueDateList = arrayListOf<String>()
    var projectparticipantList: ArrayList<ProjectParticipantsModel>? = null
    val contactList = ArrayList<ContactModel>()
    var filterDateMax: String? = null
    var filterDateMin: String? = null

    override fun getContentView() = R.layout.fragment_filter

    override fun initUi() {
        filterTypeList.add(requireContext().resources.getString(R.string.f_date_range))
        filterTypeList.add(requireContext().resources.getString(R.string.f_task_status))
        filterTypeList.add(requireContext().resources.getString(R.string.f_tag))
        filterTypeList.add(requireContext().resources.getString(R.string.f_priority))
        filterTypeList.add(requireContext().resources.getString(R.string.f_assigned_To))
        filterTypeList.add(requireContext().resources.getString(R.string.f_zone))
        filterTypeList.add(requireContext().resources.getString(R.string.f_space))
        filterTypeList.add(requireContext().resources.getString(R.string.f_red_flag))
        filterTypeList.add(requireContext().resources.getString(R.string.f_due_date))

        filterRV.adapter = FilterTypeAdapter(requireContext(), filterTypeList, this)

        taskStatusList.add(requireContext().resources.getString(R.string.progress))
        taskStatusList.add(requireContext().resources.getString(R.string.review))
        taskStatusList.add(requireContext().resources.getString(R.string.completed))
        taskStatusList.add(requireContext().resources.getString(R.string.reopened))

        priorityList.add(requireContext().resources.getString(R.string.none))
        priorityList.add(requireContext().resources.getString(R.string.low))
        priorityList.add(requireContext().resources.getString(R.string.normal))
        priorityList.add(requireContext().resources.getString(R.string.high))

        redFlagList.add(requireContext().resources.getString(R.string.yes))
        redFlagList.add(requireContext().resources.getString(R.string.no))

        dueDateList.add(requireContext().resources.getString(R.string.passed))
        dueDateList.add(requireContext().resources.getString(R.string.not_passed))

        changeFilterUI(selectedFilterPosition)

        close.setOnClickListener {
            anInterface.onClose()
        }

        cta.setOnClickListener {

            filterDateRangeList.clear()
            filterDateMin?.let { it1 -> filterDateRangeList.add(it1) }
            filterDateMax?.let { it1 -> filterDateRangeList.add(it1) }


            anInterface.setFilter()
        }

        clearFilterTV.setOnClickListener {
            filterTagList.clear()
            filterZoneList.clear()
            filterSpaceList.clear()
            filterAssigneeList.clear()
            filterDateRangeList.clear()
            filterPriority = null
            filterTaskStatus = null
            filterRedFlag = null
            filterDueDate = null
        }
    }

    override fun setObservers() {
        viewModel.actionStream.observe(viewLifecycleOwner) {
            when (it) {
                is FilterViewModel.ACTION.getAttributes -> {
                    when (it.type) {
                        0 -> {
                            tagList = it.attributeList as ArrayList<AttributeResponse>
                        }
                        1 -> {
                            zoneList = it.attributeList as ArrayList<AttributeResponse>
                        }
                        2 -> {
                            spaceList = it.attributeList as ArrayList<AttributeResponse>
                        }
                    }
                }
                else -> {}
            }
        }

        viewModel.projectParticipantsData.observe(viewLifecycleOwner) {
            this.projectparticipantList = it

            if (!projectparticipantList.isNullOrEmpty()) {
                projectparticipantList?.indices?.forEach { i ->
                    contactList.add(
                        ContactModel(
                            id = projectparticipantList?.get(i)?.id.toString(),
                            name = projectparticipantList?.get(i)?.user?.fullname,
                            role = projectparticipantList?.get(i)?.role,
                            phoneNumber = projectparticipantList?.get(i)?.user?.phoneNumber.toString(),
                            photoURI = projectparticipantList?.get(i)?.user?.profileImage
                        )
                    )
                }
            }
        }
    }

    override fun onFilterTypeClick(position: Int) {
        selectedFilterPosition = position
        changeFilterUI(selectedFilterPosition)
    }

    override fun onFilterSingleChildClick(
        childList: ArrayList<String>,
        childLabel: TextView,
        childPosition: Int
    ) {

    }

    override fun onFilterFlagClick(redFlag: Boolean) {

    }

    override fun onFilterDueDateClick(dateStr: String) {

    }

    private fun changeFilterUI(position: Int) {
        if (position == 0) {
//            DueDate Filter
            dateRangeLayout.isVisible = true
            otherLayout.isVisible = false
            dueDateFilterHandler()
        } else {
//            Other Filter
            dateRangeLayout.isVisible = false
            otherLayout.isVisible = true
            otherFilterHandler(position)
        }
    }

    private fun dueDateFilterHandler() {
        dateRangeLayout.fromET?.setOnClickListener {
            showDatePicker(it, Date().time)
        }

        dateRangeLayout.toET?.setOnClickListener {
            showDatePicker(it, Date().time)
        }
    }

    private fun otherFilterHandler(parentPosition: Int) {
        when (parentPosition) {
            1 -> {
//                Single : Task Status
                otherLayout.childFilterRV.adapter =
                    FilterSingleChildAdapter(requireContext(), taskStatusList, "TASKSTATUS")
            }
            2 -> {
//                 Multiple :Tag (API)
                otherLayout.childFilterRV.adapter =
                    FilterMultipleChildAdapter(requireContext(), tagList, 0)
            }
            3 -> {
//                Single : Priority
                otherLayout.childFilterRV.adapter =
                    FilterSingleChildAdapter(requireContext(), priorityList, "PRIORITY")
            }
            4 -> {
//                 Multiple :Assigned To (API)

                otherLayout.childFilterRV.adapter =
                    FilterAssigneeAdapter(requireContext(), contactList, filterAssigneeList)
            }
            5 -> {
//                 Multiple :Zone (API)
                otherLayout.childFilterRV.adapter =
                    FilterMultipleChildAdapter(requireContext(), zoneList, 1)
            }
            6 -> {
//                 Multiple :Space (API)
                otherLayout.childFilterRV.adapter =
                    FilterMultipleChildAdapter(requireContext(), spaceList, 2)
            }
            7 -> {
//                 Single :Red Flag
                otherLayout.childFilterRV.adapter =
                    FilterSingleChildAdapter(requireContext(), redFlagList, "REDFLAG")
            }
            8 -> {
//                 Single :Due Date
                otherLayout.childFilterRV.adapter =
                    FilterSingleChildAdapter(requireContext(), dueDateList, "DUEDATE")
            }

        }

    }

    private var dpd: DatePickerDialog? = null
    private fun showDatePicker(view: View, dateInMillis: Long) {
        val c = Calendar.getInstance().apply {
            timeInMillis = dateInMillis
        }

        dpd?.cancel()
        dpd = DatePickerDialog(
            requireContext(), R.style.DialogTheme, { _, year, monthOfYear, dayOfMonth ->

                if (view.id == R.id.fromET) {
                    dateRangeLayout.fromLabel?.setEndIconDrawable(R.drawable.ic_calendar_visible)
                    dateRangeLayout.fromLabel?.endIconMode = TextInputLayout.END_ICON_CUSTOM
                } else {
                    dateRangeLayout.toLabel?.setEndIconDrawable(R.drawable.ic_calendar_visible)
                    dateRangeLayout.toLabel?.endIconMode = TextInputLayout.END_ICON_CUSTOM
                }

                setDueDate(
                    view, Calendar.getInstance().apply {
                        set(Calendar.YEAR, year)
                        set(Calendar.MONTH, monthOfYear)
                        set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    }.timeInMillis
                )
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)
        )

        dpd?.show()
    }

    private var fromDateMillis: Long = 0
    private fun setDueDate(view: View, timeInMillies: Long) {

        if (view.id == R.id.fromET) {
            fromDateMillis = timeInMillies
        }
        if (!fromDateMillis.equals(0)) {
            if (view.id == R.id.toET) {
                if (fromDateMillis > timeInMillies) {
                    Toast.makeText(
                        requireContext(),
                        requireContext().resources.getString(R.string.to_date_error),
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
            }
        }
        (view as? TextView)?.text = SimpleDateFormat(
            getString(R.string.date_format_template), Locale.getDefault()
        ).format(
            Date(timeInMillies)
        )

        val filterDate = SimpleDateFormat(
            getString(R.string.date_format_template1), Locale.getDefault()
        ).format(
            Date(timeInMillies)
        )

        if (view.id == R.id.fromET) {
            filterDateMin = filterDate
        } else {
            filterDateMax = filterDate
        }

    }


}