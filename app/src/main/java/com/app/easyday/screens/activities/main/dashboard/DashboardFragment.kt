package com.app.easyday.screens.activities.main.dashboard

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.view.KeyEvent
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
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
//            val action = DashboardFragmentDirections.dashboardToCamera()
//            val nav: NavController = Navigation.findNavController(requireView())
//            if (nav.currentDestination != null && nav.currentDestination?.id == R.id.dashboardFragment) {
//                nav.navigate(action)
//            }
            showDialog()
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
//                fragment = InboxFragment()
//                tag = InboxFragment.TAG
                showDialog()
            }
            R.id.reports -> {
//                fragment = ReportsFragment()
//                tag = ReportsFragment.TAG
                showDialog()
            }
            R.id.more -> {
//                fragment = MoreFragment()
//                tag = MoreFragment.TAG
                showDialog()
            }
        }
        if (fragment != null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.childContent, fragment)
                .addToBackStack(tag)
                .commit()
        }

    }
    private fun showDialog() {
        val dialog = Dialog(requireActivity(), R.style.DialogTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.coming_soon_dialog)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(0))
//        val body = dialog.findViewById(R.id.body) as TextView
//        body.text = title
        val yesBtn = dialog.findViewById(R.id.yesBtn) as TextView

        yesBtn.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

    }

    override fun setObservers() {

    }


}