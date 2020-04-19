package com.sungbin.autoreply.bot.three.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.sungbin.autoreply.bot.three.R
import com.sungbin.autoreply.bot.three.dto.bot.DebugListItem
import com.sungbin.sungbintool.Utils

class DebugListAdapter (private val list: ArrayList<DebugListItem>?, private val act: Activity) :
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
        val sender = list!![position].sender
        val gravity = list[position].gravity
        val message = list[position].message!!

        if(gravity == 0){ //나
            viewholder.sender_R.text = sender
            viewholder.msg_R.text = message
            viewholder.view_R.setOnLongClickListener {
                Utils.copy(act, message)
                return@setOnLongClickListener false
            }
        }
        else { //봇
            viewholder.content_R.visibility = View.GONE
            viewholder.content_L.visibility = View.VISIBLE

            viewholder.sender_L.text = sender
            viewholder.msg_L.text = message
            viewholder.view_L.setOnLongClickListener {
                Utils.copy(act, message)
                return@setOnLongClickListener false
            }
        }
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun getItem(position: Int): DebugListItem {
        return list!![position]
    }

}
