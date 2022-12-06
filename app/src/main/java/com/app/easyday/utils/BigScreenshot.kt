package com.app.easyday.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.SystemClock
import android.util.Log
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.view.ViewGroup

class BigScreenshot(
    private val processScreenshot: ProcessScreenshot,
    private val viewGroup: ViewGroup,
    private val container: ViewGroup
) {
    private val bitmaps: MutableList<Bitmap> = ArrayList()
    private var isScreenShots = false

    fun startScreenshot() {
        bitmaps.clear()
        isScreenShots = true
        autoScroll()
        Log.e("isScreenShots", isScreenShots.toString())
    }

    fun stopScreenshot() {
        isScreenShots = false
    }

    private fun autoScroll() {
        val delay = 16
        val step = 10
        val motionEvent = MotionEvent.obtain(
            SystemClock.uptimeMillis(),
            SystemClock.uptimeMillis(),
            MotionEvent.ACTION_DOWN,
            (viewGroup.width / 2
                    ).toFloat(),
            (viewGroup.height / 2
                    ).toFloat(),
            0
        )
        viewGroup.dispatchTouchEvent(motionEvent)
        motionEvent.action = MotionEvent.ACTION_MOVE
        motionEvent.setLocation(
            motionEvent.x, motionEvent.y - ViewConfiguration.get(
                viewGroup.context
            ).scaledTouchSlop
        )
        viewGroup.dispatchTouchEvent(motionEvent)
        val startScrollY = motionEvent.y.toInt()
        viewGroup.postDelayed(object : Runnable {
            override fun run() {
                if (!isScreenShots) {
                    drawRemainAndAssemble(startScrollY, motionEvent.y.toInt())
                    return
                }
                drawIfNeeded(startScrollY, motionEvent.y.toInt())
                motionEvent.action = MotionEvent.ACTION_MOVE
                val nextStep: Int
                val gap = (startScrollY - motionEvent.y.toInt() + step) % container.height
                nextStep = if (gap in 1 until step) {
                    step - gap
                } else {
                    step
                }
                motionEvent.setLocation(
                    motionEvent.x.toInt().toFloat(),
                    (motionEvent.y.toInt() - nextStep).toFloat()
                )
                viewGroup.dispatchTouchEvent(motionEvent)
                viewGroup.postDelayed(this, delay.toLong())
            }
        }, delay.toLong())
    }

    private fun drawRemainAndAssemble(startScrollY: Int, curScrollY: Int) {
        if ((curScrollY - startScrollY) % container.height != 0) {
            val film = Bitmap.createBitmap(container.width, container.height, Bitmap.Config.RGB_565)
            val canvas = Canvas()
            canvas.setBitmap(film)
            container.draw(canvas)
            val part = (startScrollY - curScrollY) / container.height
            val remainHeight = startScrollY - curScrollY - container.height * part
            val remainBmp = Bitmap.createBitmap(
                film,
                0,
                container.height - remainHeight,
                container.width,
                remainHeight
            )
            bitmaps.add(remainBmp)
        }
        assembleBmp()
    }

    private fun assembleBmp() {
        var h = 0
        for (bitmap in bitmaps) {
            h += bitmap.height
        }
        val bitmap = Bitmap.createBitmap(container.width, h, Bitmap.Config.RGB_565)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)
        for (b in bitmaps) {
            canvas.drawBitmap(b, 0f, 0f, null)
            canvas.translate(0f, b.height.toFloat())
        }
        processScreenshot.getScreenshot(bitmap)
    }

    private fun drawIfNeeded(startScrollY: Int, curScrollY: Int) {
        if ((curScrollY - startScrollY) % container.height == 0) {
            val film = Bitmap.createBitmap(container.width, container.height, Bitmap.Config.RGB_565)
            val canvas = Canvas()
            canvas.setBitmap(film)
            container.draw(canvas)
            bitmaps.add(film)
        }
    }

    interface ProcessScreenshot {
        fun getScreenshot(bitmap: Bitmap?)
    }
}
