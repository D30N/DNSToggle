package com.deon.dnstoggle

import android.content.ComponentName
import android.content.Context
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

class DnsTile : TileService() {
    override fun onStartListening() {
        super.onStartListening()
        syncTile()
    }

    override fun onClick() {
        super.onClick()
        DnsControl.toggle(this)
        syncTile()
    }

    private fun syncTile() {
        val tile = qsTile ?: return
        val on = DnsControl.isOn(this)
        tile.state = if (on) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        tile.subtitle = if (on) DnsControl.savedHost(this) else "Off"
        tile.updateTile()
    }

    companion object {
        fun requestUpdate(ctx: Context) {
            try {
                TileService.requestListeningState(
                    ctx, ComponentName(ctx, DnsTile::class.java)
                )
            } catch (e: Exception) {
                // Tile not added to the QS panel yet — nothing to update.
            }
        }
    }
}
