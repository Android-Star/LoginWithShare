package com.example.wilson.loginwithshare;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.wilson.loginwithshare.QQ.QQUtils;
import com.example.wilson.loginwithshare.base.Util;
import com.example.wilson.loginwithshare.entity.QQUser;
import com.google.gson.Gson;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.wilson.loginwithshare.base.Config.QQ_SCOPE_ALL;

/**
 * Created by ggg on 2018/8/2.
 */

public class QQActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvQQLogin;
    private TextView tvQQShareImageText;
    private TextView tvQQShareImage;
    private TextView tvQQShareApp;
    private TextView tvQQShareAudio;
    private TextView tvQQShareVideo;
    private TextView tvQQShareImageTextToz;
    private TextView tvQQShareToz;
    private ImageView ivHead;
    private TextView tvName;
    private ArrayList<String> imageUrls = new ArrayList<>();

    private IUiListener qqShareListener, qqLoginListener;
    private static final int REQUEST_CODE_PICK_VIDEO = 100;
    private static final int REQUEST_CODE_PICK_Image = 200;
    private boolean isShareImageToz = false;

    private UserInfo userInfo;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                QQUser user = new Gson().fromJson(String.valueOf(msg.obj), QQUser.class);
                if (user != null) {
                    Log.d("qqlogin", "userInfo:昵称：" + user.getNickname() + "  性别:" + user.getGender() + "  地址：" + user.getProvince() + user.getCity());
                    Log.d("qqlogin", "头像路径：" + user.getFigureurl_qq_2());
                    Glide.with(QQActivity.this).load(user.getFigureurl_qq_2()).into(ivHead);
                    tvName.setText(user.getNickname());
                }
                JSONObject response = null;
                try {
                    response = new JSONObject(String.valueOf(msg.obj));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    };
    private String QQ_uid;


    public static void start(Context context, Bundle bundle) {
        Intent intent = new Intent(context, QQActivity.class);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qq);
        tvQQLogin = findViewById(R.id.tv_qq_login);
        tvQQShareImageText = findViewById(R.id.tv_qq_shareimagetext);
        tvQQShareImage = findViewById(R.id.tv_qq_shareimage);
        tvQQShareAudio = findViewById(R.id.tv_qq_shareaudio);
        tvQQShareApp = findViewById(R.id.tv_qq_shareapp);
        tvQQShareVideo = findViewById(R.id.tv_qq_sharevideo);
        tvQQShareToz = findViewById(R.id.tv_qq_share_qz);
        tvQQShareImageTextToz = findViewById(R.id.tv_qq_shareimagetext_qz);
        ivHead = findViewById(R.id.iv_head);
        tvName = findViewById(R.id.tv_name);

        tvQQLogin.setOnClickListener(this);
        tvQQShareVideo.setOnClickListener(this);
        tvQQShareToz.setOnClickListener(this);
        tvQQShareImageTextToz.setOnClickListener(this);

        tvQQShareImageText.setOnClickListener(this);
        tvQQShareImage.setOnClickListener(this);
        tvQQShareAudio.setOnClickListener(this);
        tvQQShareApp.setOnClickListener(this);

        qqShareListener = new IUiListener() {

            @Override
            public void onComplete(Object o) {
                Toast.makeText(QQActivity.this, "分享成功", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(UiError uiError) {
                Toast.makeText(QQActivity.this, "分享失败", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancel() {
                Toast.makeText(QQActivity.this, "分享取消", Toast.LENGTH_SHORT).show();

            }
        };

        qqLoginListener = new IUiListener() {
            @Override
            public void onComplete(Object o) {
                Toast.makeText(QQActivity.this, "登录成功", Toast.LENGTH_SHORT).show();

                try {
                    org.json.JSONObject jsonObject = new org.json.JSONObject(o.toString());
                    initOpenidAndToken(jsonObject);
                    updateUserInfo();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(UiError uiError) {
                Toast.makeText(QQActivity.this, "登录失败", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancel() {
                Toast.makeText(QQActivity.this, "登录取消", Toast.LENGTH_SHORT).show();

            }
        };


    }

    /**
     * 获取登录QQ腾讯平台的权限信息(用于访问QQ用户信息)
     *
     * @param jsonObject
     */
    public void initOpenidAndToken(org.json.JSONObject jsonObject) {
        try {
            String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
            String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
            String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
                    && !TextUtils.isEmpty(openId)) {
                QQUtils.getInstance(QQActivity.this).setAccessTokenWithId(token, expires, openId);
                QQ_uid = openId;
            }
        } catch (Exception e) {
        }
    }

    private void updateUserInfo() {
        IUiListener listener = new IUiListener() {
            @Override
            public void onError(UiError e) {
            }

            @Override
            public void onComplete(final Object response) {
                Message msg = new Message();
                msg.obj = response;
                Log.e("................", response.toString());
                msg.what = 0;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onCancel() {
                Toast.makeText(QQActivity.this, "登录取消", Toast.LENGTH_SHORT).show();
            }
        };
        userInfo = new UserInfo(this, QQUtils.getInstance(QQActivity.this).getQQToken());
        userInfo.getUserInfo(listener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //分享：
        if (requestCode == Constants.REQUEST_QQ_SHARE) {
            Tencent.onActivityResultData(requestCode, resultCode, data, qqShareListener);
        } else if (requestCode == REQUEST_CODE_PICK_VIDEO) {
            String path = null;
            if (resultCode == Activity.RESULT_OK) {
                if (data != null && data.getData() != null) {
                    // 根据返回的URI获取对应的SQLite信息
                    Uri uri = data.getData();
                    path = Util.getPath(this, uri);
                }
            }
            if (path != null) {
                QQUtils.getInstance(QQActivity.this).shareVideoToQzone("哈哈", path, qqShareListener);
            } else {
                Toast.makeText(QQActivity.this, "请重新选择视频", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == REQUEST_CODE_PICK_Image) {
            String path = null;
            if (resultCode == Activity.RESULT_OK) {
                if (data != null && data.getData() != null) {
                    // 根据返回的URI获取对应的SQLite信息
                    Uri uri = data.getData();
                    path = Util.getPath(this, uri);
                }
            }
            if (path != null) {
                if (!imageUrls.contains(path)) {
                    imageUrls.add(path);
                }
                if (isShareImageToz) {
                    QQUtils.getInstance(QQActivity.this).shareImageToQzone("纯图片", imageUrls, qqShareListener);
                } else {
                    QQUtils.getInstance(QQActivity.this).shareImageTextToQzone("图文分享", "http://www.qq.com", "哈哈", imageUrls, qqShareListener);
                }

            } else {
                Toast.makeText(QQActivity.this, "请重新选择图片", Toast.LENGTH_LONG).show();
            }
        }
        //登录：
        if (requestCode == Constants.REQUEST_LOGIN ||
                requestCode == Constants.REQUEST_APPBAR) {
            Tencent.onActivityResultData(requestCode, resultCode, data, qqLoginListener);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_qq_login:
                QQUtils.getInstance(QQActivity.this).qqLogin(QQ_SCOPE_ALL, qqLoginListener);
                break;
            case R.id.tv_qq_shareimagetext:
                QQUtils.getInstance(QQActivity.this).shareImageTextToQ(
                        "标题",
                        "http://www.qq.com/",
                        "副标题",
                        "http://y.gtimg.cn/music/photo_new/T002R300x300M000003KIU6V02sS7C.jpg?max_age=2592000",
                        "酷秀",
                        false,
                        null,
                        qqShareListener
                );
                break;
            case R.id.tv_qq_shareimage:
                QQUtils.getInstance(QQActivity.this).shareImageToQ(
                        getExternalFilesDir(null) + "/aaa.jpg",
                        "go",
                        false,
                        null,
                        qqShareListener
                );
                break;
            case R.id.tv_qq_shareaudio:
                QQUtils.getInstance(QQActivity.this).shareMusicToQ(
                        "酷秀",
                        "http://c.y.qq.com/v8/playsong.html?songid=109325260&songmid=000kuo2H2xJqfA&songtype=0&source=mqq&_wv=1",
                        "乔紫乔",
                        "http://y.gtimg.cn/music/photo_new/T002R300x300M000003KIU6V02sS7C.jpg?max_age=2592000",
                        "酷秀",
                        "http://ws.stream.qqmusic.qq.com/C100000kuo2H2xJqfA.m4a?fromtag=0",
                        false,
                        null,
                        qqShareListener
                );
                break;
            case R.id.tv_qq_shareapp:
                QQUtils.getInstance(QQActivity.this).shareApplicationToQ(
                        "酷秀分享",
                        "http://url.cn/424xgot",
                        "办公|57.4MB|785万次下载|4.6/5星",
                        "http://url.cn/424xgoi",
                        "酷秀直播",
                        false,
                        null,
                        qqShareListener
                );
                break;
            case R.id.tv_qq_share_qz:
                isShareImageToz = true;
                startPickLocaleImage(this, REQUEST_CODE_PICK_Image);

                break;
            case R.id.tv_qq_sharevideo:
                startPickLocaleVedio(this, REQUEST_CODE_PICK_VIDEO);

                break;
            case R.id.tv_qq_shareimagetext_qz:
                isShareImageToz = false;
                startPickLocaleImage(this, REQUEST_CODE_PICK_Image);
                break;
            default:
                break;
        }
    }

    private static final void startPickLocaleImage(Activity activity, int requestId) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        if (android.os.Build.VERSION.SDK_INT >= 19) {
            intent.setAction("android.intent.action.OPEN_DOCUMENT");
        } else {
            intent.setAction(Intent.ACTION_GET_CONTENT);
        }
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        activity.startActivityForResult(
                Intent.createChooser(intent, "本地图片"), requestId);
    }

    private static final void startPickLocaleVedio(Activity activity, int requestId) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        if (android.os.Build.VERSION.SDK_INT >= 19) {
            intent.setAction("android.intent.action.OPEN_DOCUMENT");
        } else {
            intent.setAction(Intent.ACTION_GET_CONTENT);
        }
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("video/*");
        activity.startActivityForResult(
                Intent.createChooser(intent, "本地视频"), requestId);
    }
}
