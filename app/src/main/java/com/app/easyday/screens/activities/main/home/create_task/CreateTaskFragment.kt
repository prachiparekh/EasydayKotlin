package com.app.easyday.screens.activities.main.home.create_task

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.viewpager2.widget.ViewPager2
import com.app.easyday.R
import com.app.easyday.app.sources.local.interfaces.*
import com.app.easyday.app.sources.local.model.ContactModel
import com.app.easyday.app.sources.local.model.Media
import com.app.easyday.app.sources.remote.model.AddTaskRequestModel
import com.app.easyday.app.sources.remote.model.AttributeResponse
import com.app.easyday.app.sources.remote.model.ProjectParticipantsModel
import com.app.easyday.screens.activities.main.home.HomeFragment.Companion.selectedProjectID
import com.app.easyday.screens.base.BaseFragment
import com.app.easyday.screens.dialogs.AddSpaceZoneBottomSheetDialog
import com.app.easyday.screens.dialogs.AddTagBottomSheetDialog
import com.app.easyday.screens.dialogs.AsigneeSelectionBottomSheetDialog
import com.app.easyday.screens.dialogs.DueDateBottomSheetDialog
import com.app.easyday.utils.DeviceUtils
import com.app.easyday.utils.FileUtil
import com.passiondroid.imageeditorlib.ImageEditActivity
import com.passiondroid.imageeditorlib.ImageEditor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.delete_dialog_layout.*
import kotlinx.android.synthetic.main.fragment_create_task.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.text.SimpleDateFormat


@AndroidEntryPoint
class CreateTaskFragment : BaseFragment<CreateTaskViewModel>(), FilterTypeInterface,
    AttributeSelectionInterface, AddSpaceZoneAttributeInterface,
    FilterCloseInterface, AddAttributeInterface,
    AssigneeInterface {

    override fun getContentView() = R.layout.fragment_create_task
    private var filterTypeList = arrayListOf<String>()
    private var drawableList = arrayListOf<Drawable>()
    private var priorityList = arrayListOf<String>()
    var taskAdapter: TaskFilterAdapter? = null
    private var imgAdapter: BottomImageAdapter? = null
    var selectedUriList = ArrayList<Media>()
    var mediaAdapter: MediaAdapter? = null
    var isSelected:Boolean = false


    var tagBSFDialog: AddTagBottomSheetDialog? = null
    var spaceZoneBSFDialog: AddSpaceZoneBottomSheetDialog? = null
    var projectparticipantList: ArrayList<ProjectParticipantsModel>? = null
    var tagList = arrayListOf<AttributeResponse>()
    var zoneList = arrayListOf<AttributeResponse>()
    var spaceList = arrayListOf<AttributeResponse>()

    private var selectedTagList = arrayListOf<Int>()
    private var selectedZone :Int? = null
    private var selectedSpace :Int? = null
    private var selectedPriority: Int? = null
    var redFlag = 0 // False
    var priorityClick = 0 // False
    var selectedDate: String? = null

//    *****************

    override fun onResume() {
        super.onResume()
        requireActivity().window?.statusBarColor = resources.getColor(R.color.black)
        if (selectedUriList.isEmpty()) {
            textT.isVisible = false
            edit.isVisible = false
        } else {
            textT.isVisible = true
            edit.isVisible = true
        }
        delete.isVisible = selectedUriList.isNotEmpty()
    }

    override fun initUi() {

        selectedUriList = arguments?.getParcelableArrayList<Media>("uriList") as ArrayList<Media>


        DeviceUtils.initProgress(requireContext())
        selectedProjectID?.let { viewModel.getAttributes(it, 0) }
        selectedProjectID?.let { viewModel.getAttributes(it, 1) }
        selectedProjectID?.let { viewModel.getAttributes(it, 2) }
        selectedProjectID?.let { viewModel.getProjectParticipants(it) }

        tagBSFDialog =
            AddTagBottomSheetDialog(
                requireContext(),
                arrayListOf(),
                this,
                this,
                this
            )

        mediaAdapter = MediaAdapter(
            requireContext(),
            onItemClick = { isVideo, uri ->
                if (isVideo) {
                    val play =
                        Intent(Intent.ACTION_VIEW, uri).apply { setDataAndType(uri, "video/mp4") }
                    startActivity(play)
                }
            },
            onDeleteClick = { isEmpty, uri ->
                if (!isEmpty) {
                    val resolver = requireContext().applicationContext.contentResolver
                    resolver.delete(uri, null, null)
                }
            }
        )

        pagerPhotos.apply {
            adapter = mediaAdapter?.apply { submitList(selectedUriList) }
        }

        filterTypeList.add(requireContext().resources.getString(R.string.f_priority))
        filterTypeList.add(requireContext().resources.getString(R.string.f_tag))
        filterTypeList.add(requireContext().resources.getString(R.string.f_red_flag))
        filterTypeList.add(requireContext().resources.getString(R.string.f_space))
        filterTypeList.add(requireContext().resources.getString(R.string.f_zone))
        filterTypeList.add(requireContext().resources.getString(R.string.f_due_date))

        drawableList.add(requireContext().resources.getDrawable(R.drawable.ic_priority))
        drawableList.add(requireContext().resources.getDrawable(R.drawable.ic_tag))
        drawableList.add(requireContext().resources.getDrawable(R.drawable.ic_flag))
        drawableList.add(requireContext().resources.getDrawable(R.drawable.ic_buliding))
        drawableList.add(requireContext().resources.getDrawable(R.drawable.ic_zone))
        drawableList.add(requireContext().resources.getDrawable(R.drawable.ic_calender))

        priorityList.add(requireContext().resources.getString(R.string.none))
        priorityList.add(requireContext().resources.getString(R.string.low))
        priorityList.add(requireContext().resources.getString(R.string.normal))
        priorityList.add(requireContext().resources.getString(R.string.high))
        selectedPriority = 0

        close.setOnClickListener {
            Navigation.findNavController(requireView()).popBackStack()
        }

        taskAdapter =
            TaskFilterAdapter(requireContext(), filterTypeList, priorityList, isSelected, drawableList, this)
        filterRV.adapter = taskAdapter

        imgAdapter =
            BottomImageAdapter(
                requireContext(),
                selectedUriList,
                0,
                onItemClick = { position, item ->
                    pagerPhotos.currentItem = position
                })
        imgRV.adapter = imgAdapter

        pagerPhotos.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                imgAdapter?.setPosition(position)
            }
        })


        edit.setOnClickListener {
            if (selectedUriList[pagerPhotos.currentItem].uri != null) {
                val imagePath = selectedUriList[pagerPhotos.currentItem].uri?.let { it1 ->
                    FileUtil.getPath(
                        it1,
                        requireContext()
                    )
                }
                if (imagePath?.let { it1 -> File(it1).exists() } == true) {
                    val intent = Intent(context, ImageEditActivity::class.java)
                    intent.putExtra(ImageEditor.EXTRA_IS_PAINT_MODE, true)
                    intent.putExtra(ImageEditor.EXTRA_IS_STICKER_MODE, false)
                    intent.putExtra(ImageEditor.EXTRA_IS_TEXT_MODE, true)
                    intent.putExtra(ImageEditor.EXTRA_IS_CROP_MODE, false)
                    intent.putExtra(ImageEditor.EXTRA_HAS_FILTERS, false)
                    intent.putExtra(ImageEditor.EXTRA_IMAGE_PATH, imagePath)
                    startActivityForResult(intent, ImageEditor.RC_IMAGE_EDITOR)
                } else {
                    Toast.makeText(context, "Invalid image path", Toast.LENGTH_SHORT).show()
                }
            }

        }
        textT.setOnClickListener {
            if (selectedUriList[pagerPhotos.currentItem].uri != null) {
                val imagePath = selectedUriList[pagerPhotos.currentItem].uri?.let { it1 ->
                    FileUtil.getPath(
                        it1,
                        requireContext()
                    )
                }
                if (imagePath?.let { it1 -> File(it1).exists() } == true) {
                    val intent = Intent(context, ImageEditActivity::class.java)
                    intent.putExtra(ImageEditor.EXTRA_IS_PAINT_MODE, true)
                    intent.putExtra(ImageEditor.EXTRA_IS_STICKER_MODE, false)
                    intent.putExtra(ImageEditor.EXTRA_IS_TEXT_MODE, true)
                    intent.putExtra(ImageEditor.EXTRA_IS_CROP_MODE, false)
                    intent.putExtra(ImageEditor.EXTRA_HAS_FILTERS, false)
                    intent.putExtra(ImageEditor.EXTRA_IMAGE_PATH, imagePath)
                    startActivityForResult(intent, ImageEditor.RC_IMAGE_EDITOR)
                } else {
                    Toast.makeText(context, "Invalid image path", Toast.LENGTH_SHORT).show()
                }
            }

        }

        delete.setOnClickListener {

            if (selectedUriList.isNotEmpty()) {
                filterRV.visibility = View.GONE
                delete_activated.visibility = View.VISIBLE
                context?.resources?.getColor(R.color.black)
                    ?.let { it1 -> delete.setColorFilter(it1) }
                val dialog = Dialog(requireContext())
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setCancelable(false)
                dialog.setContentView(R.layout.delete_dialog_layout)
                dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))

                dialog.show()

                dialog.sure_delete_Tv.setOnClickListener {

                    selectedUriList.removeAt(pagerPhotos.currentItem)
                    mediaAdapter?.notifyDataSetChanged()
                    imgAdapter?.notifyDataSetChanged()
                    dialog.dismiss()

                    delete_activated.visibility = View.GONE
                    context?.resources?.getColor(R.color.white)
                        ?.let { it1 -> delete.setColorFilter(it1) }

                    filterRV.visibility = View.VISIBLE
                }
                dialog.cancel_dismis_Tv.setOnClickListener {
                    dialog.dismiss()

                    delete_activated.visibility = View.GONE
                    context?.resources?.getColor(R.color.white)
                        ?.let { it1 -> delete.setColorFilter(it1) }

                    filterRV.visibility = View.VISIBLE
                }
            }
        }

        imgAdd.setOnClickListener {
            Navigation.findNavController(requireView()).popBackStack()
        }

        send.setOnClickListener {
            if (taskNameET.text?.isNotEmpty() == true) {

                if (selectedDate != null) {
                    val contactList = ArrayList<ContactModel>()
                    if (!projectparticipantList.isNullOrEmpty()) {
                        projectparticipantList?.indices?.forEach { i ->
                            contactList.add(
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

                    val fragment =
                        AsigneeSelectionBottomSheetDialog(
                            requireContext(),
                            contactList,
                            this, this
                        )
                    childFragmentManager.let {
                        fragment.show(it, "Space")
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        requireContext().resources.getString(R.string.due_date_required),
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } else {
                Toast.makeText(
                    requireContext(),
                    requireContext().resources.getString(R.string.task_title_required),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    override fun setObservers() {
        viewModel.actionStream.observe(viewLifecycleOwner) {
            when (it) {
                is CreateTaskViewModel.ACTION.getAttributes -> {
                    when (it.type) {
                        0 -> {
                            tagList = it.attributeList as ArrayList<AttributeResponse>
                            tagBSFDialog?.clearTagList()
                            for (i in tagList.indices) {
                                tagBSFDialog?.setTagListData(tagList[i])
                            }
                        }
                        1 -> {
                            zoneList = it.attributeList as ArrayList<AttributeResponse>
                            spaceZoneBSFDialog?.clearAttrList()
                            for (i in zoneList.indices) {
                                spaceZoneBSFDialog?.setAttrListData(zoneList[i])
                            }
                        }
                        2 -> {
                            spaceList = it.attributeList as ArrayList<AttributeResponse>
                            spaceZoneBSFDialog?.clearAttrList()
                            for (i in spaceList.indices) {
                                spaceZoneBSFDialog?.setAttrListData(spaceList[i])
                            }
                        }
                    }
                }
                is CreateTaskViewModel.ACTION.showError -> {
                    DeviceUtils.dismissProgress()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
                is CreateTaskViewModel.ACTION.taskResponse -> {
                    DeviceUtils.dismissProgress()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()

                    val action = CreateTaskFragmentDirections.createTaskToDashboard()
                    val nav: NavController = Navigation.findNavController(requireView())
                    if (nav.currentDestination != null && nav.currentDestination?.id == R.id.createTaskFragment) {
                        nav.navigate(action)
                    }
                }
            }
        }

        viewModel.projectParticipantsData.observe(viewLifecycleOwner) {
            this.projectparticipantList = it
        }
    }

    override fun onFilterTypeClick(position: Int) {
        when (position) {
            1 -> {
                //Tag

                if (tagBSFDialog?.isAdded == false)
                    childFragmentManager.let {
                        tagBSFDialog?.show(it, "tags")
                    }
            }
            3 -> {
                //Space

                spaceZoneBSFDialog =
                    AddSpaceZoneBottomSheetDialog(
                        requireContext(),
                        spaceList,
                        selectedSpace,
                        this,
                        this,
                        2, this
                    )
                if (spaceZoneBSFDialog?.isAdded == false)
                    childFragmentManager.let {
                        spaceZoneBSFDialog?.show(it, "Space")
                    }
            }
            4 -> {
                //Zone

                spaceZoneBSFDialog =
                    AddSpaceZoneBottomSheetDialog(
                        requireContext(),
                        zoneList,
                        selectedZone,
                        this,
                        this,
                        1, this
                    )
                if (spaceZoneBSFDialog?.isAdded == false)
                    childFragmentManager.let {
                        spaceZoneBSFDialog?.show(it, "Zone")
                    }
            }
        }
    }

    override fun onFilterSingleChildClick(
        childList: ArrayList<String>,
        childLabel: TextView,
        childPosition: Int
    ) {
        selectedPriority = childPosition

    }


    override fun onFilterFlagClick(redFlag: Boolean) {
        if (redFlag)
            this.redFlag = 1
        else
            this.redFlag = 0

    }

    override fun onFilterDueDateClick(dateStr: String) {
        val fragment =
            DueDateBottomSheetDialog(requireContext(), dateStr, this)
        childFragmentManager.let {
            fragment.show(it, "due_date")
        }
    }
    override fun onClickSpaceZoneAttribute(selectedAttrList: Int, type: Int) {
        when (type) {

            1 -> {
                this.selectedZone = selectedAttrList

            }
            2 -> {
                this.selectedSpace = selectedAttrList

            }
        }
    }
    override fun onClickAttribute(selectedAttrList: ArrayList<Int>, type: Int) {
        when (type) {
            0 -> {
                this.selectedTagList = selectedAttrList

            }
//            1 -> {
//                this.selectedZoneList = selectedAttrList
//
//            }
//            2 -> {
//                this.selectedSpace = selectedAttrList
//
//            }
        }
    }


    override fun onCloseClick() {
        taskAdapter?.closeFilter()

    }

    override fun onDateClick(datestr: String) {

        selectedDate = datestr
        taskAdapter?.dueDateFilter(datestr)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        when (requestCode) {
            ImageEditor.RC_IMAGE_EDITOR ->
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val imagePath: String? = data.getStringExtra(ImageEditor.EXTRA_EDITED_PATH)
                    val mfile = imagePath?.let { File(it) }
                    selectedUriList[pagerPhotos.currentItem].uri = Uri.fromFile(mfile)
                    mediaAdapter?.notifyItemChanged(pagerPhotos.currentItem)
                    imgAdapter?.notifyItemChanged(pagerPhotos.currentItem + 1)
                }
        }
    }

    override fun addAttribute(attributeType: Int, attributeName: String) {
        selectedProjectID?.let {
            viewModel.addAttributes(
                projectId = it,
                type = attributeType,
                attributeName = attributeName
            )
        }
    }

    override fun onSkipAssignee() {
        createTask(null)
    }

    override fun onSelestAssignee(assigneelist: ArrayList<Int>) {
        createTask(assigneelist)
    }

    private fun createTask(assigneeList: ArrayList<Int>?) {
        val fileList = ArrayList<File>()

        for (i in selectedUriList.indices) {
            selectedUriList[i].uri?.let {
                FileUtil.getPath(it, requireContext())?.let { File(it) }
                    ?.let { fileList.add(it) }
            }
        }

        val attachmentBodyList: ArrayList<MultipartBody.Part> = ArrayList()
        for (i in 0 until fileList.size) {
            val requestList: RequestBody =
                fileList[i].asRequestBody("*/*".toMediaTypeOrNull())

            val attachmentBody =
                MultipartBody.Part.createFormData(
                    "task_media",
                    fileList[i].name,
                    requestList
                )

            attachmentBodyList.add(attachmentBody)
        }

        var duedate: String? = null
        if (selectedDate != null) {
            var spf = SimpleDateFormat("dd/MM/yy")
            val newDate = spf.parse(selectedDate)
            spf = SimpleDateFormat("yyyy-MM-dd")
            duedate = spf.format(newDate)
        }

        viewModel.addTask(
            AddTaskRequestModel(
                selectedProjectID,
                taskNameET.text.toString(),
                "",
                selectedPriority,
                redFlag,
                duedate,
                selectedTagList,
                selectedZone,
                selectedSpace,
                attachmentBodyList,
                assigneeList
            )
        )
    }




}