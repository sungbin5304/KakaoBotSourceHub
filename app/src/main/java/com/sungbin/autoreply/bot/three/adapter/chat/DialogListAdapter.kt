package com.sungbin.autoreply.bot.three.adapter.chat

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.dialogs.DialogsList
import com.stfalcon.chatkit.dialogs.DialogsListAdapter
import com.sungbin.autoreply.bot.three.R
import com.sungbin.autoreply.bot.three.dto.chat.model.Dialog
import com.sungbin.autoreply.bot.three.utils.ui.Glide
import com.sungbin.autoreply.bot.three.view.chat.activity.MessagesActivity
import com.sungbin.autoreply.bot.three.view.chat.viewholder.dialog.DialogViewHolder
import com.sungbin.sungbintool.ToastUtils

class DialogListAdapter constructor(
    act: Activity, chatList: ArrayList<Dialog>?,
    openChatList: ArrayList<Dialog>?,
    fabItem: ExtendedFloatingActionButton
) : PagerAdapter(), DialogsListAdapter.OnDialogClickListener<Dialog?>,
    DialogsListAdapter.OnDialogLongClickListener<Dialog?> {

    var dialogsAdapter: DialogsListAdapter<Dialog?>? = null
    var dialogsList: DialogsList? = null
    var ctx: Activity = act
    var chatItem: ArrayList<Dialog>? = chatList
    var openChatItem: ArrayList<Dialog>? = openChatList
    var fab: ExtendedFloatingActionButton = fabItem

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.view_dialog_page, container, false)
        val imageLoader = ImageLoader { imageView: ImageView?, url: String?, _: Any? ->
            Glide.set(ctx, url!!, imageView!!)
        }
        val dialogsList = view.findViewById<DialogsList>(R.id.dialogsList)

        dialogsAdapter = DialogsListAdapter(
            R.layout.item_dialog_viewholder,
            DialogViewHolder::class.java,
            imageLoader
        )

        dialogsAdapter!!.setOnDialogClickListener(this)
        dialogsAdapter!!.setOnDialogLongClickListener(this)
        dialogsList!!.setAdapter(dialogsAdapter)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            dialogsList.setOnScrollChangeListener { _, _, y, _, oldY ->
                if (y > oldY) {
                    //Down
                    fab.shrink()
                }
                if (y < oldY) {
                    //Up
                    fab.extend()
                }
            }
        }

        when (position) {
            0 -> {
                dialogsAdapter!!.setItems(chatItem as List<Dialog?>)
            }
            else -> {
                dialogsAdapter!!.setItems(openChatItem as List<Dialog?>)
            }
        }

        refresh()
        container.addView(view)
        return view
    }

    override fun onDialogClick(dialog: Dialog?) {
        ctx.startActivity(
            Intent(ctx, MessagesActivity::class.java)
                .putExtra("dialogId", dialog!!.id)
        )
    }

    override fun onDialogLongClick(dialog: Dialog?) {
        ToastUtils.show(
            ctx, "${dialog!!.dialogName} 롱클릭",
            ToastUtils.SHORT, ToastUtils.INFO
        )
    }

    override fun destroyItem(
        container: ViewGroup,
        position: Int,
        obj: Any
    ) {
        container.removeView(obj as View)
    }

    override fun getCount(): Int {
        return 2
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view === obj as View
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "채팅"
            else -> "오픈채팅"
        }
    }

    fun refresh() {
        //dialogsAdapter!!.notifyDataSetChanged()
    }
}