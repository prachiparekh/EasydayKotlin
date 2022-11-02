package com.app.easyday.screens.activities.main.home.task_detail

import android.annotation.SuppressLint
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import com.app.easyday.R
import com.app.easyday.app.sources.local.interfaces.FilterTypeInterface
import com.app.easyday.app.sources.local.model.ContactModel
import com.app.easyday.app.sources.remote.model.ProjectParticipantsModel
import com.app.easyday.app.sources.remote.model.TaskResponse
import com.app.easyday.screens.activities.main.home.task_detail.Discussion.TaskAssignedAdapter
import com.app.easyday.screens.base.BaseFragment
import com.app.easyday.screens.dialogs.AsigneeSelectionBottomSheetDialog.Companion.assignedUserList
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.android.synthetic.main.fragment_task_details.*

class TaskDetailsFragment : BaseFragment<TaskDetailsViewModel>(), FilterTypeInterface {

    var taskModel: TaskResponse? = null

    private var selectedPriority: Int? = null
    var projectparticipantList: ArrayList<ProjectParticipantsModel>? = null
    var adapter: TaskAssignedAdapter? = null
    var userAssignList: ArrayList<ContactModel>? = null

    override fun getContentView() = R.layout.fragment_task_details


    @SuppressLint("SetTextI18n")
    override fun initUi() {
        requireActivity().window?.statusBarColor = resources.getColor(R.color.navy_blue)

        taskModel = arguments?.getParcelable("taskModel") as TaskResponse?

        val title = taskModel?.title
        val description = taskModel?.description

        taskTitle.text = title
        taskDescription.text = description

//        taskDescription.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//            }
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//            }
//
//            override fun afterTextChanged(p0: Editable?) {
//                if (p0 != null) {
//                    taskLength.text = "${p0.length}/500"
//                }
//            }
//
//        })
        taskLength.text = "${taskDescription?.length()}/500"

        if (title != null && description != null) {

//            taskLength.text = description.length.toString() +"/" + 500

            taskLength.visibility = View.VISIBLE
            taskTitle.visibility = View.VISIBLE
            taskDescription.visibility = View.VISIBLE
            view2.visibility = View.VISIBLE
            view3.visibility = View.VISIBLE
            videoProgressCL.visibility = View.VISIBLE
            view4.visibility = View.VISIBLE
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
            0 -> show_priority.isVisible = false
            1 -> show_priority.setImageDrawable(requireContext().resources.getDrawable(R.drawable.ic_low))
            2 -> show_priority.setImageDrawable(requireContext().resources.getDrawable(R.drawable.ic_normal))
            3 -> show_priority.setImageDrawable(requireContext().resources.getDrawable(R.drawable.ic_urgent))
        }
//        selectedPriority = 0

        if (!projectparticipantList.isNullOrEmpty()) {
            projectparticipantList?.indices?.forEach { i ->
                userAssignList?.add(
                    ContactModel(
                        id = projectparticipantList?.get(i)?.id.toString(),
                        name = projectparticipantList?.get(i)?.user?.fullname,
                        role = projectparticipantList?.get(i)?.role,
                        phoneNumber = projectparticipantList?.get(i)?.user?.phoneNumber.toString(),
                        photoURI = projectparticipantList?.get(i)?.user?.profileImage
                    )

                )
            }
        }

        taskAssignRV.layoutManager = FlexboxLayoutManager(requireContext())
//        taskAssignRV.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
//        adapter = userAssignList?.let { TaskAssignedAdapter(requireContext(), it) }
        adapter = TaskAssignedAdapter(requireContext(), assignedUserList)
        taskAssignRV.adapter = adapter

    }

    override fun setObservers() {
    }

    override fun onFilterTypeClick(position: Int) {

    }

    override fun onFilterSingleChildClick(childList: ArrayList<String>, childPosition: Int) {
        selectedPriority = childPosition

    }

    override fun onFilterFlagClick(redFlag: Boolean) {

    }

    override fun onFilterDueDateClick(dateStr: String) {

    }


}