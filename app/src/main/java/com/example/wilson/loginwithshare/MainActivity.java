package com.example.wilson.loginwithshare;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * 新浪
     */
    private TextView mTvSina;
    /**
     * 新浪
     */
    private TextView mTvQq;
    /**
     * 新浪
     */
    private TextView mTvWechat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();


        copyFile("eeee.mp4");
        copyFile("aaa.png");
        copyFile("bbbb.jpg");
        copyFile("ccc.JPG");
        copyFile("eee.jpg");
        copyFile("ddd.jpg");
        copyFile("fff.jpg");
        copyFile("ggg.JPG");
        copyFile("hhhh.jpg");
        copyFile("kkk.JPG");

    }

    private void copyFile(final String fileName) {

        final File file = new File(getExternalFilesDir(null).getPath() + "/" + fileName);
        if (!file.exists()) {
            //复制文件
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        InputStream inputStream = getAssets().open(fileName);
                        OutputStream outputStream = new FileOutputStream(file);
                        byte[] buffer = new byte[1444];
                        int readSize = 0;
                        while ((readSize = inputStream.read(buffer)) != 0) {
                            outputStream.write(buffer, 0, readSize);
                        }
                        inputStream.close();
                        outputStream.close();
                    } catch (Exception e) {
                    }

                }
            });
            thread.start();
        }
    }

    private void initView() {
        mTvSina = (TextView) findViewById(R.id.tv_sina);
        mTvSina.setOnClickListener(this);
        mTvQq = (TextView) findViewById(R.id.tv_qq);
        mTvQq.setOnClickListener(this);
        mTvWechat = (TextView) findViewById(R.id.tv_wechat);
        mTvWechat.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.tv_sina:
                SinaActivity.start(MainActivity.this, null);
                break;
            case R.id.tv_qq:
                QQActivity.start(MainActivity.this, null);
                break;
            case R.id.tv_wechat:
                WechatActivity.start(MainActivity.this, null);
                break;
        }
    }
}
