package com.app.easyday.screens.activities.main.reports

import com.app.easyday.R
import com.app.easyday.screens.base.BaseFragment
import com.faskn.lib.PieChart
import com.faskn.lib.Slice
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_chart.*

@AndroidEntryPoint
class ChartFragment : BaseFragment<ChartViewModel>() {

    companion object {
        val tag = "Chart"
    }

    override fun getContentView() = R.layout.fragment_chart

    override fun initUi() {
        val taskChart1 = PieChart(
            slices = taskSlices(), clickListener = null, sliceStartPoint = 0f, sliceWidth = 50f
        ).build()

        taskChart.setPieChart(taskChart1)

        val priorityChart1 = PieChart(
            slices = prioritySlices(), clickListener = null, sliceStartPoint = 0f, sliceWidth = 50f
        ).build()

        priorityChart.setPieChart(priorityChart1)

        val flagChart1 = PieChart(
            slices = flagSlices(), clickListener = null, sliceStartPoint = 0f, sliceWidth = 50f
        ).build()

        flagChart.setPieChart(flagChart1)

    }

    override fun setObservers() {

    }

    private fun taskSlices(): ArrayList<Slice> {
        return arrayListOf(
            Slice(
                20F,
                R.color.sky_blue,
                requireContext().resources.getString(R.string.inprogress)
            ),
            Slice(
                12F,
                R.color.yellow,
                requireContext().resources.getString(R.string.review)
            ),
            Slice(
                44F,
                R.color.green,
                requireContext().resources.getString(R.string.completed)
            ),
            Slice(
                86F,
                R.color.red,
                requireContext().resources.getString(R.string.reopened)
            )
        )
    }

    private fun prioritySlices(): ArrayList<Slice> {
        return arrayListOf(
            Slice(
                64F,
                R.color.red,
                requireContext().resources.getString(R.string.urgent_p)
            ),
            Slice(
                50F,
                R.color.green,
                requireContext().resources.getString(R.string.low_p)
            ),
            Slice(
                48F,
                R.color.yellow,
                requireContext().resources.getString(R.string.normal)
            )
        )
    }

    private fun flagSlices(): ArrayList<Slice> {
        return arrayListOf(
            Slice(
                64F,
                R.color.red,
                requireContext().resources.getString(R.string.red_flagged)
            ),
            Slice(
                50F,
                R.color.tab_gray,
                requireContext().resources.getString(R.string.not_flagged)
            )
        )
    }


}