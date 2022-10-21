package com.app.easyday.utils.camera_utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.animation.doOnStart
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.Executor

class Camera_Extensions {
    companion object {
        fun Window.fitSystemWindows() {
            WindowCompat.setDecorFitsSystemWindows(this, false)
        }

        fun View.onWindowInsets(action: (View, WindowInsetsCompat) -> Unit) {
            ViewCompat.requestApplyInsets(this)
            ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
                action(v, insets)
                insets
            }
        }

        var View.topMargin: Int
            get() = (this.layoutParams as ViewGroup.MarginLayoutParams).topMargin
            set(value) {
                updateLayoutParams<ViewGroup.MarginLayoutParams> { topMargin = value }
            }

        var View.topPadding: Int
            get() = paddingTop
            set(value) {
                updateLayoutParams {
                    setPaddingRelative(
                        paddingStart,
                        value,
                        paddingEnd,
                        paddingBottom
                    )
                }
            }

        var View.bottomMargin: Int
            get() = (this.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin
            set(value) {
                updateLayoutParams<ViewGroup.MarginLayoutParams> { bottomMargin = value }
            }

        var View.endMargin: Int
            get() = (this.layoutParams as ViewGroup.MarginLayoutParams).marginEnd
            set(value) {
                updateLayoutParams<ViewGroup.MarginLayoutParams> { marginEnd = value }
            }

        var View.startPadding: Int
            get() = paddingStart
            set(value) {
                updateLayoutParams {
                    setPaddingRelative(
                        value,
                        paddingTop,
                        paddingEnd,
                        paddingBottom
                    )
                }
            }

        fun ImageView.toggleButton(
            flag: Boolean,
            rotationAngle: Float,
            @DrawableRes firstIcon: Int,
            @DrawableRes secondIcon: Int,
            action: (Boolean) -> Unit
        ) {
            if (flag) {
                if (rotationY == 0f) rotationY = rotationAngle
                animate().rotationY(0f).apply {
                    setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)
                            action(!flag)
                        }
                    })
                }.duration = 200
                GlobalScope.launch(Dispatchers.Main) {
                    delay(100)
                    setImageResource(firstIcon)
                }
            } else {
                if (rotationY == rotationAngle) rotationY = 0f
                animate().rotationY(rotationAngle).apply {
                    setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)
                            action(!flag)
                        }
                    })
                }.duration = 200
                GlobalScope.launch(Dispatchers.Main) {
                    delay(100)
                    setImageResource(secondIcon)
                }
            }
        }


        fun ViewGroup.circularReveal(button: ImageView) {
            ViewAnimationUtils.createCircularReveal(
                this,
                button.x.toInt() + button.width / 2,
                button.y.toInt() + button.height / 2,
                0f,
                if (button.context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) this.width.toFloat() else this.height.toFloat()
            ).apply {
                duration = 500
                doOnStart { visibility = View.VISIBLE }
            }.start()
        }



    }
}