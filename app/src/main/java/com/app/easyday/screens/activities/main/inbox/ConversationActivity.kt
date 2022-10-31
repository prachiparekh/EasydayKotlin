package com.app.easyday.screens.activities.main.inbox

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.app.easyday.R
import kotlinx.android.synthetic.main.activity_conversation.*

class ConversationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation)

        iv_back.setOnClickListener { onBackPressed() }

        edt_msg.setOnClickListener {
            cam.visibility = View.GONE
            mic.visibility = View.GONE
            send.visibility = View.VISIBLE
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}