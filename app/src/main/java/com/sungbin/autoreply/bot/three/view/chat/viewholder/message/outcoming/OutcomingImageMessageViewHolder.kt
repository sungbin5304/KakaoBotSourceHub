package com.sungbin.autoreply.bot.three.view.chat.viewholder.message.outcoming

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import com.stfalcon.chatkit.messages.MessageHolders.OutcomingImageMessageViewHolder
import com.sungbin.autoreply.bot.three.R
import com.sungbin.autoreply.bot.three.dto.chat.model.Message
import com.sungbin.autoreply.bot.three.utils.chat.FormatUtils
import com.sungbin.autoreply.bot.three.view.ui.imageview.activity.DetailImageActivity
import com.sungbin.sungbintool.ui.TagableRoundImageView

class OutcomingImageMessageViewHolder(
    itemView: View?,
    payload: Any?
) : OutcomingImageMessageViewHolder<Message>(
    itemView,
    payload
) {
    private val imageView: TagableRoundImageView = itemView!!.findViewById(R.id.image)
    private val messageDate: TextView = itemView!!.findViewById(R.id.messageDate)
    private val context = itemView!!.context
    override fun onBind(message: Message) {
        super.onBind(message)
        messageDate.text = FormatUtils.createDate(message.createdAt)

        if (!message.content!!.url.toString().contains("jpg")) {
            val imageName = message.content!!.url.toString()
            when {
                imageName.contains("gif") -> imageView.set("gif")
                imageName.contains("mp4") -> imageView.set("mp4")
            }
        }

        imageView.setOnClickListener {
            val transitionName = context.getString(R.string.transition)
            val intent = Intent(context, DetailImageActivity::class.java)
                .putExtra("image", message.content!!.url.toString())
                .putExtra("name", message.text).putExtra("avatar", message.user.avatar)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                context as Activity,
                imageView, transitionName
            )
            context.startActivity(intent, options.toBundle())
        }
    }
}