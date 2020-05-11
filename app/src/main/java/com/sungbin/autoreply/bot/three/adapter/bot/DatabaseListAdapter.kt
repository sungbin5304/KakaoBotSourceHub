package com.sungbin.autoreply.bot.three.adapter.bot

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.sungbin.autoreply.bot.three.R
import com.sungbin.autoreply.bot.three.dto.bot.DatabaseListItem

class DatabaseListAdapter(private val list: ArrayList<DatabaseListItem>?,
                          private val act: Activity) :
        RecyclerView.Adapter<DatabaseListAdapter.DatabaseListViewHolder>() {

    private var ctx: Context? = null

    inner class DatabaseListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var title: TextView = view.findViewById(R.id.tv_name)
        var size: TextView = view.findViewById(R.id.tv_size)
        var remove: Button = view.findViewById(R.id.btn_remove)
        var viewSource: Button = view.findViewById(R.id.btn_view)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): DatabaseListViewHolder {
        ctx = viewGroup.context
        val view = LayoutInflater.from(ctx).inflate(R.layout.layout_databases, viewGroup, false)
        return DatabaseListViewHolder(view)
    }

    override fun onBindViewHolder(@NonNull viewholder: DatabaseListViewHolder, position: Int) {
        val name = list!![position].name
        val size = list[position].size

        viewholder.title.text = name
        viewholder.size.text = size
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

    fun getItem(position: Int): DatabaseListItem {
        return list!![position]
    }

}
