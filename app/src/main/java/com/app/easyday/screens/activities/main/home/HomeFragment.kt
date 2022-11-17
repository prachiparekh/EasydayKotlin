package com.app.easyday.screens.activities.main.home


import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.observe
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.app.easyday.R
import com.app.easyday.app.sources.local.interfaces.ProjectInterface
import com.app.easyday.app.sources.local.interfaces.TaskFilterApplyInterface
import com.app.easyday.app.sources.local.interfaces.TaskInterfaceClick
import com.app.easyday.app.sources.local.prefrences.AppPreferencesDelegates
import com.app.easyday.app.sources.remote.model.ProjectRespModel
import com.app.easyday.app.sources.remote.model.TaskResponse
import com.app.easyday.screens.activities.main.dashboard.DashboardFragmentDirections
import com.app.easyday.screens.activities.main.home.task_detail.TaskAdapter
import com.app.easyday.screens.base.BaseFragment
import com.app.easyday.screens.dialogs.FilterBottomSheetDialog
import com.app.easyday.screens.dialogs.ProjectListDialog
import com.app.easyday.utils.DeviceUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_home.*
import me.toptas.fancyshowcase.FancyShowCaseQueue
import me.toptas.fancyshowcase.FancyShowCaseView
import me.toptas.fancyshowcase.listener.OnViewInflateListener


@AndroidEntryPoint
class HomeFragment : BaseFragment<HomeViewModel>(),
    ProjectInterface, TaskFilterApplyInterface, TaskInterfaceClick {

    companion object {
        const val TAG = "HomeFragment"
        var selectedProjectID: Int? = null
        var selectedColor: String? = null
        var createProjectTitle: ProjectRespModel? = null
    }

    private lateinit var queue: FancyShowCaseQueue
    private lateinit var fancyView2: FancyShowCaseView
    private lateinit var fancyView1: FancyShowCaseView
    private var filterDialog: FilterBottomSheetDialog? = null
    private var projectList = arrayListOf<ProjectRespModel>()
    var selectedProjectPosition: Int? = null

   override fun getContentView() = R.layout.fragment_home

    override fun initUi() {

        DeviceUtils.initProgress(requireContext())

        if (!AppPreferencesDelegates.get().showcaseSeen) {

            fancyView2 = FancyShowCaseView.Builder(requireActivity())
                .customView(R.layout.showcaseview2, object : OnViewInflateListener {
                    override fun onViewInflated(view: View) {
                        setAnimatedContent(view, fancyView2)
                    }
                })
                .closeOnTouch(false)
                .build()

            fancyView1 = FancyShowCaseView.Builder(requireActivity())
                .customView(R.layout.showcaseview1, object : OnViewInflateListener {
                    override fun onViewInflated(view: View) {
                        setAnimatedContent(view, fancyView1)
                    }
                })
                .closeOnTouch(false)
                .build()

            queue = FancyShowCaseQueue().apply {
                add(fancyView2)
                add(fancyView1)
                show()
            }
        }

        filterDialog = FilterBottomSheetDialog(this)
        filterTV.setOnClickListener {
            if (filterDialog?.isAdded == false)
                filterDialog?.show(childFragmentManager, "filter")
        }

        cta.setOnClickListener {
            val action = DashboardFragmentDirections.dashboardToCamera()
            val nav: NavController = Navigation.findNavController(requireView())
            nav.navigate(action)
        }

        search.setOnClickListener {
            val action = DashboardFragmentDirections.dashboardToSearch()
            val nav: NavController = Navigation.findNavController(requireView())
            if (nav.currentDestination != null && nav.currentDestination?.id == R.id.dashboardFragment) {
                nav.navigate(action)
            }
        }

        notification.setOnClickListener {
            val action = DashboardFragmentDirections.dashboardToMoreNotifications()
            val nav: NavController = Navigation.findNavController(requireView())
            if (nav.currentDestination != null && nav.currentDestination?.id == R.id.dashboardFragment) {
                nav.navigate(action)
            }
        }

        profile.setOnClickListener {
            val action = DashboardFragmentDirections.dashboardToMoreViewProfile()
            val nav: NavController = Navigation.findNavController(requireView())
            if (nav.currentDestination != null && nav.currentDestination?.id == R.id.dashboardFragment) {
                nav.navigate(action)
            }
        }

        createProjectTitle = arguments?.getParcelable("projectName") as ProjectRespModel?
    }


    override fun setObservers() {

        viewModel.userProfileData.observe(viewLifecycleOwner) { userData ->

            val separated: List<String>? = userData?.profileImage?.split("?")
            val imageUrl = separated?.get(0).toString()

            if (userData?.profileImage != null) {
                val options = RequestOptions()
                profile.clipToOutline = true
                Glide.with(requireContext())
                    .load(imageUrl)
                    .error(requireContext().resources.getDrawable(R.drawable.ic_profile_circle))
                    .apply(
                        options.fitCenter()
                            .skipMemoryCache(true)
                            .priority(Priority.HIGH)
                            .format(DecodeFormat.PREFER_ARGB_8888)
                    )
                    .into(profile)


            }

        }

        viewModel.projectList.observe(viewLifecycleOwner) { projectList ->

            if (viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED) {

                if (!projectList.isNullOrEmpty()) {

                    this.projectList = projectList
                    TextViewCompat.setCompoundDrawableTintList(
                        activeProject,
                        ColorStateList.valueOf(
                            Color.parseColor(projectList[0].assignColor)
                        )
                    )

                    if (AppPreferencesDelegates.get().activeProject == 0) {
                        selectedProjectID = projectList[0].id
                        selectedColor = projectList[0].assignColor
                    } else {
                        selectedProjectID = AppPreferencesDelegates.get().activeProject
                    }

                    if (selectedProjectID == null) {
                        selectedProjectID = projectList[0].id
                        selectedColor = projectList[0].assignColor

                    }
                    val mProject = projectList.find { it.id == selectedProjectID }
                    selectedColor = mProject?.assignColor
                    activeProject.text = mProject?.projectName

                    for (i in projectList.indices) {
                        if (projectList[i].id == selectedProjectID) {
                            selectedProjectPosition = i
                        }

                    }
                    TextViewCompat.setCompoundDrawableTintList(
                        activeProject,
                        ColorStateList.valueOf(
                            Color.parseColor(mProject?.assignColor)
                        )
                    )

                    selectedProjectID?.let { viewModel.getAllTask(it) }
                }

                activeProject.setOnClickListener {
                    DeviceUtils.showProgress()
                    val fragment = ProjectListDialog(this, projectList, selectedProjectPosition)
                    childFragmentManager.let {
                        fragment.show(it, "projects")
                        DeviceUtils.dismissProgress()
                    }
                }
            }
        }

        viewModel.taskList.observe(viewLifecycleOwner) {

            //            if (viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED) {
            if (it.isNullOrEmpty()) {
                noTaskCL.isVisible = true
                taskRV.isVisible = false
            } else {
                noTaskCL.isVisible = false
                taskRV.isVisible = true
                taskRV.adapter = TaskAdapter(requireContext(), it, this)
            }
            //            }
            DeviceUtils.dismissProgress()
        }
    }

    private fun setAnimatedContent(view: View, fancyShowCaseView: FancyShowCaseView) {
        Handler().postDelayed({

            val mainAnimation =
                AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_from_the_left)
            mainAnimation.fillAfter = true

            val subAnimation =
                AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_from_the_left)
            subAnimation.fillAfter = true

            if (fancyShowCaseView == fancyView2) {
                val ctaNext = view.findViewById<View>(R.id.ctaNext) as TextView
                val text2 = view.findViewById<View>(R.id.text2) as TextView
                val skip = view.findViewById<View>(R.id.skip) as TextView
                ctaNext.setOnClickListener { fancyShowCaseView.hide() }
                skip.setOnClickListener {
                    AppPreferencesDelegates.get().showcaseSeen = true
                    queue.cancel(true)
                }
                text2.startAnimation(mainAnimation)
            } else {
                val done = view.findViewById<View>(R.id.done) as TextView
                val text1 = view.findViewById<View>(R.id.text1) as TextView
                done.setOnClickListener {
                    AppPreferencesDelegates.get().showcaseSeen = true
                    queue.cancel(true)
                }
                Handler().postDelayed({ text1.startAnimation(subAnimation) }, 80)
            }
        }, 200)
    }

    override fun onClickProject(projectPosition: Int) {
        if (projectPosition == -1) {
            //            Create New Project
            val action = DashboardFragmentDirections.dashboardToAddProject()
            val nav: NavController = Navigation.findNavController(requireView())
            if (nav.currentDestination != null && nav.currentDestination?.id == R.id.dashboardFragment) {
                nav.navigate(action)
            }
        } else {
            //            Switch with ProjectID

            selectedProjectPosition = projectPosition
            TextViewCompat.setCompoundDrawableTintList(
                activeProject,
                ColorStateList.valueOf(
                    Color.parseColor(projectList[projectPosition].assignColor)
                )
            )
            selectedProjectID = projectList[projectPosition].id
            activeProject.text = projectList[projectPosition].projectName
            selectedColor = projectList[projectPosition].assignColor
            selectedProjectID?.let { viewModel.getAllTask(it) }
            AppPreferencesDelegates.get().activeProject = projectList[projectPosition].id ?: 0
        }


    }

    override fun setFilter() {
        selectedProjectID?.let { viewModel.getFilterTask(it) }
        filterDialog?.dismiss()
    }

    override fun onClose() {
        filterDialog?.dismiss()
    }

    override fun onTaskClick(taskModel: TaskResponse) {
        val action = DashboardFragmentDirections.dashboardToTaskDetails()
        action.taskModel = taskModel
        val nav: NavController = Navigation.findNavController(requireView())
        if (nav.currentDestination != null && nav.currentDestination?.id == R.id.dashboardFragment) {
            nav.navigate(action)
        }
    }

    override fun onDiscussionClick(taskId: Int) {
        val action = DashboardFragmentDirections.dashboardToDiscussion()
        action.taskId = taskId
        Navigation.findNavController(requireView()).navigate(action)
    }

    override fun onSearchResult(count: Int) {

    }


}
