package com.sungbin.autoreply.bot.three.view.bot.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ClipboardManager
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.jaredrummler.materialspinner.MaterialSpinner
import com.sungbin.autoreply.bot.three.R
import com.sungbin.autoreply.bot.three.adapter.bot.DebugListAdapter
import com.sungbin.autoreply.bot.three.dto.bot.DebugMessageItem
import com.sungbin.autoreply.bot.three.listener.KakaoTalkListener
import com.sungbin.autoreply.bot.three.listener.OnSwipeListener
import com.sungbin.autoreply.bot.three.utils.Base64Utils
import com.sungbin.autoreply.bot.three.utils.RhinoUtils
import com.sungbin.autoreply.bot.three.utils.bot.DebugUtils
import com.sungbin.autoreply.bot.three.view.bot.activity.DashboardActivity
import com.sungbin.autoreply.bot.three.view.ui.bottombar.SmoothBottomBar
import com.sungbin.autoreply.bot.three.view.ui.drawerlayout.DrawerLayout
import com.sungbin.sungbintool.DataUtils
import com.sungbin.sungbintool.DialogUtils
import com.sungbin.sungbintool.StringUtils
import com.sungbin.sungbintool.ToastUtils
import com.sungbin.sungbintool.ui.TagableRoundImageView
import gun0912.tedimagepicker.builder.TedImagePicker
import kotlinx.android.synthetic.main.fragment_sandbox.*

@Suppress("DEPRECATION", "NAME_SHADOWING", "SENSELESS_COMPARISON")
class SandboxFragment constructor(private val fragmentManage: FragmentManager,
                                  private val view: Int,
                                  private val bottombar: SmoothBottomBar,
                                  private val textview: TextView,
                                  private val isTutorial: Boolean
) : Fragment() {

    private var rooms = ArrayList<String>()
    private var senders = ArrayList<String>()

    private lateinit var etRoom: EditText
    private lateinit var etInput: EditText
    private lateinit var tvGuide: TextView
    private lateinit var swEvalMode: Switch
    private lateinit var swGroupChat: Switch
    private lateinit var ibSend: ImageButton
    private lateinit var rvList: RecyclerView
    private lateinit var dlLayout: DrawerLayout
    private lateinit var msRoom: MaterialSpinner
    private lateinit var rlLayout: RelativeLayout
    private lateinit var msSender: MaterialSpinner
    private lateinit var adapter: DebugListAdapter
    private lateinit var nvNavigation: NavigationView
    private lateinit var lavSwipe: LottieAnimationView
    private lateinit var trivProfile: TagableRoundImageView
    private lateinit var clWelcome: ConstraintLayout
    private lateinit var lavWelcome: LottieAnimationView
    private lateinit var btnNext: Button

    @SuppressLint("InflateParams")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_sandbox, container, false)
        activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        rvList = view.findViewById(R.id.rv_list)
        ibSend = view.findViewById(R.id.ib_send)
        etInput = view.findViewById(R.id.et_input)
        tvGuide = view.findViewById(R.id.tv_guide)
        rlLayout = view.findViewById(R.id.rl_layout)
        lavSwipe = view.findViewById(R.id.lav_swipe)
        dlLayout = view.findViewById(R.id.dl_layout)
        nvNavigation = view.findViewById(R.id.nv_navigation)
        clWelcome = view.findViewById(R.id.cl_welcome)
        lavWelcome = view.findViewById(R.id.lav_welcome)
        btnNext = view.findViewById(R.id.btn_next)

        val headerView = LayoutInflater
            .from(context!!)
            .inflate(R.layout.header_layout_sender_list, null, false)
        etRoom = headerView.findViewById(R.id.et_room)
        msRoom = headerView.findViewById(R.id.ms_room)
        msSender = headerView.findViewById(R.id.ms_sender)
        swEvalMode = headerView.findViewById(R.id.sw_eval_mode)
        trivProfile = headerView.findViewById(R.id.triv_profile)
        swGroupChat = headerView.findViewById(R.id.sw_group_chat)

        nvNavigation.addHeaderView(headerView)

        return view
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        btn_next.setOnClickListener {
            textview.text = getString(R.string.add_bot)
            val fragmentTransaction = fragmentManage.beginTransaction()
            fragmentTransaction.replace(view,
                AddBotFragment(fragmentManage, view, bottombar, textview, isTutorial)
            ).commit()
            bottombar.setActiveItem(2)
            DashboardActivity.bottomBarIndex = 2
        }

        val isFirstOpen = DataUtils.readData(context!!, "FirstOpen", "true").toBoolean()
        if(isFirstOpen && !isTutorial){
            lavSwipe.playAnimation()
            lavSwipe.setOnTouchListener(object : OnSwipeListener(context!!) {
                override fun onSwipeLeftToRight() {
                    DataUtils.saveData(context!!, "FirstOpen", "false")
                    lavSwipe.cancelAnimation()
                    lavSwipe.setAnimation(R.raw.congratulations)
                    lavSwipe.playAnimation()
                    lavSwipe.repeatCount = 1
                    Handler().postDelayed({
                        tvGuide.visibility = View.GONE
                        lavSwipe.visibility = View.GONE
                        rlLayout.visibility = View.VISIBLE
                        dlLayout.openDrawer(GravityCompat.START)
                        DialogUtils.showOnce(
                            context!!,
                            getString(R.string.experimental_function),
                            getString(R.string.sandbox_experimental_function_description),
                            "experimental_sandbox",
                            null, false
                        )
                    }, 1000)

                }
                override fun onSwipeRightToLeft() {
                }
                override fun onSwipeBottomToTop() {
                }
                override fun onSwipeTopToBottom() {
                }
            })
        }
        else {
            if(isTutorial && isFirstOpen){
                tvGuide.visibility = View.GONE
                lavSwipe.visibility = View.GONE
                clWelcome.visibility = View.VISIBLE
                lavWelcome.playAnimation()
                btnNext.visibility = View.VISIBLE
            }
            else {
                tvGuide.visibility = View.GONE
                lavSwipe.visibility = View.GONE
                rlLayout.visibility = View.VISIBLE
            }
        }

        swEvalMode.isChecked = DataUtils.readData(context!!, "EvalMode", "false").toBoolean()
        swEvalMode.setOnCheckedChangeListener { _, boolean ->
            DataUtils.saveData(context!!, "EvalMode", boolean.toString())
        }

        swGroupChat.isChecked = DataUtils.readData(context!!, "GroupChat", "false").toBoolean()
        swGroupChat.setOnCheckedChangeListener { _, boolean ->
            DataUtils.saveData(context!!, "GroupChat", boolean.toString())
        }

        etRoom.text = StringUtils.toEditable(
            DataUtils.readData(context!!, "DebugRoom", "Debug Room")
        )
        etRoom.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(editable: Editable?) {
                DataUtils.saveData(context!!, "DebugRoom", editable.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })

        senders = DebugUtils.getSenderList()
        if(senders.isNotEmpty()){
            Glide
                .with(context!!)
                .load(
                    DebugUtils.getProfileImagePath(
                        senders[
                                DataUtils.readData(
                                    context!!,
                                    "SenderPosition",
                                    "0"
                                )
                                    .toInt()
                        ]
                    )
                )
                .into(trivProfile)
        }

        senders.add(getString(R.string.default_user))
        senders.add(getString(R.string.icon_add_sender))
        msSender.setItems(senders.toList())
        msSender.selectedIndex = DataUtils.readData(context!!, "SenderPosition", "0").toInt()
        msSender.setOnItemSelectedListener { _, _, _, item ->
            val position = senders.indexOf(item.toString())
            when (item) {
                getString(R.string.icon_add_sender) -> {
                    msSender.selectedIndex = 0
                    adapter = DebugListAdapter(
                        DebugUtils.getMessges(rooms[msRoom.selectedIndex]),
                        senders[0],
                        activity!!
                    )
                    rvList.adapter = adapter
                    adapter.notifyDataSetChanged()
                    DataUtils.saveData(context!!, "SenderPosition", "0")
                    Glide
                        .with(context!!)
                        .load(
                            DebugUtils.getProfileImagePath(
                                senders[0]
                            )
                        )
                        .into(trivProfile)
                    showSenderAddDialog()
                }
                else -> {
                    adapter = DebugListAdapter(
                        DebugUtils.getMessges(rooms[msRoom.selectedIndex]),
                        senders[position],
                        activity!!
                    )
                    rvList.adapter = adapter
                    adapter.notifyDataSetChanged()
                    DataUtils.saveData(context!!, "SenderPosition", position.toString())
                    msSender.selectedIndex = position
                    Glide
                        .with(context!!)
                        .load(
                            DebugUtils.getProfileImagePath(
                                item.toString()
                            )
                        )
                        .into(trivProfile)
                }
            }
        }

        rooms = DebugUtils.getRoomList()
        rooms.add(getString(R.string.eval_room))
        rooms.add(getString(R.string.icon_room_sender))
        msRoom.setItems(rooms.toList())
        val position = DataUtils.readData(context!!, "RoomPosition", "0").toInt()
        etRoom.text = StringUtils.toEditable(rooms[position])
        msRoom.selectedIndex = position
        msRoom.setOnItemSelectedListener { _, _, _, item ->
            val position = rooms.indexOf(item.toString())
            when (item) {
                getString(R.string.icon_room_sender) -> {
                    etRoom.text = StringUtils.toEditable(rooms[0])
                    msRoom.selectedIndex = 0
                    adapter = DebugListAdapter(
                        DebugUtils.getMessges(rooms[0]),
                        senders[msSender.selectedIndex],
                        activity!!
                    )
                    rvList.adapter = adapter
                    adapter.notifyDataSetChanged()
                    DataUtils.saveData(context!!, "RoomPosition", "0")
                    showRoomAddDialog()
                }
                else -> {
                    adapter = DebugListAdapter(
                        DebugUtils.getMessges(item.toString()),
                        senders[msSender.selectedIndex],
                        activity!!
                    )
                    rvList.adapter = adapter
                    etRoom.text = StringUtils.toEditable(item.toString())
                    adapter.notifyDataSetChanged()
                    DataUtils.saveData(context!!, "RoomPosition", position.toString())
                    msRoom.selectedIndex = position
                }
            }
        }

        rvList.layoutManager = LinearLayoutManager(context)
        adapter = DebugListAdapter(
            DebugUtils.getMessges(rooms[msRoom.selectedIndex]),
            senders[msSender.selectedIndex],
            activity!!
        )
        rvList.adapter = adapter
        adapter.notifyDataSetChanged()

        rvList.setOnTouchListener(object : OnSwipeListener(context!!) {
            override fun onSwipeLeftToRight() {
                dlLayout.openDrawer(GravityCompat.START)
            }
            override fun onSwipeRightToLeft() {
            }
            override fun onSwipeBottomToTop() {
            }
            override fun onSwipeTopToBottom() {
            }
        })

        ibSend.setOnClickListener {
            val msg = etInput.text.toString()
            var room = etRoom.text.toString()
            val sender = senders[msSender.selectedIndex]
            val isGroupChat = swGroupChat.isChecked

            if(swEvalMode.isChecked) room = getString(R.string.eval_room)

            val messageItem =
                DebugMessageItem(
                    sender,
                    msg
                )

            DebugUtils.addMessage(room, messageItem)

            if(swEvalMode.isChecked) {
                val result = RhinoUtils(context!!).runJs(msg)

                val resultItem =
                    DebugMessageItem(
                        getString(R.string.bot_name),
                        result
                    )

                DebugUtils.addMessage(room, resultItem)
            }
            else {
                KakaoTalkListener.chatHook(
                    sender, msg, room,
                    isGroupChat, null, null,
                    "com.kakao.talk", context!!,
                    true
                )
            }

            etInput.text = StringUtils.toEditable("")
            val messages = DebugUtils.getMessges(room)
            adapter.notifyDataSetChanged()
            val scrollPosition = if(messages != null) {
                messages.size - 1
            }
            else 0
            rvList.scrollToPosition(scrollPosition)
        }
    }

    private fun showRoomAddDialog(){
        lateinit var alert: AlertDialog
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle(getString(R.string.add_debug_room))

        val edittext = EditText(context)
        dialog.setView(edittext)
        dialog.setPositiveButton(getString(R.string.add_done)) { _, _ ->
            val name = edittext.text.toString()
            if(name.isBlank()){
                ToastUtils.show(
                    context!!,
                    getString(R.string.input_room_name),
                    ToastUtils.SHORT,
                    ToastUtils.WARNING
                )
            }
            else {
                DebugUtils.saveRoom(name)
                rooms = DebugUtils.getRoomList()
                rooms.add(getString(R.string.eval_room))
                rooms.add(getString(R.string.icon_room_sender))
                msRoom.setItems(rooms)
                val position = rooms.size - 3
                msRoom.selectedIndex = position
                etRoom.text = StringUtils.toEditable(rooms[position])
                DataUtils.saveData(context!!, "RoomPosition", position.toString())
            }
        }

        alert = dialog.create()
        alert.show()
    }

    @SuppressLint("InflateParams")
    private fun showSenderAddDialog(){
        var base64: String? = null
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle(getString(R.string.add_sender))

        val layout = LayoutInflater
            .from(context)
            .inflate(R.layout.dialog_layout_sender_add, null, false)
        val icon = layout.findViewById<ImageView>(R.id.iv_icon)
        val input = layout.findViewById<EditText>(R.id.et_sender)
        icon.setOnClickListener {
            lateinit var iconAlert: AlertDialog
            val items = arrayOf(getString(R.string.select_image), getString(R.string.input_base64))
            val iconDialog = AlertDialog.Builder(context)
            iconDialog.setTitle(getString(R.string.select_sender_profile))
            iconDialog.setSingleChoiceItems(items, -1) { _, id ->
                if(id == 0){
                    TedImagePicker
                        .with(context!!)
                        .start {
                            Glide
                                .with(context!!)
                                .asBitmap()
                                .load(it)
                                .into(icon)
                            iconAlert.cancel()
                        }
                }
                else {
                    iconAlert.cancel()
                    lateinit var inputAlert: AlertDialog
                    val inputDialog = AlertDialog.Builder(context)
                    inputDialog.setTitle(getString(R.string.input_base64))

                    val edittext = EditText(context)
                    val clipboard = context!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    try {
                        edittext.text =
                            StringUtils.toEditable(clipboard.primaryClip!!.getItemAt(0).text.toString())
                    } catch (ignored: Exception) {}
                    inputDialog.setPositiveButton(getString(R.string.input_done)){ _, _ ->
                        try{
                            base64 = edittext.text.toString()
                            val bitmap = Base64Utils.base642bitmap(edittext.text.toString())
                            icon.setImageBitmap(bitmap)
                            inputAlert.dismiss()
                        }
                        catch (e: Exception){
                            ToastUtils.show(
                                context!!,
                                getString(R.string.not_base64_image),
                                ToastUtils.SHORT,
                                ToastUtils.WARNING
                            )
                            edittext.text = StringUtils.toEditable("")
                        }
                    }
                    inputDialog.setView(edittext)
                    inputAlert = inputDialog.create()
                    inputAlert.show()
                }
            }
            iconAlert = iconDialog.create()
            iconAlert.show()
        }
        dialog.setPositiveButton(getString(R.string.add_done)){ _, _ ->
            val name = input.text.toString()
            if(name.isBlank()){
                ToastUtils.show(
                    context!!,
                    getString(R.string.input_sender_name),
                    ToastUtils.SHORT,
                    ToastUtils.WARNING
                )
            }
            else {
                if (base64 != null) {
                    DebugUtils.saveSender(
                        name,
                        base64!!
                    )
                } else {
                    DebugUtils.saveSender(
                        name,
                        Base64Utils.bitmap2base64(
                            ((icon.drawable) as BitmapDrawable)
                                .toBitmap()
                        )
                    )
                }
                senders = DebugUtils.getSenderList()
                senders.add(getString(R.string.default_user))
                senders.add(getString(R.string.icon_add_sender))
                msSender.setItems(senders.toList())
                val position = senders.size - 3
                msSender.selectedIndex = position
                DataUtils.saveData(context!!, "SenderPosition", position.toString())
                Glide
                    .with(context!!)
                    .load(
                        DebugUtils.getProfileImagePath(
                            name
                        )
                    )
                    .into(trivProfile)
            }
        }
        dialog.setView(layout)
        dialog.show()
    }
}
