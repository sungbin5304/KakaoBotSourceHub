package com.sungbin.autoreply.bot.three.view.hub.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import com.sungbin.autoreply.bot.three.R
import com.sungbin.autoreply.bot.three.adapter.hub.BoardListAdapter
import com.sungbin.autoreply.bot.three.dto.hub.BoardActionItem
import com.sungbin.autoreply.bot.three.dto.hub.BoardDataItem
import com.sungbin.autoreply.bot.three.dto.hub.BoardListItem
import com.sungbin.autoreply.bot.three.utils.chat.ChatModuleUtils
import com.sungbin.autoreply.bot.three.view.hub.PostActivity
import com.sungbin.sungbintool.ToastUtils
import com.sungbin.sungbintool.Utils
import java.lang.Exception
import kotlin.collections.ArrayList


@Suppress("UNUSED_ANONYMOUS_PARAMETER")
class BoardList : Fragment() {

    @SuppressLint("InflateParams", "StaticFieldLeak")
    private var adapter: BoardListAdapter? = null
    private var uid: String? = null
    private var items: ArrayList<BoardListItem>? = null
    private val reference = FirebaseDatabase.getInstance().reference
    private var boardCash: ArrayList<BoardDataItem>? = null
    private var badBoardList: ArrayList<String>? = null
    private var goodBoardList: ArrayList<String>? = null
    private var alert: AlertDialog? = null
    private var boardListView: RecyclerView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        uid = ChatModuleUtils.getDeviceId(context!!)
        items = ArrayList()
        badBoardList = ArrayList()
        goodBoardList = ArrayList()
        adapter = BoardListAdapter(
            items,
            goodBoardList,
            badBoardList,
            activity
        )
        boardCash = ArrayList<BoardDataItem>()

        val view = inflater.inflate(R.layout.fragment_board_list, null)

        boardListView = view.findViewById<RecyclerView>(R.id.list)
        boardListView!!.layoutManager = LinearLayoutManager(context)
        boardListView!!.adapter = adapter

        val post_board = view.findViewById<FloatingActionButton>(R.id.post_board)
        val nestedScrollView = view.findViewById<NestedScrollView>(R.id.nest_scroll_view)
        nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, y, _, oldy ->
            if (y > oldy) {
                //Down
                post_board.hide()
            }
            if (y < oldy) {
                //Up
                post_board.show()
            }
        })


        post_board.setOnClickListener {
            ToastUtils.show(context!!,
                getString(R.string.string_loading),
                ToastUtils.SHORT, ToastUtils.INFO
            )
            startActivity(Intent(context, PostActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
           ToastUtils.show(context!!,
               getString(R.string.press_long_sort_board),
                ToastUtils.SHORT, ToastUtils.INFO
           )
        }

        post_board.setOnLongClickListener {
            showSortDialog()
            return@setOnLongClickListener false
        }

        reference.child("User Action").child(uid!!).child("board_good")
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                    try {
                        val actionData = dataSnapshot.getValue(BoardActionItem::class.java)
                        goodBoardList!!.add(actionData!!.uuid!!)
                    }
                    catch (e: Exception) {
                        Utils.error(context!!,
                            e, "Load board_good listener.")
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

        reference.child("User Action").child(uid!!).child("board_bad")
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                    try {
                        val actionData = dataSnapshot.getValue(BoardActionItem::class.java)
                        badBoardList!!.add(actionData!!.uuid!!)
                    }
                    catch (e: Exception) {
                        Utils.error(context!!,
                            e, "Load board_bad listener.")
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

        reference.child("Board").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                try {
                    val boardDataItem = dataSnapshot.getValue(BoardDataItem::class.java)
                    if(!boardCash!!.contains(boardDataItem)) {
                        val boardListItem =
                            BoardListItem(
                                boardDataItem!!.title,
                                boardDataItem.desc, boardDataItem.good_count,
                                boardDataItem.bad_count, boardDataItem.uuid
                            )
                        items!!.add(boardListItem)
                        adapter!!.notifyDataSetChanged()
                        boardCash!!.add(boardDataItem)
                    }
                }
                catch (e: Exception) {
                    Utils.error(context!!,
                        e, "Load board list listener.")
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

        return view
    }

    private fun showSortDialog(){
        val dialog = AlertDialog.Builder(activity!!)
        val action = arrayOf(getString(R.string.sort_good_count),
            getString(R.string.sort_hate_count),
            getString(R.string.sort_date_new),
            getString(R.string.sort_date_old))
        dialog.setTitle(getString(R.string.board_sort))
        dialog.setNegativeButton(getString(R.string.string_cancel), null)
        dialog.setSingleChoiceItems(action, -1) { _, which ->
            alert!!.cancel()
            /*when(which){
                0 ->{ //좋아요순
                    items!!.sortWith(Comparator {
                            data1, data2 ->
                        Log.d("SSS - 1", data1.title)
                        Log.d("SSS - 2", data2.title)
                        data1.good_count!!.compareTo(data2.bad_count!!)})
                    alert!!.cancel()
                    Log.d("TTT - 1", items!!.get(0).title)
                    Log.d("TTT - 2", items!!.get(1).title)
                    adapter = BoardListAdapter(items, goodBoardList, badBoardList, activity)
                    boardListView!!.adapter = adapter
                    adapter!!.notifyDataSetChanged()
                }
                1 ->{ //싫어요순
                    items!!.sortWith(Comparator {
                            data2, data1 -> data1.good_count!!.compareTo(data2.bad_count!!)})
                    alert!!.cancel()
                    Log.d("TTT - 3", items!!.get(0).title)
                    Log.d("TTT - 4", items!!.get(1).title)
                    adapter!!.notifyDataSetChanged()
                }
                2 ->{ //최신순

                    alert!!.cancel()
                }
                3 ->{  //오래된순

                    alert!!.cancel()
                }
            }*/
            ToastUtils.show(context!!,
                getString(R.string.string_making),
                ToastUtils.SHORT, ToastUtils.INFO
            )
        }
        alert = dialog.create()
        alert!!.show()
    }

}