/*
 * Web Search Customizer (OSS edition)
 * Copyright (c) 2026 The Web Search Customizer OSS contributors
 *
 * SPDX-License-Identifier: MIT
 */
package io.github.websearchcustomizer

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast

/**
 * Transparent pass-through for https links.
 *
 * Some launchers, assistants and OEM ROMs only offer "Web search" style
 * delegation to apps that also declare themselves able to handle links.
 * This activity preserves that capability of the original app, but instead
 * of blindly re-firing the intent (which could loop back onto itself),
 * it forwards the link to another installed handler.
 *
 * The intent filter intentionally does NOT declare CATEGORY_DEFAULT or
 * CATEGORY_BROWSABLE, so this app can never be selected as the system
 * default browser and will only run when explicitly targeted.
 */
class LinkProxyActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val data = intent?.data
        if (data != null) {
            forward(data)
        }
        finish()
    }

    private fun forward(data: Uri) {
        val view = Intent(Intent.ACTION_VIEW, data)
        val pm = packageManager

        // Prefer the user's default handler, unless it is this app itself.
        var target: String? = try {
            pm.resolveActivity(view, PackageManager.MATCH_DEFAULT_ONLY)
                ?.activityInfo?.packageName
        } catch (_: Exception) {
            null
        }
        if (target == packageName) {
            target = try {
                pm.queryIntentActivities(view, 0)
                    .firstOrNull { it.activityInfo.packageName != packageName }
                    ?.activityInfo?.packageName
            } catch (_: Exception) {
                null
            }
        }

        if (target == null) {
            Toast.makeText(this, R.string.error_no_browser, Toast.LENGTH_SHORT).show()
            return
        }

        view.setPackage(target)
        try {
            startActivity(view)
        } catch (_: Exception) {
            Toast.makeText(this, R.string.error_no_browser, Toast.LENGTH_SHORT).show()
        }
    }
}
