package com.sungbin.autoreply.bot.three.view.chat.viewholder.message.incoming

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import com.sungbin.autoreply.bot.three.R
import com.sungbin.autoreply.bot.three.dto.chat.model.Message
import com.stfalcon.chatkit.messages.MessageHolders.IncomingImageMessageViewHolder
import com.sungbin.autoreply.bot.three.view.activity.imageview.ui.activity.DetailActivity
import net.wujingchao.android.view.SimpleTagImageView

class IncomingImageMessageViewHolder(
    itemView: View?,
    payload: Any?
) : IncomingImageMessageViewHolder<Message>(
    itemView,
    payload
) {
    private val imageView: SimpleTagImageView = itemView!!.findViewById(R.id.image)
    private val onlineIndicator: View = itemView!!.findViewById(R.id.onlineIndicator)
    private val context = itemView!!.context
    override fun onBind(message: Message) {
        super.onBind(message)
        val isOnline = message.user.online
        if (isOnline) {
            onlineIndicator.setBackgroundResource(R.drawable.shape_bubble_online)
        } else {
            onlineIndicator.setBackgroundResource(R.drawable.shape_bubble_offline)
        }
        if(message.content!!.url.toString().contains(".gif")) imageView.tagEnable = true

        imageView.setOnClickListener {
            val transitionName = context.getString(R.string.transition_name)
            val intent = Intent(context, DetailActivity::class.java).putExtra("image", message.content!!.url.toString())
                .putExtra("name", message.user.name).putExtra("avatar", message.user.avatar)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(context as Activity,
                imageView, transitionName)
            context.startActivity(intent, options.toBundle())
        }
    }

}