package com.sungbin.autoreply.bot.three.chat.custom

import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.messages.MessagesListAdapter
import com.sungbin.autoreply.bot.three.R
import com.sungbin.autoreply.bot.three.chat.custom.DemoMessagesActivity
import com.sungbin.autoreply.bot.three.dto.model.Message
import com.sungbin.autoreply.bot.three.fixtures.MessagesFixtures
import java.text.SimpleDateFormat
import java.util.*

abstract class DemoMessagesActivity : AppCompatActivity(),
    MessagesListAdapter.SelectionListener, MessagesListAdapter.OnLoadMoreListener {
    protected val senderId = "0"
    protected var imageLoader: ImageLoader? = null
    protected var messagesAdapter: MessagesListAdapter<Message?>? =
        null
    private var menu: Menu? = null
    private var selectionCount = 0
    private var lastLoadedDate: Date? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageLoader =
            ImageLoader { imageView: ImageView?, url: String?, payload: Any? ->
                Glide.with(this@DemoMessagesActivity).load(url).into(imageView!!)
            }
    }

    override fun onStart() {
        super.onStart()
        messagesAdapter!!.addToStart(MessagesFixtures.textMessage, true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        this.menu = menu
        menuInflater.inflate(R.menu.chat_actions_menu, menu)
        onSelectionChanged(0)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete -> messagesAdapter!!.deleteSelectedMessages()
            R.id.action_copy -> messagesAdapter!!.copySelectedMessagesText(
                this,
                messageStringFormatter,
                true
            )
        }
        return true
    }

    override fun onBackPressed() {
        if (selectionCount == 0) {
            super.onBackPressed()
        } else {
            messagesAdapter!!.unselectAllItems()
        }
    }

    override fun onLoadMore(page: Int, totalItemsCount: Int) {
        if (totalItemsCount < TOTAL_MESSAGES_COUNT) {
            loadMessages()
        }
    }

    override fun onSelectionChanged(count: Int) {
        selectionCount = count
        menu!!.findItem(R.id.action_delete).isVisible = count > 0
        menu!!.findItem(R.id.action_copy).isVisible = count > 0
    }

    protected fun loadMessages() {
        Handler().postDelayed({
            val messages: ArrayList<Message> =
                MessagesFixtures.getMessages(lastLoadedDate)
            lastLoadedDate = messages[messages.size - 1].createdAt
            messagesAdapter!!.addToEnd(messages as List<Message?>?, false)
        }, 1000)
    }

    private val messageStringFormatter: MessagesListAdapter.Formatter<Message?>
        private get() = MessagesListAdapter.Formatter { message: Message? ->
            val createdAt =
                SimpleDateFormat("MMM d, EEE 'at' h:mm a", Locale.getDefault())
                    .format(message!!.createdAt)
            var text = message.text
            if (text == null) text = "[attachment]"
            String.format(
                Locale.getDefault(), "%s: %s (%s)",
                message.user.name, text, createdAt
            )
        }

    companion object {
        private const val TOTAL_MESSAGES_COUNT = 100
    }
}