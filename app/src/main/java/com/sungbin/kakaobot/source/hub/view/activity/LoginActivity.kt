package com.sungbin.kakaobot.source.hub.view.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.shashank.sony.fancytoastlib.FancyToast
import com.shazam.android.widget.text.reflow.ReflowTextAnimatorHelper
import com.sungbin.kakaobot.source.hub.R
import com.sungbin.kakaobot.source.hub.utils.Utils
import gun0912.tedkeyboardobserver.TedKeyboardObserver
import kotlinx.android.synthetic.main.activity_login.*

import kotlinx.android.synthetic.main.content_login.*
import org.apache.commons.lang3.StringUtils
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private var verificationIdString: String? = null
    private lateinit var snsLoginCallBack: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        toolbar.title = ""
        setSupportActionBar(toolbar)

        if(!Utils.readData(applicationContext,
                "uid", "false")!!.toBoolean()) {
            Utils.toast(
                applicationContext,
                getString(R.string.welcome_join_auto_login),
                FancyToast.LENGTH_SHORT, FancyToast.SUCCESS)
            finish()
            startActivity(
                Intent(applicationContext, MainActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        mAuth = FirebaseAuth.getInstance()
        mAuth!!.setLanguageCode("kr")

        TedKeyboardObserver(this)
            .listen {
                if(it) copyright.visibility = View.GONE
                else copyright.visibility = View.VISIBLE
            }

        welcomeCenter.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                welcomeCenter.viewTreeObserver.removeOnPreDrawListener(this)

                val animator = ReflowTextAnimatorHelper
                    .Builder(welcomeCenter, welcomeTop)
                    .withDuration(1000, 3000).buildAnimator()

                animator.startDelay = 500
                animator.addListener(object : AnimatorListenerAdapter() {
                    @SuppressLint("RestrictedApi")
                    override fun onAnimationEnd(animation: Animator) {
                        welcomeCenter.visibility = View.GONE
                        welcomeTop.visibility = View.VISIBLE

                        val fade = AlphaAnimation(0f, 1f)
                        fade.duration = 1500

                        center_layout.visibility = View.VISIBLE
                        center_layout_below.visibility = View.VISIBLE
                        copyright.visibility = View.VISIBLE
                        input_done.visibility = View.VISIBLE

                        center_layout.animation = fade
                        center_layout_below.animation = fade
                        copyright.animation = fade
                        input_done.animation = fade
                    }
                })
                animator.start()

                return true
            }
        })

        send_check_number.setOnClickListener {
            if(StringUtils.isBlank(input_number.text.toString())){
                Utils.toast(applicationContext,
                    getString(R.string.please_input_number),
                    FancyToast.LENGTH_SHORT, FancyToast.WARNING)
                input_number_layout.error = getString(R.string.please_input_number)
            } else {
                if(input_number.text.toString().length != 11){
                    Utils.toast(applicationContext,
                        getString(R.string.please_input_correct_number),
                        FancyToast.LENGTH_SHORT, FancyToast.WARNING)
                    input_number_layout.error = getString(R.string.please_input_correct_number)
                }
                else {
                    input_number_layout.error = null
                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        input_number.text.toString().replaceFirst("0", "+82"),
                        60,
                        TimeUnit.SECONDS,
                        this,
                        snsLoginCallBack)
                    Utils.toast(applicationContext,
                        getString(R.string.send_code),
                        FancyToast.LENGTH_SHORT, FancyToast.SUCCESS)
                }
            }
        }

        snsLoginCallBack = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Utils.toast(applicationContext,
                    getString(R.string.cant_send_code_try_again),
                    FancyToast.LENGTH_SHORT, FancyToast.WARNING)
                Utils.error(applicationContext,
                    e, "Send SNS Login Code")
            }

            override fun onCodeSent(
                verificationId: String?,
                token: PhoneAuthProvider.ForceResendingToken) {
                Utils.toast(applicationContext,
                    getString(R.string.code_is_sent),
                    FancyToast.LENGTH_SHORT, FancyToast.SUCCESS)
                verificationIdString = verificationId
            }
        }

        input_done.setOnClickListener {
            if(StringUtils.isBlank(input_check_number.text.toString())){
                Utils.toast(applicationContext,
                    getString(R.string.please_input_code),
                    FancyToast.LENGTH_SHORT, FancyToast.WARNING)
                center_layout_below.error = getString(R.string.please_input_code)
            }
            else {
                if(input_check_number.text.toString().length != 6){
                    Utils.toast(applicationContext,
                        getString(R.string.code_is_six_num),
                        FancyToast.LENGTH_SHORT, FancyToast.WARNING)
                    center_layout_below.error = getString(R.string.code_is_six_num)
                }
                else {
                    center_layout_below.error = null
                    val credential = PhoneAuthProvider.getCredential(
                        verificationIdString!!,
                        input_check_number.text.toString())
                    mAuth!!.signInWithCredential(credential)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                Utils.toast(
                                    applicationContext,
                                    getString(R.string.welcome_join),
                                    FancyToast.LENGTH_SHORT, FancyToast.SUCCESS)
                                Utils.saveData(
                                    applicationContext,
                                    "uid", FirebaseAuth.getInstance().currentUser!!.uid)
                                finish()
                                startActivity(Intent(applicationContext, MainActivity::class.java)
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                            }
                            else {
                                Utils.toast(
                                    this,
                                    getString(R.string.number_is_not_matched),
                                    FancyToast.LENGTH_SHORT, FancyToast.WARNING)
                                center_layout_below.error = getString(R.string.number_is_not_matched)
                            }
                        }
                }
            }
        }
    }

}
