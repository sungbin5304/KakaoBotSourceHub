package com.sungbin.autoreply.bot.three.chat.custom

import android.view.View
import com.stfalcon.chatkit.dialogs.DialogsListAdapter.DialogViewHolder
import com.sungbin.autoreply.bot.three.R
import com.sungbin.autoreply.bot.three.dto.model.Dialog

class CustomDialogViewHolder(itemView: View) :
    DialogViewHolder<Dialog>(itemView) {
    private val onlineIndicator: View
    override fun onBind(dialog: Dialog) {
        super.onBind(dialog)
        if (dialog.users.size > 1) {
            onlineIndicator.visibility = View.GONE
        } else {
            val isOnline = dialog.users[0].isOnline
            onlineIndicator.visibility = View.VISIBLE
            if (isOnline) {
                onlineIndicator.setBackgroundResource(R.drawable.shape_bubble_online)
            } else {
                onlineIndicator.setBackgroundResource(R.drawable.shape_bubble_offline)
            }
        }
    }

    init {
        onlineIndicator = itemView.findViewById(R.id.onlineIndicator)
    }
}