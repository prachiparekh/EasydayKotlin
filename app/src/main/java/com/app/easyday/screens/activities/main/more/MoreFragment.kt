package com.app.easyday.screens.activities.main.more

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.app.easyday.R
import com.app.easyday.app.sources.local.interfaces.DeleteLogoutProfileInterface
import com.app.easyday.app.sources.local.prefrences.AppPreferencesDelegates
import com.app.easyday.app.sources.remote.model.UserActivityResponse
import com.app.easyday.databinding.FragmentMoreBinding
import com.app.easyday.screens.activities.auth.AuthActivity
import com.app.easyday.screens.activities.main.dashboard.DashboardFragment.Companion.selectedTabID
import com.app.easyday.screens.activities.main.dashboard.DashboardFragmentDirections
import com.app.easyday.screens.activities.main.home.HomeFragment
import com.app.easyday.screens.activities.main.home.HomeViewModel.Companion.userModel
import com.app.easyday.screens.dialogs.DeleteUserDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MoreFragment : Fragment(), OnClickListener,DeleteLogoutProfileInterface {

    companion object {
        const val TAG = "MoreFragment"
    }

    val userActivityModel :UserActivityResponse? = null
    var binding: FragmentMoreBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_more, container, false)

        binding?.userName?.text = userModel?.fullname
        binding?.profession?.text = userModel?.profession

        val info = requireContext().packageManager.getPackageInfo(
            requireContext().packageName, 0
        )
        binding?.versionName?.text =
            requireContext().resources.getString(R.string.version, info.versionName)

        if (userModel?.profileImage != null) {
            val options = RequestOptions()
            binding?.avatar?.clipToOutline = true
            binding?.avatar?.let {
                Glide.with(requireContext())
                    .load(userModel?.profileImage)
                    .apply(
                        options.centerCrop()
                            .skipMemoryCache(true)
                            .priority(Priority.HIGH)
                            .format(DecodeFormat.PREFER_ARGB_8888)
                    )
                    .into(it)
            }
        }

        binding?.profileLL?.setOnClickListener(this)
        binding?.privacyLL?.setOnClickListener(this)
        binding?.logoutLL?.setOnClickListener(this)
        binding?.deviceLL?.setOnClickListener(this)
        binding?.activityLogLL?.setOnClickListener(this)
        binding?.notepadLL?.setOnClickListener(this)
        binding?.feedbackLL?.setOnClickListener(this)
        binding?.notificationLL?.setOnClickListener(this)
        return binding?.root
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
        HomeFragment.selectedProjectID = null
        selectedTabID = R.id.home
        AppPreferencesDelegates.get().token = null.toString()
        requireContext().startActivity(Intent(requireContext(), AuthActivity::class.java))
    }


}