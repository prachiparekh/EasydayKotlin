package com.app.easyday.screens.activities.main.home.task_detail

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.compose.ui.graphics.Color
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import com.app.easyday.R
import com.app.easyday.app.sources.local.model.ContactModel
import com.app.easyday.app.sources.remote.model.*
import com.app.easyday.screens.base.BaseFragment
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.back_to_setting_layout.view.*
import kotlinx.android.synthetic.main.fragment_task_details.*

class TaskDetailsFragment : BaseFragment<TaskDetailsViewModel>() {

    var taskModel: TaskResponse? = null

    override fun getContentView() = R.layout.fragment_task_details


    @SuppressLint("SetTextI18n", "ResourceAsColor")
    override fun initUi() {
        requireActivity().window?.statusBarColor = resources.getColor(R.color.navy_blue)

        taskModel = arguments?.getParcelable("taskModel") as TaskResponse?

        val title = taskModel?.title
        val description = taskModel?.description
        val participantsNameList = taskModel?.taskParticipants
        val flag = taskModel?.redFlag

        val tags = taskModel?.taskTags


        if (flag == true){
            mFlag.setImageDrawable(requireContext().resources.getDrawable(R.drawable.ic_flaged))
        }else{
            mFlag.setImageDrawable(requireContext().resources.getDrawable(R.drawable.ic_flag))
            mFlag.setColorFilter(R.color.l_gray)
        }

        if (tags != null && tags.size > 0){
            tagRV.adapter = context?.let { TaskTagAdapter(it, tags as java.util.ArrayList<TaskAttributeResponse>) }
        }else {
            tagLL.visibility = View.GONE
            view6.visibility = View.GONE
        }

        taskAssignRV.adapter = context?.let {
            TaskAssignedAdapter(
                it,
                participantsNameList as ArrayList<TaskParticipantsItem>
            )
        }

        var spaceStr=""
        taskModel?.taskSpaces?.indices?.forEach { i ->
            if (i == 0)
                spaceStr += taskModel?.taskSpaces?.get(i)?.projectAttribute?.attributeName
            else
                spaceStr += "," + taskModel?.taskSpaces?.get(i)?.projectAttribute?.attributeName
        }
        space_Tv.text = spaceStr

        var zoneStr=""
        taskModel?.taskZones?.indices?.forEach { i ->
            if (i == 0)
                zoneStr += taskModel?.taskZones?.get(i)?.projectAttribute?.attributeName
            else
                zoneStr += "," + taskModel?.taskZones?.get(i)?.projectAttribute?.attributeName
        }

        if (spaceStr.isEmpty() && zoneStr.isEmpty()){
            spaceLL.visibility = View.GONE
            view7.visibility = View.GONE

        }else if (spaceStr.isEmpty() && !zoneStr.isEmpty()){
            spc.visibility = View.GONE
            view_s_z.visibility = View.GONE
        }else if (zoneStr.isEmpty() && !spaceStr.isEmpty()){
            zn.visibility = View.GONE
            view_s_z.visibility = View.GONE
        }
        else{
            spaceLL.visibility = View.VISIBLE
            view7.visibility = View.VISIBLE
            view_s_z.visibility = View.VISIBLE
        }

        Log.e("spaceStr", zoneStr.toString())
        zones_Tv.text = zoneStr

        taskTitle.text = title
        taskDescription.text = description

        taskLength.text = "${taskDescription?.length()}/500"

        if (title != null) {

            if (description != null) {
                taskLength.visibility = View.GONE
                taskTitle.visibility = View.VISIBLE
                taskDescription.visibility = View.GONE
                view2.visibility = View.VISIBLE
                view3.visibility = View.GONE
                videoProgressCL.visibility = View.GONE
                view4.visibility = View.GONE
            }
        } else {
            taskLength.visibility = View.GONE
            taskTitle.visibility = View.GONE
            taskDescription.visibility = View.GONE
            view2.visibility = View.GONE
            view3.visibility = View.GONE
            videoProgressCL.visibility = View.GONE
            view4.visibility = View.GONE
        }

        back_task.setOnClickListener {
            Navigation.findNavController(requireView()).popBackStack()
        }

        when (taskModel?.priority) {
            0 -> {
                show_priority.isVisible = false
                priority_Tv.text = "None"
                priority_Tv.setTextColor(resources.getColor(R.color.black))
            }
            1 -> {
                show_priority.setImageDrawable(requireContext().resources.getDrawable(R.drawable.ic_low))
                priority_Tv.text = context?.resources?.getString(R.string.low_p)
                priority_Tv.setTextColor(resources.getColor(R.color.green))
            }

            2 -> {
                show_priority.setImageDrawable(requireContext().resources.getDrawable(R.drawable.ic_normal))
                priority_Tv.text = context?.resources?.getString(R.string.normal_p)
                priority_Tv.setTextColor(resources.getColor(R.color.yellow))
            }
            3 -> {
                show_priority.setImageDrawable(requireContext().resources.getDrawable(R.drawable.ic_urgent))
                priority_Tv.text = context?.resources?.getString(R.string.urgent_p)
                priority_Tv.setTextColor(resources.getColor(R.color.red))
            }
        }

        var mediaAdapter: TaskMediaAdapter? = null
        mediaAdapter = context?.let {
            TaskMediaAdapter(
                it,
                onItemClick = { isImg, uri ->
                    if (!isImg) {
                        val play = Intent(Intent.ACTION_VIEW, uri.toUri())
                        play.setDataAndType(
                            uri.toUri(),
                            "video/mp4"
                        )
                        context?.startActivity(play)
                    }
                },

                )
        }
        mediaPager.apply {
            adapter = mediaAdapter?.apply { submitList(taskModel?.taskMedia) }
        }

        TabLayoutMediator(dots_layout, mediaPager) { tab, position ->

        }.attach()


    }

    override fun setObservers() {
    }


}