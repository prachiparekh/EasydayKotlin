package com.app.easyday.screens.dialogs

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.annotation.NonNull
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.app.easyday.R
import com.app.easyday.app.sources.remote.model.TaskAttributeResponse
import com.app.easyday.app.sources.remote.model.TaskParticipantsItem
import com.app.easyday.app.sources.remote.model.TaskResponse
import com.app.easyday.databinding.DetailsLayoutBinding
import com.app.easyday.screens.activities.main.home.create_task.TaskMediaAdapter
import com.app.easyday.screens.activities.main.home.task_detail.TaskAssignedAdapter
import com.app.easyday.screens.activities.main.home.task_detail.TaskTagAdapter
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayoutMediator

class DisplayDetailsDialog(
    val mContext: Context,
    val activity: Activity,
    var taskModel: TaskResponse
) :
BottomSheetDialogFragment() {

    var binding: DetailsLayoutBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.details_layout, container, false)
        isCancelable = false

        activity.window?.addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
//        activity.window?.statusBarColor = resources.getColor(R.color.navy_blue)

        val title = taskModel.title
        val description = taskModel.description
        val participantsNameList = taskModel.taskParticipants
        val flag = taskModel.redFlag

        val tags = taskModel.taskTags


        if (flag == true){
            binding?.mFlag?.setImageDrawable(requireContext().resources.getDrawable(R.drawable.ic_flaged))
        }else{
            binding?.mFlag?.setImageDrawable(requireContext().resources.getDrawable(R.drawable.ic_flag))
            binding?.mFlag?.setColorFilter(R.color.l_gray)
        }

        if (tags != null && tags.size > 0){
            binding?.tagRV?.adapter = context?.let { TaskTagAdapter(it, tags as java.util.ArrayList<TaskAttributeResponse>) }
        }else {
            binding?.tagLL?.visibility = View.GONE
            binding?.view6?.visibility = View.GONE
        }

        binding?.taskAssignRV?.adapter = context?.let {
            TaskAssignedAdapter(
                it,
                participantsNameList as ArrayList<TaskParticipantsItem>
            )
        }

        var spaceStr = ""
        taskModel.taskSpaces?.indices?.forEach { i ->
            spaceStr += if (i == 0)
                taskModel.taskSpaces?.get(i)?.projectAttribute?.attributeName
            else
                "," + taskModel.taskSpaces?.get(i)?.projectAttribute?.attributeName
        }
        binding?.spaceTv?.text = spaceStr

        var zoneStr = ""
        taskModel.taskZones?.indices?.forEach { i ->
            if (i == 0)
                zoneStr += taskModel.taskZones?.get(i)?.projectAttribute?.attributeName
            else
                zoneStr += "," + taskModel.taskZones?.get(i)?.projectAttribute?.attributeName
        }

        binding?.zonesTv?.text = zoneStr

        if (spaceStr.isEmpty() && zoneStr.isEmpty()) {
            binding?.spaceLL?.visibility = View.GONE
            binding?.view7?.visibility = View.GONE

        } else if (spaceStr.isEmpty() && zoneStr.isNotEmpty()) {
            binding?.spc?.visibility = View.GONE
            binding?.viewSZ?.visibility = View.GONE
        } else if (zoneStr.isEmpty() && !spaceStr.isEmpty()) {
            binding?.zn?.visibility = View.GONE
            binding?.viewSZ?.visibility = View.GONE
        } else {
            binding?.spaceLL?.visibility = View.VISIBLE
            binding?.view7?.visibility = View.VISIBLE
            binding?.viewSZ?.visibility = View.VISIBLE
        }


        binding?.taskTitle?.text = title
        binding?.taskDescription?.text = description

        binding?.taskLength?.text = "${binding?.taskDescription?.length()}/500"

        if (title != null) {

            if (description != null) {
                binding?.taskLength?.visibility = View.GONE
                binding?.taskTitle?.visibility = View.VISIBLE
                binding?.taskDescription?.visibility = View.GONE
                binding?.view2?.visibility = View.VISIBLE
                binding?.view3?.visibility = View.GONE
                binding?.videoProgressCL?.visibility = View.GONE
                binding?.view4?.visibility = View.GONE
            }
        } else {
            binding?.taskLength?.visibility = View.GONE
            binding?.taskTitle?.visibility = View.GONE
            binding?.taskDescription?.visibility = View.GONE
            binding?.view2?.visibility = View.GONE
            binding?.view3?.visibility = View.GONE
            binding?.videoProgressCL?.visibility = View.GONE
            binding?.view4?.visibility = View.GONE
        }

        when (taskModel.priority) {
            0 -> {
                 binding?.showPriority?.isVisible = false
                 binding?.priorityTv?.text = "None"
                 binding?.priorityTv?.setTextColor(resources.getColor(R.color.black))
            }
            1 -> {
                 binding?.showPriority?.setImageDrawable(requireContext().resources.getDrawable(R.drawable.ic_low))
                 binding?.priorityTv?.text = context?.resources?.getString(R.string.low_p)
                 binding?.priorityTv?.setTextColor(resources.getColor(R.color.green))
            }

            2 -> {
                 binding?.showPriority?.setImageDrawable(requireContext().resources.getDrawable(R.drawable.ic_normal))
                 binding?.priorityTv?.text = context?.resources?.getString(R.string.normal_p)
                 binding?.priorityTv?.setTextColor(resources.getColor(R.color.yellow))
            }
            3 -> {
                binding?.showPriority?.setImageDrawable(requireContext().resources.getDrawable(R.drawable.ic_urgent))
                binding?.priorityTv?.text = context?.resources?.getString(R.string.urgent_p)
                binding?.priorityTv?.setTextColor(resources.getColor(R.color.red))
            }
        }

        var mediaAdapter: TaskMediaAdapter? = null
        mediaAdapter = context?.let {
            TaskMediaAdapter(
                it, requireActivity(), manager = childFragmentManager,
                onItemClick = { isImg, uri ->
                    if (!isImg) { }
                })
        }


        Log.e("model", taskModel.toString())
        binding?.mediaPager?.apply {
            adapter = mediaAdapter?.apply { submitList(taskModel?.taskMedia) }
        }

        binding?.mediaPager?.let {
            binding?.dotsLayout?.let { it1 ->
                TabLayoutMediator(it1, it) { tab, position ->

                }.attach()
            }
        }

        binding?.backTask?.setOnClickListener {
            dismiss()
        }

        binding?.root?.isFocusableInTouchMode = true
        binding?.root?.requestFocus()
        binding?.root?.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {

                dismiss()
                true
            } else false
        }
        return binding?.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.setOnShowListener {

            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { it1 ->
                val behaviour = BottomSheetBehavior.from(it1)
                setupFullHeight(it1)
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED

                behaviour.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(@NonNull bottomSheet: View, newState: Int) {
                        if (newState == BottomSheetBehavior.STATE_COLLAPSED || newState == BottomSheetBehavior.STATE_DRAGGING) {
                            behaviour.state = BottomSheetBehavior.STATE_EXPANDED
                        }
                    }

                    override fun onSlide(@NonNull bottomSheet: View, slideOffset: Float) {
                        // React to dragging events
                    }
                })
            }

        }

        return dialog
    }

    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }
}