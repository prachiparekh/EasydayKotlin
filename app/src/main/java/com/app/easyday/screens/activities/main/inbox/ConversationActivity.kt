package com.app.easyday.screens.activities.main.inbox

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.app.easyday.R
import kotlinx.android.synthetic.main.activity_conversation.*

class ConversationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation)

        iv_back.setOnClickListener { onBackPressed() }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}