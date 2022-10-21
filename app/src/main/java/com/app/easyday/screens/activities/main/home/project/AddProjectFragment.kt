package com.app.easyday.screens.activities.main.home.project

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.easyday.R
import com.app.easyday.app.sources.local.interfaces.ColorInterface
import com.app.easyday.app.sources.remote.model.AddProjectRequestModel
import com.app.easyday.databinding.FragmentAddProjectBinding
import com.app.easyday.screens.activities.main.home.project.adapter.ColorAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddProjectFragment : Fragment(), ColorInterface {

    var selectedColor = R.color.color1

    private val colorList = intArrayOf(
        R.color.color1,
        R.color.color2,
        R.color.color3,
        R.color.color4,
        R.color.color5,
        R.color.color6,
        R.color.color7,
        R.color.color8
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        val binding: FragmentAddProjectBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_add_project, container, false)
        binding.toolBar.setNavigationOnClickListener {
            Navigation.findNavController(requireView()).popBackStack()
        }

        binding.description.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                if (p0 != null) {
                    binding.etLength.text = "${p0.length}/110"
                }
            }

        })

        binding.colorRV.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        binding.colorRV.adapter = ColorAdapter(requireContext(), colorList, this)

        binding.cta.setOnClickListener {
            if (binding.projectName.text.isNullOrEmpty()) {
                Toast.makeText(
                    requireContext(),
                    requireContext().resources.getString(R.string.name_required),
                    Toast.LENGTH_SHORT
                )
                    .show()
                return@setOnClickListener
            }

            if (binding.description.text.isNullOrEmpty()) {
                Toast.makeText(
                    requireContext(),
                    requireContext().resources.getString(R.string.desc_required),
                    Toast.LENGTH_SHORT
                )
                    .show()
                return@setOnClickListener
            }

            val createProjectModel = AddProjectRequestModel(
                requireContext().resources.getString(selectedColor),
                binding.description.text.toString(),
                binding.projectName.text.toString(), arrayListOf()
            )

            val action = AddProjectFragmentDirections.addProjectToAddParticipant()
            action.createProjectModel = createProjectModel
            val nav: NavController = Navigation.findNavController(requireView())
            if (nav.currentDestination != null && nav.currentDestination?.id == R.id.addProjectFragment) {
                nav.navigate(action)
            }
        }

        return binding.root
    }

    override fun onColorClick(color: Int) {
        selectedColor = color
    }


}