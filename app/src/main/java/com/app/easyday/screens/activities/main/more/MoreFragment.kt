package com.app.easyday.screens.activities.main.more

import android.content.Intent
import android.view.View
import android.view.View.OnClickListener
import androidx.lifecycle.observe
import androidx.navigation.Navigation
import com.app.easyday.R
import com.app.easyday.app.sources.local.interfaces.DeleteLogoutProfileInterface
import com.app.easyday.app.sources.local.prefrences.AppPreferencesDelegates
import com.app.easyday.databinding.FragmentMoreBinding
import com.app.easyday.screens.activities.auth.AuthActivity
import com.app.easyday.screens.activities.main.dashboard.DashboardFragmentDirections
import com.app.easyday.screens.activities.main.home.HomeFragment
import com.app.easyday.screens.activities.main.home.HomeViewModel.Companion.userModel
import com.app.easyday.screens.base.BaseFragment
import com.app.easyday.screens.dialogs.DeleteUserDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_more.*


@AndroidEntryPoint
class MoreFragment : BaseFragment<MoreViewModel>(), OnClickListener, DeleteLogoutProfileInterface {

    companion object {
        const val TAG = "MoreFragment"
    }

    var binding: FragmentMoreBinding? = null

    override fun getContentView() = R.layout.fragment_more

    override fun initUi() {
        userName?.text = userModel?.fullname
        profession?.text = userModel?.profession

        val info = requireContext().packageManager.getPackageInfo(
            requireContext().packageName, 0
        )
        versionName?.text =
            requireContext().resources.getString(R.string.version, info.versionName)

        val separated: List<String>? = userModel?.profileImage?.split("?")
        val imageUrl = separated?.get(0).toString()

        if (userModel?.profileImage != null) {
            val options = RequestOptions()
            avatar?.clipToOutline = true
            avatar?.let {
                Glide.with(requireContext())
                    .load(imageUrl)
                    .error(requireContext().resources.getDrawable(R.drawable.ic_user))
                    .apply(
                        options.centerCrop()
                            .skipMemoryCache(true)
                            .priority(Priority.HIGH)
                            .format(DecodeFormat.PREFER_ARGB_8888)
                    )
                    .into(it)
            }
        }

        profileLL?.setOnClickListener(this)
        privacyLL?.setOnClickListener(this)
        logoutLL?.setOnClickListener(this)
        deviceLL?.setOnClickListener(this)
        activity_logLL?.setOnClickListener(this)
        notepadLL?.setOnClickListener(this)
        feedbackLL?.setOnClickListener(this)
        notificationLL?.setOnClickListener(this)
    }

    override fun setObservers() {
        viewModel.userData.observe(viewLifecycleOwner) { userModel ->
//            selectedTabID = R.id.home
            AppPreferencesDelegates.get().token = null.toString()
            AppPreferencesDelegates.get().activeProject = 0
            HomeFragment.selectedProjectID = null
            requireContext().startActivity(Intent(requireContext(), AuthActivity::class.java))
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.profileLL -> {
                val direction = DashboardFragmentDirections.dashboardToMoreViewProfile()
                Navigation.findNavController(requireView()).navigate(direction)
            }
            R.id.privacyLL -> {
                val direction = DashboardFragmentDirections.dashboardToMorePrivacy()
                Navigation.findNavController(requireView()).navigate(direction)
            }
            R.id.logoutLL -> {
                val dialog = DeleteUserDialog("LOGOUT", this)
                if (!dialog.isAdded) {
                    dialog.show(childFragmentManager, "LOGOUT")
                }
            }
            R.id.deviceLL -> {
                val direction = DashboardFragmentDirections.dashboardToMoreDevices()
                Navigation.findNavController(requireView()).navigate(direction)

            }
            R.id.activity_logLL -> {

                val direction = DashboardFragmentDirections.dashboardToMoreActivityLog()
//                direction.userActivityModel = userActivityModel
                Navigation.findNavController(requireView()).navigate(direction)
            }

            R.id.notepadLL -> {
                val direction = DashboardFragmentDirections.dashboardToNotepad()
                Navigation.findNavController(requireView()).navigate(direction)
            }
            R.id.feedbackLL -> {
                val direction = DashboardFragmentDirections.dashboardToMoreFeedback()
                Navigation.findNavController(requireView()).navigate(direction)
            }
            R.id.notificationLL -> {
                val direction = DashboardFragmentDirections.dashboardToMoreNotifications()
                Navigation.findNavController(requireView()).navigate(direction)
            }

        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().window?.statusBarColor = resources.getColor(R.color.green)
    }

    override fun OnDeleteClick() {
    }

    override fun OnLogoutClick() {
        viewModel.logoutUser()

    }


}