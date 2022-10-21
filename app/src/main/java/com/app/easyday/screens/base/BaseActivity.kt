package com.app.easyday.screens.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.app.easyday.R
import com.app.easyday.navigation.UiEvent
import com.app.easyday.utils.DeviceUtils
import com.app.easyday.utils.IntentUtil.Companion.PICK_IMAGE_CHOOSER_REQUEST_CODE
import com.app.easyday.utils.IntentUtil.Companion.getImagePath
import com.passiondroid.imageeditorlib.ImageEditor
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.fragment_create_task.*
import java.io.File
import java.lang.reflect.ParameterizedType


abstract class BaseActivity<VM : BaseViewModel> : AppCompatActivity() {

    lateinit var viewModel: VM

    companion object{
        var profileLogoListener: OnProfileLogoChangeListener? = null
    }

    interface OnProfileLogoChangeListener {
        fun onCropLogo(uri: Uri)
        fun onChangeLogo(uri: Uri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getContentView())

        viewModel = ViewModelProvider(this)[getViewModelClass()]

        viewModel.uiEventStream.observe(this) { it -> processUiEvent(it) }

        setupUi()
        setupObservers()
        viewModel.onActivityCreated()

    }


    abstract fun getContentView(): Int
    abstract fun setupUi()
    abstract fun setupObservers()


    private fun getViewModelClass(): Class<VM> {
        val type = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0]
        return type as Class<VM>
    }

    fun processUiEvent(uiEvent: UiEvent) {

        when (uiEvent) {

            is UiEvent.HideKeyboard -> {
                DeviceUtils.hideKeyboard(this)
            }
            is UiEvent.ShowToast -> {
                showToast(getString(uiEvent.messageResId))
            }
            is UiEvent.ShowToastMsg -> {
                showToast(uiEvent.message)
            }
        }
    }

    private fun showToast(message: String) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val uri = getPickImageResultUri(baseContext, data)
            if (uri != null) {
                profileLogoListener?.onCropLogo(uri)
            }
        }

        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                if (result != null) {
                    profileLogoListener?.onChangeLogo(result.uri)
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(
                    this,
                    baseContext.getString(R.string.crop_fail) + result.error,
                    Toast.LENGTH_LONG
                ).show()
            }
        }


        if (resultCode == Activity.RESULT_OK && data != null && requestCode ==ImageEditor.RC_IMAGE_EDITOR) {
            val imagePath: String? = data.getStringExtra(ImageEditor.EXTRA_EDITED_PATH)
            var mfile = File(imagePath)

//            selectedUriList[pagerPhotos.currentItem].uri = Uri.fromFile(mfile)
//            mediaAdapter?.notifyItemChanged(pagerPhotos.currentItem)
        }
    }

    open fun getPickImageResultUri(context: Context, data: Intent?): Uri? {
        var isCamera = true
        if (data != null && data.data != null) {
            val action = data.action
            isCamera = action != null && action == MediaStore.ACTION_IMAGE_CAPTURE
        }
        return if (isCamera || data?.data == null) {
//            --------1---------
//            getCaptureImageOutputUri(context)
//            --------2---------
            /* val photo = data?.extras?.get("data") as Bitmap?
             getImageUri(context, photo)*/
//            --------3---------
            Uri.fromFile(getImagePath())
        } else data.data
    }

    open fun getCaptureImageOutputUri(context: Context): Uri? {
        var outputFileUri: Uri? = null
        val getImage = context.externalCacheDir
        if (getImage != null) {
            outputFileUri = Uri.fromFile(File(getImage.path, "pickImageResult.jpeg"))
        }
        return outputFileUri
    }

}