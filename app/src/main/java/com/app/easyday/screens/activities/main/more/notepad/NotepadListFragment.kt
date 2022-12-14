package com.app.easyday.screens.activities.main.more.notepad

import android.app.Dialog
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.observe
import androidx.navigation.Navigation
import com.app.easyday.R
import com.app.easyday.app.sources.local.interfaces.NoteInterface
import com.app.easyday.app.sources.remote.model.NoteResponse
import com.app.easyday.screens.activities.main.home.HomeFragment
import com.app.easyday.screens.base.BaseFragment
import com.app.easyday.utils.DeviceUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_notepad_list.*

@AndroidEntryPoint
class NotepadListFragment : BaseFragment<NoteViewModel>(), NoteInterface {
    val adapter: NotepadListAdapter? = null
    var noteList: ArrayList<NoteResponse>? = null

    override fun getContentView() = R.layout.fragment_notepad_list

    override fun initUi() {
        requireActivity().window?.statusBarColor = resources.getColor(R.color.navy_blue)

        createNote.setOnClickListener {
            val direction = NotepadListFragmentDirections.notepadListToCreateNote()
            Navigation.findNavController(requireView()).navigate(direction)
        }
        back.setOnClickListener { Navigation.findNavController(requireView()).popBackStack() }

        HomeFragment.selectedProjectID?.let { viewModel.getNote(it) }
    }

    override fun setObservers() {
        viewModel.notepadData.observe(viewLifecycleOwner) { response ->

            if (response.isNullOrEmpty()) {
                emptyState.isVisible = true
                notelist.isVisible = false
            } else {
                noteList = response
                emptyState.isVisible = false
                notelist.isVisible = true
                notesRV.adapter = NotepadListAdapter(requireContext(), response, this)
            }
            DeviceUtils.dismissProgress()

        }

        viewModel.noteData.observe(viewLifecycleOwner) {

        }
    }


    override fun OnConvertClick() {

    }

    override fun OnDeleteNoteClick(position: Int) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.delete_dialog_layout)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.setGravity(Gravity.CENTER)

        val delete = dialog.findViewById<TextView>(R.id.sure_delete_Tv)
        val cancel = dialog.findViewById<TextView>(R.id.cancel_dismis_Tv)
        val confirmation = dialog.findViewById<TextView>(R.id.sure_Tv)

        confirmation.text = requireContext().resources.getString(R.string.delete_note)

        delete.setOnClickListener {

            noteList?.get(position)?.let { it1 -> it1.id?.let { it2 -> viewModel.deleteNote(it2) } }
            dialog.dismiss()
        }
        cancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun OnShareClick() {

    }

}