package com.sungbin.autoreply.bot.three.view.chat.viewholder.dialog

import android.view.View
import com.stfalcon.chatkit.dialogs.DialogsListAdapter.DialogViewHolder
import com.sungbin.autoreply.bot.three.R
import com.sungbin.autoreply.bot.three.dto.chat.model.Dialog

class DialogViewHolder(itemView: View) : DialogViewHolder<Dialog>(itemView) {
    private val onlineIndicator: View = itemView.findViewById(R.id.onlineIndicator)
    override fun onBind(dialog: Dialog) {
        super.onBind(dialog)
        if (dialog.users.size > 1) {
            onlineIndicator.visibility = View.GONE
        } else {
            val isOnline = true
            onlineIndicator.visibility = View.VISIBLE
            if (isOnline) {
                onlineIndicator.setBackgroundResource(R.drawable.shape_bubble_online)
            } else {
                onlineIndicator.setBackgroundResource(R.drawable.shape_bubble_offline)
            }
        }
    }

}