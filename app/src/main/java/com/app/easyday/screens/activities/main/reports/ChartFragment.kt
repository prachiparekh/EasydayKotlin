package com.app.easyday.screens.activities.main.reports

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.easyday.R
import com.app.easyday.app.sources.remote.model.*
import com.app.easyday.screens.activities.main.home.HomeFragment
import com.app.easyday.screens.base.BaseFragment
import com.faskn.lib.PieChart
import com.faskn.lib.Slice
import com.google.android.flexbox.FlexboxLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_chart.*
import kotlinx.android.synthetic.main.fragment_chart.completedTV
import kotlinx.android.synthetic.main.fragment_chart.lowTV
import kotlinx.android.synthetic.main.fragment_chart.normalTV
import kotlinx.android.synthetic.main.fragment_chart.progressTV
import kotlinx.android.synthetic.main.fragment_chart.reviewTV
import kotlinx.android.synthetic.main.fragment_chart.urgentTV
import kotlinx.android.synthetic.main.fragment_line.*

@AndroidEntryPoint
class ChartFragment : BaseFragment<ChartViewModel>() {

    var model : ReportResponse? = null
    var isLineFragment: Boolean = false

    companion object {
        val tag = "Chart"
    }

    override fun getContentView() = R.layout.fragment_chart

    override fun initUi() {

        HomeFragment.selectedProjectID?.let { viewModel.getReport(it) }


    }

    override fun setObservers() {

        viewModel.ReportData.observe(viewLifecycleOwner){ ReportTaskModel ->

            model = ReportTaskModel
            val progress = ReportTaskModel?.tasksData?.inProgress.toString()
            val review = ReportTaskModel?.tasksData?.inReview.toString()
            val complete = ReportTaskModel?.tasksData?.completed.toString()
            val reopened = ReportTaskModel?.tasksData?.reopened.toString()
            val total_task = ReportTaskModel?.tasksData?.totalTask.toString()
            val urgent = ReportTaskModel?.tasksData?.urgent.toString()
            val low = ReportTaskModel?.tasksData?.low.toString()
            val normal = ReportTaskModel?.tasksData?.normal.toString()
            val redFlaged = ReportTaskModel?.tasksData?.redFlaged.toString()
            val notFlaged = ReportTaskModel?.tasksData?.notFlaged.toString()
            progressTV.text = requireContext().resources.getString(R.string.task_count, progress)
            reviewTV.text = requireContext().resources.getString(R.string.task_count, review)
            completedTV.text = requireContext().resources.getString(R.string.task_count, complete)
            reopenTV.text = requireContext().resources.getString(R.string.task_count, reopened)
            totalTaskTV.text = total_task
            totalPriorityTV.text = total_task
            totalflagTV.text = total_task
            urgentTV.text = urgent
            lowTV.text = low
            normalTV.text = normal
            flaggedTV.text = redFlaged
            notFlaggedTV.text = notFlaged

            val taskChart1 = ReportTaskModel?.let { taskSlices(it) }?.let {
                PieChart(
                    slices = it, clickListener = null, sliceStartPoint = 0f, sliceWidth = 50f
                ).build()
            }
            taskChart1?.let { taskChart.setPieChart(it) }

            val priorityChart1 = ReportTaskModel?.let { prioritySlices(it) }?.let {
                PieChart(
                    slices = it, clickListener = null, sliceStartPoint = 0f, sliceWidth = 50f
                ).build()
            }
            priorityChart1?.let { priorityChart.setPieChart(it) }

            val flagChart1 = ReportTaskModel?.let { flagSlices(it) }?.let {
                PieChart(
                    slices = it, clickListener = null, sliceStartPoint = 0f, sliceWidth = 50f
                ).build()
            }
            flagChart1?.let { flagChart.setPieChart(it) }

//            val priorityChart1 = PieChart(
//                slices = prioritySlices(), clickListener = null, sliceStartPoint = 0f, sliceWidth = 50f
//            ).build()
//
//            priorityChart.setPieChart(priorityChart1)

//            val flagChart1 = PieChart(
//                slices = flagSlices(), clickListener = null, sliceStartPoint = 0f, sliceWidth = 50f
//            ).build()
//
//            flagChart.setPieChart(flagChart1)

            val participant= ReportTaskModel?.projectParticipants
            report_participant_RV_1.adapter = context?.let {
                ReportParticipantAdapter(
                    it,
                    participant as ArrayList<ReportParticipantsModel>, isLineFragment
                )
            }

            val tagList = ReportTaskModel?.projectTags

            tagRV_1.adapter = context?.let {
                TagAdapter(
                    it,
                    tagList as ArrayList<ReportTagResponse>, isLineFragment
                )
            }
            tagRV_1.layoutManager= LinearLayoutManager(requireContext())

        }

    }

    private fun taskSlices(model: ReportResponse): ArrayList<Slice> {

        return arrayListOf(
            Slice(
                model.tasksData?.inProgress?.toFloat() ?:0F,
                R.color.sky_blue,
                requireContext().resources.getString(R.string.inprogress)
            ),
            Slice(
                model.tasksData?.inReview?.toFloat() ?:0F,
                R.color.yellow,
                requireContext().resources.getString(R.string.review)
            ),
            Slice(
                model.tasksData?.completed?.toFloat() ?:0F,
                R.color.green,
                requireContext().resources.getString(R.string.completed)
            ),
            Slice(
                model.tasksData?.reopened?.toFloat() ?:0F,
                R.color.red,
                requireContext().resources.getString(R.string.reopened)
            )
        )
    }

    private fun prioritySlices(model: ReportResponse): ArrayList<Slice> {
        return arrayListOf(
            Slice(
                model.tasksData?.urgent?.toFloat() ?:0F,
                R.color.red,
                requireContext().resources.getString(R.string.urgent_p)
            ),
            Slice(
                model.tasksData?.low?.toFloat() ?:0F,
                R.color.green,
                requireContext().resources.getString(R.string.low_p)
            ),
            Slice(
                model.tasksData?.normal?.toFloat() ?:0F,
                R.color.yellow,
                requireContext().resources.getString(R.string.normal)
            )
        )
    }

    private fun flagSlices(model: ReportResponse): ArrayList<Slice> {
        return arrayListOf(
            Slice(
                model.tasksData?.redFlaged?.toFloat() ?:0F,
                R.color.red,
                requireContext().resources.getString(R.string.red_flagged)
            ),
            Slice(
                model.tasksData?.notFlaged?.toFloat() ?:0F,
                R.color.tab_gray,
                requireContext().resources.getString(R.string.not_flagged)
            )
        )
    }


}