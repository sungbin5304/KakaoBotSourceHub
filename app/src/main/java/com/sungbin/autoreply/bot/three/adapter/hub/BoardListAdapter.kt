package com.sungbin.autoreply.bot.three.adapter.hub

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.sungbin.autoreply.bot.three.R
import com.sungbin.autoreply.bot.three.dto.hub.BoardListItem
import com.sungbin.autoreply.bot.three.view.hub.activity.PostViewActivity
import com.sungbin.sungbintool.ToastUtils

class BoardListAdapter(private val list: ArrayList<BoardListItem>?,
                       private val good: ArrayList<String>?,
                       private val bad: ArrayList<String>?,
                       private val act: Activity?) :
    RecyclerView.Adapter<BoardListAdapter.BoardViewHolder>() {

    private var ctx: Context? = null

    inner class BoardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var title: TextView = view.findViewById(R.id.board_title)
        var desc: TextView = view.findViewById(R.id.board_desc)
        var good_count: TextView = view.findViewById(R.id.board_good_count)
        var bad_count: TextView = view.findViewById(R.id.board_bad_count)
        var view: CardView = view.findViewById(R.id.board_view)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): BoardViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.view_board_list, viewGroup, false)
        ctx = viewGroup.context
        return BoardViewHolder(view)
    }

    override fun onBindViewHolder(@NonNull viewholder: BoardViewHolder, position: Int) {
        val title = list!![position].title
        val desc = list[position].desc
        val good_count = list[position].good_count
        val bad_count = list[position].bad_count
        val uuid = list[position].uuid

        viewholder.title.text = title
        viewholder.desc.text = desc
        viewholder.good_count.text = good_count.toString()
        viewholder.bad_count.text = bad_count.toString()

        if(good!!.contains(uuid)) viewholder.good_count.setTypeface(null, Typeface.BOLD)
        if(bad!!.contains(uuid)) viewholder.bad_count.setTypeface(null, Typeface.BOLD)

        viewholder.view.setOnClickListener {
            ToastUtils.show(ctx!!,
                act!!.getString(R.string.string_loading),
                ToastUtils.SHORT, ToastUtils.INFO
            )
            ctx!!.startActivity(Intent(ctx, PostViewActivity::class.java)
                .putExtra("uuid", uuid)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
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

    fun getItem(position: Int): BoardListItem {
        return list!![position]
    }
}
