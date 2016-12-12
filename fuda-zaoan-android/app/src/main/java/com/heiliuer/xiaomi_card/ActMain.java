package com.heiliuer.xiaomi_card;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.GsonBuilder;

import java.util.LinkedHashMap;

public class ActMain extends ActBase {
    private ScreenLockChecker screenLockChecker;
    public static final int REQUEST_CODE_ACT_BUSCARD = 22;

    public static final int REQUEST_CODE_CLOSE_NFC = 30;
    private static final int REQUEST_CODE_OPEN_NFC = 40;

    private SinglonToast singlonToast;

    private SharedPreferences commomShare;
    private SharedPreferences.Editor editCommomShare;
    private View handleView;
    private EditText editUserID, editUserKey;
    private TextView txtResult;
    private GsonBuilder prettyGsonBuilder;
    private View btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        editUserID = (EditText) findViewById(R.id.edit_UserID);
        editUserKey = (EditText) findViewById(R.id.edit_UserKey);

        initShareEditText(editUserID, Constants.SHARE_KEY_STR_USER_ID);
        initShareEditText(editUserKey, Constants.SHARE_KEY_STR_USER_KEY);

//        editUserID.setText("161127054");
//        editUserKey.setText("221511");

        prettyGsonBuilder = new GsonBuilder().setPrettyPrinting();

        txtResult = (TextView) findViewById(R.id.txt_result);

        commomShare = ApplicationMy.sharedPreferences;
        editCommomShare = ApplicationMy.editShare;


        singlonToast = new SinglonToast(this);


        screenLockChecker = new ScreenLockChecker(this);

        initEvents();

        if (commomShare.getBoolean(Constants.SHARE_KEY_IS_OPEN_CARD_ON_ENTER, true) || screenLockChecker.isScreenLocked()) {
        }


        if (commomShare.getBoolean(Constants.SHARE_KEY_IS_AUTO_CLICK_IN_LOCKED, true)) {
            startMainService();
        }

    }

    private void initShareEditText(EditText editText, String shareKey) {
        editText.setText(ApplicationMy.sharedPreferences.getString(shareKey, ""));
    }

    private void startMainService() {
        if (ServiceMain.SERVICE_MAIN_RUNNING == null) {
            startService(new Intent(this, ServiceMain.class));
        }
    }


    private void initEvents() {

        handleView = findViewById(R.id.imgBtn_info);
        handleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActMain.this, ActInfo.class));
            }
        });


        btnStart = findViewById(R.id.btn_start);
        btnStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final String UserID = getUserID();
                final String UserKey = getUserKey();
                if (TextUtils.isEmpty(UserID) || TextUtils.isEmpty(getUserKey())) {
                    singlonToast.showSinglonToast(R.string.empty_user_login_params, false);
                    return;
                }
                saveUserEditText();
                btnStart.setEnabled(false);
                new Thread() {
                    @Override
                    public void run() {
                        final LinkedHashMap<String, Result> results = SignMain.sign(UserID, UserKey);

                        handleView.post(new Runnable() {
                            @Override
                            public void run() {
                                btnStart.setEnabled(true);
                                refreshResultOnUiThread(results);
                            }
                        });
                    }
                }.start();
            }
        });


        initCheckedTextEvent(R.id.check_auto_click_in_locked, Constants.SHARE_KEY_IS_AUTO_CLICK_IN_LOCKED, true, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((CheckedTextView) v).isChecked();
                if (checked) {
                    startMainService();
                } else {
                    if (ServiceMain.SERVICE_MAIN_RUNNING != null) {
                        stopService(new Intent(ActMain.this, ServiceMain.class));
                    }
                }

                singlonToast.showSinglonToast(checked ? R.string.opened_service : R.string.closed_service, false);
            }
        });

        initCheckedTextEvent(R.id.check_close_nfc, Constants.SHARE_KEY_IS_CLOSE_NFC_AFTER, false, null);
        initCheckedTextEvent(R.id.check_open_card_on_enter, Constants.SHARE_KEY_IS_OPEN_CARD_ON_ENTER, true, null);
        initCheckedTextEvent(R.id.check_auto_click_card, Constants.SHARE_KEY_IS_AUTO_CLICK_CARD, true, null);
    }

    private void saveUserEditText() {
        editCommomShare.putString(Constants.SHARE_KEY_STR_USER_KEY, editUserKey.getText().toString());
        editCommomShare.putString(Constants.SHARE_KEY_STR_USER_ID, editUserID.getText().toString());
        editCommomShare.apply();
    }

    private void refreshResultOnUiThread(LinkedHashMap<String, Result> results) {
        String resultsStr = prettyGsonBuilder.create().toJson(results);
        singlonToast.showSinglonToast(resultsStr, true);
        txtResult.setText(resultsStr);
    }

    private String getUserKey() {
        return editUserKey.getText().toString();
    }

    private String getUserID() {
        return editUserID.getText().toString();
    }


    private void initCheckedTextEvent(int checkResId, final String key, boolean defValue, final View.OnClickListener toggleListener) {
        final CheckedTextView checkedTextView = (CheckedTextView) findViewById(checkResId);
        checkedTextView.setChecked(ApplicationMy.sharedPreferences.getBoolean(key, defValue));
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editCommomShare == null) {
                    ApplicationMy.editShare = commomShare.edit();
                }
                checkedTextView.toggle();
                editCommomShare.putBoolean(key, checkedTextView.isChecked());
                editCommomShare.apply();
                if (toggleListener != null) {
                    toggleListener.onClick(v);
                }
            }
        };
        checkedTextView.setOnClickListener(onClickListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (editCommomShare != null) {
            editCommomShare.commit();
        }
    }


    private void confirmCloseNfcWithDialog() {
        AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
        alertbox.setTitle(R.string.info);
        alertbox.setMessage(getString(R.string.msg_nfcoff));
        alertbox.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertbox.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActMain.this.finish();
            }
        });
        alertbox.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_CLOSE_NFC:
                // this.finish();
                break;
            case REQUEST_CODE_OPEN_NFC:
                break;
            case REQUEST_CODE_ACT_BUSCARD:
                if (screenLockChecker.isScreenLocked()) {
                    //锁屏状态下，退出activity，防着再次进入无法打开刷卡的问题
                    ActMain.this.finish();
                } else if (commomShare.getBoolean(Constants.SHARE_KEY_IS_CLOSE_NFC_AFTER, false)) {
                }
                break;
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_goto_forum) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
