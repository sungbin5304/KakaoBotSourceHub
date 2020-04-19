package com.sungbin.autoreply.bot.three.adapter

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.shashank.sony.fancytoastlib.FancyToast
import com.sungbin.autoreply.bot.three.R
import com.sungbin.autoreply.bot.three.dto.hub.CommentListItem
import com.sungbin.autoreply.bot.three.utils.ui.DialogUtils
import com.sungbin.autoreply.bot.three.utils.Utils
import org.apache.commons.lang3.StringUtils
import kotlin.collections.ArrayList

class CommentListAdapter(private val list: ArrayList<CommentListItem>?,
                         private val act: Activity,
                         private val uuid: String) :
    RecyclerView.Adapter<CommentListAdapter.CommentViewHolder>() {

    private var ctx: Context? = null
    private var alert: AlertDialog? = null

    inner class CommentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var sender: TextView = view.findViewById(R.id.comment_sender)
        var comment: TextView = view.findViewById(R.id.comment_content)
        var view: CardView = view.findViewById(R.id.comment_view)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.view_comment_list, viewGroup, false)
        ctx = viewGroup.context
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(@NonNull viewholder: CommentViewHolder, position: Int) {
        val reference = FirebaseDatabase.getInstance().reference.child("Board Comment").child(uuid)
        val name = list!![position].name
        val content = list[position].comment
        val uuid = list[position].uuid
        val uid = list[position].uid
        val key = list[position].key
        val myUid = Utils.readData(ctx!!, "uid", "")

        viewholder.sender.text = name
        setReadMore(viewholder.comment, content!!, 3)

        viewholder.view.setOnLongClickListener {
            if(uid != myUid) {
                Utils.toast(act, act.getString(R.string.can_action_my_comment),
                    FancyToast.LENGTH_SHORT, FancyToast.WARNING)
                return@setOnLongClickListener false
            }
            val dialog = AlertDialog.Builder(act)
            val action = arrayOf(act.getString(R.string.string_edit_comment),
                act.getString(R.string.string_delete_comment))
            dialog.setTitle(act.getString(R.string.choose_want_action))
            dialog.setNegativeButton(act.getString(R.string.string_cancel), null)
            dialog.setSingleChoiceItems(action, -1) { _, which ->
                alert!!.cancel()
                when(which){
                    0 ->{ //수정
                        val builder = AlertDialog.Builder(act)
                        dialog.setTitle(act.getString(R.string.string_edit_comment))

                        val layout = LinearLayout(act)
                        layout.orientation = LinearLayout.VERTICAL

                        val textview = TextView(act)
                        textview.text = act.getString(R.string.max_length_one_fiive_zero)
                        textview.gravity = Gravity.CENTER
                        layout.addView(textview)

                        val input = EditText(act)
                        input.hint = act.getString(R.string.string_comment)
                        input.text = SpannableStringBuilder(content)
                        input.filters = arrayOf(InputFilter.LengthFilter(150))
                        layout.addView(input)

                        builder.setView(
                            DialogUtils.makeMarginLayout(
                                act.resources,
                                ctx!!, layout
                            )
                        )
                        builder.setNegativeButton(act.getString(R.string.string_cancel), null)
                        builder.setPositiveButton(act.getString(R.string.post_complete)) { _, _ ->
                            val comment = input.text.toString()
                            if(StringUtils.isBlank(comment)){
                                Utils.toast(ctx!!,
                                    act.getString(R.string.please_input_comment),
                                    FancyToast.LENGTH_SHORT, FancyToast.WARNING)
                            }
                            else {
                                val item =
                                    CommentListItem(
                                        name,
                                        comment, uuid, uid, key
                                    )
                                reference.child(key!!).setValue(item)
                                Utils.toast(ctx!!,
                                    act.getString(R.string.comment_edit_success),
                                    FancyToast.LENGTH_SHORT, FancyToast.SUCCESS)
                            }
                        }
                        builder.show()
                    }
                    1 ->{ //삭제
                        reference.child(key!!).removeValue()
                        Utils.toast(ctx!!,
                            act.getString(R.string.comment_delete_success),
                            FancyToast.LENGTH_SHORT, FancyToast.SUCCESS)
                    }
                }
            }
            alert = dialog.create()
            alert!!.show()
            return@setOnLongClickListener false
        }
    }

    private fun setReadMore(view: TextView, text: String, maxLine: Int) {
        val context = view.context
        val expanedText = " ... 더보기"

        if (view.tag != null && view.tag == text) return
        view.tag = text
        view.text = text
        view.post {
            if (view.lineCount >= maxLine) {

                val lineEndIndex = view.layout.getLineVisibleEnd(maxLine - 1)

                val split = text.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                var splitLength = 0

                var lessText = ""
                for (item in split) {
                    splitLength += item.length + 1
                    if (splitLength >= lineEndIndex) {
                        lessText += if (item.length >= expanedText.length) {
                            item.substring(0, item.length - expanedText.length) + expanedText
                        } else {
                            item + expanedText
                        }
                        break
                    }
                    lessText += item + "\n"
                }
                val spannableString = SpannableString(lessText)
                spannableString.setSpan(
                    object : ClickableSpan() {
                        override fun onClick(vew: View) {
                            view.text = text
                        }

                        override fun updateDrawState(ds: TextPaint) {
                            ds.color = ContextCompat.getColor(context, R.color.colorPrimary)
                        }
                    },
                    spannableString.length - expanedText.length,
                    spannableString.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                view.text = spannableString
                view.movementMethod = LinkMovementMethod.getInstance()
            }
        }
    }

    fun deleteItem(position: Int){
        list!!.removeAt(position)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun getItem(position: Int): CommentListItem {
        return list!![position]
    }
}
