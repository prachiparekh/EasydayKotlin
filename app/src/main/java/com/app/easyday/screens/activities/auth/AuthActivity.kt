package com.app.easyday.screens.activities.auth

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.app.easyday.R
import com.app.easyday.screens.activities.main.MainActivity
import com.app.easyday.screens.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_auth.*

@AndroidEntryPoint
class AuthActivity : BaseActivity<AuthViewModel>() {


    override fun getContentView()=R.layout.activity_auth

    override fun setupUi() {
    }

    override fun setupObservers() {
    }


}