package com.app.easyday.screens.activities.main.inbox

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.app.easyday.R
import kotlinx.android.synthetic.main.activity_conversation.*

class ConversationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation)

        iv_back.setOnClickListener { onBackPressed() }

        edt_msg.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p1 <= 1){
                    cam.visibility = View.GONE
                    mic.visibility = View.GONE
                    attach.visibility = View.VISIBLE
                    send.visibility = View.VISIBLE
                }else if (p1 == 0){
                    cam.visibility = View.VISIBLE
                    mic.visibility = View.VISIBLE
                    attach.visibility = View.VISIBLE
                    send.visibility = View.GONE
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                if (p0 != null) {
                    cam.visibility = View.GONE
                    mic.visibility = View.GONE
                    attach.visibility = View.VISIBLE
                    send.visibility = View.VISIBLE
                }else {
                    cam.visibility = View.VISIBLE
                    mic.visibility = View.VISIBLE
                    attach.visibility = View.VISIBLE
                    send.visibility = View.GONE
                }
            }

        })

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}