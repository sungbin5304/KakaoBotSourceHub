package com.sungbin.autoreply.bot.three.view.bot.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fsn.cauly.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.marcoscg.licenser.Library
import com.marcoscg.licenser.License
import com.marcoscg.licenser.LicenserDialog
import com.savvyapps.togglebuttonlayout.ToggleButtonLayout
import com.sungbin.autoreply.bot.three.R
import com.sungbin.autoreply.bot.three.adapter.apps.AppListAdapter
import com.sungbin.autoreply.bot.three.api.Black
import com.sungbin.autoreply.bot.three.dto.apps.AppInfo
import com.sungbin.autoreply.bot.three.listener.KakaoTalkListener
import com.sungbin.autoreply.bot.three.utils.bot.BotNotificationManager
import com.sungbin.sungbintool.DataUtils
import com.sungbin.sungbintool.StringUtils
import com.sungbin.sungbintool.ToastUtils
import kotlinx.android.synthetic.main.fragment_setting.*
import java.text.Collator
import java.util.*
import kotlin.collections.ArrayList


class SettingFragment : Fragment(), CaulyAdViewListener, CaulyInterstitialAdListener {

    private var showInterstitial = false

    private var swBotOnoff: Switch? = null
    private var swAutoSave: Switch? = null
    private var swKeepScope: Switch? = null
    private var swNotHighting: Switch? = null
    private var swErrorBotOff: Switch? = null
    private var swNotErrorHighting: Switch? = null

    private var etTextSize: EditText? = null
    private var etPackages: EditText? = null
    private var etBlackRoom: EditText? = null
    private var etBlackSender: EditText? = null
    private var etHtmlLimitTime: EditText? = null

    private var sbTextSize: SeekBar? = null
    private var sbHtmlLimitTime: SeekBar? = null

    private var btnShowAd: Button? = null
    private var btnRemoveAd: Button? = null
    private var btnSelectApp: Button? = null
    private var btnShowLicense: Button? = null

    private var llAd: LinearLayout? = null
    private var svLayout: ScrollView? = null
    private var tvPreviewSize: TextView? = null
    private var fabSave: FloatingActionButton? = null
    private var tblFavorateLanguage: ToggleButtonLayout? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_setting, container, false)

        swAutoSave = view.findViewById(R.id.sw_auto_save)
        swBotOnoff = view.findViewById(R.id.sw_bot_onoff)
        swKeepScope = view.findViewById(R.id.sw_keep_scope)
        swNotHighting = view.findViewById(R.id.sw_not_highting)
        swErrorBotOff = view.findViewById(R.id.sw_error_bot_off)
        swNotErrorHighting = view.findViewById(R.id.sw_not_error_highting)

        etPackages = view.findViewById(R.id.et_packages)
        etTextSize = view.findViewById(R.id.et_text_size)
        etBlackRoom = view.findViewById(R.id.et_black_room)
        etBlackSender = view.findViewById(R.id.et_black_sender)
        etHtmlLimitTime = view.findViewById(R.id.et_html_limit_time)

        sbTextSize = view.findViewById(R.id.sb_text_size)
        sbHtmlLimitTime = view.findViewById(R.id.sb_html_limit_time)

        btnShowAd = view.findViewById(R.id.btn_show_ad)
        btnRemoveAd = view.findViewById(R.id.btn_remove_ad)
        btnSelectApp = view.findViewById(R.id.btn_select_app)
        tvPreviewSize = view.findViewById(R.id.tv_preview_size)
        btnShowLicense = view.findViewById(R.id.btn_show_license)

        llAd = view.findViewById(R.id.ll_ad)
        fabSave = view.findViewById(R.id.fab_save)
        svLayout = view.findViewById(R.id.sv_layout)
        tblFavorateLanguage = view.findViewById(R.id.tbl_favorite_langauge)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Black.init(context!!)

        val adInfo =
            CaulyAdInfoBuilder(getString(R.string.cauly_ad_id))
                .effect("TopSlide")
                .bannerHeight("Fixed_50").build()

        val adView = CaulyAdView(context)
        adView.setAdInfo(adInfo)
        adView.setAdViewListener(this)
        llAd!!.addView(adView)

        fabSave!!.hide()


        val textSize = DataUtils.readData(context!!, "TextSize", "17")
        val htmlLimitTime = DataUtils.readData(context!!, "HtmlLimitTime", "5")
        val packages = DataUtils.readData(context!!, "packages", "com.kakao.talk")
        val autoSave = DataUtils.readData(context!!, "AutoSave", "true").toBoolean()
        val keepScope = DataUtils.readData(context!!, "KeepScope", "false").toBoolean()
        val favoriteFanguage = DataUtils.readData(context!!, "FavoriteLanguage", "null")
        val notHighting = DataUtils.readData(context!!, "NotHighting", "false").toBoolean()
        val errorBotOff = DataUtils.readData(context!!, "ErrorBotOff", "false").toBoolean()
        val notErrorHighting = DataUtils.readData(context!!, "NotErrorHighting", "false").toBoolean()

        swAutoSave!!.isChecked = autoSave
        swKeepScope!!.isChecked = keepScope
        swNotHighting!!.isChecked = notHighting
        swErrorBotOff!!.isChecked = errorBotOff
        tvPreviewSize!!.textSize = textSize.toFloat()
        swNotErrorHighting!!.isChecked = notErrorHighting
        etTextSize!!.text = StringUtils.toEditable(textSize)
        etPackages!!.text = StringUtils.toEditable(packages)
        etHtmlLimitTime!!.text = StringUtils.toEditable(htmlLimitTime)

        etBlackRoom!!.text = StringUtils.toEditable(Black.readRoom().trim())
        etBlackSender!!.text = StringUtils.toEditable(Black.readSender().trim())

        if(favoriteFanguage != "null"){
            if(favoriteFanguage == "자바스크립트"){
                tblFavorateLanguage!!.setToggled(R.id.javascript, true)
            }
            else {
                tblFavorateLanguage!!.setToggled(R.id.simple, true)
            }
        }

        swBotOnoff!!.isChecked = DataUtils.readData(context!!, "BotOn", "true").toBoolean()
        swBotOnoff!!.setOnCheckedChangeListener { _, boolean ->
            if(boolean){
                BotNotificationManager.create(context!!)
            }
            else {
                BotNotificationManager.delete(context!!)
            }
        }

        swAutoSave!!.setOnCheckedChangeListener { _, boolean ->
            DataUtils.saveData(context!!, "AutoSave", boolean.toString())
        }

        swKeepScope!!.setOnCheckedChangeListener { _, boolean ->
            DataUtils.saveData(context!!, "KeepScope", boolean.toString())
        }

        swNotHighting!!.setOnCheckedChangeListener { _, boolean ->
            DataUtils.saveData(context!!, "NotHighting", boolean.toString())
        }

        swErrorBotOff!!.setOnCheckedChangeListener { _, boolean ->
            DataUtils.saveData(context!!, "ErrorBotOff", boolean.toString())
        }

        swNotErrorHighting!!.setOnCheckedChangeListener { _, boolean ->
            DataUtils.saveData(context!!, "NotErrorHighting", boolean.toString())
        }

        sbTextSize!!.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                etTextSize!!.setText(i.toString())
                tvPreviewSize!!.textSize = i.toFloat()
                DataUtils.saveData(context!!, "TextSize", i.toString())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        sbHtmlLimitTime!!.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                etHtmlLimitTime!!.setText(i.toString())
                DataUtils.saveData(context!!, "HtmlLimitTime", i.toString())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        etTextSize!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun afterTextChanged(editable: Editable) {
                try {
                    etTextSize!!.setSelection(etTextSize!!.text.length)
                    val i = editable.toString().toInt()
                    if (i < 1 || i > 30) {
                        ToastUtils.show(
                            context!!,
                            getString(R.string.can_set_one_thirty),
                            ToastUtils.SHORT,
                            ToastUtils.WARNING
                        )
                    }
                    else {
                        sbTextSize!!.progress = i
                        tvPreviewSize!!.textSize = i.toFloat()
                        DataUtils.saveData(context!!, "TextSize", i.toString())
                    }
                }
                catch (e: Exception) {
                    ToastUtils.show(
                        context!!,
                        getString(R.string.can_set_one_thirty),
                        ToastUtils.SHORT,
                        ToastUtils.WARNING
                    )
                }
            }
        })

        etHtmlLimitTime!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun afterTextChanged(editable: Editable) {
                try {
                    etHtmlLimitTime!!.setSelection(etHtmlLimitTime!!.text.length)
                    val i = editable.toString().toInt()
                    if (i < 1 || i > 10) {
                        ToastUtils.show(
                            context!!,
                            getString(R.string.can_set_one_ten),
                            ToastUtils.SHORT,
                            ToastUtils.WARNING
                        )
                    }
                    else {
                        sbHtmlLimitTime!!.progress = i
                        DataUtils.saveData(context!!, "HtmlLimitTime", i.toString())
                    }
                }
                catch (e: Exception) {
                    ToastUtils.show(
                        context!!,
                        getString(R.string.can_set_one_ten),
                        ToastUtils.SHORT,
                        ToastUtils.WARNING
                    )
                }
            }
        })

        btnShowAd!!.setOnClickListener {
            ToastUtils.show(
                context!!,
                getString(R.string.string_thanks),
                ToastUtils.LONG,
                ToastUtils.SUCCESS
            )
            @Suppress("NAME_SHADOWING")
            val adInfo = CaulyAdInfoBuilder(getString(R.string.cauly_ad_id)).build()
            val interstial = CaulyInterstitialAd()
            interstial.setAdInfo(adInfo)
            interstial.requestInterstitialAd(activity)
            interstial.setInterstialAdListener(this)
            showInterstitial = true
        }

        btnShowLicense!!.setOnClickListener {
            showLicenseDialog()
        }

        btnSelectApp!!.setOnClickListener {
            showAppSelectDialog()
        }

        fabSave!!.setOnClickListener {
            @Suppress("NAME_SHADOWING")
            val packages = etPackages!!.text.toString()
            val blackRoom = etBlackRoom!!.text.toString()
            val blackSender = etBlackSender!!.text.toString()

            DataUtils.saveData(context!!, "packages", packages)
            DataUtils.saveData(context!!, "RoomBlackList", blackRoom)
            DataUtils.saveData(context!!, "SenderBlackList", blackSender)

            if(tblFavorateLanguage!!.selectedToggles().isNotEmpty()) {
                DataUtils.saveData(
                    context!!,
                    "FavoriteLanguage",
                    tblFavorateLanguage!!.selectedToggles()[0].title.toString()
                )
            }

            ToastUtils.show(
                context!!,
                getString(R.string.string_saved),
                ToastUtils.SHORT,
                ToastUtils.SUCCESS
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            svLayout!!.setOnScrollChangeListener { _, _, y, _, oldY ->
                 if (y > oldY) { //Down
                     fabSave!!.show()
                 }
                 if (y < oldY) { //Up
                     fabSave!!.hide()
                 }
             }
        }

    }

    /*----- 전면 광고 -----*/
    override fun onReceiveInterstitialAd(
        ad: CaulyInterstitialAd,
        isChargeableAd: Boolean
    ) {
        // 전면 광고 호출 성공
        if (showInterstitial) {
            ad.show()
            showInterstitial = false
        } else ad.cancel()
    }

    override fun onFailedToReceiveInterstitialAd(
        ad: CaulyInterstitialAd?,
        errorCode: Int,
        errorMsg: String?
    ) {
        // 전면 광고 수신 실패할 경우 호출됨.
    }

    override fun onClosedInterstitialAd(ad: CaulyInterstitialAd?) {
        // 전면 광고가 닫기 버튼으로 닫힌 경우 호출됨.
    }

    override fun onLeaveInterstitialAd(arg: CaulyInterstitialAd?) {
        // 전면 광고가 뒤로가기 버튼으로 닫힌 경우 호출됨
    }
    /*----- 전면 광고 끝 -----*/

    /*----- 배너 광고 -----*/
    override fun onReceiveAd(adView: CaulyAdView?, isChargeableAd: Boolean) {
        // 광고 수신 성공 & 노출된 경우 호출됨.
        // 수신된 광고가 무료 광고인 경우 isChargeableAd 값이 false 임.
    }

    override fun onFailedToReceiveAd(
        adView: CaulyAdView?,
        errorCode: Int,
        errorMsg: String?
    ) {
        // 배너 광고 수신 실패할 경우 호출됨.
    }

    override fun onShowLandingScreen(adView: CaulyAdView?) {
        // 광고 배너를 클릭하여 랜딩 페이지가 열린 경우 호출됨.
    }

    override fun onCloseLandingScreen(adView: CaulyAdView?) {
        // 광고 배너를 클릭하여 랜딩 페이지가 닫힌 경우 호출됨.
    }
    /*----- 배너 광고 끝 -----*/

    @Suppress("DEPRECATION")
    @SuppressLint("WrongConstant")
    fun getAppList(): ArrayList<AppInfo> {
        val pm = context!!.packageManager

        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val appList = pm.queryIntentActivities(intent, 0)

        val appInfoList = ArrayList<AppInfo>()
        for (app in appList) {
            val name = app.loadLabel(pm).toString()
            val icon = app.loadIcon(pm)
            val packageString = app.activityInfo.packageName
            appInfoList.add(AppInfo(name, icon, packageString))
        }
        Collections.sort(
            appInfoList.toList()
        ) { object1, object2 ->
            Collator.getInstance().compare(object1!!.name, object2!!.name)
        }
        return appInfoList
    }

    private fun showAppSelectDialog(){
        ToastUtils.show(
            context!!,
            context!!.getString(R.string.load_app_list),
            ToastUtils.SHORT,
            ToastUtils.INFO
        )

        Thread {
            val appInfoList = getAppList()

            val layout = LayoutInflater
                .from(context)
                .inflate(
                    R.layout.layout_app_list_dialog,
                    null,
                    false
                )

            val dialog = AlertDialog.Builder(context)
            dialog.setTitle("어플리케이션 선택")

            val recyclerView = layout.findViewById<RecyclerView>(R.id.rv_apps)
            val editText = layout.findViewById<EditText>(R.id.et_search)
            val adapter = AppListAdapter(appInfoList)

            recyclerView.layoutManager = LinearLayoutManager(
                activity,
                LinearLayoutManager.VERTICAL,
                false
            )
            recyclerView.adapter = adapter

            editText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(editable: Editable?) {
                    adapter.search(editable.toString())
                    recyclerView.smoothScrollToPosition(0)
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

            })

            dialog.setView(layout)
            var alert: AlertDialog? = null

            activity!!.runOnUiThread {
                alert = dialog.create()
                alert!!.show()
            }

            adapter.setOnAppClickListener {
                activity!!.runOnUiThread {
                    et_packages.text = StringUtils.toEditable("${et_packages.text}\n$it")
                    alert!!.cancel()
                }
            }


        }.start()
    }

    private fun showLicenseDialog(){
        LicenserDialog(activity)
            .setTitle("Opensource Licenses")
            .setCustomNoticeTitle("Licenses for Libraries: ")
            .setLibrary(
                Library(
                    "Kotlin",
                    "https://github.com/JetBrains/kotlin",
                    License.APACHE2)
            )
            .setLibrary(
                Library(
                    "Android Support Libraries",
                    "https://developer.android.com/topic/libraries/support-library/index.html",
                    License.APACHE2
                )
            )
            .setLibrary(
                Library(
                    "Firebase",
                    "https://github.com/firebase/quickstart-android",
                    License.APACHE2
                )
            )
            .setLibrary(
                Library(
                    "Google Play Service",
                    "play-services-plugins",
                    License.APACHE2
                )
            )
            .setLibrary(
                Library(
                    "ChatKit",
                    "https://github.com/stfalcon-studio/ChatKit",
                    License.APACHE2
                )
            )
            .setLibrary(
                Library(
                    "file-dialogs",
                    "https://github.com/RustamG/file-dialogs",
                    License.MIT

                )
            )
            .setLibrary(
                Library(
                    "Licenser",
                    "https://github.com/marcoscgdev/Licenser",
                    License.MIT
                )
            )
            .setLibrary(
                Library(
                    "Glide",
                    "https://github.com/bumptech/glide",
                    License.BSD3
                )
            )
            .setLibrary(
                Library(
                    "SweetAlertDialog",
                    "https://github.com/F0RIS/sweet-alert-dialog",
                    License.MIT
                )
            )
            .setLibrary(
                Library(
                    "AndroidUtils",
                    "https://github.com/sungbin5304/AndroidUtils",
                    License.APACHE2
                )
            )
            .setLibrary(
                Library(
                    "SmoothBottomBar",
                    "https://github.com/ibrahimsn98/SmoothBottomBar",
                    License.MIT
                )
            )
            .setLibrary(
                Library(
                    "ToggleButtonLayout",
                    "https://github.com/savvyapps/ToggleButtonLayout",
                    License.MIT
                )
            )
            .setLibrary(
                Library(
                    "MaterialPopupMenu",
                    "https://github.com/zawadz88/MaterialPopupMenu",
                    License.APACHE2
                )
            )
            .setLibrary(
                Library(
                    "Jsoup",
                    "https://jsoup.org/",
                    License.MIT
                )
            )
            .setLibrary(
                Library(
                    "commons io",
                    "https://github.com/apache/commons-io",
                    License.APACHE2
                )
            )
            .setLibrary(
                Library(
                    "hsv alph color picker android",
                    "https://github.com/martin-stone/hsv-alpha-color-picker-android",
                    License.APACHE2
                )
            )
            .setLibrary(
                Library(
                    "rhino android",
                    "https://github.com/F43nd1r/rhino-android",
                    License.APACHE2
                )
            )
            .setLibrary(
                Library(
                    "CrashReporter",
                    "https://github.com/MindorksOpenSource/CrashReporter",
                    License.APACHE2
                )
            )
            .setLibrary(
                Library(
                    "SmartTabLayout",
                    "https://github.com/ogaclejapan/SmartTabLayout",
                    License.APACHE2
                )
            )
            .setLibrary(
                Library(
                    "richeditor android",
                    "https://github.com/wasabeef/richeditor-android",
                    License.APACHE2
                )
            )
            .setLibrary(
                Library(
                    "TedImagePicker",
                    "https://github.com/ParkSangGwon/TedImagePicker",
                    License.APACHE2
                )
            )
            .setLibrary(
                Library(
                    "Kakao SDK",
                    "https://developers.kakao.com/docs/latest/ko/sdk-download/android",
                    License.APACHE2
                )
            )
            .setLibrary(
                Library(
                    "Lottie",
                    "https://github.com/airbnb/lottie-android",
                    License.APACHE2
                )
            )
            .setLibrary(
                Library(
                    "HttpComponents",
                    "https://github.com/apache/httpcomponents-client",
                    License.APACHE2
                )
            )
            .setLibrary(
                Library(
                    "material components android",
                    "https://github.com/material-components/material-components-android",
                    License.APACHE2
                )
            )
            .setLibrary(
                Library(
                    "MaterialSpinner",
                    "https://github.com/jaredrummler/MaterialSpinner",
                    License.APACHE2
                )
            )
            .setLibrary(
                Library(
                    "Android View Animations",
                    "https://github.com/daimajia/AndroidViewAnimations",
                    License.MIT
                )
            )
            .setLibrary(
                Library(
                    "Cauly SDK",
                    "https://github.com/cauly/Android-SDK",
                    License.MIT
                )
            )
            .show()
    }

}