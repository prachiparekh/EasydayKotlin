package com.app.easyday.screens.activities.main

import android.os.Bundle
import com.app.easyday.R
import com.app.easyday.screens.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<MainViewModel>() {

    override fun getContentView() = R.layout.activity_main

    override fun setupUi() {
    }

    override fun setupObservers() {
    }
}