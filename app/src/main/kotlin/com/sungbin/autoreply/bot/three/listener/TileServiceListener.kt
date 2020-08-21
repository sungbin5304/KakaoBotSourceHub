package com.sungbin.autoreply.bot.three.listener

import android.annotation.TargetApi
import android.app.AlertDialog
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.sungbin.autoreply.bot.three.R
import com.sungbin.autoreply.bot.three.utils.bot.BotNotificationManager
import com.sungbin.sungbintool.DataUtils

@TargetApi(24)
class TileServiceListener : TileService() {
    override fun onClick() {
        val tile = qsTile
        val tileState = tile.state

        if (tileState == Tile.STATE_INACTIVE) { //버튼 눌렀을때 활성화 되는 경우 발동
            // 보여줄 다이얼로그 생성
            val dialog = AlertDialog.Builder(this)
                .setTitle(getString(R.string.app_name))
                .setMessage("봇이 켜집니다.")
                .setPositiveButton("닫기") { _, _ ->
                    DataUtils.saveData(applicationContext, "BotOn", "true")
                    BotNotificationManager.create(applicationContext)
                }
                .create()
            // 다이얼로그 호출
            showDialog(dialog)
        }

        if (tileState == Tile.STATE_ACTIVE) { //버튼 눌렀을때 비활성화 되는 경우 발동
            // 보여줄 다이얼로그 생성
            val dialog = AlertDialog.Builder(this)
                .setTitle(getString(R.string.app_name))
                .setMessage("봇이 꺼집니다.")
                .setPositiveButton("닫기") { _, _ ->
                    DataUtils.saveData(applicationContext, "BotOn", "false")
                    BotNotificationManager.delete(applicationContext)
                }.create()
            // 다이얼로그 호출
            showDialog(dialog)
        }

        if (tileState != Tile.STATE_UNAVAILABLE) {
            tile.state =
                if (tileState == Tile.STATE_ACTIVE) Tile.STATE_INACTIVE else Tile.STATE_ACTIVE
            tile.updateTile()
        }
    }

    override fun onTileAdded() {
        val tile = qsTile
        tile.state = if (DataUtils.readData(applicationContext, "BotOn", "false").toBoolean()) {
            Tile.STATE_ACTIVE
        } else Tile.STATE_INACTIVE
        tile.updateTile()
    }
}