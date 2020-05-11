package com.sungbin.autoreply.bot.three.adapter.apps


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.sungbin.autoreply.bot.three.R
import com.sungbin.autoreply.bot.three.dto.apps.AppInfo

/**
 * Created by SungBin on 2020-05-10.
 */

class AppListAdapter (private val list: ArrayList<AppInfo>) :
    RecyclerView.Adapter<AppListAdapter.AppListViewHolder>() {

    private var listener: OnAppClickListener? = null

    interface OnAppClickListener {
        fun onAppClick(packageString: String)
    }

    fun setOnAppClickListener(listener: OnAppClickListener) {
        this.listener = listener
    }

    fun setOnAppClickListener(listener: (String) -> Unit) {
        this.listener = object : OnAppClickListener {
            override fun onAppClick(packageString: String) {
                listener(packageString)
            }
        }
    }

    inner class AppListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var icon: ImageView = view.findViewById(R.id.iv_icon)
        var name: TextView = view.findViewById(R.id.tv_name)
        var packageString: TextView = view.findViewById(R.id.tv_package)
        var layout: ConstraintLayout = view.findViewById(R.id.cl_layout)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): AppListViewHolder {
        val view = LayoutInflater
            .from(viewGroup.context)
            .inflate(R.layout.layout_app_list, viewGroup, false)
        return AppListViewHolder(view)
    }

    override fun onBindViewHolder(@NonNull viewholder: AppListViewHolder, position: Int) {
        val name = list[position].name
        val packageString = list[position].packageString
        val icon = list[position].icon

        viewholder.name.text = name
        viewholder.packageString.text = packageString
        viewholder.icon.setImageDrawable(icon)

        viewholder.layout.setOnClickListener {
            if (listener != null) {
                listener!!.onAppClick(packageString)
            }
        }

        viewholder.name.setOnClickListener {
            if (listener != null) {
                listener!!.onAppClick(packageString)
            }
        }

        viewholder.icon.setOnClickListener {
            if (listener != null) {
                listener!!.onAppClick(packageString)
            }
        }

        viewholder.packageString.setOnClickListener {
            if (listener != null) {
                listener!!.onAppClick(packageString)
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

    fun getItem(position: Int): AppInfo {
        return list[position]
    }

    fun search(name: String) {
        var item: AppInfo? = null
        for(element in list){
            if(element.name.contains(name)){
                item = element
                break
            }
            else continue
        }
        if(item != null){
            list.remove(item)
            list.add(0, item)
        }
        notifyDataSetChanged()
    }

}
