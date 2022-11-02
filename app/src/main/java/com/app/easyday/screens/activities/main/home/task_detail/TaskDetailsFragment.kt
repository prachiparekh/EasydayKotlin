package com.app.easyday.screens.activities.main.home.task_detail

import android.annotation.SuppressLint
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.easyday.R
import com.app.easyday.app.sources.local.interfaces.FilterTypeInterface
import com.app.easyday.app.sources.local.model.ContactModel
import com.app.easyday.app.sources.remote.model.*
import com.app.easyday.screens.activities.main.home.task_detail.Discussion.TaskAssignedAdapter
import com.app.easyday.screens.activities.main.home.task_detail.TaskAdapter.Companion.useritem
import com.app.easyday.screens.base.BaseFragment
import com.app.easyday.screens.dialogs.AddSpaceZoneBottomSheetDialog
import com.app.easyday.screens.dialogs.AddTagBottomSheetDialog
import com.app.easyday.screens.dialogs.AsigneeSelectionBottomSheetDialog.Companion.assignedUserList
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.android.synthetic.main.fragment_task_details.*

class TaskDetailsFragment : BaseFragment<TaskDetailsViewModel>(), FilterTypeInterface {

    var projectTitle: TaskResponse? = null

    //    private val args: TaskDetailsFragmentArgs by navArgs()
    private var selectedPriority: Int? = null
    private lateinit var taskList: ArrayList<TaskResponse>

    var tagBSFDialog: AddTagBottomSheetDialog? = null
    var spaceZoneBSFDialog: AddSpaceZoneBottomSheetDialog? = null
    var projectparticipantList: ArrayList<ProjectParticipantsModel>? = null
    var tagList = arrayListOf<AttributeResponse>()
    var zoneList = arrayListOf<AttributeResponse>()
    var spaceList = arrayListOf<AttributeResponse>()

    var adapter: TaskAssignedAdapter? = null
    var userAssignList: ArrayList<ContactModel>? = null

    override fun getContentView() = R.layout.fragment_task_details


    @SuppressLint("SetTextI18n")
    override fun initUi() {
        requireActivity().window?.statusBarColor = resources.getColor(R.color.navy_blue)

        projectTitle = arguments?.getParcelable("projectName") as ProjectRespModel?

        val title = projectTitle?.projectName
        val description = projectTitle?.description
        val users = projectTitle?.projectInvited
//        val title = args.projectName

        taskTitle.text = title
//        userName_2.text = title
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

        when (useritem?.priority) {
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
//        viewModel.actionStream.observe(viewLifecycleOwner) {
//            when (it) {
//                is TaskDetailsViewModel.ACTIONN.getAttributes -> {
//                    when (it.type) {
//                        0 -> {
//                            tagList = it.attributeList as ArrayList<AttributeResponse>
//                            tagBSFDialog?.clearTagList()
//                            for (i in tagList.indices) {
//                                tagBSFDialog?.setTagListData(tagList[i])
//                            }
//                        }
//                        1 -> {
//                            zoneList = it.attributeList as ArrayList<AttributeResponse>
//                            spaceZoneBSFDialog?.clearAttrList()
//                            for (i in zoneList.indices) {
//                                spaceZoneBSFDialog?.setAttrListData(zoneList[i])
//                            }
//                        }
//                        2 -> {
//                            spaceList = it.attributeList as ArrayList<AttributeResponse>
//                            spaceZoneBSFDialog?.clearAttrList()
//                            for (i in spaceList.indices) {
//                                spaceZoneBSFDialog?.setAttrListData(spaceList[i])
//                            }
//                        }
//                    }
//                }
//                is TaskDetailsViewModel.ACTIONN.showError -> {
//                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
//                }
//                is TaskDetailsViewModel.ACTIONN.taskResponse -> {
//                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
//
//                    val action = CreateTaskFragmentDirections.createTaskToDashboard()
//                    val nav: NavController = Navigation.findNavController(requireView())
//                    if (nav.currentDestination != null && nav.currentDestination?.id == R.id.createTaskFragment) {
//                        nav.navigate(action)
//                    }
//                }
//            }
//        }
//
//        viewModel.projectParticipantsData.observe(viewLifecycleOwner) {
//            this.projectparticipantList = it
//        }
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