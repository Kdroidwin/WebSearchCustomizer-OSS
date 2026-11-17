/*
 * Web Search Customizer (OSS edition)
 * Copyright (c) 2026 The Web Search Customizer OSS contributors
 *
 * SPDX-License-Identifier: MIT
 */
package io.github.websearchcustomizer

import android.app.Activity
import android.app.SearchManager
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.preference.PreferenceManager
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

/**
 * Invisible activity that receives search requests from other apps and
 * opens them in the user's configured search engine.
 *
 * Handled intents:
 * - [Intent.ACTION_WEB_SEARCH] with a "query" string extra (the "Web search"
 *   context menu action used by many apps, WebView and Chrome Custom Tabs)
 * - [Intent.ACTION_SEND] with text/plain (share text straight into a search)
 */
class SearchActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent
        val query = when {
            intent?.hasExtra(SearchManager.QUERY) == true ->
                intent.getStringExtra(SearchManager.QUERY)
            intent?.hasExtra(Intent.EXTRA_TEXT) == true ->
                intent.getStringExtra(Intent.EXTRA_TEXT)
            else -> null
        }

        if (!query.isNullOrBlank()) {
            val target = browserIntent(buildUrl(query))
            if (target == null) {
                Toast.makeText(this, R.string.error_generic, Toast.LENGTH_SHORT).show()
            } else {
                try {
                    startActivity(target)
                } catch (_: ActivityNotFoundException) {
                    Toast.makeText(this, R.string.error_no_browser, Toast.LENGTH_SHORT).show()
                } catch (_: Exception) {
                    Toast.makeText(this, R.string.error_generic, Toast.LENGTH_SHORT).show()
                }
            }
        }
        finish()
    }

    /** Builds the final search URL from the configured engine template. */
    private fun buildUrl(query: String): String {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        var template = prefs.getString(Prefs.KEY_ENGINE, Prefs.DEFAULT_ENGINE)
            ?: Prefs.DEFAULT_ENGINE
        if (template == Prefs.ENGINE_CUSTOM) {
            template = prefs.getString(Prefs.KEY_ENGINE_CUSTOM, Prefs.DEFAULT_ENGINE_CUSTOM)
                ?: Prefs.DEFAULT_ENGINE_CUSTOM
        }

        val encoded = try {
            URLEncoder.encode(query, "UTF-8").replace("+", "%20")
        } catch (_: UnsupportedEncodingException) {
            query
        }

        return when {
            template.contains(PLACEHOLDER) -> template.replace(PLACEHOLDER, encoded)
            template.contains(PLACEHOLDER_ALT) -> template.replace(PLACEHOLDER_ALT, encoded)
            else -> template + encoded
        }
    }

    private fun browserIntent(url: String): Intent? {
        val uri = Uri.parse(url)
        val scheme = uri.scheme
        val isWebLink = scheme.equals("http", ignoreCase = true) ||
            scheme.equals("https", ignoreCase = true)
        return if (isWebLink) Intent(Intent.ACTION_VIEW, uri) else null
    }

    private companion object {
        const val PLACEHOLDER = "%s"
        const val PLACEHOLDER_ALT = "{query}"
    }
}
