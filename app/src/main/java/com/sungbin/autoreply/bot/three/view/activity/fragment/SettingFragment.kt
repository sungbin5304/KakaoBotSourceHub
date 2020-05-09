package com.sungbin.autoreply.bot.three.view.activity.fragment

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import com.savvyapps.togglebuttonlayout.ToggleButtonLayout
import com.sungbin.autoreply.bot.three.R
import com.sungbin.autoreply.bot.three.receiver.NotificationActionClickReceiver
import com.sungbin.autoreply.bot.three.view.activity.DashboardActivity
import com.sungbin.sungbintool.NotificationUtils


class SettingFragment : Fragment() {

    private var swBotOnoff: Switch? = null
    private var swAutoSave: Switch? = null
    private var swNotHighting: Switch? = null
    private var swNotErrorHighting: Switch? = null
    private var swErrorBotOff: Switch? = null

    private var tblFavorateLanguage: ToggleButtonLayout? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_setting, container, false)

        swBotOnoff = view.findViewById(R.id.sw_bot_onoff)
        swAutoSave = view.findViewById(R.id.sw_auto_save)
        swNotHighting = view.findViewById(R.id.sw_not_highting)
        swNotErrorHighting = view.findViewById(R.id.sw_not_error_highting)
        swErrorBotOff = view.findViewById(R.id.sw_error_bot_off)

        tblFavorateLanguage = view.findViewById(R.id.tbl_favorite_langauge)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        swBotOnoff!!.setOnCheckedChangeListener { _, boolean ->
            Log.d("AAA", boolean.toString())
            if(boolean){
                val intent = Intent(context, NotificationActionClickReceiver::class.java)
                val pi = PendingIntent.getBroadcast(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
                @Suppress("DEPRECATION")
                val action = Notification.Action(
                    R.drawable.icon,
                    getString(R.string.bot_off), pi
                )

                val contentIntent =
                    PendingIntent.getActivity(
                        context, 0,
                        Intent(context, DashboardActivity::class.java),
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )

                NotificationUtils.setGroupName(getString(R.string.app_name))
                NotificationUtils.createChannel(
                    context!!, getString(R.string.app_name),
                    getString(R.string.app_name)
                )
                NotificationUtils.addAction(action)
                NotificationUtils.setPendingIntent(contentIntent)
                NotificationUtils.showNormalNotification(
                    context!!, 1, getString(R.string.now_bot_working),
                    getString(R.string.now_bot_working_two)
                )
            }
            else {
                NotificationUtils.deleteNotification(context!!, 1)
            }
        }
    }

    object NotificationUtils {

        /**
         * Created by SungBin on 2018. 01. 07.
         */

        private var GROUP_NAME = "undefined"
        private var actions = arrayOf<Notification.Action>()
        private var pendingIntent: PendingIntent? = null

        private val smallIcon: Int
            get() = R.drawable.icon

        fun setGroupName(name: String) {
            GROUP_NAME = name
        }

        fun addAction(action: Notification.Action){
            actions.plus(action)
        }

        fun setPendingIntent(pendingIntent: PendingIntent){
            this.pendingIntent = pendingIntent
        }

        fun createChannel(context: Context, name: String, description: String) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                val group1 = NotificationChannelGroup(GROUP_NAME, GROUP_NAME)
                getManager(context).createNotificationChannelGroup(group1)

                val channelMessage =
                    NotificationChannel(Channel.NAME, name, android.app.NotificationManager.IMPORTANCE_DEFAULT)
                channelMessage.description = description
                channelMessage.group = GROUP_NAME
                channelMessage.lightColor = R.color.colorAccent
                channelMessage.enableVibration(true)
                channelMessage.vibrationPattern = longArrayOf(0, 0)
                getManager(context).createNotificationChannel(channelMessage)
            }
        }

        private fun getManager(context: Context): android.app.NotificationManager {
            return context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        }

        fun showNormalNotification(context: Context, id: Int, title: String, content: String) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                val builder = Notification.Builder(context, Channel.NAME)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setSmallIcon(smallIcon)
                    .setAutoCancel(true)
                    .setOngoing(true)

                for(action in actions) {
                    builder.addAction(action)
                }

                if(pendingIntent != null) {
                    builder.setContentIntent(pendingIntent)
                }

                getManager(context).notify(id, builder.build())
            } else {
                val builder = Notification.Builder(context)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setSmallIcon(smallIcon)
                    .setAutoCancel(true)
                    .setOngoing(true)
                getManager(context).notify(id, builder.build())
            }
        }

        fun showInboxStyleNotification(context: Context, id: Int, title: String, content: String, boxText: Array<String>) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                val builder = Notification.Builder(context, Channel.NAME)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setSmallIcon(smallIcon)
                    .setAutoCancel(true)
                    .setOngoing(true)
                val inboxStyle = Notification.InboxStyle()
                inboxStyle.setBigContentTitle(title)
                inboxStyle.setSummaryText(content)

                for (str in boxText) {
                    inboxStyle.addLine(str)
                }

                builder.style = inboxStyle

                getManager(context).notify(id, builder.build())
            } else {
                val builder = Notification.Builder(context)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setSmallIcon(smallIcon)
                    .setAutoCancel(true)
                    .setOngoing(true)
                val inboxStyle = Notification.InboxStyle()
                inboxStyle.setBigContentTitle(title)
                inboxStyle.setSummaryText(content)

                for (str in boxText) {
                    inboxStyle.addLine(str)
                }

                builder.style = inboxStyle

                getManager(context).notify(id, builder.build())
            }
        }

        fun deleteNotification(context: Context, id: Int) {
            NotificationManagerCompat.from(context).cancel(id)
        }

        annotation class Channel {
            companion object {
                const val NAME = "CHANNEL"
            }
        }

    }

}