package com.sungbin.kakaobot.source.hub.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.shashank.sony.fancytoastlib.FancyToast
import com.sungbin.kakaobot.source.hub.R
import com.sungbin.kakaobot.source.hub.dto.BoardListItem
import com.sungbin.kakaobot.source.hub.utils.Utils
import com.sungbin.kakaobot.source.hub.view.activity.MainActivity
import com.sungbin.kakaobot.source.hub.view.activity.PostActivity
import com.sungbin.kakaobot.source.hub.view.activity.PostViewActivity

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
            Utils.toast(ctx!!,
                act!!.getString(R.string.string_loading),
                FancyToast.LENGTH_SHORT, FancyToast.INFO)
            ctx!!.startActivity(Intent(ctx, PostViewActivity::class.java)
                .putExtra("uuid", uuid)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
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
