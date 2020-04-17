package com.sungbin.autoreply.bot.three.fixtures

import com.sungbin.autoreply.bot.three.dto.model.Dialog
import com.sungbin.autoreply.bot.three.dto.model.Message
import com.sungbin.autoreply.bot.three.dto.model.User
import java.util.*

class DialogsFixtures private constructor() : FixturesData() {
    companion object {
        val dialogs: ArrayList<Dialog>
            get() {
                val chats =
                    ArrayList<Dialog>()
                for (i in 0..19) {
                    val calendar = Calendar.getInstance()
                    calendar.add(Calendar.DAY_OF_MONTH, -(i * i))
                    calendar.add(Calendar.MINUTE, -(i * i))
                    chats.add(getDialog(i, calendar.time))
                }
                return chats
            }

        private fun getDialog(
            i: Int,
            lastMessageCreatedAt: Date
        ): Dialog {
            val users =
                users
            return Dialog(
                FixturesData.randomId,
                if (users.size > 1)
                    groupChatTitles[users.size - 2]!! else users[0].name,
                if (users.size > 1)
                    groupChatImages[users.size - 2]!! else
                    randomAvatar,
                users,
                getMessage(lastMessageCreatedAt),
                if (i < 3) 3 - i else 0
            )
        }

        private val users: ArrayList<User>
            private get() {
                val users =
                    ArrayList<User>()
                val usersCount: Int = 1 + rnd.nextInt(4)
                for (i in 0 until usersCount) {
                    users.add(user)
                }
                return users
            }

        private val user: User
            private get() = User(
                randomId,
                randomName,
                randomAvatar,
                randomBoolean
            )

        private fun getMessage(date: Date): Message {
            return Message(
                randomId,
                user,
                randomMessage,
                date
            )
        }
    }

    init {
        throw AssertionError()
    }
}