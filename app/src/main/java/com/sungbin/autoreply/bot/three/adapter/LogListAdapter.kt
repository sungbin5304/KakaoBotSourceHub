package com.sungbin.autoreply.bot.three.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.sungbin.autoreply.bot.three.R
import com.sungbin.autoreply.bot.three.dto.LogListItem

class LogListAdapter (private val list: ArrayList<LogListItem>?, private val act: Activity) :
    RecyclerView.Adapter<LogListAdapter.LogListViewHolder>() {

    private var ctx: Context? = null

    inner class LogListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var title: TextView = view.findViewById(R.id.tv_name)
        var desc: TextView = view.findViewById(R.id.tv_desc)
        var type: ImageView = view.findViewById(R.id.iv_icon)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): LogListViewHolder {
        ctx = viewGroup.context
        val view = LayoutInflater.from(ctx).inflate(R.layout.layout_log, viewGroup, false)
        return LogListViewHolder(view)
    }

    override fun onBindViewHolder(@NonNull viewholder: LogListViewHolder, position: Int) {
        val name = list!![position].name
        val type = list[position].type
        val desc = list[position].desc
        val script = list[position].script

        viewholder.title.text = name
        viewholder.desc.text = desc
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

    fun getItem(position: Int): LogListItem {
        return list!![position]
    }

}
