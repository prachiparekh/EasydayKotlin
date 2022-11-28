package com.app.easyday.screens.activities.main.more.privacy

import android.content.Intent
import androidx.core.view.isVisible
import androidx.lifecycle.observe
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.app.easyday.R
import com.app.easyday.app.sources.local.interfaces.DeleteLogoutProfileInterface
import com.app.easyday.app.sources.local.prefrences.AppPreferencesDelegates
import com.app.easyday.screens.activities.auth.AuthActivity
import com.app.easyday.screens.activities.main.home.HomeFragment
import com.app.easyday.screens.base.BaseFragment
import com.app.easyday.screens.dialogs.DeleteUserDialog
import com.app.easyday.utils.DeviceUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_privacy_security.*

@AndroidEntryPoint
class PrivacySecurityFragment : BaseFragment<PrivacySecurityViewModel>(),
    DeleteLogoutProfileInterface {

    override fun getContentView() = R.layout.fragment_privacy_security

    override fun initUi() {

        DeviceUtils.initProgress(requireContext())
        option.setOnClickListener {
            deleteProfile.isVisible = true
            blankRL.isVisible = true
        }

        blankRL.setOnClickListener {
            blankRL.isVisible = false
            deleteProfile.isVisible = false
        }

        val dialog = DeleteUserDialog("DELETE", this)
        deleteProfile.setOnClickListener {
            if (!dialog.isAdded) {
                dialog.show(childFragmentManager, "DELETE")
                blankRL.isVisible = false
                deleteProfile.isVisible = false
            }
        }

        delete.setOnClickListener {
            if (!dialog.isAdded)
                dialog.show(childFragmentManager, "DELETE")
        }

        policyLL.setOnClickListener {
            val action = PrivacySecurityFragmentDirections.morePrivacyToTerms()

            action.url = "http://63.32.98.218/privacy_policy.html"
            action.title = requireContext().resources.getString(R.string.policy)
            val nav: NavController = Navigation.findNavController(requireView())
            if (nav.currentDestination != null && nav.currentDestination?.id == R.id.privacySecurityFragment) {
                nav.navigate(action)
            }
        }

        termsLL.setOnClickListener {
            val action = PrivacySecurityFragmentDirections.morePrivacyToTerms()

            action.url = "http://63.32.98.218/terms_of_service.html"
            action.title = requireContext().resources.getString(R.string.terms)
            val nav: NavController = Navigation.findNavController(requireView())
            if (nav.currentDestination != null && nav.currentDestination?.id == R.id.privacySecurityFragment) {
                nav.navigate(action)
            }
        }

        back.setOnClickListener {
            Navigation.findNavController(requireView()).popBackStack()
        }
    }

    override fun setObservers() {
        viewModel.userData.observe(viewLifecycleOwner) { deletelogoutResponse ->

//            selectedTabID = R.id.home
            AppPreferencesDelegates.get().token = null.toString()
            AppPreferencesDelegates.get().activeProject = 0
            HomeFragment.selectedProjectID = null
            requireContext().startActivity(Intent(requireContext(), AuthActivity::class.java))

        }
    }

    override fun OnDeleteClick() {
        DeviceUtils.showProgress()
        viewModel.deleteProfile()

    }

    override fun OnLogoutClick() {

    }

}