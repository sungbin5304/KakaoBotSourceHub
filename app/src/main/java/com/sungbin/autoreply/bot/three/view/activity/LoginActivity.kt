package com.sungbin.autoreply.bot.three.view.activity

import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.kakao.auth.ISessionCallback
import com.kakao.auth.Session
import com.kakao.network.ErrorResult
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import com.kakao.util.exception.KakaoException
import com.sungbin.autoreply.bot.three.R
import com.sungbin.autoreply.bot.three.dto.chat.item.UserItem
import com.sungbin.autoreply.bot.three.dto.chat.model.User
import com.sungbin.autoreply.bot.three.utils.chat.ChatModuleUtils
import com.sungbin.autoreply.bot.three.view.chat.DialogsActivity
import com.sungbin.sungbintool.DialogUtils
import com.sungbin.sungbintool.ToastUtils
import com.sungbin.sungbintool.Utils
import kotlinx.android.synthetic.main.content_login.*
import org.apache.commons.lang3.StringUtils
import java.util.*


class LoginActivity : AppCompatActivity() {

    private val RC_SIGN_IN = 1000
    private lateinit var deviceId: String
    private lateinit var auth: FirebaseAuth
    private lateinit var sessionCallback: ISessionCallback
    private lateinit var googleSignInClient: GoogleSignInClient
    private var reference = FirebaseDatabase.getInstance().reference.child("User")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_login)

        deviceId = ChatModuleUtils.getDeviceId(applicationContext)
        /*UserManagement.getInstance()
            .requestLogout(object : LogoutResponseCallback() {
                override fun onCompleteLogout() {
                }
            })*/

       reference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                try {
                    val item = dataSnapshot.getValue(UserItem::class.java)!!
                    val user = User(item.id!!, item.name!!, item.avatar!!, item.isOnline!!,
                        item.roomList, item.friendsList)
                    ChatModuleUtils.addUser(user)
                }
                catch (e: Exception) {
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

        sessionCallback = object : ISessionCallback {
            override fun onSessionOpened() {
                UserManagement.getInstance().me(object : MeV2ResponseCallback() {
                    override fun onSuccess(result: MeV2Response) {
                        ToastUtils.show(applicationContext, getString(R.string.login_success),
                            ToastUtils.SHORT, ToastUtils.SUCCESS)
                        val user = UserItem(deviceId, result.kakaoAccount.profile.nickname,
                            result.kakaoAccount.profile.profileImageUrl ?:
                            "https://cdn.pixabay.com/photo/2020/03/28/15/20/cat-4977436_960_720.jpg", true,
                            ArrayList<String>(), ArrayList<String>())
                        reference.child(deviceId).setValue(user)

                        finish()
                        //startActivity(Intent(applicationContext, PermissionActivity::class.java))
                        startActivity(Intent(applicationContext, DialogsActivity::class.java))
                    }

                    override fun onSessionClosed(errorResult: ErrorResult) {
                        Utils.error(applicationContext, Exception(errorResult.errorMessage), "Kakao Login")
                    }
                })
            }

            override fun onSessionOpenFailed(exception: KakaoException) {}
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_login_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = FirebaseAuth.getInstance()
        auth.setLanguageCode("kr")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(applicationContext, R.color.colorWhite)
        }

        Session.getCurrentSession().addCallback(sessionCallback)
        kakao_login_btn.setOnClickListener {
            kakao_login.performClick()
        }
        google_login_btn.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        btn_login.setOnClickListener {
            if(StringUtils.isBlank(et_id.text.toString()) ||
                StringUtils.isBlank(et_pw.text.toString())){
                ToastUtils.show(applicationContext, getString(R.string.please_input_all),
                    ToastUtils.SHORT, ToastUtils.WARNING)
            }
            else if(!Patterns.EMAIL_ADDRESS.matcher(
                    et_id.text.toString()).matches()) {
                ToastUtils.show(applicationContext, getString(R.string.please_input_email_type),
                    ToastUtils.SHORT, ToastUtils.WARNING)
            }
            else if(et_pw.text.toString().length < 6) {
                ToastUtils.show(applicationContext, getString(R.string.min_pw_length_six),
                    ToastUtils.SHORT, ToastUtils.WARNING)
            }
            else {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    et_id.text.toString(), et_pw.text.toString())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            ToastUtils.show(applicationContext, getString(R.string.login_success),
                                ToastUtils.SHORT, ToastUtils.SUCCESS)
                            val user = UserItem(deviceId, "사용자 ${Random().nextInt(1000)}",
                                "https://cdn.pixabay.com/photo/2020/03/28/15/20/cat-4977436_960_720.jpg", true,
                                ArrayList<String>(), ArrayList<String>())
                            reference.child(deviceId).setValue(user)

                            finish()
                            startActivity(Intent(applicationContext, PermissionActivity::class.java))
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                        } else {
                            ToastUtils.show(applicationContext, getString(R.string.register_account),
                                ToastUtils.SHORT, ToastUtils.INFO)

                            FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                                et_id.text.toString(), et_pw.text.toString())
                                .addOnCompleteListener { task2 ->
                                    if (task2.isSuccessful) {
                                        ToastUtils.show(applicationContext, getString(R.string.please_do_login),
                                            ToastUtils.SHORT, ToastUtils.SUCCESS)
                                    } else {
                                        ToastUtils.show(applicationContext, task2.exception.toString(),
                                            ToastUtils.LONG, ToastUtils.ERROR)
                                    }
                                }
                        }
                    }
            }
        }
    }

    private fun getAppVersionName(): String{
        return packageManager.getPackageInfo(packageName, 0).versionName
    }

    private fun checkNewVersion(remoteConfig: FirebaseRemoteConfig){
        val lastVersion = remoteConfig.getString("last_version").replace("\"", "")
        if(lastVersion != getAppVersionName()){
            DialogUtils.show(this, "앱 업데이트 필요",
                "사용중이신 KakaoTalkBotHub의 버전이 낮아서 더 이상 사용하실 수 없습니다.\n계속해서 사용하시려면 업데이트를 해 주새요.",
                DialogInterface.OnClickListener { _, _ -> finish() }, false)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Session.getCurrentSession().removeCallback(sessionCallback)
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val googleUser = auth.currentUser!!
                    var profileImage = googleUser.photoUrl.toString()
                    if(profileImage == "null")
                        profileImage = "https://cdn.pixabay.com/photo/2020/03/28/15/20/cat-4977436_960_720.jpg"
                    ToastUtils.show(applicationContext, getString(R.string.login_success),
                        ToastUtils.SHORT, ToastUtils.SUCCESS)
                    val user = UserItem(deviceId, googleUser.displayName!!, profileImage, true,
                        ArrayList<String>(), ArrayList<String>())
                    reference.child(deviceId).setValue(user)

                    finish()
                    startActivity(Intent(applicationContext, PermissionActivity::class.java))
                } else {
                    Utils.error(applicationContext, task.exception!!, "Google Login")
                }
            }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        @Nullable data: Intent?
    ) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return
        }
        if (requestCode == RC_SIGN_IN) { //Google Login
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)!!
            if (result.isSuccess) {
                ToastUtils.show(applicationContext, getString(R.string.process_on_login),
                    ToastUtils.SHORT, ToastUtils.INFO)
                val account = result.signInAccount
                firebaseAuthWithGoogle(account!!)
            } else {
                ToastUtils.show(
                    applicationContext,
                    getString(R.string.error_when_google_login),
                    ToastUtils.SHORT, ToastUtils.ERROR
                )
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}