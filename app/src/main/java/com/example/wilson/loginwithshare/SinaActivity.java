package com.example.wilson.loginwithshare;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.wilson.loginwithshare.sina.SinaUtils;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbAuthListener;
import com.sina.weibo.sdk.auth.WbConnectErrorMessage;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * Created by ggg on 2018/8/8.
 */

public class SinaActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     * 登录
     */
    private TextView tvLogin;
    /**
     * 文字
     */
    private Button mTvShareText;
    /**
     * 单张图片
     */
    private Button mTvShareImage;
    /**
     * 图文（单张）
     */
    private Button mTvShareTextImage;
    /**
     * 图pian（多张）
     */
    private Button mTvShareImages;
    /**
     * 图文（多张）
     */
    private Button mTvShareTextImages;
    /**
     * 视频
     */
    private Button mTvShareVideo;
    /**
     * 视频+文字
     */
    private Button mTvShareVideoText;
    /**
     * page
     */
    private Button mTvSharePage;
    /**
     * 登录
     */
    private Button mTvLogin;
    private ImageView mIvHead;
    private TextView mTvName;

    public static void start(Context context, Bundle bundle) {
        Intent intent = new Intent(context, SinaActivity.class);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_sina);
        initView();
    }

    //用于分享回调
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        SinaUtils.getInstance(this).doResultIntent(intent, new WbShareCallback() {
            @Override
            public void onWbShareSuccess() {
                Toast.makeText(SinaActivity.this, "onWbShareSuccess", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onWbShareCancel() {
                Toast.makeText(SinaActivity.this, "onWbShareCancel", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onWbShareFail() {
                Toast.makeText(SinaActivity.this, "onWbShareFail", Toast.LENGTH_SHORT).show();

            }
        });
    }

    //用于登录回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SinaUtils.getInstance(this).authorizeCallBack(requestCode, resultCode, data);
    }

    private void initView() {
        mTvShareText = (Button) findViewById(R.id.tv_share_text);
        mTvShareText.setOnClickListener(this);
        mTvShareImage = (Button) findViewById(R.id.tv_share_image);
        mTvShareImage.setOnClickListener(this);
        mTvShareTextImage = (Button) findViewById(R.id.tv_share_text_image);
        mTvShareTextImage.setOnClickListener(this);
        mTvShareImages = (Button) findViewById(R.id.tv_share_images);
        mTvShareImages.setOnClickListener(this);
        mTvShareTextImages = (Button) findViewById(R.id.tv_share_text_images);
        mTvShareTextImages.setOnClickListener(this);
        mTvShareVideo = (Button) findViewById(R.id.tv_share_video);
        mTvShareVideo.setOnClickListener(this);
        mTvShareVideoText = (Button) findViewById(R.id.tv_share_video_text);
        mTvShareVideoText.setOnClickListener(this);
        mTvSharePage = (Button) findViewById(R.id.tv_share_page);
        mTvSharePage.setOnClickListener(this);
        tvLogin = findViewById(R.id.tv_login);
        tvLogin.setOnClickListener(this);
        mTvLogin = (Button) findViewById(R.id.tv_login);
        mTvLogin.setOnClickListener(this);
        mIvHead = (ImageView) findViewById(R.id.iv_head);
        mTvName = (TextView) findViewById(R.id.tv_name);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.tv_share_text:
                SinaUtils.getInstance(SinaActivity.this).sendText(
                        "呵呵哒 我是测试数据"
                );
                break;
            case R.id.tv_share_image:
                SinaUtils.getInstance(SinaActivity.this).sendImage(
                        BitmapFactory.decodeResource(getResources(), R.drawable.timg)
                );
                break;
            case R.id.tv_share_text_image:
                SinaUtils.getInstance(SinaActivity.this).sendImageText(
                        "呵呵哒 我是测试数据",
                        BitmapFactory.decodeResource(getResources(), R.drawable.timg)
                );
                break;
            case R.id.tv_share_images:
                List<String> paths = new ArrayList<>();
                paths.add(getExternalFilesDir(null) + "/aaa.jpg");
                paths.add(getExternalFilesDir(null) + "/bbbb.jpg");
                paths.add(getExternalFilesDir(null) + "/ccc.jpg");
                paths.add(getExternalFilesDir(null) + "/ddd.jpg");
                paths.add(getExternalFilesDir(null) + "/eee.jpg");
                paths.add(getExternalFilesDir(null) + "/fff.jpg");
                paths.add(getExternalFilesDir(null) + "/ggg.jpg");
                paths.add(getExternalFilesDir(null) + "/hhhh.jpg");
                paths.add(getExternalFilesDir(null) + "/kkk.jpg");
                SinaUtils.getInstance(SinaActivity.this).sendImages(
                        paths
                );
                break;
            case R.id.tv_share_text_images:
                List<String> paths1 = new ArrayList<>();
                paths1.add(getExternalFilesDir(null) + "/aaa.jpg");
                paths1.add(getExternalFilesDir(null) + "/bbbb.jpg");
                paths1.add(getExternalFilesDir(null) + "/ccc.jpg");
                paths1.add(getExternalFilesDir(null) + "/ddd.jpg");
                paths1.add(getExternalFilesDir(null) + "/eee.jpg");
                paths1.add(getExternalFilesDir(null) + "/fff.jpg");
                paths1.add(getExternalFilesDir(null) + "/ggg.jpg");
                paths1.add(getExternalFilesDir(null) + "/hhhh.jpg");
                paths1.add(getExternalFilesDir(null) + "/kkk.jpg");
                SinaUtils.getInstance(SinaActivity.this).sendImagesText(
                        paths1, "呵呵哒 我是测试数据"
                );
                break;
            case R.id.tv_share_video:
                String videoPath = getExternalFilesDir(null) + "/eeee.mp4";
                SinaUtils.getInstance(SinaActivity.this).sendVideo(
                        videoPath
                );
                break;
            case R.id.tv_share_video_text:
                String videoPath1 = getExternalFilesDir(null) + "/eeee.mp4";
                SinaUtils.getInstance(SinaActivity.this).sendVideoText(
                        videoPath1, "呵呵哒 我是测试数据"
                );
                break;
            case R.id.tv_share_page:
                SinaUtils.getInstance(SinaActivity.this).sendpage(
                        "我是标题",
                        "我是描述",
                        "呵呵哒 我是测试数据",
                        BitmapFactory.decodeResource(getResources(), R.drawable.timg),
                        "http://www.baidu.com"
                );
                break;
            case R.id.tv_login:
                SinaUtils.getInstance(SinaActivity.this).authorize(new WbAuthListener() {
                    @Override
                    public void onSuccess(final Oauth2AccessToken oauth2AccessToken) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Oauth2AccessToken mAccessToken = oauth2AccessToken;
                                if (mAccessToken.isSessionValid()) {
                                    //获取个人资料
                                    //https://api.weibo.com/2/users/show.json
                                    OkHttpUtils.get()
                                            .url("https://api.weibo.com/2/users/show.json")
                                            .addParams("access_token", mAccessToken.getToken())
                                            .addParams("uid", mAccessToken.getUid())
                                            .build()
                                            .execute(new StringCallback() {
                                                @Override
                                                public void onError(Call call, Exception e, int id) {
                                                    e.printStackTrace();
                                                }

                                                @Override
                                                public void onResponse(String response, int id) {
                                                    JSONObject jsonObject = null;
                                                    try {
                                                        jsonObject = new JSONObject(response);
                                                        String headUrl = jsonObject.getString("profile_image_url");
                                                        String name = jsonObject.getString("name");
                                                        Glide.with(SinaActivity.this).load(headUrl).into(mIvHead);
                                                        mTvName.setText(name);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });

                                }
                            }
                        });
                    }

                    @Override
                    public void cancel() {
                        Toast.makeText(SinaActivity.this, "取消授权", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(WbConnectErrorMessage wbConnectErrorMessage) {
                        Toast.makeText(SinaActivity.this, "取消授权授权失败", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
        }
    }

}
