package com.app.easyday.screens.activities.main.home.task_detail

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.app.easyday.R
import com.app.easyday.app.sources.remote.model.AddProjectRequestModel
import com.app.easyday.app.sources.remote.model.ProjectRespModel
import com.app.easyday.screens.activities.main.dashboard.DashboardFragmentDirections
import com.app.easyday.screens.activities.main.home.HomeViewModel
import com.app.easyday.screens.activities.main.more.profile.ViewProfileViewModel
import com.app.easyday.screens.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_task_details.*
import kotlinx.android.synthetic.main.fragment_view_profile.*

class TaskDetailsFragment : BaseFragment<TaskDetailsViewModel>() {

    var projectTitle: ProjectRespModel? = null

//    private val args: TaskDetailsFragmentArgs by navArgs()


    override fun getContentView() = R.layout.fragment_task_details

    override fun initUi() {
        requireActivity().window?.statusBarColor = resources.getColor(R.color.navy_blue)

        projectTitle = arguments?.getParcelable("projectName") as ProjectRespModel?

        val title = projectTitle?.projectName
        val description = projectTitle?.description
//        val title = args.projectName

        taskTitle.text = title
        userName_2.text = title
        taskDescription.text = description

        if(title != null && description != null){

            taskLength.text = description.length.toString() +"/" +500

            taskLength.visibility = View.VISIBLE
            taskTitle.visibility = View.VISIBLE
            taskDescription.visibility = View.VISIBLE
            view2.visibility = View.VISIBLE
            view3.visibility = View.VISIBLE
            videoProgressCL.visibility = View.VISIBLE
            view4.visibility = View.VISIBLE
        }else{
            taskLength.visibility = View.GONE
            taskTitle.visibility = View.GONE
            taskDescription.visibility = View.GONE
            view2.visibility = View.GONE
            view3.visibility = View.GONE
            videoProgressCL.visibility = View.GONE
            view4.visibility = View.GONE
        }


        discussion_1.setOnClickListener {
            Toast.makeText(context, "husssssssss", Toast.LENGTH_SHORT).show()
//            val direction = DashboardFragmentDirections.dashboardToDiscussion()
//            Navigation.findNavController(requireView()).navigate(direction)
//            val nav: NavController = Navigation.findNavController(requireView())
//            nav.navigate(direction)

        }

    }

    override fun setObservers() {

    }


}