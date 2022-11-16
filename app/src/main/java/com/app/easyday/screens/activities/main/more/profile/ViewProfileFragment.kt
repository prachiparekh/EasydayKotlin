package com.app.easyday.screens.activities.main.more.profile

import android.Manifest
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import com.app.easyday.R
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
import kotlinx.android.synthetic.main.fragment_view_profile.*
import java.io.File

@AndroidEntryPoint
class ViewProfileFragment : BaseFragment<ViewProfileViewModel>(),
    BaseActivity.OnProfileLogoChangeListener {

    var isEditMode = false
    var mImageFile: File? = null
    override fun getContentView() = R.layout.fragment_view_profile

    override fun initUi() {
        requireActivity().window?.statusBarColor = resources.getColor(R.color.navy_blue)
        option.setOnClickListener {
            editProfile.isVisible = true
            blankRL.isVisible = true
        }

        blankRL.setOnClickListener {
            blankRL.isVisible = false
            editProfile.isVisible = false
        }

        editProfile.setOnClickListener {
            changeToEditMode()
        }

        fullName.setText(userModel?.fullname)
        profession.setText(userModel?.profession)
        checkData()

        val separated: List<String>? = userModel?.profileImage?.split("?")
        val imageUrl = separated?.get(0).toString()

        if (userModel?.profileImage != null) {
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

        requireView().isFocusableInTouchMode = true
        requireView().requestFocus()
        requireView().setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                if (isEditMode) {
                    isEditMode = false
                    fullName.isEnabled = false
                    profession.isEnabled = false
                    cta.isVisible = false
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

        camera.setOnClickListener {
            if (isEditMode) {
                BaseActivity.profileLogoListener = this

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

    private fun changeToEditMode() {
        isEditMode = true
        blankRL.isVisible = false
        editProfile.isVisible = false
        fullName.isEnabled = true
        profession.isEnabled = true
        cta.isVisible = true
        option.isVisible = false
        title1.text = requireContext().resources.getString(R.string.edit_profile)
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

    override fun setObservers() {

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