package com.sungbin.autoreply.bot.three.chat.custom

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.stfalcon.chatkit.dialogs.DialogsList
import com.stfalcon.chatkit.dialogs.DialogsListAdapter
import com.sungbin.autoreply.bot.three.R
import com.sungbin.autoreply.bot.three.dto.model.Dialog
import com.sungbin.autoreply.bot.three.fixtures.DialogsFixtures
import com.sungbin.sungbintool.ToastUtils

class CustomHolderDialogsActivity : DemoDialogsActivity() {
    private var dialogsList: DialogsList? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_holder_dialogs)
        dialogsList = findViewById<View>(R.id.dialogsList) as DialogsList
        initAdapter()
    }

    override fun onDialogClick(dialog: Dialog?) {
        CustomHolderMessagesActivity.open(this)
    }

    private fun initAdapter() {
        super.dialogsAdapter =
            DialogsListAdapter(
                R.layout.item_custom_dialog_view_holder,
                CustomDialogViewHolder::class.java,
                super.imageLoader
            )
        super.dialogsAdapter!!.setItems(DialogsFixtures.dialogs as List<Dialog?>?)
        super.dialogsAdapter!!.setOnDialogClickListener(this)
        dialogsList!!.setAdapter(super.dialogsAdapter)
    }

    companion object {
        fun open(context: Context) {
            context.startActivity(Intent(context, CustomHolderDialogsActivity::class.java))
        }
    }
}