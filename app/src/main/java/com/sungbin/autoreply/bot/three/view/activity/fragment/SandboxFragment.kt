package com.sungbin.autoreply.bot.three.view.activity.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sungbin.autoreply.bot.three.R
import com.sungbin.autoreply.bot.three.adapter.DebugListAdapter
import com.sungbin.autoreply.bot.three.dto.bot.DebugListItem
import com.sungbin.autoreply.bot.three.utils.RhinoUtils


@Suppress("DEPRECATION")
class SandboxFragment : Fragment() {

    var rcDebug: RecyclerView? = null
    var etInput: EditText? = null
    var ibSend: ImageButton? = null
    var etRoomName:EditText? = null
    var etSenderName:EditText? = null
    val items = ArrayList<DebugListItem>()
    var adapter: DebugListAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_sandbox, container, false)
        rcDebug = view.findViewById(R.id.debugView)
        etInput = view.findViewById(R.id.input)
        ibSend = view.findViewById(R.id.send)
        etRoomName= view.findViewById(R.id.roomName)
        etSenderName = view.findViewById(R.id.senderName)

        adapter = DebugListAdapter(items, activity!!)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        ibSend!!.setOnClickListener {
            val message = etInput!!.text.toString()
            val result = RhinoUtils(context!!).runJs(message)

            val messageItem =
                DebugListItem(
                    "나",
                    0,
                    message
                )
            val resultItem =
                DebugListItem(
                    "봇",
                    1,
                    result
                )

            items.add(messageItem)
            items.add(resultItem)
            adapter!!.notifyDataSetChanged()

            etInput!!.setText("")
            rcDebug!!.scrollToPosition(items.size - 1)
        }

        rcDebug!!.layoutManager = LinearLayoutManager(context)
        rcDebug!!.adapter = adapter
    }
}
