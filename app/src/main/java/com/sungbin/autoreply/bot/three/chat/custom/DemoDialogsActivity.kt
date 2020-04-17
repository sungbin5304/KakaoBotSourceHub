package com.sungbin.autoreply.bot.three.chat.custom

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.dialogs.DialogsListAdapter
import com.stfalcon.chatkit.dialogs.DialogsListAdapter.OnDialogClickListener
import com.stfalcon.chatkit.dialogs.DialogsListAdapter.OnDialogLongClickListener
import com.sungbin.autoreply.bot.three.chat.custom.DemoDialogsActivity
import com.sungbin.autoreply.bot.three.dto.model.Dialog

abstract class DemoDialogsActivity : AppCompatActivity(),
    OnDialogClickListener<Dialog?>,
    OnDialogLongClickListener<Dialog> {
    protected var imageLoader: ImageLoader? = null
    protected var dialogsAdapter: DialogsListAdapter<Dialog?>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageLoader =
            ImageLoader { imageView: ImageView?, url: String?, payload: Any? ->
                Glide.with(this@DemoDialogsActivity).load(url).into(imageView!!)
            }
    }

    override fun onDialogLongClick(dialog: Dialog?) {
        AppUtils.showToast(
            this,
            dialog!!.dialogName,
            false
        )
    }
}