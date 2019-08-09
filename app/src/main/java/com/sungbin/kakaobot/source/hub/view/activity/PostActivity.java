package com.sungbin.kakaobot.source.hub.view.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.rarepebble.colorpicker.ColorPickerView;
import com.rustamg.filedialogs.FileDialog;
import com.rustamg.filedialogs.OpenFileDialog;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.sungbin.kakaobot.source.hub.R;
import com.sungbin.kakaobot.source.hub.dto.BoardDataItem;
import com.sungbin.kakaobot.source.hub.utils.DialogUtils;
import com.sungbin.kakaobot.source.hub.utils.Utils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.UUID;

import cn.pedant.SweetAlert.SweetAlertDialog;
import jp.wasabeef.richeditor.RichEditor;

public class PostActivity extends AppCompatActivity implements FileDialog.OnFileSelectedListener{

    private StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("Board Script");
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Board");
    private boolean error = false;
    private TextInputEditText inputTitle, inputDesc;
    private RichEditor mEditor;
    private AlertDialog alert;
    private ActionMode mActionMode;
    private Button upload;
    private File script = null;
    private String version = "0.0.1";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        final HorizontalScrollView scrollView = findViewById(R.id.scroll);
        upload = findViewById(R.id.uploadScript);

        inputTitle = findViewById(R.id.inputTitleText);
        inputDesc = findViewById(R.id.inputDescText);

        mEditor = findViewById(R.id.editor);
        mEditor.setEditorFontSize(17);
        mEditor.setPadding(10, 10, 10, 10);

        mEditor.setOnTouchListener((view, motionEvent) -> {
            if(mActionMode == null){
                mActionMode = startActionMode(mActionCallback, ActionMode.TYPE_FLOATING);
            }
            return false;
        });

        upload.setOnClickListener(v -> {
            Utils.toast(getApplicationContext(),
                    getResources().getString(R.string.select_upload_script),
                    FancyToast.LENGTH_SHORT,
                    FancyToast.SUCCESS);
            FileDialog dialog = new OpenFileDialog();
            Bundle args = new Bundle();
            args.putString(FileDialog.EXTENSION, "js");
            dialog.setArguments(args);
            dialog.show(getSupportFragmentManager(), OpenFileDialog.class.getName());
        });

        findViewById(R.id.action_undo).setOnClickListener(v -> mEditor.undo());

        findViewById(R.id.action_redo).setOnClickListener(v -> mEditor.redo());

        findViewById(R.id.action_bold).setOnClickListener(v -> mEditor.setBold());

        findViewById(R.id.action_italic).setOnClickListener(v -> mEditor.setItalic());

        findViewById(R.id.action_strikethrough).setOnClickListener(v -> mEditor.setStrikeThrough());

        findViewById(R.id.action_underline).setOnClickListener(v -> mEditor.setUnderline());

        findViewById(R.id.action_txt_color).setOnClickListener(v -> textColorSet());

        findViewById(R.id.action_indent).setOnClickListener(v -> mEditor.setIndent());

        findViewById(R.id.action_outdent).setOnClickListener(v -> mEditor.setOutdent());

        findViewById(R.id.action_align_left).setOnClickListener(v -> mEditor.setAlignLeft());

        findViewById(R.id.action_align_center).setOnClickListener(v -> mEditor.setAlignCenter());

        findViewById(R.id.action_align_right).setOnClickListener(v -> mEditor.setAlignRight());

        findViewById(R.id.action_blockquote).setOnClickListener(v -> mEditor.setBlockquote());

        findViewById(R.id.action_insert_bullets).setOnClickListener(v -> mEditor.setBullets());

        findViewById(R.id.action_insert_numbers).setOnClickListener(v -> mEditor.setNumbers());

        findViewById(R.id.action_insert_image).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                final Context ctx = PostActivity.this;
                AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
                dialog.setTitle(R.string.insert_image_title);

                LinearLayout layout = new LinearLayout(ctx);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText title = new EditText(ctx);
                title.setHint(R.string.input_image_title);
                layout.addView(title);

                final EditText adress = new EditText(ctx);
                adress.setHint(R.string.input_image_adress);
                layout.addView(adress);

                dialog.setView(layout);
                dialog.setNegativeButton("취소", null);
                dialog.setPositiveButton("확인", (dialogInterface, i) -> {
                    String titleStr = title.getText().toString();
                    String adressStr = adress.getText().toString();

                    if(StringUtils.isBlank(titleStr)||StringUtils.isBlank(adressStr)){
                        Utils.toast(ctx, getString(R.string.plz_input_all), FancyToast.SUCCESS, FancyToast.WARNING);
                    }
                    else{
                        mEditor.insertImage(adressStr, titleStr);
                        Utils.toast(ctx, getString(R.string.success_insert), FancyToast.LENGTH_SHORT, FancyToast.SUCCESS);
                    }
                });
                dialog.show();
            }
        });

        findViewById(R.id.action_insert_link).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                final Context ctx = PostActivity.this;
                AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
                dialog.setTitle(R.string.insert_link);

                LinearLayout layout = new LinearLayout(ctx);
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText title = new EditText(ctx);
                title.setHint(R.string.input_adress_title);
                layout.addView(title);

                final EditText adress = new EditText(ctx);
                adress.setHint(R.string.input_link_adress);
                layout.addView(adress);

                dialog.setView(DialogUtils.INSTANCE.makeMarginLayout(getResources(),
                        ctx, layout));
                dialog.setNegativeButton("취소", null);
                dialog.setPositiveButton("확인", (dialogInterface, i) -> {
                    String titleStr = title.getText().toString();
                    String adressStr = adress.getText().toString();

                    if(StringUtils.isBlank(titleStr)||StringUtils.isBlank(adressStr)){
                        Utils.toast(ctx, getString(R.string.plz_input_all), FancyToast.SUCCESS, FancyToast.WARNING);
                    }
                    else{
                        mEditor.insertLink(adressStr, titleStr);
                        Utils.toast(ctx, getString(R.string.success_insert), FancyToast.LENGTH_SHORT, FancyToast.SUCCESS);
                    }
                });
                dialog.show();
            }
        });
    }

    @Override
    public void onFileSelected(FileDialog dialog, File file) {
        upload.setText(file.getName());
        script = file;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        menu.add(0, 1, 0, getResources().getString(R.string.upload_string)).
                setIcon(R.drawable.ic_save_white_24dp)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == 1){
            if(!StringUtils.isBlank(inputTitle.getText().toString())
                    && !StringUtils.isBlank(inputDesc.getText().toString())
                    && !StringUtils.isBlank(mEditor.getHtml())){

                String key = UUID.randomUUID().toString().replace("-", "");
                DatabaseReference root = reference.child(key);
                BoardDataItem boardDataItem = new BoardDataItem(inputTitle.getText().toString(),
                        inputDesc.getText().toString(), 0, 0, upload.getText()+":V."+version,
                        version, key);
                root.setValue(boardDataItem);
                if(script == null){
                    FancyToast.makeText(getApplicationContext(), getString(R.string.success_request), FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                    finish();
                }
                else {
                    final SweetAlertDialog pDialog = new SweetAlertDialog(PostActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                    pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorPrimary));
                    pDialog.setTitle(getResources().getString(R.string.script_uploading));
                    pDialog.setCancelable(false);
                    pDialog.show();
                    Uri file = Uri.fromFile(script);
                    StorageReference riversRef = storageRef.child(upload.getText().toString());
                    riversRef.putFile(file).addOnFailureListener(exception -> {
                        pDialog.dismissWithAnimation();
                        Utils.toast(PostActivity.this,
                                "스크립트 업로드에 실패했습니다.\n\n"+exception.getMessage(),
                                FancyToast.LENGTH_SHORT,
                                FancyToast.ERROR);
                    }).addOnSuccessListener(taskSnapshot -> {
                        pDialog.dismissWithAnimation();
                        Utils.toast(PostActivity.this,
                                getResources().getString(R.string.script_upload_done),
                                FancyToast.LENGTH_SHORT,
                                FancyToast.SUCCESS);
                        finish();
                    });
                }
            }
            else{
                Utils.toast(getApplicationContext(),
                        getString(R.string.plz_input_all),
                        FancyToast.LENGTH_SHORT, FancyToast.WARNING);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void textColorSet(){
        alert = null;
        final Context ctx = PostActivity.this;
        AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);

        LinearLayout layout = new LinearLayout(getApplicationContext());
        layout.setBackgroundColor(Color.parseColor("#ffffff"));
        layout.setOrientation(LinearLayout.VERTICAL);

        final ColorPickerView picker = new ColorPickerView(ctx);
        picker.showAlpha(true);
        picker.showHex(true);
        picker.showPreview(true);
        picker.setColor(Color.parseColor("#00000000"));
        picker.setOriginalColor(Color.parseColor("#00000000"));
        picker.addColorObserver(observableColor -> picker.setOriginalColor(observableColor.getColor()));
        layout.addView(picker);

        Button save = new Button(ctx);
        save.setText("선택 완료");
        save.setOnClickListener(view -> {
            int color = picker.getColor();
            mEditor.setTextColor(color);
            Utils.toast(ctx, getString(R.string.color_select), FancyToast.LENGTH_SHORT, FancyToast.SUCCESS);
            alert.cancel();
        });
        layout.addView(save);
        dialog.setView(DialogUtils.INSTANCE.makeMarginLayout(getResources(),
                PostActivity.this, layout));

        alert = dialog.create();
        alert.show();
    }

    ActionMode.Callback2 mActionCallback = new ActionMode.Callback2() {
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            menu.add(0, 1, 0, "H1");
            menu.add(0, 2, 0, "H2");
            menu.add(0, 3, 0, "H3");
            menu.add(0, 4, 0, "H4");
            menu.add(0, 5, 0, "H5");
            menu.add(0, 6, 0, "H6");
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int id = item.getItemId();
            mEditor.setHeading(id);
            return false;
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK) {
            Uri fileUri = data.getData();
            Utils.toast(getApplicationContext(), fileUri.toString(),
                    FancyToast.LENGTH_SHORT, FancyToast.INFO);
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture){

    }
}
