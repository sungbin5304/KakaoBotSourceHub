package com.sungbin.autoreply.bot.three.adapter.bot

import android.app.Activity
import android.content.Context
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sungbin.autoreply.bot.three.R
import com.sungbin.autoreply.bot.three.dto.bot.LogListItem
import com.sungbin.autoreply.bot.three.utils.bot.LogUtils

class LogListAdapter (private val list: ArrayList<LogListItem>?, private val act: Activity) :
    RecyclerView.Adapter<LogListAdapter.LogListViewHolder>() {

    private var ctx: Context? = null
    interface OnLogRemovedListener {
        fun onRemoved()
    }

    private var listener: OnLogRemovedListener? = null
    fun setOnLogRemovedListener(listener: OnLogRemovedListener?) {
        this.listener = listener
    }

    fun setOnDatabaseRemovedListener(listener: () -> Unit) {
        this.listener = object : OnLogRemovedListener {
            override fun onRemoved() {
                listener()
            }
        }
    }

    inner class LogListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView = view.findViewById(R.id.tv_name)
        var content: TextView = view.findViewById(R.id.tv_content)
        var time: TextView = view.findViewById(R.id.tv_time)
        var type: ImageView = view.findViewById(R.id.iv_icon)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): LogListViewHolder {
        ctx = viewGroup.context
        val view = LayoutInflater.from(ctx).inflate(R.layout.layout_log, viewGroup, false)
        return LogListViewHolder(view)
    }

    override fun onBindViewHolder(@NonNull viewholder: LogListViewHolder, position: Int) {
        val name = list!![position].name
        val type = list[position].type!!.toInt()
        val content = list[position].content
        val time = list[position].time
        val color = when(type){
            LogUtils.Type.SUCCESS -> R.color.colorGreen
            LogUtils.Type.INFO -> R.color.colorPrimary
            LogUtils.Type.ERROR -> R.color.colorPink
            else -> R.color.colorLightGray
        }

        viewholder.name.text = name
        viewholder.content.text = content
        viewholder.time.text = time
        viewholder.type.setColorFilter(ContextCompat.getColor(act, color), PorterDuff.Mode.SRC_IN)
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
