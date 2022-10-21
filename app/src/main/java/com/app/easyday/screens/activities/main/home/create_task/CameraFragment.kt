package com.app.easyday.screens.activities.main.home.create_task

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.hardware.display.DisplayManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
import androidx.camera.core.CameraSelector.DEFAULT_FRONT_CAMERA
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import coil.fetch.VideoFrameUriFetcher
import coil.load
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.app.easyday.R
import com.app.easyday.app.sources.local.model.Media
import com.app.easyday.databinding.FragmentCameraBinding
import com.app.easyday.utils.IntentUtil
import com.app.easyday.utils.camera_utils.*
import com.app.easyday.utils.camera_utils.CameraUtils.Companion.getMedia
import com.app.easyday.utils.camera_utils.CameraUtils.Companion.outputDirectory
import com.app.easyday.utils.camera_utils.Camera_Extensions.Companion.bottomMargin
import com.app.easyday.utils.camera_utils.Camera_Extensions.Companion.circularReveal
import com.app.easyday.utils.camera_utils.Camera_Extensions.Companion.endMargin
import com.app.easyday.utils.camera_utils.Camera_Extensions.Companion.onWindowInsets
import com.app.easyday.utils.camera_utils.Camera_Extensions.Companion.startPadding
import com.app.easyday.utils.camera_utils.Camera_Extensions.Companion.toggleButton
import com.app.easyday.utils.camera_utils.Camera_Extensions.Companion.topPadding
import com.theartofdev.edmodo.cropper.CropImage.getPickImageResultUri
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executor
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.properties.Delegates


@SuppressLint("RestrictedApi")
@AndroidEntryPoint
class CameraFragment : Fragment() {

    var binding: FragmentCameraBinding? = null
    var mImageVideoUriList = arrayListOf<Media>()
    private var videoCapture: VideoCapture? = null
    private var isRecording = false
    var isVideo = false
    private var isTorchOn = false
    private var camera: Camera? = null
    var isVideoRunning = false

    companion object {
        private const val TAG = "CameraXDemo"
        const val KEY_FLASH = "sPrefFlashCamera"
        private const val RATIO_4_3_VALUE = 4.0 / 3.0 // aspect ratio 4x3
        private const val RATIO_16_9_VALUE = 16.0 / 9.0 // aspect ratio 16x9
    }

//    video


//    ******************


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_camera, container, false)

        return binding?.root
    }

    private val displayManager by lazy { requireContext().getSystemService(Context.DISPLAY_SERVICE) as DisplayManager }

    // An instance of a helper function to work with Shared Preferences
    private val prefs by lazy { SharedPrefsManager.newInstance(requireContext()) }

    private var preview: Preview? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var imageCapture: ImageCapture? = null
    private var imageAnalyzer: ImageAnalysis? = null

    private var displayId = -1

    // Selector showing which camera is selected (front or back)
    private var lensFacing = DEFAULT_BACK_CAMERA
    private var hdrCameraSelector: CameraSelector? = null

    // Selector showing which flash mode is selected (on, off or auto)
    private var flashMode by Delegates.observable(ImageCapture.FLASH_MODE_OFF) { _, _, new ->
        binding?.btnFlash?.setImageResource(
            when (new) {
                ImageCapture.FLASH_MODE_ON -> R.drawable.ic_flash_on
                ImageCapture.FLASH_MODE_AUTO -> R.drawable.ic_flash_auto
                else -> R.drawable.ic_flash_off
            }
        )
    }

    private val displayListener = object : DisplayManager.DisplayListener {
        override fun onDisplayAdded(displayId: Int) = Unit
        override fun onDisplayRemoved(displayId: Int) = Unit

        @SuppressLint("UnsafeExperimentalUsageError", "UnsafeOptInUsageError")
        override fun onDisplayChanged(displayId: Int) = view?.let { view ->
            if (displayId == this@CameraFragment.displayId) {
                preview?.targetRotation = view.display.rotation

                if (isVideo) {
                    videoCapture?.setTargetRotation(view.display.rotation)
                } else {
                    imageCapture?.targetRotation = view.display.rotation
                    imageAnalyzer?.targetRotation = view.display.rotation
                }
            }
        } ?: Unit
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        flashMode = prefs.getInt(KEY_FLASH, ImageCapture.FLASH_MODE_OFF)

        if (IntentUtil.cameraPermission(requireActivity()) && IntentUtil.readPermission(
                requireActivity()
            ) && IntentUtil.recordAudioPermission(
                requireActivity()
            )
        ) {
            openCamera()
        } else
            onPermission()

        requireView().isFocusableInTouchMode = true
        requireView().requestFocus()
        requireView().setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                when (binding?.llFlashOptions?.visibility) {
                    View.VISIBLE -> binding?.btnFlash?.let {
                        binding?.llFlashOptions?.circularClose(
                            it
                        )
                    }
                    else -> Navigation.findNavController(requireView()).popBackStack()
                }
                true
            } else false
        }

        initViews()

        displayManager.registerDisplayListener(displayListener, null)

        binding?.run {
            viewFinder.addOnAttachStateChangeListener(object :
                View.OnAttachStateChangeListener {
                override fun onViewDetachedFromWindow(v: View) =
                    displayManager.registerDisplayListener(displayListener, null)

                override fun onViewAttachedToWindow(v: View) =
                    displayManager.unregisterDisplayListener(displayListener)
            })

            btnTakePicture.actionListener = object : CameraVideoButton.ActionListener {
                override fun onCancelled() {

                }

                override fun onStartRecord() {

                    startVideoCamera()
                }

                override fun onEndRecord() {

                    recordVideo()
                }

                override fun onDurationTooShortError() {

                }

                override fun onSingleTap() {

                    takePicture()
                }

            }

            btnGallery.setOnClickListener { openPreview() }
            btnImage.setOnClickListener { openPreview() }
            btnSwitchCamera.setOnClickListener { toggleCamera() }

            btnFlash.setOnClickListener { selectFlash() }

            btnFlashOff.setOnClickListener { closeFlashAndSelect(ImageCapture.FLASH_MODE_OFF) }
            btnFlashOn.setOnClickListener { closeFlashAndSelect(ImageCapture.FLASH_MODE_ON) }
            btnFlashAuto.setOnClickListener { closeFlashAndSelect(ImageCapture.FLASH_MODE_AUTO) }

            binding?.close?.setOnClickListener {
                Navigation.findNavController(requireView()).popBackStack()
            }

            binding?.skipPhoto?.setOnClickListener {
                navigateToTask()
            }


        }
    }

    /**
     * Create some initial states
     * */
    private fun initViews() {
        adjustInsets()
    }


    private fun adjustInsets() {
//        activity?.window?.fitSystemWindows()
        binding?.btnTakePicture?.onWindowInsets { view, windowInsets ->
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                view.bottomMargin =
                    windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            } else {
                view.endMargin = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).right
            }
        }

        binding?.llFlashOptions?.onWindowInsets { view, windowInsets ->
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                view.topPadding =
                    windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).top
            } else {
                view.startPadding =
                    windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).left
            }
        }
    }

    /**
     * Change the facing of camera
     *  toggleButton() function is an Extension function made to animate button rotation
     * */
    @SuppressLint("RestrictedApi")
    fun toggleCamera() = binding?.btnSwitchCamera?.toggleButton(
        flag = lensFacing == DEFAULT_BACK_CAMERA,
        rotationAngle = 180f,
        firstIcon = R.drawable.ic_rotate,
        secondIcon = R.drawable.ic_rotate
    ) {
        lensFacing = if (it) {
            DEFAULT_BACK_CAMERA
        } else {
            DEFAULT_FRONT_CAMERA
        }

        startCamera()
    }

    /**
     * Navigate to PreviewFragment
     * */
    private fun openPreview() {
        IntentUtil.getVideoImageChooserIntent(requireContext())?.let {
            startActivityForResult(
                it,
                IntentUtil.PICK_IMAGE_CHOOSER_REQUEST_CODE
            )
        }
    }

    private fun selectFlash() =
        binding?.btnFlash?.let { binding?.llFlashOptions?.circularReveal(it) }

    /**
     * This function is called from XML view via Data Binding to select a FlashMode
     *  possible values are ON, OFF or AUTO
     *  circularClose() function is an Extension function which is adding circular close
     * */
    private fun closeFlashAndSelect(@ImageCapture.FlashMode flash: Int) =
        binding?.btnFlash?.let {
            binding?.llFlashOptions?.circularClose(it) {
                flashMode = flash
                binding?.btnFlash?.setImageResource(
                    when (flash) {
                        ImageCapture.FLASH_MODE_ON -> R.drawable.ic_flash_on
                        ImageCapture.FLASH_MODE_OFF -> R.drawable.ic_flash_off
                        else -> R.drawable.ic_flash_auto
                    }
                )

                imageCapture?.flashMode = flashMode

                prefs.putInt(KEY_FLASH, flashMode)
            }
        }

    private fun ViewGroup.circularClose(button: ImageView, action: () -> Unit = {}) {
        ViewAnimationUtils.createCircularReveal(
            this,
            button.x.toInt() + button.width / 2,
            button.y.toInt() + button.height / 2,
            if (button.context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) this.width.toFloat() else this.height.toFloat(),
            0f
        ).apply {
            duration = 500
            doOnStart { action() }
            doOnEnd { visibility = View.GONE }
        }.start()
    }


    private fun setLastPictureThumbnail() = binding?.btnGallery?.post {
        getMedia(requireContext()).firstOrNull() // check if there are any photos or videos in the app directory
            ?.let { setGalleryThumbnail(it.uri) } // preview the last one

    }

    /**
     * Unbinds all the lifecycles from CameraX, then creates new with new parameters
     * */
    private fun startCamera() {
        // This is the CameraX PreviewView where the camera will be rendered
        val viewFinder = binding?.viewFinder

        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({

            try {
                cameraProvider = cameraProviderFuture.get()
            } catch (e: InterruptedException) {
                Toast.makeText(requireContext(), "Error starting camera", Toast.LENGTH_SHORT).show()
                return@addListener
            } catch (e: ExecutionException) {
                Toast.makeText(requireContext(), "Error starting camera", Toast.LENGTH_SHORT).show()
                return@addListener
            }

            // The display information
            val metrics = DisplayMetrics().also { viewFinder?.display?.getRealMetrics(it) }
            // The ratio for the output image and preview
            val aspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels)
            // The display rotation
            val rotation = viewFinder?.display?.rotation

            val localCameraProvider = cameraProvider
                ?: throw IllegalStateException("Camera initialization failed.")

            // The Configuration of camera preview
            preview = rotation?.let {
                Preview.Builder()
                    .setTargetAspectRatio(aspectRatio) // set the camera aspect ratio
                    .setTargetRotation(it) // set the camera rotation
                    .build()
            }

            // The Configuration of image capture
            imageCapture = rotation?.let {
                ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY) // setting to have pictures with highest quality possible (may be slow)
                    .setFlashMode(flashMode) // set capture flash
                    .setTargetAspectRatio(aspectRatio) // set the capture aspect ratio
                    .setTargetRotation(it) // set the capture rotation
                    .build()
            }

            // The Configuration of image analyzing
            imageAnalyzer = rotation?.let {
                ImageAnalysis.Builder()
                    .setTargetAspectRatio(aspectRatio) // set the analyzer aspect ratio
                    .setTargetRotation(it) // set the analyzer rotation
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST) // in our analysis, we care about the latest image
                    .build()
                    .also { setLuminosityAnalyzer(it) }
            }

            // Unbind the use-cases before rebinding them
            localCameraProvider.unbindAll()
            // Bind all use cases to the camera with lifecycle
            if (viewFinder != null) {
                bindToLifecycle(localCameraProvider, viewFinder)
            }


        }, ContextCompat.getMainExecutor(requireContext()))
    }


    private fun setLuminosityAnalyzer(imageAnalysis: ImageAnalysis) {
        // Use a worker thread for image analysis to prevent glitches
        val analyzerThread = HandlerThread("LuminosityAnalysis").apply { start() }
        imageAnalysis.setAnalyzer(
            ThreadExecutor(Handler(analyzerThread.looper)),
            LuminosityAnalyzer()
        )
    }

    private fun bindToLifecycle(
        localCameraProvider: ProcessCameraProvider,
        viewFinder: PreviewView
    ) {
        try {
            localCameraProvider.bindToLifecycle(
                viewLifecycleOwner, // current lifecycle owner
                hdrCameraSelector ?: lensFacing, // either front or back facing
                preview, // camera preview use case
                imageCapture, // image capture use case
                imageAnalyzer // image analyzer use case
            )
            // Attach the viewfinder's surface provider to preview use case
            preview?.setSurfaceProvider(viewFinder.surfaceProvider)
        } catch (e: Exception) {

        }
    }

    /**
     *  Detecting the most suitable aspect ratio for current dimensions
     *
     *  @param width - preview width
     *  @param height - preview height
     *  @return suitable aspect ratio
     */
    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    @Suppress("NON_EXHAUSTIVE_WHEN")
    private fun takePicture() = lifecycleScope.launch(Dispatchers.Main) {

        captureImage()
    }

    private fun captureImage() {
        val localImageCapture =
            imageCapture ?: throw IllegalStateException("Camera initialization failed.")

        // Setup image capture metadata
        val metadata = ImageCapture.Metadata().apply {
            // Mirror image when using the front camera
            isReversedHorizontal = lensFacing == DEFAULT_FRONT_CAMERA
        }
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        // Options fot the output image file
        val outputOptions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, System.currentTimeMillis())
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, outputDirectory)
            }

            val contentResolver = requireContext().contentResolver

            // Create the output uri
            val contentUri =
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

            ImageCapture.OutputFileOptions.Builder(contentResolver, contentUri, contentValues)
        } else {
            File(outputDirectory).mkdirs()
            val file = File(outputDirectory, "${System.currentTimeMillis()}.jpg")

            ImageCapture.OutputFileOptions.Builder(file)
        }.setMetadata(metadata).build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            localImageCapture.takePicture(
                outputOptions, // the options needed for the final image
                requireContext().mainExecutor(), // the executor, on which the task will run
                object :
                    ImageCapture.OnImageSavedCallback { // the callback, about the result of capture process
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        // This function is called if capture is successfully completed
                        outputFileResults.savedUri
                            ?.let { uri ->
//                                setGalleryThumbnail(uri)
                                if (getMedia(requireContext()).isEmpty()) return

                                mImageVideoUriList.add(
                                    Media(
                                        uri,
                                        false,
                                        System.currentTimeMillis()
                                    )
                                )
                                navigateToTask()
                                Log.d(TAG, "Photo saved in $uri")
                            }
                            ?: setLastPictureThumbnail()
                    }

                    override fun onError(exception: ImageCaptureException) {
                        // This function is called if there is an errors during capture process
                        val msg = "Photo capture failed: ${exception.message}"
                        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()

                        exception.printStackTrace()
                    }
                }
            )
        }
    }

    private fun setGalleryThumbnail(savedUri: Uri?) = binding?.btnImage?.load(savedUri) {
        binding?.btnImage?.isVisible = true
        binding?.btnImage?.clipToOutline = true
        binding?.btnGallery?.isVisible = false
        listener(object : ImageRequest.Listener {
            override fun onError(request: ImageRequest, throwable: Throwable) {
                super.onError(request, throwable)
                binding?.btnGallery?.load(savedUri) {
                    binding?.btnImage?.isVisible = false
                    binding?.btnGallery?.isVisible = true
                    placeholder(R.drawable.ic_no_picture)
                    transformations(CircleCropTransformation())
                    fetcher(VideoFrameUriFetcher(requireContext()))
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        displayManager.unregisterDisplayListener(displayListener)
    }


    private fun onPermission() {

        val permissions = mutableListOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                add(Manifest.permission.ACCESS_MEDIA_LOCATION)
            }
        }

        val permissionRequest =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                if (permissions.all { it.value }) {
                    openCamera()
                }
            }

        permissionRequest.launch(permissions.toTypedArray())
    }

    override fun onResume() {
        super.onResume()
        binding?.btnTakePicture?.isPressed = false
        requireActivity().window?.statusBarColor = resources.getColor(R.color.black)
    }

    private fun openCamera() {
        binding?.viewFinder.let { vf ->
            vf?.post {
                // Setting current display ID
                displayId = vf.display.displayId
                startCamera()
                lifecycleScope.launch(Dispatchers.IO) {
                    // Do on IO Dispatcher
                    setLastPictureThumbnail()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IntentUtil.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val uri = getPickImageResultUri(requireContext(), data)

            if (uri != null) {
                val cr: ContentResolver = requireContext().contentResolver
                val mime = cr.getType(uri)

                if (mime?.startsWith("image/", 0) == true || uri.toString()
                        .endsWith(".png") || uri.toString().endsWith(".jpg") || uri.toString()
                        .endsWith(".jpeg")
                ) {
                    mImageVideoUriList.add(Media(uri, false, System.currentTimeMillis()))
                    navigateToTask()
                } else {
//            ***********Video***********
                    val videoFullPath = data?.data?.let {
                        URIPathHelper.getPath(
                            requireContext(),
                            it
                        )
                    }
                    if (videoFullPath != null) {
                        mImageVideoUriList.add(
                            Media(
                                Uri.parse(videoFullPath),
                                true,
                                System.currentTimeMillis()
                            )
                        )
                        navigateToTask()
                    }
                }
            }
        }

    }


    fun navigateToTask() {

        val bundle = Bundle()
        bundle.putParcelableArrayList("uriList", mImageVideoUriList)
        val nav = binding?.root?.let { it1 ->
            Navigation.findNavController(
                it1
            )
        }
        if (nav?.currentDestination != null && nav.currentDestination?.id == R.id.cameraFragment) {
            nav.navigate(R.id.camera_to_create_task, bundle)
        }

    }

    //    Video
    @SuppressLint("MissingPermission")
    private fun recordVideo() {


        camera?.cameraControl?.enableTorch(isTorchOn)

        // Options fot the output video file
        val outputOptions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, System.currentTimeMillis())
                put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
                put(MediaStore.MediaColumns.RELATIVE_PATH, outputDirectory)
            }

            requireContext().contentResolver.run {
                val contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                VideoCapture.OutputFileOptions.Builder(this, contentUri, contentValues)
            }
        } else {
            File(outputDirectory).mkdirs()
            val file = File("$outputDirectory/${System.currentTimeMillis()}.mp4")

            VideoCapture.OutputFileOptions.Builder(file)
        }.build()

        if (!isRecording) {

            videoCapture?.startRecording(
                outputOptions, // the options needed for the final video
                requireContext().mainExecutor(), // the executor, on which the task will run
                object :
                    VideoCapture.OnVideoSavedCallback { // the callback after recording a video
                    override fun onVideoSaved(outputFileResults: VideoCapture.OutputFileResults) {
                        // Create small preview
                        outputFileResults.savedUri
                            ?.let { uri ->
//                                    setGalleryThumbnail(uri)

                                isVideoRunning = false
                                mImageVideoUriList.add(Media(uri, true, System.currentTimeMillis()))
                                navigateToTask()

                            }
                            ?: setLastPictureThumbnail()

                    }

                    override fun onError(
                        videoCaptureError: Int,
                        message: String,
                        cause: Throwable?
                    ) {
                        // This function is called if there is an error during recording process

                        val msg = "Video capture failed: $message"

                        isVideoRunning = false
                        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()

                        cause?.printStackTrace()

                    }
                })
        } else {

            videoCapture?.stopRecording()
        }
        isRecording = !isRecording

    }

    override fun onStop() {
        super.onStop()
        camera?.cameraControl?.enableTorch(false)
    }

    private fun startVideoCamera() {
        // This is the Texture View where the camera will be rendered
        val viewFinder = binding?.viewFinder
        isTorchOn = false == (flashMode == ImageCapture.FLASH_MODE_OFF)
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
//            cameraProvider = cameraProviderFuture.get()

            // The display information
            val metrics = DisplayMetrics().also { viewFinder?.display?.getRealMetrics(it) }
            // The ratio for the output image and preview
            val aspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels)
            // The display rotation
            val rotation = viewFinder?.display?.rotation

            val localCameraProvider = cameraProvider
                ?: throw IllegalStateException("1.Camera initialization failed.")

            // The Configuration of camera preview
            preview = rotation?.let {
                Preview.Builder()
                    .setTargetAspectRatio(aspectRatio) // set the camera aspect ratio
                    .setTargetRotation(it) // set the camera rotation
                    .build()
            }

            val videoCaptureConfig =
                VideoCapture.DEFAULT_CONFIG.config // default config for video capture
            // The Configuration of video capture
            videoCapture = VideoCapture.Builder
                .fromConfig(videoCaptureConfig)
                .build()

            localCameraProvider.unbindAll() // unbind the use-cases before rebinding them

            try {
                // Bind all use cases to the camera with lifecycle
                camera = localCameraProvider.bindToLifecycle(
                    viewLifecycleOwner, // current lifecycle owner
                    lensFacing, // either front or back facing
                    preview, // camera preview use case
                    videoCapture, // video capture use case
                )

                // Attach the viewfinder's surface provider to preview use case
                preview?.setSurfaceProvider(viewFinder?.surfaceProvider)

                recordVideo()
            } catch (e: Exception) {

            }
        }, ContextCompat.getMainExecutor(requireContext()))


    }

    fun Context.mainExecutor(): Executor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        mainExecutor
    } else {
        MainExecutor()
    }

}