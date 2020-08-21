package com.sungbin.autoreply.bot.three.utils.chat

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import android.util.Log
import com.sungbin.autoreply.bot.three.dto.chat.item.UserItem
import com.sungbin.autoreply.bot.three.dto.chat.model.Dialog
import com.sungbin.autoreply.bot.three.dto.chat.model.User
import java.util.*
import kotlin.collections.ArrayList

object ChatModuleUtils {
    private val allUserIdIndex: ArrayList<String> = ArrayList()
    private val allUserData: ArrayList<User> = ArrayList()

    private val allDialogIdIndex: ArrayList<String> = ArrayList()
    private val allDialogData: ArrayList<Dialog> = ArrayList()

    fun addDialog(dialog: Dialog) {
        allDialogIdIndex.add(dialog.id)
        allDialogData.add(dialog)
    }

    fun getDialog(id: String): Dialog? {
        return try {
            allDialogData[allDialogIdIndex.indexOf(id)]
        } catch (e: Exception) {
            null
        }
    }

    fun getAllDialog(): ArrayList<Dialog> {
        return allDialogData
    }

    fun addUser(user: User) {
        allUserIdIndex.add(user.id)
        allUserData.add(user)
        Log.d("ZZZ - 0", user.id)
    }

    fun getUser(id: String): User? {
        return try {
            allUserData[allUserIdIndex.indexOf(id)]
        } catch (e: Exception) {
            null
        }
    }

    fun createUserItem(user: User): UserItem {
        return UserItem(
            user.id, user.name,
            user.avatar, user.online, user.rooms, user.friends
        )
    }

    fun getAllUser(): ArrayList<User> {
        return allUserData
    }

    @SuppressLint("HardwareIds")
    fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    val randomUuid: String
        get() = UUID.randomUUID().toString().substring(0, 10)

}