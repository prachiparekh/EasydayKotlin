package com.app.easyday.screens.activities.auth

import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.compose.ui.text.toLowerCase
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.observe
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.app.easyday.R
import com.app.easyday.app.sources.local.model.PhoneCodeModel
import com.app.easyday.screens.base.BaseFragment
import com.app.easyday.screens.dialogs.CodeDialog
import com.app.easyday.utils.CountryCityUtils
import com.app.easyday.utils.DeviceUtils
import com.app.easyday.utils.FileUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_login.*
import org.json.JSONObject
import java.util.*


@AndroidEntryPoint
class LoginFragment : BaseFragment<LoginViewModel>(), CodeDialog.CountyPickerItems {

    override fun getContentView() = R.layout.fragment_login
    private var countryCode: String? = null
    private var trimCode = ""

    override fun getStatusBarColor() = ContextCompat.getColor(requireContext(), R.color.bg_white)

    override fun initUi() {
        DeviceUtils.initProgress(requireContext())

        setPhoneCountryData()
        cta.setOnClickListener {
            errorText.isVisible = false
            DeviceUtils.hideKeyboard(requireContext())
            if (phone.text?.isNotEmpty() == true && countryCode?.isNotEmpty() == true) {
                trimCode = countryCode?.replace("\\s+".toRegex(), "").toString()
                trimCode = trimCode.replace("+", "")
                viewModel.sendOTP(phone.text.toString(), trimCode)
            } else {
                Toast.makeText(
                    requireContext(),
                    requireContext().resources.getString(R.string.number_requires),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setPhoneCountryData() {
        val phoneModelList: ArrayList<PhoneCodeModel> = ArrayList()
        val obj = FileUtil.loadJSONFromAsset(requireContext())?.let { JSONObject(it) }

        var defaultCode: PhoneCodeModel? = null
        if (obj != null) {
            for (key in obj.keys()) {
                val keyStr = key as String
                val keyValue = obj[keyStr]
                val code = PhoneCodeModel(keyStr, keyValue as String?)
                phoneModelList.add(code)
                if (code.key == "US") {
                    defaultCode = code
                }
            }
        }


        phoneCode.text = "+ ${defaultCode?.value}"
        countryCode = phoneCode.text.toString()

        phoneFlag.text =
            CountryCityUtils.getFlagId(
                CountryCityUtils.firstTwo(
                    defaultCode?.key?.toLowerCase(Locale.getDefault()).toString()
                )
            )

        val phoneCodeDialog = CodeDialog(requireActivity(), phoneModelList, this)

        cc_dialog.setOnClickListener {
            if(!phoneCodeDialog.isAdded)
            phoneCodeDialog.show(requireActivity().supportFragmentManager, null)
        }

        setHighLightedText(terms, requireContext().resources.getString(R.string.terms))
        setHighLightedText(terms, requireContext().resources.getString(R.string.policy))
    }

    override fun setObservers() {
        viewModel.actionStream.observe(viewLifecycleOwner) {
            when (it) {
                is LoginViewModel.ACTION.GetOTPMsg -> {
                    Toast.makeText(requireContext(), it.msg, Toast.LENGTH_SHORT).show()

                    val action = LoginFragmentDirections.loginToOtp()
                    action.phoneNumber = phone.text.toString()

                    action.countryCode = trimCode
                    val nav: NavController = Navigation.findNavController(requireView())
                    if (nav.currentDestination != null && nav.currentDestination?.id == R.id.loginFragment) {
                        nav.navigate(action)
                    }
                }
                is LoginViewModel.ACTION.GetErrorMsg -> {
                    errorText.text = it.msg
                    errorText.isVisible = true
                }
            }
        }
    }

    override fun pickCountry(countries: PhoneCodeModel) {
        phoneCode.text = "+ ${countries.value}"
        countryCode = phoneCode.text.toString()
        phoneFlag.text =
            CountryCityUtils.getFlagId(
                CountryCityUtils.firstTwo(
                    countries.key?.toLowerCase(Locale.getDefault()).toString()
                ).toString()
            )
    }

    private fun setHighLightedText(tv: TextView, textToHighlight: String) {

        val tvt = tv.text.toString()
        var ofe = tvt.indexOf(textToHighlight, 0)
        val wordToSpan: Spannable = SpannableString(tv.text)
        var ofs = 0
        while (ofs < tvt.length && ofe != -1) {
            ofe = tvt.indexOf(textToHighlight, ofs)
            if (ofe == -1) break else {
                wordToSpan.setSpan(
                    object : ClickableSpan() {
                        override fun onClick(textView: View) {
                            val action = LoginFragmentDirections.loginToTerms()
                            if (textToHighlight == requireContext().resources.getString(R.string.terms))
                                action.url = "http://63.32.98.218/terms_of_service.html"
                            else
                                action.url = "http://63.32.98.218/privacy_policy.html"
                            action.title = textToHighlight
                            val nav: NavController = Navigation.findNavController(requireView())
                            if (nav.currentDestination != null && nav.currentDestination?.id == R.id.loginFragment) {
                                nav.navigate(action)
                            }
                        }

                        override fun updateDrawState(ds: TextPaint) {
                            super.updateDrawState(ds)
                            ds.isUnderlineText = false
                        }
                    },
                    ofe,
                    ofe + textToHighlight.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                tv.movementMethod = LinkMovementMethod.getInstance();
                tv.setText(wordToSpan, TextView.BufferType.SPANNABLE)
            }
            ofs = ofe + 1
        }
    }
}