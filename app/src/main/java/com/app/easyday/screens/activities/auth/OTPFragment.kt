package com.app.easyday.screens.activities.auth

import android.os.CountDownTimer
import android.provider.Settings
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.observe
import androidx.navigation.Navigation
import com.app.easyday.R
import com.app.easyday.app.sources.local.prefrences.AppPreferencesDelegates
import com.app.easyday.screens.base.BaseFragment
import com.app.easyday.utils.DeviceUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_o_t_p.*

@AndroidEntryPoint
class OTPFragment : BaseFragment<OTPViewModel>() {

    companion object {
        var isOTPComplete = MutableLiveData<Boolean>()
        private var mPhoneNumber: String? = null
        private var mCountryCode: String? = null
        var mViewModel: OTPViewModel? = null
    }

    override fun getContentView() = R.layout.fragment_o_t_p
    override fun getStatusBarColor() = ContextCompat.getColor(requireContext(), R.color.bg_white)

    private val timer = object : CountDownTimer(1000 * 60 * 5, 1000) {
        override fun onTick(millisUntilFinished: Long) {

            val minutes = millisUntilFinished / 1000 / 60
            val seconds = millisUntilFinished / 1000 % 60
            resendCode.text = requireContext().resources.getString(
                R.string.resend_code_in,
                "$minutes:$seconds"
            )
            resendCode.isEnabled = false
        }

        override fun onFinish() {
            resendCode.text =
                Html.fromHtml("<u>${requireContext().resources.getString(R.string.resend_code)}</u>")
            resendCode.setTextColor(requireContext().resources.getColor(R.color.green))
            resendCode.isEnabled = true
        }
    }

    override fun initUi() {
        DeviceUtils.initProgress(requireContext())
        mViewModel = viewModel
        mPhoneNumber = arguments?.getString("phoneNumber")
        mCountryCode = arguments?.getString("countryCode")
        phoneNumber.text = Html.fromHtml("<u>$mCountryCode$mPhoneNumber</u>")


        timer.start()

        requireView().isFocusableInTouchMode = true
        requireView().requestFocus()
        requireView().setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                onBackPress()
                true
            } else false
        }

        back.setOnClickListener {
            onBackPress()
        }

        change.setOnClickListener {
            onBackPress()
        }

        resendCode.setOnClickListener {
            mPhoneNumber?.let { mCountryCode?.let { it1 -> viewModel.resendOTP(it, it1) } }
        }

        //GenericTextWatcher here works only for moving to next EditText when a number is entered
//first parameter is the current EditText and second parameter is next EditText
        tv1.addTextChangedListener(GenericTextWatcher(tv1, tv2))
        tv2.addTextChangedListener(GenericTextWatcher(tv2, tv3))
        tv3.addTextChangedListener(GenericTextWatcher(tv3, tv4))
        tv4.addTextChangedListener(GenericTextWatcher(tv4, tv5))
        tv5.addTextChangedListener(GenericTextWatcher(tv5, tv6))
        tv6.addTextChangedListener(GenericTextWatcher(tv6, null))

//GenericKeyEvent here works for deleting the element and to switch back to previous EditText
//first parameter is the current EditText and second parameter is previous EditText
        tv1.setOnKeyListener(GenericKeyEvent(tv1, null))
        tv2.setOnKeyListener(GenericKeyEvent(tv2, tv1))
        tv3.setOnKeyListener(GenericKeyEvent(tv3, tv2))
        tv4.setOnKeyListener(GenericKeyEvent(tv4, tv3))
        tv5.setOnKeyListener(GenericKeyEvent(tv5, tv4))
        tv6.setOnKeyListener(GenericKeyEvent(tv6, tv5))

    }

    private fun onBackPress() {
        timer.cancel()
        Navigation.findNavController(requireView()).popBackStack()
    }

    override fun setObservers() {
        isOTPComplete.observe(viewLifecycleOwner) { it ->
            if (viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED) {
                if (it) {
                    val deviceID: String = Settings.Secure.getString(
                        requireContext().contentResolver,
                        Settings.Secure.ANDROID_ID
                    )

                    val fullOTP = tv1.text.toString() +
                            tv2.text.toString() +
                            tv3.text.toString() +
                            tv4.text.toString() +
                            tv5.text.toString() +
                            tv6.text.toString()
                    mPhoneNumber?.let { it1 ->
                        mCountryCode?.let { it2 ->
                            mViewModel?.verifyOTP(
                                it1, fullOTP,
                                it2, deviceID, android.os.Build.MODEL
                            )
                        }
                    }

                }
            }
        }

        viewModel.actionStream.observe(viewLifecycleOwner) {
            when (it) {
                is OTPViewModel.ACTION.VerifyOTP -> {
                    wrongOtp.isGone = it.result
                    if (it.result) {
                        tv1.setBackgroundResource(R.drawable.ic_green_square)
                        tv2.setBackgroundResource(R.drawable.ic_green_square)
                        tv3.setBackgroundResource(R.drawable.ic_green_square)
                        tv4.setBackgroundResource(R.drawable.ic_green_square)
                        tv5.setBackgroundResource(R.drawable.ic_green_square)
                        tv6.setBackgroundResource(R.drawable.ic_green_square)
                    } else {
                        tv1.setBackgroundResource(R.drawable.ic_red_square)
                        tv2.setBackgroundResource(R.drawable.ic_red_square)
                        tv3.setBackgroundResource(R.drawable.ic_red_square)
                        tv4.setBackgroundResource(R.drawable.ic_red_square)
                        tv5.setBackgroundResource(R.drawable.ic_red_square)
                        tv6.setBackgroundResource(R.drawable.ic_red_square)
                    }

                    if (it.msg != null)
                        Toast.makeText(requireContext(), it.msg, Toast.LENGTH_SHORT).show()
                }
                is OTPViewModel.ACTION.IsNewUser -> {
                    timer.cancel()

                    val action =
                        OTPFragmentDirections
                            .otpToProfile()
                    AppPreferencesDelegates.get().token = it.model.token.toString()
                    action.isNewUser = it.model.isNewUser != false

                    action.phoneNumber = mPhoneNumber
                    action.countryCode = mCountryCode
                    Navigation.findNavController(requireView()).navigate(action)
                }
                is OTPViewModel.ACTION.GetOTPMsg -> {
                    Toast.makeText(requireContext(), it.msg, Toast.LENGTH_SHORT).show()
                    resendCode.setTextColor(requireContext().resources.getColor(R.color.gray))
                    timer.start()
                }
            }
        }
    }

    class GenericKeyEvent internal constructor(
        private val currentView: EditText,
        private val previousView: EditText?
    ) : View.OnKeyListener {
        override fun onKey(p0: View?, keyCode: Int, event: KeyEvent?): Boolean {
            if (event?.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL
                && currentView.id != R.id.tv1 && currentView.text.isEmpty()
            ) {
                previousView?.text = null
                previousView?.requestFocus()
                return true
            }

            return false
        }
    }


    class GenericTextWatcher internal constructor(
        private val currentView: View,
        private val nextView: View?
    ) :
        TextWatcher {
        override fun afterTextChanged(editable: Editable) {
            val text = editable.toString()
            when (currentView.id) {
                R.id.tv1 -> if (text.length == 1) nextView?.requestFocus()
                R.id.tv2 -> if (text.length == 1) nextView?.requestFocus()
                R.id.tv3 -> if (text.length == 1) nextView?.requestFocus()
                R.id.tv4 -> if (text.length == 1) nextView?.requestFocus()
                R.id.tv5 -> if (text.length == 1) nextView?.requestFocus()
                R.id.tv6 -> if (text.length == 1) {
                    isOTPComplete.value = true
                }
            }
        }

        override fun beforeTextChanged(
            arg0: CharSequence,
            arg1: Int,
            arg2: Int,
            arg3: Int
        ) {
        }

        override fun onTextChanged(
            arg0: CharSequence,
            arg1: Int,
            arg2: Int,
            arg3: Int
        ) {
        }

    }
}