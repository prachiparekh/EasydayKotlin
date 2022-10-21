package com.app.easyday.screens.activities.main.home.project

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.telephony.PhoneNumberUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.app.easyday.R
import com.app.easyday.app.sources.local.model.ContactModel
import com.app.easyday.app.sources.remote.model.AddProjectRequestModel
import com.app.easyday.databinding.FragmentAddParticipantsBinding
import com.app.easyday.screens.activities.main.home.project.adapter.ParticipentAdapter
import com.app.easyday.utils.DeviceUtils
import com.app.easyday.utils.IntentUtil.Companion.contactPermission
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.CompositePermissionListener
import com.karumi.dexter.listener.single.PermissionListener
import com.karumi.dexter.listener.single.SnackbarOnDeniedPermissionListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_add_participants.*
import java.util.*


@AndroidEntryPoint
class AddParticipantsFragment : Fragment() {


    companion object {
        var createProjectModel: AddProjectRequestModel? = null
        var binding: FragmentAddParticipantsBinding? = null
        var adapter: ParticipentAdapter? = null

        fun getPhotoUri(context: Context, id: String): Uri? {
            try {
                val cur: Cursor? = context.contentResolver.query(
                    ContactsContract.Data.CONTENT_URI,
                    null,
                    ContactsContract.Data.CONTACT_ID + "=" + id + " AND "
                            + ContactsContract.Data.MIMETYPE + "='"
                            + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'", null,
                    null
                )
                if (cur != null) {
                    if (!cur.moveToFirst()) {
                        return null // no photo
                    }
                } else {
                    return null // error in cursor process
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
            val person =
                ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id.toLong())
            return Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_add_participants, container, false)
        createProjectModel =
            arguments?.getParcelable("createProjectModel") as AddProjectRequestModel?

        binding?.toolBar?.setNavigationOnClickListener {
            Navigation.findNavController(requireView()).popBackStack()
        }

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DeviceUtils.initProgress(requireContext())

        if (contactPermission(requireActivity())) {

            AsyncTaskExample(requireContext()).execute()
        } else
            onPermission()
    }


    private fun onPermission() {

        val contactsPermissionListener = CompositePermissionListener(
            object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    AsyncTaskExample(requireContext()).execute()
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {

                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    if (p1 != null) {
//                        showPermissionRationale(p1)
                    }
                }

            },
            SnackbarOnDeniedPermissionListener.Builder.with(
                requireView(),
                R.string.contacts_permission_denied_feedback
            )
                .withOpenSettingsButton(R.string.permission_rationale_settings_button_text)
                .withCallback(object : Snackbar.Callback() {
                    override fun onShown(snackbar: Snackbar) {
                        super.onShown(snackbar)
                    }

                    override fun onDismissed(snackbar: Snackbar, event: Int) {
                        super.onDismissed(snackbar, event)
                    }
                })
                .build()
        )

        Dexter.withContext(requireContext())
            .withPermission(Manifest.permission.READ_CONTACTS)
            .withListener(contactsPermissionListener)
            .check()
    }


    class AsyncTaskExample(val context: Context) :
        AsyncTask<Void, Void, Void>() {

        var contactList = ArrayList<ContactModel>()

        override fun onPreExecute() {
            super.onPreExecute()
            DeviceUtils.showProgress()
            contactList.clear()
        }

        @SuppressLint("Range")
        override fun doInBackground(vararg p0: Void): Void? {
            val cr: ContentResolver = context.contentResolver
            val cur = cr.query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.Contacts.DISPLAY_NAME + " ASC "
            )
            var lastnumber = ""

            val number = ArrayList<String>()
            while (cur?.moveToNext() == true) {

                val id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID))
                val name =
                    cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                if (cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                        .toInt() > 0
                ) {
                    val pCur = cr.query(
                        Phone.CONTENT_URI,
                        null,
                        Phone.CONTACT_ID + " = ?",
                        arrayOf(id),
                        null
                    )
                    while (pCur?.moveToNext() == true) {
                        lastnumber = pCur.getString(pCur.getColumnIndex(Phone.NUMBER))



                        when (pCur.getInt(pCur.getColumnIndex(Phone.TYPE))) {
                            Phone.TYPE_MOBILE -> {

                                val mBitmapURI = getPhotoUri(context, id)

                                var formattedPhone: String? = null
                                formattedPhone =
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        PhoneNumberUtils.formatNumber(
                                            lastnumber,
                                            Locale.getDefault().country
                                        )
                                    } else {
                                        PhoneNumberUtils.formatNumber(lastnumber)
                                    }

                                val splitarray = formattedPhone.split("\\s".toRegex())

                                var code: String? = null
                                var phnumber = ""
                                for (i in splitarray.indices) {
                                    if (splitarray[i].startsWith("+"))
                                        code = splitarray[i]
                                    else {
                                        phnumber = "$phnumber${splitarray[i]}"
                                    }
                                }

                                phnumber = phnumber.replace("[^0-9]+".toRegex(), "")
                                code = code?.replace("[^0-9]+".toRegex(), "")


                                if (!number.contains(phnumber)) {
                                    number.add(phnumber)
                                    contactList.add(
                                        ContactModel(
                                            id,
                                            name,
                                            context.resources.getString(R.string.user_role),
                                            phnumber,
                                            code,
                                            mBitmapURI.toString()
                                        )
                                    )


                                }
                            }
                        }
                    }
                    pCur?.close()
                }
            }
            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            adapter = ParticipentAdapter(
                context,
                contactList
            )
            binding?.participentRV?.adapter = adapter


            binding?.mSearch?.setOnQueryTextListener(object :
                SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    if (newText.isEmpty())
                        binding?.searchImageView?.visibility = View.VISIBLE
                    else
                        binding?.searchImageView?.visibility = View.INVISIBLE
                    adapter?.filter?.filter(newText)
                    return true
                }
            })

            binding?.cta?.setOnClickListener {
                if (adapter?.getList() != null) {

                    val action = AddParticipantsFragmentDirections.addParticipantToAddAdmin()

                    createProjectModel?.participants = adapter?.getList()

                    action.createProjectModel = createProjectModel
                    val nav: NavController? = binding?.root?.let { it1 ->
                        Navigation.findNavController(
                            it1
                        )
                    }
                    if (nav?.currentDestination != null && nav.currentDestination?.id == R.id.addParticipantsFragment) {
                        nav.navigate(action)
                    }
                }
            }

            DeviceUtils.dismissProgress()
        }
    }


}