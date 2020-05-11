package com.sungbin.autoreply.bot.three.adapter.bot

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sungbin.autoreply.bot.three.R
import com.sungbin.autoreply.bot.three.dto.bot.DebugMessageItem
import com.sungbin.sungbintool.ReadMoreUtils
import com.sungbin.sungbintool.Utils

class DebugListAdapter (private val message: ArrayList<DebugMessageItem>?,
                        private val myName: String,
                        private val act: Activity) :
    RecyclerView.Adapter<DebugListAdapter.DebugListViewHolder>() {

    private var ctx: Context? = null

    inner class DebugListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var sender_R: TextView = view.findViewById(R.id.sender_view_R)
        var msg_R: TextView = view.findViewById(R.id.content_view_R)
        var view_R: androidx.cardview.widget.CardView = view.findViewById(R.id.debug_card_view_R)
        var content_R: RelativeLayout = view.findViewById(R.id.right_content_view)

        var sender_L: TextView = view.findViewById(R.id.sender_view)
        var msg_L: TextView = view.findViewById(R.id.content_view)
        var view_L: androidx.cardview.widget.CardView = view.findViewById(R.id.debug_card_view)
        var content_L: RelativeLayout = view.findViewById(R.id.left_content_view)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): DebugListViewHolder {
        ctx = viewGroup.context
        val view = LayoutInflater.from(ctx).inflate(R.layout.view_debug_items_list, viewGroup, false)
        return DebugListViewHolder(view)
    }

    override fun onBindViewHolder(@NonNull viewholder: DebugListViewHolder, position: Int) {
        if(message != null) {
            val sender = message[position].sender
            val message = message[position].message

            if (myName == sender) { //나
                viewholder.sender_R.text = sender
                ReadMoreUtils.setReadMoreLength(
                    viewholder.msg_R,
                    message,
                    500,
                    ctx!!.getString(R.string.show_all),
                    ContextCompat.getColor(ctx!!, R.color.colorPrimary)
                )
                viewholder.view_R.setOnLongClickListener {
                    Utils.copy(act, message)
                    return@setOnLongClickListener false
                }
            }
            else { //봇
                viewholder.content_R.visibility = View.GONE
                viewholder.content_L.visibility = View.VISIBLE

                viewholder.sender_L.text = sender
                ReadMoreUtils.setReadMoreLength(
                    viewholder.msg_L,
                    message,
                    500,
                    ctx!!.getString(R.string.show_all),
                    ContextCompat.getColor(ctx!!, R.color.colorPrimary)
                )
                viewholder.view_L.setOnLongClickListener {
                    Utils.copy(act, message)
                    return@setOnLongClickListener false
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return message?.size ?: 0
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun getItem(position: Int): DebugMessageItem {
        return message!![position]
    }

}
