package com.example.wilson.loginwithshare;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.wilson.loginwithshare.wechat.WechatUtils;

/**
 * Created by ggg on 2018/8/9.
 */

public class WechatActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * 微信登录
     */
    private TextView mTvWxLogin;
    private ImageView mIvHead;
    private TextView mTvName;

    public static void start(Context context, Bundle bundle) {
        Intent intent = new Intent(context, WechatActivity.class);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_wechat);
        initView();
    }

    private void initView() {
        mTvWxLogin = (TextView) findViewById(R.id.tv_wx_login);
        mTvWxLogin.setOnClickListener(this);
        mIvHead = (ImageView) findViewById(R.id.iv_head);
        mTvName = (TextView) findViewById(R.id.tv_name);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.tv_wx_login:
                WechatUtils.getInstance(WechatActivity.this).weichatLogin();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 0) {
            String headUrl = data.getStringExtra("headUrl");
            String name = data.getStringExtra("nickname");
            Log.d("url:", headUrl);
            Log.d("name:", name);
            Glide.with(WechatActivity.this).load(headUrl).into(mIvHead);
            mTvName.setText(name);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
