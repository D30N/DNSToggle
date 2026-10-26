package com.deon.dnstoggle

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {
    private lateinit var statusText: TextView
    private lateinit var hostField: EditText
    private lateinit var toggleBtn: Button
    private lateinit var setupCard: LinearLayout

    private val bg = Color.rgb(8, 14, 26)
    private val cardBg = Color.rgb(15, 23, 42)
    private val chipBg = Color.rgb(30, 41, 59)
    private val teal = Color.rgb(45, 212, 191)
    private val tealDarkText = Color.rgb(4, 32, 28)
    private val rose = Color.rgb(244, 63, 94)
    private val slate = Color.rgb(148, 163, 184)
    private val white = Color.rgb(226, 232, 240)

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()

    private fun rounded(color: Int, radiusDp: Int, strokeColor: Int? = null): GradientDrawable {
        val g = GradientDrawable()
        g.setColor(color)
        g.cornerRadius = dp(radiusDp).toFloat()
        if (strokeColor != null) g.setStroke(dp(1), strokeColor)
        return g
    }

    private fun modernButton(text: String, filled: Boolean): Button {
        val b = Button(this)
        b.text = text
        b.isAllCaps = false
        b.textSize = 15f
        if (filled) {
            b.background = rounded(teal, 12)
            b.setTextColor(tealDarkText)
            b.typeface = Typeface.DEFAULT_BOLD
        } else {
            b.background = rounded(chipBg, 12, Color.rgb(51, 65, 85))
            b.setTextColor(white)
        }
        b.stateListAnimator = null
        return b
    }

    private fun chip(text: String): Button {
        val b = Button(this)
        b.text = text
        b.isAllCaps = false
        b.textSize = 13f
        b.setTextColor(teal)
        b.background = rounded(Color.TRANSPARENT, 20, teal)
        b.stateListAnimator = null
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, dp(40)
        )
        lp.setMargins(0, 0, dp(8), 0)
        b.layoutParams = lp
        b.setPadding(dp(14), 0, dp(14), 0)
        return b
    }

    private fun card(): LinearLayout {
        val l = LinearLayout(this)
        l.orientation = LinearLayout.VERTICAL
        l.setPadding(dp(16), dp(16), dp(16), dp(16))
        l.background = rounded(cardBg, 16)
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        lp.setMargins(0, 0, 0, dp(14))
        l.layoutParams = lp
        return l
    }

    private fun label(text: String, size: Float, color: Int, bold: Boolean = false): TextView {
        val t = TextView(this)
        t.text = text
        t.textSize = size
        t.setTextColor(color)
        if (bold) t.typeface = Typeface.DEFAULT_BOLD
        return t
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.setBackgroundColor(bg)
        root.setPadding(dp(20), dp(28), dp(20), dp(20))

        root.addView(label("DNS Toggle", 26f, white, true))
        val sub = label("Private DNS — one tap ON / OFF", 13f, slate)
        sub.setPadding(0, dp(2), 0, dp(18))
        root.addView(sub)

        // Status + big toggle
        val statusCard = card()
        statusText = label("", 15f, white, true)
        statusCard.addView(statusText)
        toggleBtn = Button(this)
        toggleBtn.isAllCaps = false
        toggleBtn.textSize = 16f
        toggleBtn.typeface = Typeface.DEFAULT_BOLD
        toggleBtn.stateListAnimator = null
        val blp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, dp(54)
        )
        blp.setMargins(0, dp(14), 0, 0)
        toggleBtn.layoutParams = blp
        toggleBtn.setOnClickListener {
            if (!DnsControl.hasPermission(this)) {
                Toast.makeText(this, "Please finish the one-time setup below first", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (DnsControl.toggle(this)) refresh() else
                Toast.makeText(this, "Could not change DNS — check the setup", Toast.LENGTH_LONG).show()
        }
        statusCard.addView(toggleBtn)
        val openSettings = label("Open Private DNS settings instead ›", 13f, slate)
        openSettings.setPadding(0, dp(14), 0, 0)
        openSettings.setOnClickListener {
            try {
                startActivity(Intent(Settings.ACTION_SETTINGS))
            } catch (e: Exception) { /* ignore */ }
        }
        statusCard.addView(openSettings)
        root.addView(statusCard)

        // Hostname card
        val hostCard = card()
        hostCard.addView(label("DNS hostname", 14f, white, true))
        hostField = EditText(this)
        hostField.setTextColor(white)
        hostField.setHintTextColor(slate)
        hostField.hint = "dns.adguard-dns.com"
        hostField.textSize = 15f
        hostField.isSingleLine = true
        hostField.background = rounded(Color.TRANSPARENT, 10, Color.rgb(51, 65, 85))
        hostField.setPadding(dp(12), dp(12), dp(12), dp(12))
        val hlp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        hlp.setMargins(0, dp(10), 0, 0)
        hostField.layoutParams = hlp
        hostCard.addView(hostField)

        val presets = LinearLayout(this)
        presets.orientation = LinearLayout.HORIZONTAL
        for ((name, host) in listOf(
            "AdGuard" to "dns.adguard-dns.com",
            "AdGuard Family" to "family.adguard-dns.com",
            "Cloudflare" to "one.one.one.one",
            "Google" to "dns.google",
            "Quad9" to "dns.quad9.net"
        )) {
            val c = chip(name)
            c.setOnClickListener { hostField.setText(host); hostField.setSelection(host.length) }
            presets.addView(c)
        }
        val presetsScroll = HorizontalScrollView(this)
        presetsScroll.isHorizontalScrollBarEnabled = false
        presetsScroll.addView(presets)
        val plp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        plp.setMargins(0, dp(12), 0, 0)
        presetsScroll.layoutParams = plp
        hostCard.addView(presetsScroll)

        val saveBtn = modernButton("Save hostname", true)
        saveBtn.setOnClickListener {
            val h = hostField.text.toString().trim()
            if (h.isEmpty() || h.contains(" ") || h.contains("/")) {
                Toast.makeText(this, "That doesn't look like a valid hostname", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            DnsControl.saveHost(this, h)
            if (DnsControl.isOn(this)) DnsControl.setOn(this, true) // apply live
            DnsControl.refreshSurfaces(this)
            refresh()
            Toast.makeText(this, "Saved: $h", Toast.LENGTH_SHORT).show()
        }
        val slp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, dp(50)
        )
        slp.setMargins(0, dp(12), 0, 0)
        saveBtn.layoutParams = slp
        hostCard.addView(saveBtn)
        root.addView(hostCard)

        // Setup card (shown only when permission is missing)
        setupCard = card()
        setupCard.addView(label("One-time setup (needs a PC)", 14f, teal, true))
        val cmd = "adb shell pm grant com.deon.dnstoggle android.permission.WRITE_SECURE_SETTINGS"
        val steps = label(
            "1. Turn ON USB debugging on your phone (Developer options).\n" +
            "2. Connect the phone to a PC and run this command:",
            13f, white
        )
        steps.setPadding(0, dp(8), 0, 0)
        setupCard.addView(steps)
        val cmdView = label(cmd, 12f, teal)
        cmdView.setPadding(dp(8), dp(10), dp(8), 0)
        setupCard.addView(cmdView)
        val copyBtn = modernButton("Copy command", false)
        copyBtn.setOnClickListener {
            val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            cm.setPrimaryClip(ClipData.newPlainText("adb", cmd))
            Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show()
        }
        val clp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, dp(46)
        )
        clp.setMargins(0, dp(12), 0, 0)
        copyBtn.layoutParams = clp
        setupCard.addView(copyBtn)
        val note = label(
            "That's it! After that, the widget, the Quick Settings tile and the button above all work with one tap.",
            12f, slate
        )
        note.setPadding(0, dp(12), 0, 0)
        setupCard.addView(note)
        root.addView(setupCard)

        val credit = label("This app was made by Deon", 12f, teal)
        credit.paintFlags = credit.paintFlags or android.graphics.Paint.UNDERLINE_TEXT_FLAG
        credit.gravity = Gravity.CENTER
        credit.setPadding(0, dp(10), 0, dp(4))
        credit.setOnClickListener {
            try {
                startActivity(
                    Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://deepakdeon.github.io"))
                )
            } catch (e: Exception) { /* ignore */ }
        }
        root.addView(credit)

        val updateBtn = modernButton("Check for updates", false)
        updateBtn.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, dp(46)
        ).apply { setMargins(0, dp(8), 0, 0) }
        updateBtn.setOnClickListener { UpdateCheck.check(this, manual = true) }
        root.addView(updateBtn)

        val scroll = ScrollView(this)
        scroll.setBackgroundColor(bg)
        scroll.addView(root)
        setContentView(scroll)
        refresh()
        UpdateCheck.autoCheck(this)
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    private fun refresh() {
        if (!::statusText.isInitialized) return
        val on = DnsControl.isOn(this)
        val spec = DnsControl.currentSpecifier(this)
        statusText.text = if (on) "Status: ON\n${spec ?: DnsControl.savedHost(this)}"
            else "Status: OFF"
        statusText.setTextColor(if (on) teal else white)
        toggleBtn.background = rounded(if (on) rose else teal, 12)
        toggleBtn.setTextColor(if (on) Color.WHITE else tealDarkText)
        toggleBtn.text = if (on) "Turn OFF" else "Turn ON"
        if (hostField.text.toString() != DnsControl.savedHost(this) && !hostField.hasFocus()) {
            hostField.setText(DnsControl.savedHost(this))
        }
        setupCard.visibility = if (DnsControl.hasPermission(this)) View.GONE else View.VISIBLE
    }
}
