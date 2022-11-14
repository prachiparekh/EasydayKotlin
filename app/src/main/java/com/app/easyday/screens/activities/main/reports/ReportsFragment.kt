package com.app.easyday.screens.activities.main.reports

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import com.addisonelliott.segmentedbutton.SegmentedButtonGroup
import com.app.easyday.R
import com.app.easyday.screens.activities.main.home.HomeFragment
import com.app.easyday.screens.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_reports.*

@AndroidEntryPoint
class ReportsFragment : BaseFragment<ReportsViewModel>(),
    SegmentedButtonGroup.OnPositionChangedListener {


    companion object {
        const val TAG = "ReportsFragment"
    }

    override fun getContentView() = R.layout.fragment_reports

    override fun initUi() {

        childFragmentManager.beginTransaction()
            .replace(R.id.reportContainer, LineFragment())
            .addToBackStack(tag)
            .commit()
        reportTypeGroup.onPositionChangedListener = this


        HomeFragment.selectedProjectID?.let { viewModel.getReport(it) }
    }

    override fun setObservers() {
        viewModel.ReportData.observe(viewLifecycleOwner) { ReportTaskModel ->
            val total_task = ReportTaskModel?.tasksData?.totalTask.toString()
            val projectName = ReportTaskModel?.projectName
            val assignColor = ReportTaskModel?.assignColor

            taskTV.text = requireContext().resources.getString(R.string.task_count, total_task)
            activeProject.text = projectName

            TextViewCompat.setCompoundDrawableTintList(
                activeProject,
                ColorStateList.valueOf(
                    Color.parseColor(assignColor)
                )
            )
        }
    }

    override fun onPositionChanged(position: Int) {
        var fragment: Fragment? = null
        var tag: String? = null
        when (position) {
            0 -> {
                fragment = LineFragment()
                tag = LineFragment.tag
            }
            1 -> {
                fragment = ChartFragment()
                tag = ChartFragment.tag
            }
        }
        if (fragment != null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.reportContainer, fragment)
                .addToBackStack(tag)
                .commit()
        }
    }
}


