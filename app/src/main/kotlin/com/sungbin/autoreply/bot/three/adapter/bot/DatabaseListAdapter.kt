package com.sungbin.autoreply.bot.three.adapter.bot

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.sungbin.autoreply.bot.three.R
import com.sungbin.autoreply.bot.three.dto.bot.DatabaseListItem
import com.sungbin.autoreply.bot.three.utils.bot.BotPathManager.DATABASE
import com.sungbin.autoreply.bot.three.view.bot.activity.DatabaseViewActivity
import com.sungbin.sungbintool.StorageUtils
import com.sungbin.sungbintool.ToastUtils

class DatabaseListAdapter(
    private val list: ArrayList<DatabaseListItem>?,
    private val act: Activity
) :
    RecyclerView.Adapter<DatabaseListAdapter.DatabaseListViewHolder>() {

    private var ctx: Context? = null

    interface OnDatabaseRemovedListener {
        fun onRemoved()
    }

    private var listener: OnDatabaseRemovedListener? = null
    fun setOnDatabaseRemovedListener(listener: OnDatabaseRemovedListener?) {
        this.listener = listener
    }

    fun setOnDatabaseRemovedListener(listener: () -> Unit) {
        this.listener = object : OnDatabaseRemovedListener {
            override fun onRemoved() {
                listener()
            }
        }
    }

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
        viewholder.viewSource.setOnClickListener {
            act.startActivity(
                Intent(act, DatabaseViewActivity::class.java)
                    .putExtra("path", "$DATABASE/$name")
                    .putExtra("name", name)
            )
        }
        viewholder.remove.setOnClickListener {
            ToastUtils.show(
                act,
                act.getString(R.string.long_press_remove_database),
                ToastUtils.SHORT,
                ToastUtils.INFO
            )
        }
        viewholder.remove.setOnLongClickListener {
            StorageUtils.delete("$DATABASE/$name")
            ToastUtils.show(
                act,
                act.getString(R.string.removed_database).replace("{name}", name!!),
                ToastUtils.SHORT,
                ToastUtils.SUCCESS
            )
            if (listener != null) listener!!.onRemoved()
            return@setOnLongClickListener true
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

    fun getItem(position: Int): DatabaseListItem {
        return list!![position]
    }

}
