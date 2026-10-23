package com.deon.dnstoggle

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

abstract class BaseDnsWidget : AppWidgetProvider() {
    abstract val layoutId: Int
    abstract fun face(on: Boolean): Int
    abstract val providerClass: Class<out BaseDnsWidget>

    override fun onUpdate(ctx: Context, mgr: AppWidgetManager, ids: IntArray) {
        for (id in ids) mgr.updateAppWidget(id, buildViews(ctx))
    }

    override fun onReceive(ctx: Context, intent: Intent) {
        super.onReceive(ctx, intent)
        if (intent.action == ACTION_TOGGLE) {
            DnsControl.toggle(ctx)
        }
    }

    fun buildViews(ctx: Context): RemoteViews {
        val views = RemoteViews(ctx.packageName, layoutId)
        val on = DnsControl.isOn(ctx)
        views.setImageViewResource(R.id.widget_face, face(on))
        val intent = Intent(ctx, providerClass).setAction(ACTION_TOGGLE)
        val pi = PendingIntent.getBroadcast(
            ctx, providerClass.hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_root, pi)
        return views
    }

    companion object {
        const val ACTION_TOGGLE = "com.deon.dnstoggle.WIDGET_TOGGLE"

        fun updateAll(ctx: Context) {
            val mgr = AppWidgetManager.getInstance(ctx)
            for (cls in listOf(DnsWidget::class.java, DnsWidgetRound::class.java)) {
                val ids = mgr.getAppWidgetIds(ComponentName(ctx, cls))
                if (ids.isEmpty()) continue
                val proto = cls.getDeclaredConstructor().newInstance()
                val views = proto.buildViews(ctx)
                for (id in ids) mgr.updateAppWidget(id, views)
            }
        }
    }
}

/** Square widget (icon-size, resizable). */
class DnsWidget : BaseDnsWidget() {
    override val layoutId: Int = R.layout.widget_dns
    override val providerClass = DnsWidget::class.java
    override fun face(on: Boolean): Int =
        if (on) R.drawable.widget_face_on else R.drawable.widget_face_off
}

/** Round widget (icon-size, resizable). */
class DnsWidgetRound : BaseDnsWidget() {
    override val layoutId: Int = R.layout.widget_dns_round
    override val providerClass = DnsWidgetRound::class.java
    override fun face(on: Boolean): Int =
        if (on) R.drawable.widget_face_round_on else R.drawable.widget_face_round_off
}
