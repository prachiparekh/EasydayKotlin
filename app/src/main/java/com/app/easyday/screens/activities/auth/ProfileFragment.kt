package com.app.easyday.screens.activities.auth

import android.Manifest
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.observe
import com.app.easyday.R
import com.app.easyday.app.sources.aws.AWSKeys
import com.app.easyday.app.sources.aws.S3Uploader
import com.app.easyday.app.sources.aws.S3Utils
import com.app.easyday.app.sources.local.prefrences.AppPreferencesDelegates
import com.app.easyday.screens.activities.main.MainActivity
import com.app.easyday.screens.base.BaseActivity
import com.app.easyday.screens.base.BaseActivity.Companion.profileLogoListener
import com.app.easyday.screens.base.BaseFragment
import com.app.easyday.utils.FileUtil
import com.app.easyday.utils.IntentUtil
import com.app.easyday.utils.IntentUtil.Companion.cameraPermission
import com.app.easyday.utils.IntentUtil.Companion.readPermission
import com.app.easyday.utils.IntentUtil.Companion.writePermission
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.textfield.TextInputEditText
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageActivity
import com.theartofdev.edmodo.cropper.CropImageOptions
import com.theartofdev.edmodo.cropper.CropImageView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.avatar
import kotlinx.android.synthetic.main.fragment_profile.profession
import java.io.File


@AndroidEntryPoint
class ProfileFragment : BaseFragment<ProfileViewModel>(), BaseActivity.OnProfileLogoChangeListener {

    override fun getContentView() = R.layout.fragment_profile
    var isNewUser: Boolean? = null
//    var mImageFile: String? = null

    private var s3uploaderObj: S3Uploader? = null
    var mImageFile: File? = null
    private var urlFromS3: String? = null
    var uUserName = ""
    var uUserProfession = ""
    var uUserImage = ""

    override fun getStatusBarColor() = ContextCompat.getColor(requireContext(), R.color.bg_white)

    override fun initUi() {

        s3uploaderObj = S3Uploader(requireContext(), AWSKeys.FOLDER_NAME_PROFILE_IMAGES)

        val mPhoneNumber = arguments?.getString("phoneNumber")
        val mCountryCode = arguments?.getString("countryCode")
        isNewUser = arguments?.getBoolean("isNewUser", true)
        if (isNewUser == false) {
            viewModel.getProfile()
        }
        profileLogoListener = this
        camera.setOnClickListener {


            if (cameraPermission(requireActivity()) && readPermission(requireActivity()) && writePermission(
                    requireActivity()
                )
            )
                openIntent()
            else
                onPermission()
        }

        cta.setOnClickListener {
            if (isNewUser == true) {
                if (fullName.text.isNullOrEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        requireContext().resources.getString(R.string.name_constarin),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                if (profession.text.isNullOrEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        requireContext().resources.getString(R.string.profession_constarin),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                if (mImageFile != null) {
                    mImageFile?.let { it1 ->
                        uploadAudioTos3(
                            it1
                        )
                    }
                } else {
                    viewModel.createUser(
                        fullName.text.toString(),
                        profession.text.toString(), null
                    )
                }


            } else {

                //update API
//                viewModel.updateUser(mImageFile, fullName.text.toString(), "")
                val intent = Intent(requireActivity(), MainActivity::class.java)
                requireActivity().startActivity(intent)
                requireActivity().finish()
            }
        }

        fullName.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if (s.isNotEmpty()) {
                    setTextViewDrawableColor(fullName, R.color.green)
                } else {
                    setTextViewDrawableColor(fullName, R.color.gray)
                }
                checkData()
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {

            }
        })

        profession.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if (s.isNotEmpty()) {
                    setTextViewDrawableColor(profession, R.color.green)
                } else {
                    setTextViewDrawableColor(profession, R.color.gray)
                }
                checkData()
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {

            }
        })

        requireView().isFocusableInTouchMode = true
        requireView().requestFocus()
        requireView().setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                requireActivity().finish()
                true
            } else false
        }
    }

    private fun uploadAudioTos3(
        imageFile: File
    ) {
        val path = imageFile.absolutePath

        s3uploaderObj?.initUpload(path)
        s3uploaderObj?.setOns3UploadDone(object : S3Uploader.S3UploadInterface {
            override fun onUploadSuccess(response: String?) {
                if (response.equals("Success", ignoreCase = true)) {

                    urlFromS3 = S3Utils.generates3ShareUrl(
                        requireContext(),
                        path,
                        AWSKeys.FOLDER_NAME_PROFILE_IMAGES
                    )
                    if (!TextUtils.isEmpty(urlFromS3)) {


                        urlFromS3?.let {
                            viewModel.createUser(
                                fullName.text.toString(),
                                profession.text.toString(),
                                it
                            )
                        }

//                                urlFromS3?.let {
//                                viewModel.updateUser(
//                                    fullName.text.toString(),
//                                    profession.text.toString(),
//                                    it
//                                )}


                    }
                }
            }

            override fun onUploadError(response: String?) {

                Log.e("TAG", "Error Uploading: $response")
            }
        })
    }


    private fun setTextViewDrawableColor(editText: TextInputEditText, color: Int) {
        for (drawable in editText.compoundDrawables) {
            if (drawable != null) {
                drawable.colorFilter =
                    PorterDuffColorFilter(
                        ContextCompat.getColor(editText.context, color),
                        PorterDuff.Mode.SRC_IN
                    )
            }
        }
    }

    fun checkData() {
        if (!fullName.text.isNullOrEmpty() && !profession.text.isNullOrEmpty()) {
            cta.isEnabled = true
            cta.alpha = 1F
        } else {
            cta.isEnabled = false
            cta.alpha = 0.5F
        }
    }

    private fun onPermission() {

        Dexter.withContext(requireContext())
            .withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                    if (p0?.areAllPermissionsGranted() == true)
                        openIntent()
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                }

            }).withErrorListener {}

            .check()
    }


    private fun openIntent() {
        IntentUtil.getPickImageChooserIntent(requireContext(), requireActivity())?.let {
            requireActivity().startActivityForResult(
                it,
                IntentUtil.PICK_IMAGE_CHOOSER_REQUEST_CODE
            )
        }
    }

    override fun setObservers() {
        viewModel.userData.observe(viewLifecycleOwner) { userData ->
            fullName.setText(userData?.fullname)
            profession.setText(userData?.profession)

            uUserName = userData?.fullname.toString()
            uUserProfession = userData?.profession.toString()
            uUserImage = userData?.profileImage.toString()

            if (userData?.profileImage != null) {
                val options = RequestOptions()
                avatar.clipToOutline = true
                Glide.with(requireContext())
                    .load(userData.profileImage)
                    .apply(
                        options.centerCrop()
                            .skipMemoryCache(true)
                            .priority(Priority.HIGH)
                            .format(DecodeFormat.PREFER_ARGB_8888)
                    )
                    .into(avatar)
            }

            if (isNewUser == true)
                AppPreferencesDelegates.get().token = userData?.token.toString()


        }

        viewModel.actionStream.observe(viewLifecycleOwner) {
            when (it) {
                is ProfileViewModel.ACTION.onAddUpdateUser -> {

                    requireActivity().startActivity(
                        Intent(
                            requireActivity(),
                            MainActivity::class.java
                        )
                    )
                }
                is ProfileViewModel.ACTION.onError -> {
                    if (it.msg != null)
                        Toast.makeText(requireContext(), it.msg, Toast.LENGTH_SHORT).show()
                }
            }
        }


    }

    override fun onCropLogo(uri: Uri) {
        try {
            Log.e("uri", uri.toString())
            val mOptions = CropImageOptions()
            mOptions.allowFlipping = false
            mOptions.allowRotation = false
            mOptions.aspectRatioX = 1
            mOptions.aspectRatioY = 1
            mOptions.cropShape = CropImageView.CropShape.OVAL
            val intent = Intent()
            intent.setClass(requireContext(), CropImageActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(CropImage.CROP_IMAGE_EXTRA_SOURCE, uri)
            bundle.putParcelable(CropImage.CROP_IMAGE_EXTRA_OPTIONS, mOptions)
            intent.putExtra(CropImage.CROP_IMAGE_EXTRA_BUNDLE, bundle)
            requireActivity().startActivityForResult(
                intent,
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
            )
        } catch (e: Exception) {

        }
    }

    override fun onChangeLogo(uri: Uri) {
        val selectedFile = uri.let {
            FileUtil.getPath(it, requireContext())
        }
        if (selectedFile != null) {
            val options = RequestOptions()
            avatar.clipToOutline = true
            Glide.with(requireContext())
                .load(uri)
                .apply(
                    options.centerCrop()
                        .skipMemoryCache(true)
                        .priority(Priority.HIGH)
                        .format(DecodeFormat.PREFER_ARGB_8888)
                )
                .into(avatar)
            mImageFile = File(selectedFile)
        }
    }
}