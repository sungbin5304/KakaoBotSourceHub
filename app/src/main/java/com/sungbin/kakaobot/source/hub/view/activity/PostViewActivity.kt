package com.sungbin.kakaobot.source.hub.view.activity

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.shashank.sony.fancytoastlib.FancyToast
import com.sungbin.kakaobot.source.hub.R
import com.sungbin.kakaobot.source.hub.dto.BoardDataItem
import com.sungbin.kakaobot.source.hub.utils.Utils

import kotlinx.android.synthetic.main.activity_board_view.*
import kotlinx.android.synthetic.main.activity_board_view.toolbar
import kotlinx.android.synthetic.main.content_board_view.*
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.mancj.slideup.SlideUp
import com.mancj.slideup.SlideUpBuilder
import com.sungbin.kakaobot.source.hub.adapter.CommentListAdapter
import com.sungbin.kakaobot.source.hub.dto.CommentListItem
import com.sungbin.kakaobot.source.hub.utils.DialogUtils
import kotlinx.android.synthetic.main.content_comment_page.*
import org.apache.commons.lang3.StringUtils
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList


class PostViewActivity : AppCompatActivity() {

    private var adapter: CommentListAdapter? = null
    private var items: ArrayList<CommentListItem>? = null
    private val reference = FirebaseDatabase.getInstance().reference.child("Board")
    private var slideUp: SlideUp? = null
    private var boardDataItem: BoardDataItem? = null

    @SuppressLint("ObsoleteSdkInt", "SetJavaScriptEnabled", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_view)
        toolbar.title = ""
        setSupportActionBar(toolbar)

        val uuid = intent.getStringExtra("uuid")
        reference.child(uuid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                boardDataItem = dataSnapshot.getValue(BoardDataItem::class.java)

                @Suppress("DEPRECATION")
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    viewer.loadData(boardDataItem!!.content, "text/html", "UTF-8")
                } else {
                    viewer.loadData(boardDataItem!!.content, "text/html;", "charset=UTF-8")
                }

                toolbar_title.text = boardDataItem!!.title
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Utils.toast(applicationContext,
                    databaseError.message,
                    FancyToast.LENGTH_SHORT, FancyToast.ERROR)
            }
        })

        val webSettings = viewer.settings
        webSettings.builtInZoomControls = true
        webSettings.javaScriptEnabled = true
        webSettings.setSupportZoom(false)

        slideUp = SlideUpBuilder(view_comment_list)
            .withListeners(object : SlideUp.Listener.Events {
                override fun onSlide(percent: Float) {
                    viewer.alpha = percent / 100
                    if (comment.isShown && percent < 100) {
                        comment.hide()
                    }
                }

                override fun onVisibilityChanged(visibility: Int) {
                    if (visibility == View.GONE){
                        comment.show()
                    }
                }
            })
            .withStartGravity(Gravity.BOTTOM)
            .withStartState(SlideUp.State.HIDDEN)
            .withSlideFromOtherView(layout_post_view)
            .build()

        comment.setOnClickListener {
            slideUp!!.show()
            toolbar_title.text = boardDataItem!!.title + " - 댓글"
        }

        items = ArrayList()
        adapter = CommentListAdapter(items, this@PostViewActivity, uuid)
        list.layoutManager = LinearLayoutManager(applicationContext)
        list.adapter = adapter

        val commentItemCash: ArrayList<CommentListItem> = ArrayList()
        val reference = FirebaseDatabase.getInstance().reference.child("Board Comment").child(uuid)
        reference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                try {
                    val commentDataItem = dataSnapshot.getValue(CommentListItem::class.java)
                    if(!commentItemCash.contains(commentDataItem)) {
                        items!!.add(commentDataItem!!)
                        commentItemCash.add(commentDataItem)
                        adapter!!.notifyDataSetChanged()
                    }
                }
                catch (e: Exception) {
                    Utils.error(applicationContext,
                        e, "Load comment list listener.")
                }

            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                /*val commentDataItem = dataSnapshot.getValue(CommentListItem::class.java)
                Log.d("SSS", commentDataItem!!.comment)*/
                Utils.toast(applicationContext, "댓글이 수정되었습니다.\n게시글을 다시 로드하시면 반영됩니다.",
                    FancyToast.LENGTH_SHORT, FancyToast.SUCCESS)
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                /*val commentDataItem = dataSnapshot.getValue(CommentListItem::class.java)
                Log.d("TTT", commentDataItem!!.comment)
                //items!!.remove(commentDataItem)
                //commentItemCash.remove(commentDataItem)
                Log.d("SSS", items!!.contains(commentDataItem).toString())
                //adapter!!.deleteItem(items!!.indexOf(commentDataItem))
                adapter!!.notifyDataSetChanged()*/
                Utils.toast(applicationContext, "댓글이 삭제되었습니다.\n게시글을 다시 로드하시면 반영됩니다.",
                    FancyToast.LENGTH_SHORT, FancyToast.SUCCESS)
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Utils.toast(applicationContext,
                    databaseError.message,
                    FancyToast.LENGTH_SHORT, FancyToast.ERROR)
            }
        })

        post_comment.setOnClickListener {
            val ctx = this@PostViewActivity

            val dialog = AlertDialog.Builder(ctx)
            dialog.setTitle(getString(R.string.post_comment))

            val layout = LinearLayout(ctx)
            layout.orientation = LinearLayout.VERTICAL

            val textview = TextView(ctx)
            textview.text = getString(R.string.max_length_one_fiive_zero)
            textview.gravity = Gravity.CENTER
            layout.addView(textview)

            val input = EditText(ctx)
            input.hint = getString(R.string.string_comment)
            input.filters = arrayOf(InputFilter.LengthFilter(150))
            layout.addView(input)

            dialog.setView(
                DialogUtils.makeMarginLayout(
                    resources,
                    ctx, layout
                )
            )
            dialog.setNegativeButton(getString(R.string.string_cancel), null)
            dialog.setPositiveButton(getString(R.string.post_complete)) { _, _ ->
                val comment = input.text.toString()
                if(StringUtils.isBlank(comment)){
                    Utils.toast(ctx,
                        getString(R.string.please_input_comment),
                        FancyToast.LENGTH_SHORT, FancyToast.WARNING)
                }
                else {
                    val key = UUID.randomUUID().toString().replace("-", "")
                    val item = CommentListItem(Utils.readData(applicationContext, "nickname", "User"),
                        comment, key)
                    reference.child(key).setValue(item)
                    Utils.toast(applicationContext,
                        getString(R.string.comment_post_success),
                        FancyToast.LENGTH_SHORT, FancyToast.SUCCESS)
                }
            }
            dialog.show()
        }
    }

    override fun onBackPressed() {
        if(slideUp!!.isVisible) {
            slideUp!!.hide()
            toolbar_title.text = boardDataItem!!.title
        }
        else super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(0, 1, 0, getString(R.string.string_version_list))
            .setIcon(R.drawable.ic_history_white_24dp)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if(id == 1){
            Utils.toast(applicationContext,
                "준비중...",
                FancyToast.LENGTH_SHORT, FancyToast.INFO)
        }

        return super.onOptionsItemSelected(item)
    }

}
