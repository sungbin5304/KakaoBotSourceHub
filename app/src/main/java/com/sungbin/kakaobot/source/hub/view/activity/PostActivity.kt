package com.sungbin.kakaobot.source.hub.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.view.*
import android.widget.*

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.rarepebble.colorpicker.ColorPickerView
import com.rustamg.filedialogs.FileDialog
import com.rustamg.filedialogs.OpenFileDialog
import com.shashank.sony.fancytoastlib.FancyToast
import com.sungbin.kakaobot.source.hub.R
import com.sungbin.kakaobot.source.hub.dto.BoardDataItem
import com.sungbin.kakaobot.source.hub.utils.DialogUtils
import com.sungbin.kakaobot.source.hub.utils.Utils
import org.apache.commons.lang3.StringUtils

import java.io.File
import java.util.UUID

import cn.pedant.SweetAlert.SweetAlertDialog
import com.sungbin.kakaobot.source.hub.notification.NotificationManager
import com.sungbin.kakaobot.source.hub.utils.FirebaseUtils
import jp.wasabeef.richeditor.RichEditor

@Suppress("DEPRECATION", "UNUSED_ANONYMOUS_PARAMETER")
class PostActivity : AppCompatActivity(), FileDialog.OnFileSelectedListener {

    private val storageRef = FirebaseStorage.getInstance().reference.child("Board Script")
    private val reference = FirebaseDatabase.getInstance().reference.child("Board")
    private var inputTitle: TextInputEditText? = null
    private var inputDesc: TextInputEditText? = null
    private var mEditor: RichEditor? = null
    private var alert: AlertDialog? = null
    private var mActionMode: ActionMode? = null
    private var upload: Button? = null
    private var script: File? = null
    private var version = "000"
    private var nickname: String? = null

    private var mActionCallback = @SuppressLint("NewApi")
    object : ActionMode.Callback2() {
            override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
                return false
            }

            override fun onDestroyActionMode(mode: ActionMode) {
                mActionMode = null
            }

            override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                menu.add(0, 1, 0, "H1")
                menu.add(0, 2, 0, "H2")
                menu.add(0, 3, 0, "H3")
                menu.add(0, 4, 0, "H4")
                menu.add(0, 5, 0, "H5")
                menu.add(0, 6, 0, "H6")
                return true
            }

            override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
                val id = item.itemId
                mEditor!!.setHeading(id)
                return false
            }
        }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = ""
        setSupportActionBar(toolbar)

        nickname = Utils.readData(applicationContext, "nickname", "qwertyuiopqwert")
        if(nickname == "qwertyuiopqwert"){
            Utils.toast(applicationContext,
                getString(R.string.please_app_refresh),
                FancyToast.LENGTH_LONG, FancyToast.WARNING)
            finish()
        }

        upload = findViewById(R.id.uploadScript)

        inputTitle = findViewById(R.id.inputTitleText)
        inputDesc = findViewById(R.id.inputDescText)

        mEditor = findViewById(R.id.editor)
        mEditor!!.setEditorFontSize(17)
        mEditor!!.setPadding(10, 10, 10, 10)

        mEditor!!.setOnTouchListener { _, _ ->
            if (mActionMode == null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mActionMode = startActionMode(mActionCallback, ActionMode.TYPE_FLOATING)
                }
            }
            false
        }

        upload!!.setOnClickListener { v ->
            val permissionlistener = object : PermissionListener {
                override fun onPermissionGranted() {
                    Utils.toast(
                        applicationContext,
                        resources.getString(R.string.select_upload_script),
                        FancyToast.LENGTH_SHORT,
                        FancyToast.SUCCESS)
                    val dialog = OpenFileDialog()
                    val args = Bundle()
                    args.putString(FileDialog.EXTENSION, "js")
                    dialog.arguments = args
                    dialog.show(supportFragmentManager, OpenFileDialog::class.java.name)
                }

                override fun onPermissionDenied(deniedPermissions: List<String>) {
                    Utils.toast(
                        applicationContext,
                        getString(R.string.how_to_give_permission),
                        FancyToast.LENGTH_SHORT, FancyToast.WARNING)
                }
            }
            TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .setDeniedMessage(R.string.how_to_give_permission)
                .setDeniedTitle(R.string.need_permission)
                .setRationaleTitle(R.string.need_permission)
                .setRationaleMessage(R.string.why_need_permission)
                .check()
        }

        findViewById<View>(R.id.action_undo).setOnClickListener { v -> mEditor!!.undo() }

        findViewById<View>(R.id.action_redo).setOnClickListener { v -> mEditor!!.redo() }

        findViewById<View>(R.id.action_bold).setOnClickListener { v -> mEditor!!.setBold() }

        findViewById<View>(R.id.action_italic).setOnClickListener { v -> mEditor!!.setItalic() }

        findViewById<View>(R.id.action_strikethrough).setOnClickListener { v -> mEditor!!.setStrikeThrough() }

        findViewById<View>(R.id.action_underline).setOnClickListener { v -> mEditor!!.setUnderline() }

        findViewById<View>(R.id.action_txt_color).setOnClickListener { v -> textColorSet() }

        findViewById<View>(R.id.action_indent).setOnClickListener { v -> mEditor!!.setIndent() }

        findViewById<View>(R.id.action_outdent).setOnClickListener { v -> mEditor!!.setOutdent() }

        findViewById<View>(R.id.action_align_left).setOnClickListener { v -> mEditor!!.setAlignLeft() }

        findViewById<View>(R.id.action_align_center).setOnClickListener { v -> mEditor!!.setAlignCenter() }

        findViewById<View>(R.id.action_align_right).setOnClickListener { v -> mEditor!!.setAlignRight() }

        findViewById<View>(R.id.action_blockquote).setOnClickListener { v -> mEditor!!.setBlockquote() }

        findViewById<View>(R.id.action_insert_bullets).setOnClickListener { v -> mEditor!!.setBullets() }

        findViewById<View>(R.id.action_insert_numbers).setOnClickListener { v -> mEditor!!.setNumbers() }

        findViewById<View>(R.id.action_insert_image).setOnClickListener {
            val ctx = this@PostActivity
            val dialog = AlertDialog.Builder(ctx)
            dialog.setTitle(R.string.insert_image_title)

            val layout = LinearLayout(ctx)
            layout.orientation = LinearLayout.VERTICAL

            val title = EditText(ctx)
            title.setHint(R.string.input_image_title)
            layout.addView(title)

            val adress = EditText(ctx)
            adress.setHint(R.string.input_image_adress)
            layout.addView(adress)

            dialog.setView(layout)
            dialog.setNegativeButton("취소", null)
            dialog.setPositiveButton("확인") { dialogInterface, i ->
                val titleStr = title.text.toString()
                val adressStr = adress.text.toString()

                if (StringUtils.isBlank(titleStr) || StringUtils.isBlank(adressStr)) {
                    Utils.toast(ctx, getString(R.string.plz_input_all), FancyToast.SUCCESS, FancyToast.WARNING)
                } else {
                    mEditor!!.insertImage(adressStr, titleStr)
                    Utils.toast(ctx, getString(R.string.success_insert), FancyToast.LENGTH_SHORT, FancyToast.SUCCESS)
                }
            }
            dialog.show()
        }

        findViewById<View>(R.id.action_insert_link).setOnClickListener {
            val ctx = this@PostActivity
            val dialog = AlertDialog.Builder(ctx)
            dialog.setTitle(R.string.insert_link)

            val layout = LinearLayout(ctx)
            layout.orientation = LinearLayout.VERTICAL

            val title = EditText(ctx)
            title.setHint(R.string.input_adress_title)
            layout.addView(title)

            val adress = EditText(ctx)
            adress.setHint(R.string.input_link_adress)
            layout.addView(adress)

            dialog.setView(
                DialogUtils.makeMarginLayout(
                    resources,
                    ctx, layout
                )
            )
            dialog.setNegativeButton("취소", null)
            dialog.setPositiveButton("확인") { _, _ ->
                val titleStr = title.text.toString()
                val adressStr = adress.text.toString()

                if (StringUtils.isBlank(titleStr) || StringUtils.isBlank(adressStr)) {
                    Utils.toast(ctx, getString(R.string.plz_input_all), FancyToast.SUCCESS, FancyToast.WARNING)
                } else {
                    mEditor!!.insertLink(adressStr, titleStr)
                    Utils.toast(ctx, getString(R.string.success_insert), FancyToast.LENGTH_SHORT, FancyToast.SUCCESS)
                }
            }
            dialog.show()
        }
    }

    override fun onFileSelected(dialog: FileDialog, file: File) {
        upload!!.text = (file.name.toString()).replace(".JS", ".js")
        script = file
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(0, 1, 0, resources.getString(R.string.upload_string)).setIcon(R.drawable.ic_save_white_24dp)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == 1) {
            if (!StringUtils.isBlank(inputTitle!!.text!!.toString())
                && !StringUtils.isBlank(inputDesc!!.text!!.toString())
                && !StringUtils.isBlank(mEditor!!.html)) {

                val ctx: Context = this@PostActivity
                var key = Utils.makeRandomUUID()
                if (script == null) {
                    val root = reference.child(key)
                    val boardDataItem = BoardDataItem(
                        inputTitle!!.text!!.toString(),
                        inputDesc!!.text!!.toString(), 0, 0, upload!!.text.toString() + ":V." + version,
                        version, key, nickname, mEditor!!.html)
                    root.setValue(boardDataItem)
                    Utils.toast(ctx,
                        getString(R.string.success_request),
                        FancyToast.LENGTH_SHORT, FancyToast.SUCCESS)
                    FirebaseUtils.showNoti(applicationContext,
                        "새글 알림", inputTitle!!.text!!.toString(),
                        "NewPostNoti")
                    finish()
                } else {
                    val dialog = AlertDialog.Builder(ctx)
                    dialog.setTitle(getString(R.string.set_script_version))

                    val layout = LinearLayout(ctx)
                    layout.orientation = LinearLayout.VERTICAL

                    val textview = TextView(ctx)
                    textview.text = getString(R.string.same_version_notice)
                    textview.gravity = Gravity.CENTER
                    layout.addView(textview)

                    val input = EditText(ctx)
                    input.hint = getString(R.string.string_script_version)
                    input.inputType = 0x00000002
                    input.filters = arrayOf(InputFilter.LengthFilter(3))
                    layout.addView(input)

                    dialog.setView(DialogUtils.makeMarginLayout(resources,
                        ctx, layout))
                    dialog.setNeutralButton(getString(R.string.cancel_script_upload)) { _, _ ->
                        val root = reference.child(key)
                        val boardDataItem = BoardDataItem(
                            inputTitle!!.text!!.toString(),
                            inputDesc!!.text!!.toString(), 0, 0, "null",
                            version, key, nickname, mEditor!!.html)
                        root.setValue(boardDataItem)
                        Utils.toast(ctx,
                            getString(R.string.upload_post_cancel_script),
                            FancyToast.LENGTH_SHORT, FancyToast.SUCCESS)
                        finish()
                    }
                    dialog.setPositiveButton(getString(R.string.string_upload)) { _, _ ->
                        version = input.text.toString()
                        if (StringUtils.isBlank(version)) {
                            Utils.toast(ctx,
                                "스크립트 버전을 입력해 주세요.",
                                FancyToast.LENGTH_SHORT, FancyToast.WARNING)
                        } else {
                            val pDialog = SweetAlertDialog(ctx, SweetAlertDialog.PROGRESS_TYPE)
                            pDialog.progressHelper.barColor = resources.getColor(R.color.colorPrimary)
                            pDialog.setTitle(resources.getString(R.string.script_uploading))
                            pDialog.setCancelable(false)
                            pDialog.show()
                            key = upload!!.text.toString().replace(".", "") + ":V:" + version
                            val file = Uri.fromFile(script)
                            val riversRef = storageRef.child(key)
                            riversRef.putFile(file).addOnFailureListener { exception ->
                                pDialog.dismissWithAnimation()
                                Utils.toast(ctx,
                                    "스크립트 업로드에 실패했습니다.\n\n" + exception.message,
                                    FancyToast.LENGTH_SHORT,
                                    FancyToast.ERROR)
                            }.addOnSuccessListener { taskSnapshot ->
                                pDialog.dismissWithAnimation()
                                Utils.toast(ctx,
                                    resources.getString(R.string.script_upload_done),
                                    FancyToast.LENGTH_SHORT,
                                    FancyToast.SUCCESS)

                                val root = reference.child(key)
                                val boardDataItem = BoardDataItem(
                                    inputTitle!!.text!!.toString(),
                                    inputDesc!!.text!!.toString(), 0, 0,
                                    upload!!.text.toString().replace(".", ""),
                                    version, key, nickname, mEditor!!.html)
                                root.setValue(boardDataItem)
                                finish()
                            }
                        }
                    }
                    dialog.setCancelable(false)
                    dialog.show()
                }
            } else {
                Utils.toast(applicationContext,
                    getString(R.string.plz_input_all),
                    FancyToast.LENGTH_SHORT, FancyToast.WARNING)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun textColorSet() {
        alert = null
        val ctx = this@PostActivity
        val dialog = AlertDialog.Builder(ctx)

        val layout = LinearLayout(applicationContext)
        layout.setBackgroundColor(Color.parseColor("#ffffff"))
        layout.orientation = LinearLayout.VERTICAL

        val picker = ColorPickerView(ctx)
        picker.showAlpha(true)
        picker.showHex(true)
        picker.showPreview(true)
        picker.color = Color.parseColor("#00000000")
        picker.setOriginalColor(Color.parseColor("#00000000"))
        picker.addColorObserver { observableColor -> picker.setOriginalColor(observableColor.color) }
        layout.addView(picker)

        val save = Button(ctx)
        save.text = "선택 완료"
        save.setOnClickListener { view ->
            val color = picker.color
            mEditor!!.setTextColor(color)
            Utils.toast(ctx, getString(R.string.color_select), FancyToast.LENGTH_SHORT, FancyToast.SUCCESS)
            alert!!.cancel()
        }
        layout.addView(save)
        dialog.setView(
            DialogUtils.makeMarginLayout(
                resources,
                this@PostActivity, layout
            )
        )

        alert = dialog.create()
        alert!!.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val fileUri = data!!.data
            Utils.toast(applicationContext, fileUri!!.toString(),
                FancyToast.LENGTH_SHORT, FancyToast.INFO)
        }
    }

    override fun onPointerCaptureChanged(hasCapture: Boolean) {

    }
}
