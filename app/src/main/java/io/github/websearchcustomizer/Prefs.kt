/*
 * Web Search Customizer (OSS edition)
 * Copyright (c) 2026 The Web Search Customizer OSS contributors
 *
 * SPDX-License-Identifier: MIT
 */
package io.github.websearchcustomizer

import android.content.Context
import android.os.Build
import androidx.preference.PreferenceManager

/** Preference keys and defaults, shared between activities. */
object Prefs {

    const val KEY_ENGINE = "engine"
    const val KEY_ENGINE_CUSTOM = "engine_custom"
    const val KEY_THEME = "theme"
    const val KEY_DYNAMIC_COLORS = "dynamic_colors"

    const val ENGINE_CUSTOM = "custom"

    const val THEME_SYSTEM = "system"
    const val THEME_LIGHT = "light"
    const val THEME_DARK = "dark"
    const val THEME_AMOLED = "amoled"

    const val DEFAULT_ENGINE = "https://duckduckgo.com/?q=%s"
    const val DEFAULT_ENGINE_CUSTOM = "https://lite.duckduckgo.com/lite/?q=%s"

    fun isDynamicColorsEnabled(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return false
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(KEY_DYNAMIC_COLORS, true)
    }

    fun theme(context: Context): String =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getString(KEY_THEME, THEME_SYSTEM) ?: THEME_SYSTEM
}
