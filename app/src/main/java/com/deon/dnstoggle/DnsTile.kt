package com.deon.dnstoggle

import android.content.ComponentName
import android.content.Context
import android.graphics.drawable.Icon
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
        tile.icon = Icon.createWithResource(
            this, if (on) R.drawable.ic_tile_on else R.drawable.ic_tile_off
        )
        tile.subtitle = if (on) "ON" else "OFF"
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
