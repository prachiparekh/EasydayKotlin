package com.app.easyday.screens.activities.main.reports

import android.util.Log
import androidx.fragment.app.Fragment
import com.addisonelliott.segmentedbutton.SegmentedButtonGroup
import com.app.easyday.R
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
    }

    override fun setObservers() {
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


