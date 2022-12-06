package com.app.easyday.screens.activities.main.reports

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Handler
import android.util.Log
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import com.addisonelliott.segmentedbutton.SegmentedButtonGroup
import com.app.easyday.R
import com.app.easyday.screens.activities.main.home.HomeFragment
import com.app.easyday.screens.base.BaseFragment
import com.app.easyday.utils.BigScreenshot
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
            val totalTask = ReportTaskModel?.tasksData?.totalTask.toString()
            val projectName = ReportTaskModel?.projectName
            val assignColor = ReportTaskModel?.assignColor

            taskTV.text = requireContext().resources.getString(R.string.task_count, totalTask)
            activeProject.text = projectName

            TextViewCompat.setCompoundDrawableTintList(
                activeProject,
                ColorStateList.valueOf(
                    Color.parseColor(assignColor)
                )
            )

            val view = mainCL as ViewGroup
            val ss = view.let {
                BigScreenshot(object : BigScreenshot.ProcessScreenshot {
                    override fun getScreenshot(bitmap: Bitmap?) {
                        Log.e("bitmap:", bitmap.toString())
                        img.setImageBitmap(bitmap)
                        mainCL.isVisible = false
                    }

                }, it, it)
            }

            ss.startScreenshot()

            val handler = Handler()
            handler.postDelayed({
                ss.stopScreenshot()
            }, 5000)
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


