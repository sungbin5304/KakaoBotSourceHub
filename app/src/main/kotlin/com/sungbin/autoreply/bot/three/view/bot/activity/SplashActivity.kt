package com.sungbin.autoreply.bot.three.view.bot.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.sungbin.autoreply.bot.three.R
import com.sungbin.autoreply.bot.three.dto.chat.item.UserItem
import com.sungbin.autoreply.bot.three.dto.chat.model.User
import com.sungbin.autoreply.bot.three.utils.chat.ChatModuleUtils
import com.sungbin.sungbintool.Utils

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        FirebaseDatabase.getInstance().reference.child("User")
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                    try {
                        val item = dataSnapshot.getValue(UserItem::class.java)!!
                        val user = User(
                            item.id!!, item.name!!, item.avatar!!, item.isOnline!!,
                            item.roomList, item.friendsList
                        )
                        ChatModuleUtils.addUser(user)
                    } catch (e: Exception) {
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

        Handler().postDelayed({
            finish()
            startActivity(Intent(applicationContext, DashboardActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }, 1500)

    }
}
