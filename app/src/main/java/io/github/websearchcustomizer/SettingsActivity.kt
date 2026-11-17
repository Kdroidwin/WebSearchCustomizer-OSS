/*
 * Web Search Customizer (OSS edition)
 * Copyright (c) 2026 The Web Search Customizer OSS contributors
 *
 * SPDX-License-Identifier: MIT
 */
package io.github.websearchcustomizer

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.color.DynamicColors
import com.google.android.material.R as MaterialR

class SettingsActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        // Material You: apply the system dynamic color palette (API 31+).
        var base: Context = newBase
        if (Prefs.isDynamicColorsEnabled(newBase)) {
            base = DynamicColors.wrapContextIfAvailable(
                base,
                MaterialR.style.ThemeOverlay_Material3_DynamicColors_DayNight
            )
        }
        super.attachBaseContext(base)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Surface color overlays are applied on top of the (possibly dynamic)
        // theme, so the user's chosen neutral palette always wins.
        when (Prefs.theme(this)) {
            Prefs.THEME_LIGHT -> theme.applyStyle(R.style.ThemeOverlay_WSC_White, true)
            Prefs.THEME_AMOLED -> theme.applyStyle(R.style.ThemeOverlay_WSC_Amoled, true)
        }

        setContentView(R.layout.activity_settings)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Edge-to-edge: pad the toolbar under the status bar and keep the
        // preference list clear of the navigation bar / gesture area.
        val root = findViewById<android.view.View>(R.id.settings_root)
        ViewCompat.setOnApplyWindowInsetsListener(root) { _, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )
            toolbar.setPadding(
                toolbar.paddingLeft, bars.top, toolbar.paddingRight, toolbar.paddingBottom
            )
            findViewById<android.view.View>(R.id.settings_container)
                .setPadding(0, 0, 0, bars.bottom)
            insets
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.settings_container, GeneralPreferenceFragment())
                .commit()
        }
    }
}
