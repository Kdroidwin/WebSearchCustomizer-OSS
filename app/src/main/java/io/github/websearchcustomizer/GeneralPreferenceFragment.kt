/*
 * Web Search Customizer (OSS edition)
 * Copyright (c) 2026 The Web Search Customizer OSS contributors
 *
 * SPDX-License-Identifier: MIT
 */
package io.github.websearchcustomizer

import android.app.SearchManager
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.regex.Pattern

class GeneralPreferenceFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        bindEnginePreferences()
        bindThemePreferences()
        bindAboutPreferences()
    }

    // ---------------------------------------------------------------------
    // Search engine
    // ---------------------------------------------------------------------

    private fun bindEnginePreferences() {
        val engine = findPreference<ListPreference>(Prefs.KEY_ENGINE)
        val custom = findPreference<EditTextPreference>(Prefs.KEY_ENGINE_CUSTOM)

        engine?.setOnPreferenceChangeListener { _, newValue ->
            custom?.isEnabled = newValue == Prefs.ENGINE_CUSTOM
            true
        }
        custom?.isEnabled = engine?.value == Prefs.ENGINE_CUSTOM

        custom?.setOnBindEditTextListener { editText ->
            editText.hint = getString(R.string.custom_url_hint)
            editText.inputType =
                android.text.InputType.TYPE_CLASS_TEXT or
                android.text.InputType.TYPE_TEXT_VARIATION_URI
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) = Unit

                override fun onTextChanged(
                    s: CharSequence?, start: Int, before: Int, count: Int
                ) = Unit

                override fun afterTextChanged(s: Editable?) {
                    val error = validateCustomUrl(s?.toString())
                    editText.error = error
                }
            })
            editText.setError(validateCustomUrl(editText.text?.toString()))
        }

        custom?.setOnPreferenceChangeListener { _, newValue ->
            val value = newValue as? String ?: return@setOnPreferenceChangeListener false
            validateCustomUrl(value) == null
        }

        findPreference<Preference>(KEY_ENGINE_TEST)?.setOnPreferenceClickListener {
            val test = Intent(Intent.ACTION_WEB_SEARCH)
                .putExtra(SearchManager.QUERY, getString(R.string.test_query))
            try {
                startActivity(test)
            } catch (_: ActivityNotFoundException) {
                Toast.makeText(requireContext(), R.string.error_no_browser, Toast.LENGTH_SHORT)
                    .show()
            }
            true
        }
    }

    /**
     * Returns an error message for a custom engine URL, or null when valid.
     * A valid template must start with https:// and contain a query
     * placeholder ("%s" or "{query}").
     */
    private fun validateCustomUrl(value: String?): String? {
        if (value.isNullOrBlank()) return getString(R.string.error_url_empty)
        if (!HTTPS_SCHEME.matcher(value).lookingAt()) return getString(R.string.error_url_scheme)
        val hasPlaceholder = value.contains("%s") || value.contains("{query}")
        if (!hasPlaceholder) return getString(R.string.error_url_placeholder)
        return null
    }

    // ---------------------------------------------------------------------
    // Appearance
    // ---------------------------------------------------------------------

    private fun bindThemePreferences() {
        findPreference<ListPreference>(Prefs.KEY_THEME)?.setSummaryProvider(
            androidx.preference.ListPreference.SimpleSummaryProvider.getInstance()
        )

        val dynamic = findPreference<SwitchPreferenceCompat>(Prefs.KEY_DYNAMIC_COLORS)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            dynamic?.isVisible = false
        }
    }

    private fun scheduleThemeRecreate() {
        val act = activity ?: return
        val app = act.applicationContext as App
        val before = AppCompatDelegate.getDefaultNightMode()
        app.applyNightMode()
        val after = AppCompatDelegate.getDefaultNightMode()
        if (before == after) {
            // Night mode itself did not change; recreate manually so the
            // AMOLED/White surface overlay or dynamic colors take effect.
            act.window?.decorView?.postDelayed({
                if (!act.isFinishing && !act.isDestroyed) act.recreate()
            }, RECREATE_DELAY_MS)
        }
        // Otherwise AppCompatDelegate.setDefaultNightMode() recreates
        // activities on its own.
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            Prefs.KEY_THEME, Prefs.KEY_DYNAMIC_COLORS -> scheduleThemeRecreate()
        }
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen?.sharedPreferences
            ?.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen?.sharedPreferences
            ?.unregisterOnSharedPreferenceChangeListener(this)
    }

    // ---------------------------------------------------------------------
    // About
    // ---------------------------------------------------------------------

    private fun bindAboutPreferences() {
        findPreference<Preference>(KEY_ABOUT_VERSION)?.summary = appVersion()
        findPreference<Preference>(KEY_ABOUT_SOURCE)?.setOnPreferenceClickListener {
            openUrl(getString(R.string.source_url))
            true
        }
        findPreference<Preference>(KEY_ABOUT_LICENSE)?.setOnPreferenceClickListener {
            showLicenseDialog()
            true
        }
        findPreference<Preference>(KEY_ABOUT_ORIGINAL)?.setOnPreferenceClickListener {
            openUrl(getString(R.string.original_app_url))
            true
        }
    }

    private fun appVersion(): String = try {
        requireContext().packageManager
            .getPackageInfo(requireContext().packageName, 0).versionName ?: "?"
    } catch (_: Exception) {
        "?"
    }

    private fun openUrl(url: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(requireContext(), R.string.error_no_browser, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLicenseDialog() {
        val text = """
            ${getString(R.string.app_name)} ${appVersion()}

            Copyright (c) 2026 The Web Search Customizer OSS contributors

            Permission is hereby granted, free of charge, to any person obtaining a copy
            of this software and associated documentation files (the "Software"), to deal
            in the Software without restriction, including without limitation the rights
            to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
            copies of the Software, and to permit persons to whom the Software is
            furnished to do so, subject to the following conditions:

            The above copyright notice and this permission notice shall be included in
            all copies or substantial portions of the Software.

            THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
            IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
            FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
            AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
            LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
            OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
            THE SOFTWARE.
        """.trimIndent()
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.pref_title_license))
            .setMessage(text)
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }

    private companion object {
        const val KEY_ENGINE_TEST = "engine_test"
        const val KEY_ABOUT_VERSION = "about_version"
        const val KEY_ABOUT_SOURCE = "about_source"
        const val KEY_ABOUT_LICENSE = "about_license"
        const val KEY_ABOUT_ORIGINAL = "about_original"
        const val RECREATE_DELAY_MS = 150L
        val HTTPS_SCHEME: Pattern = Pattern.compile("https://", Pattern.CASE_INSENSITIVE)
    }
}
