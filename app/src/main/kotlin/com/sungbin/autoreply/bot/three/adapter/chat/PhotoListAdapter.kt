package com.sungbin.autoreply.bot.three.adapter.chat

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.sungbin.autoreply.bot.three.R
import com.sungbin.autoreply.bot.three.utils.ui.Glide

class PhotoListAdapter(private val list: ArrayList<String>, private val act: Activity) :
    RecyclerView.Adapter<PhotoListAdapter.PhotoListViewHolder>() {

    private var ctx: Context? = null

    interface OnItemClickListener {
        fun onItemClick(imageUrl: String)
    }

    private var mListener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener?) {
        mListener = listener
    }

    inner class PhotoListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image = view.findViewById<ImageView>(R.id.iv_photo)!!
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): PhotoListViewHolder {
        ctx = viewGroup.context
        val view = LayoutInflater.from(ctx).inflate(R.layout.layout_photo, viewGroup, false)
        return PhotoListViewHolder(view)
    }

    override fun onBindViewHolder(@NonNull viewholder: PhotoListViewHolder, position: Int) {
        Glide.set(ctx!!, list[position], viewholder.image)
        viewholder.image.setOnClickListener {
            if (mListener != null) {
                mListener!!.onItemClick(list[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun getItem(position: Int): String {
        return list[position]
    }

}
