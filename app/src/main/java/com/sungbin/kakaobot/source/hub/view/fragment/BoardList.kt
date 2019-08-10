package com.sungbin.kakaobot.source.hub.view.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import com.sungbin.kakaobot.source.hub.R
import com.sungbin.kakaobot.source.hub.adapter.BoardListAdapter
import com.sungbin.kakaobot.source.hub.dto.BoardDataItem
import com.sungbin.kakaobot.source.hub.dto.BoardListItem
import com.sungbin.kakaobot.source.hub.utils.Utils
import com.sungbin.kakaobot.source.hub.view.activity.PostActivity
import java.lang.Exception


@SuppressLint("StaticFieldLeak")
private var adapter: BoardListAdapter? = null
private var uid: String? = null
private var items: ArrayList<BoardListItem>? = null
private val reference = FirebaseDatabase.getInstance().reference.child("Board");
private var boardCash: ArrayList<BoardDataItem>? = null

class BoardList : Fragment() {
    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        uid = Utils.readData(context!!, "uid", "null")!!
        items = ArrayList()
        adapter = BoardListAdapter(items, activity!!)
        boardCash = ArrayList<BoardDataItem>()

        val view = inflater.inflate(R.layout.fragment_board_list, null)

        val boardListView = view.findViewById<RecyclerView>(R.id.list)
        boardListView.layoutManager = LinearLayoutManager(context)
        boardListView.adapter = adapter

        reference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                try {
                    val boardDataItem = dataSnapshot.getValue(BoardDataItem::class.java)
                    if(!boardCash!!.contains(boardDataItem)) {
                        val boardListItem = BoardListItem(
                            boardDataItem!!.title,
                            boardDataItem.desc, boardDataItem.good_count,
                            boardDataItem.bad_count, boardDataItem.uuid)
                        items!!.add(boardListItem)
                        adapter!!.notifyDataSetChanged()
                        boardListView.scrollToPosition(adapter!!.itemCount - 1)
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
            startActivity(Intent(context, PostActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }

        return view
    }
}