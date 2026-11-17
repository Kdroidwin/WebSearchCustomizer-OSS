/*
 * Web Search Customizer (OSS edition)
 * Copyright (c) 2026 The Web Search Customizer OSS contributors
 *
 * SPDX-License-Identifier: MIT
 */
package io.github.websearchcustomizer

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        applyNightMode()
    }

    /**
     * Applies the night mode selected by the user.
     *
     * - "system"  : follow the OS dark mode
     * - "light"   : always light (white)
     * - "dark"    : always dark
     * - "amoled"  : always dark with pure black surfaces (overlay applied per activity)
     */
    fun applyNightMode() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val desired = when (prefs.getString(Prefs.KEY_THEME, Prefs.THEME_SYSTEM)) {
            Prefs.THEME_LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            Prefs.THEME_DARK, Prefs.THEME_AMOLED -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        if (AppCompatDelegate.getDefaultNightMode() != desired) {
            AppCompatDelegate.setDefaultNightMode(desired)
        }
    }
}
