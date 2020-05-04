package com.sungbin.autoreply.bot.three.view.chat

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.messages.MessageHolders
import com.stfalcon.chatkit.messages.MessageInput
import com.stfalcon.chatkit.messages.MessageInput.TypingListener
import com.stfalcon.chatkit.messages.MessagesList
import com.stfalcon.chatkit.messages.MessagesListAdapter
import com.stfalcon.chatkit.utils.DateFormatter
import com.sungbin.autoreply.bot.three.R
import com.sungbin.autoreply.bot.three.adapter.PhotoListAdapter
import com.sungbin.autoreply.bot.three.dto.chat.Content
import com.sungbin.autoreply.bot.three.dto.chat.ContentType
import com.sungbin.autoreply.bot.three.dto.chat.MessageState
import com.sungbin.autoreply.bot.three.dto.chat.item.MessageItem
import com.sungbin.autoreply.bot.three.dto.chat.item.UserItem
import com.sungbin.autoreply.bot.three.dto.chat.model.Dialog
import com.sungbin.autoreply.bot.three.dto.chat.model.Message
import com.sungbin.autoreply.bot.three.dto.chat.model.User
import com.sungbin.autoreply.bot.three.utils.chat.ChatModuleUtils
import com.sungbin.autoreply.bot.three.utils.ui.ImageUtils
import com.sungbin.autoreply.bot.three.view.chat.viewholder.message.incoming.IncomingImageMessageViewHolder
import com.sungbin.autoreply.bot.three.view.chat.viewholder.message.incoming.IncomingTextMessageViewHolder
import com.sungbin.autoreply.bot.three.view.chat.viewholder.message.outcoming.OutcomingImageMessageViewHolder
import com.sungbin.autoreply.bot.three.view.chat.viewholder.message.outcoming.OutcomingTextMessageViewHolder
import com.sungbin.sungbintool.ToastUtils
import com.sungbin.sungbintool.Utils
import gun0912.tedimagepicker.builder.TedImagePicker
import gun0912.tedimagepicker.builder.type.MediaType
import kotlinx.android.synthetic.main.activity_custom_holder_messages.*
import java.io.File
import java.net.URLDecoder
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log10
import kotlin.math.pow


@Suppress("DEPRECATION")
class MessagesActivity : AppCompatActivity(),
    MessagesListAdapter.SelectionListener,
    MessagesListAdapter.OnMessageLongClickListener<Message?>, MessageInput.InputListener,
    MessageInput.AttachmentsListener, DateFormatter.Formatter {

    private var reference = FirebaseDatabase.getInstance().reference
    private var messagesAdapter: MessagesListAdapter<Message?>? = null
    private var menu: Menu? = null
    private var selectionCount = 0
    private var lastLoadedDate: Date? = null
    private var dialog: Dialog? = null
    private var deviceId: String? = null
    private var lastMessage = ""
    private var isAutoScroll = true
    private var lastMessageId = 0
    private var rvPhoto: RecyclerView? = null
    private var rlAttachment: FrameLayout? = null
    private var inputLayout: MessageInput? = null
    private var isAnimatied = false
    private var ivCode: ImageView? = null
    private var ivPhoto: ImageView? = null
    private var ivVideo: ImageView? = null
    private var imm: InputMethodManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_holder_messages)

        ivCode = iv_code
        ivVideo = iv_video
        rvPhoto = rv_photos
        inputLayout = input
        ivPhoto = iv_picture
        rlAttachment = rl_attachment
        deviceId = ChatModuleUtils.getDeviceId(applicationContext)
        dialog = ChatModuleUtils.getDialog(intent.getStringExtra("dialogId")!!)
        imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        supportActionBar!!.title = dialog!!.dialogName
        reference = reference.child("Chat").child("Messages").child(dialog!!.id)

        val messagesList = findViewById<MessagesList>(R.id.messagesList)
        val input = findViewById<MessageInput>(R.id.input)
        val payload = IncomingTextMessageViewHolder.Payload()
        val imageLoader = ImageLoader { imageView: ImageView?, url: String?, _: Any? ->
                ImageUtils.set(url!!, imageView!!, this@MessagesActivity)
            }
        val holdersConfig = MessageHolders()
            .setIncomingTextConfig(
                IncomingTextMessageViewHolder::class.java,
                R.layout.item_incoming_text_message,
                payload
            )
            .setOutcomingTextConfig(
                OutcomingTextMessageViewHolder::class.java,
                R.layout.item_outcoming_text_message
            )
            .setIncomingImageConfig(
                IncomingImageMessageViewHolder::class.java,
                R.layout.item_incoming_content_message
            )
            .setOutcomingImageConfig(
                OutcomingImageMessageViewHolder::class.java,
                R.layout.item_outcoming_content_message
            )

        input.setInputListener(this)
        input.setAttachmentsListener(this)
        input.setTypingListener(object : TypingListener {
            override fun onStartTyping() {
                /*ToastUtils.show(applicationContext, "메시지 입력 시작",
                    ToastUtils.SHORT, ToastUtils.INFO)*/
            }
            override fun onStopTyping() {
                /*ToastUtils.show(applicationContext, "메시지 입력 종료",
                    ToastUtils.SHORT, ToastUtils.INFO)*/
            }
        })

        messagesAdapter =
            MessagesListAdapter(
                ChatModuleUtils.getDeviceId(applicationContext),
                holdersConfig,
                imageLoader
            )

        payload.avatarClickListener = object : IncomingTextMessageViewHolder.OnAvatarClickListener {
            override fun onAvatarClick() {
                ToastUtils.show(applicationContext, "Text message avatar clicked",
                    ToastUtils.SHORT, ToastUtils.SUCCESS)
            }
        }

        messagesAdapter!!.enableSelectionMode(this)
        messagesAdapter!!.setDateHeadersFormatter(this)
        messagesAdapter!!.setOnMessageLongClickListener(this)
        messagesList!!.setAdapter(messagesAdapter)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            messagesList.setOnScrollChangeListener { _, _, y, _, oldY ->
                if (y > oldY) {
                    //Down
                    isAutoScroll = true
                }
                if (y < oldY) {
                    //Up
                    isAutoScroll = false
                }
            }
        }

        reference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                try {
                    val item = dataSnapshot.getValue(MessageItem::class.java)!!

                    if(lastMessageId < item.id!!.toInt())
                        lastMessageId = item.id!!.toInt()

                    if(lastMessage == item.text){
                        return
                    }
                    else lastMessage = item.text!!

                    val messageUser = item.user!!
                    val user = User(messageUser.id!!, messageUser.name!!, messageUser.avatar!!,
                        messageUser.isOnline!!, messageUser.roomList, messageUser.friendsList)
                    val message = Message(item.id!!, item.dialogIdString!!, user, item.text!!,
                        item.createdAt!!, item.messageStatue!!, item.messageContent)
                    messagesAdapter!!.addToStart(message, isAutoScroll)
                }
                catch (e: Exception) {
                    Utils.error(applicationContext, e, "init messages")
                }

            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {

            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {

            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

        ivPhoto!!.setOnClickListener {
            TedImagePicker.with(this)
                .mediaType(MediaType.IMAGE)
                .start { uri ->
                    ToastUtils.show(applicationContext,
                        "선택된 경로 : $uri\n\n컨텐츠 공유는 파일 저정소 서버 활당량 초과로 임시 비활성화 되었습니다.",
                        ToastUtils.SHORT, ToastUtils.WARNING)
                    //addPicture(uri.toString())
                }
        }

        ivVideo!!.setOnClickListener {
            TedImagePicker.with(this)
                .mediaType(MediaType.VIDEO)
                .start { uri ->
                    ToastUtils.show(applicationContext,
                        "선택된 경로 : $uri\n\n컨텐츠 공유는 파일 저정소 서버 활당량 초과로 임시 비활성화 되었습니다.",
                        ToastUtils.SHORT, ToastUtils.WARNING)
                    //addVideo(uri.toString())
                }
        }

        val photoAdapter = PhotoListAdapter(getPathOfAllImages(-1), this)
        photoAdapter.setOnItemClickListener(object : PhotoListAdapter.OnItemClickListener {
            override fun onItemClick(imageUrl: String) {
                ToastUtils.show(applicationContext,
                    "선택된 경로 : $imageUrl\n\n컨텐츠 공유는 파일 저정소 서버 활당량 초과로 임시 비활성화 되었습니다.",
                    ToastUtils.SHORT, ToastUtils.WARNING)
                //addPicture(imageUrl)
            }
        })
        rvPhoto!!.layoutManager = GridLayoutManager(this, 3)
        rvPhoto!!.adapter = photoAdapter

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            rvPhoto!!.setOnScrollChangeListener { _, _, y, _, oldY ->
                if (y < oldY) { //Up
                    if(!isAnimatied) {
                        rlAttachment!!.visibility = View.VISIBLE
                        YoYo.with(Techniques.FadeInUp)
                            .duration(500)
                            .playOn(rlAttachment!!)
                        isAnimatied = true
                    }
                }

                if (y > oldY) { //Down
                    if(isAnimatied) {
                        YoYo.with(Techniques.FadeOutDown)
                            .withListener(object : Animator.AnimatorListener {
                                override fun onAnimationRepeat(p0: Animator?) {
                                }

                                override fun onAnimationEnd(p0: Animator?) {
                                    rlAttachment!!.visibility = View.INVISIBLE
                                }

                                override fun onAnimationCancel(p0: Animator?) {
                                }

                                override fun onAnimationStart(p0: Animator?) {
                                }
                            })
                            .duration(500)
                            .playOn(rlAttachment!!)
                        isAnimatied = false
                    }
                }
            }
        }
    }

    override fun onSubmit(input: CharSequence): Boolean {
        val myUserData = ChatModuleUtils.getUser(deviceId!!)!!
        val user = UserItem(deviceId!!, myUserData.name,
            myUserData.avatar, true, myUserData.rooms, myUserData.friends)
        val messageId = (lastMessageId + 1).toString()
        val item = MessageItem(messageId, dialog!!.id,
            user, input.toString(), Date(), MessageState.SENT, null)
        reference.child(messageId).setValue(item)
        return true
    }

    override fun onAddAttachments() {
        imm!!.hideSoftInputFromWindow(inputLayout!!.windowToken, 0)
        rvPhoto!!.visibility = View.VISIBLE
        rlAttachment!!.visibility = View.VISIBLE

        YoYo.with(Techniques.FadeOutDown)
            .withListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(p0: Animator?) {
                }

                override fun onAnimationEnd(p0: Animator?) {
                    inputLayout!!.visibility = View.GONE
                }

                override fun onAnimationCancel(p0: Animator?) {
                }

                override fun onAnimationStart(p0: Animator?) {
                }
            })
            .duration(500)
            .playOn(inputLayout)

        YoYo.with(Techniques.SlideInUp)
            .withListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(p0: Animator?) {
                }

                override fun onAnimationEnd(p0: Animator?) {
                }

                override fun onAnimationCancel(p0: Animator?) {
                }

                override fun onAnimationStart(p0: Animator?) {
                }
            })
            .duration(500)
            .playOn(rvPhoto!!)

        YoYo.with(Techniques.SlideInUp)
            .withListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(p0: Animator?) {
                }

                override fun onAnimationEnd(p0: Animator?) {
                }

                override fun onAnimationCancel(p0: Animator?) {
                }

                override fun onAnimationStart(p0: Animator?) {
                    YoYo.with(Techniques.Bounce)
                        .duration(1000)
                        .playOn(ivCode!!)
                    YoYo.with(Techniques.Bounce)
                        .duration(1250)
                        .playOn(ivPhoto!!)
                    YoYo.with(Techniques.Bounce)
                        .duration(1500)
                        .playOn(ivVideo!!)
                }
            })
            .duration(500)
            .playOn(rlAttachment!!)
        isAnimatied = true
    }

    @Suppress("NAME_SHADOWING")
    private fun addVideo(linkString: String){
        val link = URLDecoder.decode(linkString.replaceFirst("file:///", ""), "UTF-8")
        val pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.progressHelper.barColor = ContextCompat.getColor(applicationContext,
            R.color.colorAccent)
        pDialog.titleText = getString(R.string.uploading_image)
        pDialog.setCancelable(false)
        pDialog.show()

        val myUserData = ChatModuleUtils.getUser(deviceId!!)!!
        val user = UserItem(deviceId!!, myUserData.name, myUserData.avatar, true,
            myUserData.rooms, myUserData.friends)
        val file = Uri.fromFile(File(link))
        val storageRef = FirebaseStorage.getInstance().reference
        val messageId = (lastMessageId + 1).toString()
        val videoRef = storageRef.child("Chat/${dialog!!.id}/Video/$messageId.mp4")
        val videoUploadTask = videoRef.putFile(file)
        videoUploadTask.addOnFailureListener {
            val item = MessageItem(messageId, dialog!!.id,
                user, "비디오 업로드에 실패했습니다.\n\n${it.message}",
                Date(), MessageState.SENT,
                null
            )
            reference.child(messageId).setValue(item)
            pDialog.cancel()
        }.addOnProgressListener { taskSnapshot ->
            pDialog.contentText = "${getFileSize(taskSnapshot.bytesTransferred.toInt())} / " +
                    getFileSize(taskSnapshot.totalByteCount.toInt())
        }.addOnSuccessListener {
            videoRef.downloadUrl.addOnSuccessListener {
                val item = MessageItem(messageId, dialog!!.id,
                    user, file.lastPathSegment, Date(), MessageState.SENT,
                    Content(it.toString(), ContentType.VIDEO)
                )
                reference.child(messageId).setValue(item)
                pDialog.cancel()
            }
            videoRef.downloadUrl.addOnFailureListener {
                val item = MessageItem(messageId, dialog!!.id,
                    user, "비디오 다운로드 링크 추출에 실패했습니다.\n\n${it.message}",
                    Date(), MessageState.SENT,
                    null
                )
                reference.child(messageId).setValue(item)
                pDialog.cancel()
            }
        }

        doAnimated()
    }

    private fun addPicture(linkString: String){
        val link = URLDecoder.decode(linkString.replaceFirst("file:///", ""), "UTF-8")
        Log.d("video link", link)
        val pDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.progressHelper.barColor = ContextCompat.getColor(applicationContext,
            R.color.colorAccent)
        pDialog.titleText = getString(R.string.uploading_image)
        pDialog.setCancelable(false)
        pDialog.show()

        val myUserData = ChatModuleUtils.getUser(deviceId!!)!!
        val user = UserItem(deviceId!!, myUserData.name, myUserData.avatar, true,
            myUserData.rooms, myUserData.friends)
        val file = Uri.fromFile(File(link))
        val storageRef = FirebaseStorage.getInstance().reference
        val messageId = (lastMessageId + 1).toString()
        var prefix = "jpg"
        if(linkString.toLowerCase(Locale.getDefault()).contains(".gif")) prefix = "gif"
        val riversRef = storageRef.child("Chat/${dialog!!.id}/Picture/$messageId.$prefix")
        val uploadTask = riversRef.putFile(file)
        uploadTask.addOnFailureListener {
            val exception = it as StorageException
            val item = MessageItem(messageId, dialog!!.id,
                user, "이미지 업로드에 실패했습니다.\n\n${exception.errorCode}",
                Date(), MessageState.SENT,
                null
            )
            reference.child(messageId).setValue(item)
            pDialog.cancel()
        }.addOnProgressListener { taskSnapshot ->
            pDialog.contentText = "${getFileSize(taskSnapshot.bytesTransferred.toInt())} / " +
                    getFileSize(taskSnapshot.totalByteCount.toInt())
        }.addOnSuccessListener {
            riversRef.downloadUrl.addOnSuccessListener {
                val item = MessageItem(messageId, dialog!!.id,
                    user, file.lastPathSegment, Date(), MessageState.SENT,
                    Content(it.toString(), ContentType.IMAGE)
                )
                reference.child(messageId).setValue(item)
                pDialog.cancel()
            }
            riversRef.downloadUrl.addOnFailureListener {
                val exception = it as StorageException
                val item = MessageItem(messageId, dialog!!.id,
                    user, "이미지 다운로드 링크 추출에 실패했습니다.\n\n${exception.message}",
                    Date(), MessageState.SENT,
                    null
                )
                reference.child(messageId).setValue(item)
                pDialog.cancel()
            }
        }

        doAnimated()
    }

    fun getFileSize(size: Int): String {
        if (size <= 0) return "0"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups =
            (log10(size.toDouble()) / log10(1000.0)).toInt()
        return DecimalFormat("#,##0.#").format(
            size / 1000.0.pow(digitGroups.toDouble())
        ).toString() + " " + units[digitGroups]
    }

    private fun doAnimated(){
        inputLayout!!.visibility = View.VISIBLE
        YoYo.with(Techniques.SlideInUp)
            .duration(500)
            .playOn(inputLayout)

        YoYo.with(Techniques.FadeOutDown)
            .withListener(object : Animator.AnimatorListener{
                override fun onAnimationRepeat(p0: Animator?) {
                }

                override fun onAnimationEnd(p0: Animator?) {
                    rvPhoto!!.visibility = View.INVISIBLE
                }

                override fun onAnimationCancel(p0: Animator?) {
                }

                override fun onAnimationStart(p0: Animator?) {
                }
            })
            .duration(500)
            .playOn(rvPhoto!!)

        if(isAnimatied){
            isAnimatied = false
            YoYo.with(Techniques.FadeOutDown)
                .withListener(object : Animator.AnimatorListener{
                    override fun onAnimationRepeat(p0: Animator?) {
                    }

                    override fun onAnimationEnd(p0: Animator?) {
                        rlAttachment!!.visibility = View.INVISIBLE
                    }

                    override fun onAnimationCancel(p0: Animator?) {
                    }

                    override fun onAnimationStart(p0: Animator?) {
                    }
                })
                .duration(300)
                .playOn(rlAttachment!!)
        }
    }

    override fun onMessageLongClick(message: Message?) {
        ToastUtils.show(applicationContext, "${message!!.text} 클릭됨!",
            ToastUtils.SHORT, ToastUtils.SUCCESS)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        this.menu = menu
        menuInflater.inflate(R.menu.chat_actions_menu, menu)
        onSelectionChanged(0)
        return true
    }

    override fun format(date: Date?): String? {
        return when {
            DateFormatter.isToday(date) -> {
                getString(R.string.date_header_today)
            }
            DateFormatter.isYesterday(date) -> {
                getString(R.string.date_header_yesterday)
            }
            else -> {
                DateFormatter.format(
                    date,
                    "yyyy년 M월 d일 E요일"
                )
            }
        }
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
        inputLayout!!.visibility = View.VISIBLE
        if(selectionCount == 0) {
            YoYo.with(Techniques.SlideInUp)
                .duration(500)
                .playOn(inputLayout)
        }

        YoYo.with(Techniques.FadeOutDown)
            .withListener(object : Animator.AnimatorListener{
                override fun onAnimationRepeat(p0: Animator?) {
                }

                override fun onAnimationEnd(p0: Animator?) {
                    rvPhoto!!.visibility = View.INVISIBLE
                }

                override fun onAnimationCancel(p0: Animator?) {
                }

                override fun onAnimationStart(p0: Animator?) {
                }
            })
            .duration(500)
            .playOn(rvPhoto!!)

        if(isAnimatied){
            isAnimatied = false
            YoYo.with(Techniques.FadeOutDown)
                .withListener(object : Animator.AnimatorListener{
                    override fun onAnimationRepeat(p0: Animator?) {
                    }

                    override fun onAnimationEnd(p0: Animator?) {
                        rlAttachment!!.visibility = View.INVISIBLE
                    }

                    override fun onAnimationCancel(p0: Animator?) {
                    }

                    override fun onAnimationStart(p0: Animator?) {
                    }
                })
                .duration(300)
                .playOn(rlAttachment!!)
        }
        else {
            if (selectionCount == 0 &&
                    inputLayout!!.visibility == View.VISIBLE &&
                    rvPhoto!!.visibility == View.INVISIBLE &&
                    rlAttachment!!.visibility == View.INVISIBLE) {
                super.onBackPressed()
            } else {
                messagesAdapter!!.unselectAllItems()
            }
        }
    }

    override fun onSelectionChanged(count: Int) {
        selectionCount = count
        menu!!.findItem(R.id.action_delete).isVisible = count > 0
        menu!!.findItem(R.id.action_copy).isVisible = count > 0
    }

    private val messageStringFormatter: MessagesListAdapter.Formatter<Message?>
        get() = MessagesListAdapter.Formatter { message: Message? ->
            val createdAt =
                SimpleDateFormat("MMM D일, E요일 a h:m", Locale.KOREA).format(message!!.createdAt)
            val text = message.text
            String.format(
                Locale.getDefault(), "[%s] %s: %s",
                createdAt, message.user.name, text
            )
        }

    @SuppressLint("Recycle")
    private fun getPathOfAllImages(maxCount: Int = 20): ArrayList<String> {
        var count = 0
        val result: ArrayList<String> = ArrayList()
        val uri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection =
            arrayOf(MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME)
        val cursor: Cursor? = contentResolver.query(
            uri,
            projection,
            null,
            null,
            MediaStore.MediaColumns.DATE_ADDED + " desc"
        )
        val columnIndex: Int = cursor!!.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
        while (cursor.moveToNext()) {
            if(maxCount in 1 until count) break
            val absolutePathOfImage: String = cursor.getString(columnIndex)
            if (!TextUtils.isEmpty(absolutePathOfImage)) {
                result.add(absolutePathOfImage)
            }
            count++
        }
        return result
    }
}