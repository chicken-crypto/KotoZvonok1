package com.example.kotozvonok

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Outline
import android.graphics.PixelFormat
import android.net.Uri
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

object OverlayManager {
    private var view: View? = null
    private val animators = mutableListOf<ObjectAnimator>()
    private val cats = intArrayOf(
        R.drawable.cat1, R.drawable.cat2, R.drawable.cat3,
        R.drawable.cat4, R.drawable.cat5, R.drawable.cat6
    )

    fun show(context: Context, name: String, number: String) {
        val app = context.applicationContext
        if (!Settings.canDrawOverlays(app)) return
        hide(app)
        val wm = app.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val d = app.resources.displayMetrics.density
        fun dp(v: Int) = (v * d).toInt()

        val root = LinearLayout(app).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(0xEE1D1B2E.toInt())
            setPadding(dp(16), dp(40), dp(16), dp(12))
        }
        root.addView(TextView(app).apply {
            text = "Входящий звонок"; setTextColor(0xFFA29FB8.toInt()); textSize = 13f
        })
        root.addView(TextView(app).apply {
            text = name; setTextColor(Color.WHITE); textSize = 28f
        })
        root.addView(TextView(app).apply {
            text = number; setTextColor(0xFF8FA2FF.toInt()); textSize = 22f
        })

        val buttons = LinearLayout(app).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, dp(10), 0, dp(6))
        }
        buttons.addView(Button(app).apply {
            text = "🔍 Найти номер в браузере"
            setOnClickListener {
                val url = "https://www.google.com/search?q=" + Uri.encode(number)
                app.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                hide(app)
            }
        })
        buttons.addView(Button(app).apply {
            text = "✕"
            setOnClickListener { hide(app) }
        })
        root.addView(buttons)

        // Прыгающие коты
        val row = LinearLayout(app).apply {
            orientation = LinearLayout.HORIZONTAL
            clipChildren = false
            gravity = Gravity.BOTTOM
        }
        root.addView(row, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(110)))
        cats.forEachIndexed { i, res ->
            val iv = ImageView(app).apply {
                setImageResource(res)
                scaleType = ImageView.ScaleType.CENTER_CROP
                clipToOutline = true
                outlineProvider = object : ViewOutlineProvider() {
                    override fun getOutline(v: View, o: Outline) { o.setOval(0, 0, v.width, v.height) }
                }
            }
            row.addView(iv, LinearLayout.LayoutParams(0, dp(56), 1f).apply { setMargins(dp(3), 0, dp(3), 0) })
            animators += ObjectAnimator.ofFloat(iv, "translationY", 0f, -dp(46).toFloat()).apply {
                duration = 380L + i * 50L
                startDelay = i * 90L
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
                interpolator = DecelerateInterpolator()
                start()
            }
        }

        val lp = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply { gravity = Gravity.TOP }
        wm.addView(root, lp)
        view = root
    }

    fun hide(context: Context) {
        animators.forEach { it.cancel() }
        animators.clear()
        view?.let {
            val wm = context.applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            try { wm.removeView(it) } catch (_: Exception) {}
        }
        view = null
    }
}
