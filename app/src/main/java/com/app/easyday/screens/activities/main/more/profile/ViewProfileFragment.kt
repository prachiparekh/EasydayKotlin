package com.app.easyday.screens.activities.main.more.profile

import android.Manifest
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.observe
import androidx.navigation.Navigation
import com.app.easyday.R
import com.app.easyday.app.sources.aws.AWSKeys
import com.app.easyday.app.sources.aws.S3Uploader
import com.app.easyday.app.sources.aws.S3Utils
import com.app.easyday.app.sources.remote.model.UserModel
import com.app.easyday.screens.activities.auth.ProfileViewModel
import com.app.easyday.screens.activities.main.home.HomeViewModel.Companion.userModel
import com.app.easyday.screens.base.BaseActivity
import com.app.easyday.screens.base.BaseFragment
import com.app.easyday.utils.FileUtil
import com.app.easyday.utils.IntentUtil
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
import kotlinx.android.synthetic.main.fragment_view_profile.*
import kotlinx.android.synthetic.main.fragment_view_profile.avatar
import kotlinx.android.synthetic.main.fragment_view_profile.camera
import kotlinx.android.synthetic.main.fragment_view_profile.title1
import java.io.File


@AndroidEntryPoint
class ViewProfileFragment : BaseFragment<ProfileViewModel>(),
    BaseActivity.OnProfileLogoChangeListener {

    var isEditMode = false
    var mImageFile: File? = null
    private var s3uploaderObj: S3Uploader? = null
    private var urlFromS3: String? = null


    override fun getContentView() = R.layout.fragment_view_profile

    override fun initUi() {

        userModel?.let { setUserData(it) }

        s3uploaderObj = S3Uploader(requireContext(), AWSKeys.FOLDER_NAME_PROFILE_IMAGES)

        requireActivity().window?.statusBarColor = resources.getColor(R.color.navy_blue)
        option.setOnClickListener {
            editProfile.isVisible = true
            blankRL.isVisible = true
        }

        ctaTV.setOnClickListener {
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
                    uploadUpdatedImageTos3(
                        it1
                    )
                }

            } else {
                viewModel.updateUser(
                    fullName.text.toString(),
                    profession.text.toString(), null
                )


            }
            ctaTV.isVisible = false
            isEditMode = false
            fullName.isEnabled = false
            profession.isEnabled = false
        }



        blankRL.setOnClickListener {
            blankRL.isVisible = false
            editProfile.isVisible = false
        }

        editProfile.setOnClickListener {
            changeToEditMode()
        }

        checkData()

        requireView().isFocusableInTouchMode = true
        requireView().requestFocus()
        requireView().setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                if (isEditMode) {
                    isEditMode = false
                    fullName.isEnabled = false
                    profession.isEnabled = false
                    ctaTV.isVisible = false
                    option.isVisible = true
                    title1.text = requireContext().resources.getString(R.string.my_profile)
                    true
                } else {
                    false
                }

            } else false
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
        BaseActivity.profileLogoListener = this


        camera.setOnClickListener {
            if (isEditMode) {


                if (IntentUtil.cameraPermission(requireActivity()) && IntentUtil.readPermission(
                        requireActivity()
                    ) && IntentUtil.writePermission(
                        requireActivity()
                    )
                )
                    openIntent()
                else
                    onPermission()
            }
        }

        back.setOnClickListener {
            Navigation.findNavController(requireView()).popBackStack()
        }


    }

    private fun uploadUpdatedImageTos3(
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
                            viewModel.updateUser(
                                fullName.text.toString(),
                                profession.text.toString(),
                                it
                            )
                        }

                    }
                }
            }

            override fun onUploadError(response: String?) {

                Log.e("TAG", "Error Uploading: $response")
            }
        })
    }

    private fun changeToEditMode() {
        isEditMode = true
        blankRL.isVisible = false
        editProfile.isVisible = false
        fullName.isEnabled = true
        profession.isEnabled = true
        ctaTV.isVisible = true
        option.isVisible = false
        title1.text = requireContext().resources.getString(R.string.edit_profile)

        /*KeyboardUtils.addKeyboardToggleListener(
            requireActivity(),
            object : KeyboardUtils.SoftKeyboardToggleListener {
                override fun onToggleSoftKeyboard(isVisible: Boolean) {
                    if (isVisible){
                        ctaTV.isVisible = false
                    }else{
                        Handler().postDelayed({
                            ctaTV.isVisible = true
                        }, 50)
                    }
                }
            })*/
//        parent_constraint.getViewTreeObserver()
//            .addOnGlobalLayoutListener(ViewTreeObserver.OnGlobalLayoutListener {
//                val r = Rect()
//                parent_constraint.getWindowVisibleDisplayFrame(r)
//                val screenHeight: Int = parent_constraint.getRootView().getHeight()
//                val keypadHeight: Int = screenHeight - r.bottom
//                if (keypadHeight > screenHeight * 0.15) {
//
//                    ctaTV.visibility = View.GONE
//                } else {
//                    Handler().postDelayed({
//                        ctaTV.visibility = View.VISIBLE
//                    }, 50)
//
//
//                }
//            })


    }

    fun checkData() {
        if (!fullName.text.isNullOrEmpty() && !profession.text.isNullOrEmpty()) {
            ctaTV.isEnabled = true
            ctaTV.alpha = 1F
        } else {
            ctaTV.isEnabled = false
            ctaTV.alpha = 0.5F
        }
    }

    override fun setObservers() {
        viewModel.userData.observe(viewLifecycleOwner) { userData ->

            userData?.let { setUserData(it) }
        }
    }

    private fun setUserData(userModel: UserModel) {
        fullName.setText(userModel.fullname)
        profession.setText(userModel.profession)
        val separated: List<String>? = userModel.profileImage?.split("?")
        val imageUrl = separated?.get(0).toString()

        if (userModel.profileImage != null) {
            val options = RequestOptions()
            avatar.clipToOutline = true
            Glide.with(requireContext())
                .load(imageUrl)
                .error(requireContext().resources.getDrawable(R.drawable.ic_user))
                .apply(
                    options.centerCrop()
                        .skipMemoryCache(true)
                        .priority(Priority.HIGH)
                        .format(DecodeFormat.PREFER_ARGB_8888)
                )
                .into(avatar)
        }
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

    override fun onCropLogo(uri: Uri) {
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
    }

    override fun onChangeLogo(uri: Uri) {
        val selectedFile = uri.let {
            FileUtil.getPath(it, requireContext())
        }
        Log.e("uri", uri.toString())
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

        if (!isEditMode) {
            ctaTV.isVisible = true
            isEditMode = true
            fullName.isEnabled = true
            profession.isEnabled = true
        }
    }


}