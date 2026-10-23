package com.deon.dnstoggle

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings

object DnsControl {
    private const val PREFS = "dnstoggle"
    private const val KEY_HOST = "hostname"
    const val DEFAULT_HOST = "dns.adguard-dns.com"

    // Current Private DNS mode: "off", "opportunistic", "hostname", or null
    fun currentMode(ctx: Context): String? =
        Settings.Global.getString(ctx.contentResolver, "private_dns_mode")

    fun currentSpecifier(ctx: Context): String? =
        Settings.Global.getString(ctx.contentResolver, "private_dns_specifier")

    fun isOn(ctx: Context): Boolean = currentMode(ctx) == "hostname"

    fun savedHost(ctx: Context): String {
        val prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        var host = prefs.getString(KEY_HOST, null)
        if (host.isNullOrBlank()) {
            // First run: adopt whatever hostname the phone already uses.
            host = currentSpecifier(ctx)?.takeIf { it.isNotBlank() } ?: DEFAULT_HOST
            prefs.edit().putString(KEY_HOST, host).apply()
        }
        return host
    }

    fun saveHost(ctx: Context, host: String) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString(KEY_HOST, host.trim()).apply()
    }

    fun hasPermission(ctx: Context): Boolean =
        ctx.checkSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS) ==
            PackageManager.PERMISSION_GRANTED

    /** Returns true if the write succeeded. */
    fun setOn(ctx: Context, on: Boolean): Boolean {
        return try {
            val cr = ctx.contentResolver
            if (on) {
                Settings.Global.putString(cr, "private_dns_mode", "hostname")
                Settings.Global.putString(cr, "private_dns_specifier", savedHost(ctx))
            } else {
                Settings.Global.putString(cr, "private_dns_mode", "off")
            }
            true
        } catch (e: SecurityException) {
            false
        }
    }

    fun toggle(ctx: Context): Boolean {
        val target = !isOn(ctx)
        val ok = setOn(ctx, target)
        if (ok) refreshSurfaces(ctx)
        return ok
    }

    fun refreshSurfaces(ctx: Context) {
        BaseDnsWidget.updateAll(ctx)
        DnsTile.requestUpdate(ctx)
    }
}
