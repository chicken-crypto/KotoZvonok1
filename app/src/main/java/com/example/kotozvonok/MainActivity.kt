package com.example.kotozvonok

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val p = (20 * resources.displayMetrics.density).toInt()
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(p, p * 2, p, p)
        }
        root.addView(TextView(this).apply { text = "🐱 Котозвонок"; textSize = 30f })
        root.addView(TextView(this).apply {
            text = "При входящем звонке показывает имя, номер, кнопку поиска в браузере и прыгающих котов.\n\nСделай два шага ниже, потом нажми «Проверить»."
            textSize = 16f
            setPadding(0, p, 0, p)
        })
        fun btn(t: String, action: (View) -> Unit) {
            root.addView(Button(this).apply { text = t; setOnClickListener(action) })
        }
        btn("1. Разрешить доступ к звонкам и контактам") {
            requestPermissions(arrayOf(
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.READ_CONTACTS
            ), 1)
        }
        btn("2. Разрешить показ поверх других окон") {
            startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
        }
        btn("Проверить (тестовый звонок)") {
            OverlayManager.show(this, "Тестовый Котик", "+7 900 000-00-00")
        }
    }
}
