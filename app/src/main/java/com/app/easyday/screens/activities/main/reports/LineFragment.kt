package com.app.easyday.screens.activities.main.reports

import com.app.easyday.R
import com.app.easyday.app.sources.remote.model.ReportParticipantsModel
import com.app.easyday.app.sources.remote.model.ReportTagResponse
import com.app.easyday.screens.activities.main.home.HomeFragment
import com.app.easyday.screens.base.BaseFragment
import com.google.android.flexbox.FlexboxLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_line.*

@AndroidEntryPoint
class LineFragment : BaseFragment<LineViewModel>() {

    companion object
    {
        val tag="Line"
    }

    var isLineFragment: Boolean = true

    override fun getContentView() = R.layout.fragment_line

    override fun initUi() {

        HomeFragment.selectedProjectID?.let { viewModel.getReport(it) }
    }

    override fun setObservers() {
        viewModel.ReportData.observe(viewLifecycleOwner) { ReportTaskModel ->

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
            taskTV.text = requireContext().resources.getString(R.string.task_count, reopened)

            urgentTV.text = urgent
            lowTV.text = low
            normalTV.text = normal
            flagTV.text = redFlaged
            notFlagTV.text = notFlaged

            val participant = ReportTaskModel?.projectParticipants

            report_participant_RV.adapter = context?.let {
                ReportParticipantAdapter(
                    it,
                    participant as ArrayList<ReportParticipantsModel>, isLineFragment
                )
            }

            val tagList = ReportTaskModel?.projectTags

            tagRV.adapter = context?.let {
                TagAdapter(
                    it,
                    tagList as ArrayList<ReportTagResponse>, isLineFragment
                )
            }
            tagRV.layoutManager = FlexboxLayoutManager(requireContext())


        }
    }


}