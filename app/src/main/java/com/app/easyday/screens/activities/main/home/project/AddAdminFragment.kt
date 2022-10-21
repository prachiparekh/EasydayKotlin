package com.app.easyday.screens.activities.main.home.project

import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.app.easyday.R
import com.app.easyday.app.sources.remote.model.AddProjectRequestModel
import com.app.easyday.app.sources.remote.model.AddProjectRequestModelToPass
import com.app.easyday.app.sources.remote.model.ContactModelToPass
import com.app.easyday.screens.activities.main.home.project.adapter.AdminAdapter
import com.app.easyday.screens.base.BaseFragment
import com.app.easyday.utils.DeviceUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_add_admin.*


@AndroidEntryPoint
class AddAdminFragment : BaseFragment<ProjectViewModel>() {


    var adapter: AdminAdapter? = null
    var createprojectModel: AddProjectRequestModel? = null

    override fun getContentView() = R.layout.fragment_add_admin

    override fun initUi() {
        DeviceUtils.initProgress(requireContext())

        createprojectModel =
            arguments?.getParcelable("createProjectModel") as AddProjectRequestModel?
        adapter = createprojectModel?.participants?.let {
            AdminAdapter(
                requireContext(),
                it
            )
        }
        adminRV?.adapter = adapter

        mSearch?.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isEmpty()) {
                    searchImageView?.visibility = View.VISIBLE
                    adminLL?.isVisible = true
                } else {
                    searchImageView?.visibility = View.INVISIBLE
                    adminLL?.isVisible = false
                }
                adapter?.filter?.filter(newText)
                return true
            }
        })

        cta?.setOnClickListener {

            val contactToPassList= ArrayList<ContactModelToPass>()

            val mainList=createprojectModel?.participants
            mainList?.indices?.forEach { i ->
                contactToPassList.add(ContactModelToPass(mainList[i].role,mainList[i].phoneNumber,mainList[i].countryCode))
            }

            val modelToPass=AddProjectRequestModelToPass(createprojectModel?.assignColor,
            createprojectModel?.description,createprojectModel?.projectName,contactToPassList)


            modelToPass.let { it1 -> viewModel.createProject(it1) }
        }

        toolBar.setNavigationOnClickListener {
            Navigation.findNavController(requireView()).popBackStack()
        }

    }


    override fun setObservers() {
        viewModel.actionStream.observe(viewLifecycleOwner) {
            when (it) {
                is ProjectViewModel.ACTION.ProjectResponseSuccess -> {
                    Toast.makeText(
                        requireContext(),
                        requireContext().resources.getString(R.string.project_create_success),
                        Toast.LENGTH_SHORT
                    ).show()

                    val action = AddAdminFragmentDirections.addAdminToDashboard()
                    val nav: NavController = Navigation.findNavController(requireView())
                    if (nav.currentDestination != null && nav.currentDestination?.id == R.id.addAdminFragment) {
                        nav.navigate(action)
                    }
                }

                is ProjectViewModel.ACTION.ProjectResponseError -> {
                    Toast.makeText(requireContext(), it.msg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}