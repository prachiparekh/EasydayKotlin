package com.app.easyday.screens.activities.main.dashboard

import android.view.KeyEvent
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.app.easyday.R
import com.app.easyday.screens.activities.main.home.HomeFragment
import com.app.easyday.screens.activities.main.inbox.InboxFragment
import com.app.easyday.screens.activities.main.more.MoreFragment
import com.app.easyday.screens.activities.main.reports.ReportsFragment
import com.app.easyday.screens.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_dashboard.*

@AndroidEntryPoint
class DashboardFragment : BaseFragment<DashboardViewModel>() {


    override fun getContentView() = R.layout.fragment_dashboard

//    companion object {
        var selectedTabID = R.id.home
//    }

    override fun initUi() {

        requireView().isFocusableInTouchMode = true
        requireView().requestFocus()
        requireView().setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                requireActivity().finishAffinity()
                true
            } else false
        }

        openChildFragment(selectedTabID)
        bottomNavigationView.setOnItemSelectedListener {
            selectedTabID = it.itemId
            openChildFragment(selectedTabID)
            true
        }

        add.setOnClickListener {
            val action = DashboardFragmentDirections.dashboardToCamera()
            val nav: NavController = Navigation.findNavController(requireView())
            if (nav.currentDestination != null && nav.currentDestination?.id == R.id.dashboardFragment) {
                nav.navigate(action)
            }
        }
    }

    private fun openChildFragment(selectedItemID: Int) {

        var fragment: Fragment? = null
        var tag: String? = null

        when (selectedItemID) {
            R.id.home -> {
                fragment = HomeFragment()
                tag = HomeFragment.TAG
            }
            R.id.inbox -> {
                fragment = InboxFragment()
                tag = InboxFragment.TAG
            }
            R.id.reports -> {
                fragment = ReportsFragment()
                tag = ReportsFragment.TAG
            }
            R.id.more -> {
                fragment = MoreFragment()
                tag = MoreFragment.TAG
            }
        }
   if (fragment != null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.childContent, fragment)
                .addToBackStack(tag)
                .commit()
        }

    }

    override fun setObservers() {

    }


}