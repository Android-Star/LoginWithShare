package com.example.wilson.loginwithshare.sina;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.MultiImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.VideoSourceObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.auth.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.WbAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.sina.weibo.sdk.share.WbShareHandler;
import com.sina.weibo.sdk.utils.Utility;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.example.wilson.loginwithshare.base.Config.APP_KEY_SINA;
import static com.example.wilson.loginwithshare.base.Config.REDIRECT_URL;
import static com.example.wilson.loginwithshare.base.Config.SCOPE;

/**
 * 登录如果要收到回调必须重写onActivityResult方法即该类中的authorizeCallBack（必须写在activity中否则无效）
 * 分享如果想接收回调必须要重写activity的onNewIntent方法即该类中的doResultIntent否则收不到回调
 *
 * 分享功能：支持纯文字、纯图片、文字+图片、文字加视频、文字+多图片、多图片
 */

public class SinaUtils {
    private static final int THUMB_SIZE = 150;

    private static SinaUtils sinaUtils;
    private Activity activity;
    private SsoHandler mSsoHandler;
    private WbShareHandler shareHandler;

    public static SinaUtils getInstance(Activity activity) {
        if (sinaUtils == null) {
            sinaUtils = new SinaUtils(activity);
        }
        return sinaUtils;
    }

    public SinaUtils(Activity activity) {
        this.activity=activity;
        WbSdk.install(activity, new AuthInfo(activity, APP_KEY_SINA, REDIRECT_URL, SCOPE));
        mSsoHandler = new SsoHandler(activity);
        shareHandler = new WbShareHandler(activity);
        shareHandler.registerApp();
        shareHandler.setProgressColor(0xff33b5e5);
    }

    // SSO 授权, 仅客户端
    public void authorizeClientSso(WbAuthListener authListener) {
        mSsoHandler.authorizeClientSso(authListener);
    }

    // SSO 授权, 仅Web
    public void authorizeWeb(WbAuthListener authListener) {
        mSsoHandler.authorizeWeb(authListener);
    }

    // SSO 授权, ALL IN ONE   如果手机安装了微博客户端则使用客户端授权,没有则进行网页授权
    public void authorize(WbAuthListener authListener) {
        mSsoHandler.authorize(authListener);
    }

    public void logout() {
        AccessTokenKeeper.clear(activity.getApplicationContext());
    }

    //登录时onActivityResult调用
    public void authorizeCallBack(int requestCode, int resultCode, Intent data) {
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    //分享功能重写onNewIntent方法  调用此方法
    public void doResultIntent(Intent intent, WbShareCallback callback){
        shareHandler.doResultIntent(intent,callback);
    }

    //纯文本
    public void sendText(String text) {
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        weiboMessage.textObject = getTextObj(text);
        shareHandler.shareMessage(weiboMessage, false);
    }

    //纯图片（单张）
    public void sendImage(Bitmap bitmap) {
        if (bitmap == null) {
            Toast.makeText(activity, "请选择要分享的图片", Toast.LENGTH_SHORT).show();
            return;
        }
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();

        weiboMessage.imageObject = getImageObj(bitmap);
        shareHandler.shareMessage(weiboMessage, false);
    }

    //图片+文字
    public void sendImageText(String text, Bitmap bitmap) {
        if (bitmap == null) {
            Toast.makeText(activity, "请选择要分享的图片", Toast.LENGTH_SHORT).show();
            return;
        }

        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();

        if (!TextUtils.isEmpty(text)) {
            weiboMessage.textObject = getTextObj(text);
        }
        weiboMessage.imageObject = getImageObj(bitmap);
        shareHandler.shareMessage(weiboMessage, false);
    }

    //纯图片（多张）必须包含文字
    public void sendImages(List<String> paths) {
        if (WbSdk.supportMultiImage(activity)) {
            if (paths == null || paths.isEmpty()) {
                Toast.makeText(activity, "请选择要分享的图片", Toast.LENGTH_SHORT).show();
                return;
            }

            WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
            weiboMessage.textObject = getTextObj();

            weiboMessage.multiImageObject = getMultiImageObject(paths);
            shareHandler.shareMessage(weiboMessage, false);
        }
    }


    //图片（多张）+文字
    public void sendImagesText(List<String> paths, String text) {
        if (WbSdk.supportMultiImage(activity)) {
            if (paths == null || paths.isEmpty()) {
                Toast.makeText(activity, "请选择要分享的图片", Toast.LENGTH_SHORT).show();
                return;
            }
            WeiboMultiMessage weiboMessage = new WeiboMultiMessage();

            if (!TextUtils.isEmpty(text)) {
                weiboMessage.textObject = getTextObj(text);
            }

            weiboMessage.multiImageObject = getMultiImageObject(paths);
            shareHandler.shareMessage(weiboMessage, false);
        }
    }

    //视频 必须包含文字
    public void sendVideo(String videoPath) {
        if (!TextUtils.isEmpty(videoPath)) {
            WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
            weiboMessage.textObject = getTextObj();

            weiboMessage.videoSourceObject = getVideoObject(videoPath);
            shareHandler.shareMessage(weiboMessage, false);
        } else {
            Toast.makeText(activity, "视频路径为空", Toast.LENGTH_SHORT).show();
        }

    }

    //视频+文字
    public void sendVideoText(String videoPath, String text) {
        if (!TextUtils.isEmpty(videoPath)) {
            WeiboMultiMessage weiboMessage = new WeiboMultiMessage();

            if (!TextUtils.isEmpty(text)) {
                weiboMessage.textObject = getTextObj(text);
            }
            weiboMessage.videoSourceObject = getVideoObject(videoPath);
            shareHandler.shareMessage(weiboMessage, false);
        } else {
            Toast.makeText(activity, "视频路径为空", Toast.LENGTH_SHORT).show();
        }

    }

    //网页
    public void sendpage(String title, String description, String text, Bitmap bitmap, String actionUrl) {
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();

        if (!TextUtils.isEmpty(text)) {
            weiboMessage.textObject = getTextObj(text);
        }

        weiboMessage.mediaObject = getWebpageObj(title, description, actionUrl, bitmap);
        shareHandler.shareMessage(weiboMessage, false);


    }

    /**
     * 创建文本消息对象。
     *
     * @return 文本消息对象。
     */
    private TextObject getTextObj(String text) {
        TextObject textObject = new TextObject();
        textObject.text = text;
        return textObject;
    }

    private TextObject getTextObj() {
        TextObject textObject = new TextObject();
        textObject.text = "";
        return textObject;
    }


    /**
     * 创建图片消息对象。
     *
     * @return 图片消息对象。
     */
    private ImageObject getImageObj(Bitmap bitmap) {
        ImageObject imageObject = new ImageObject();
        imageObject.setImageObject(bitmap);
        return imageObject;
    }

    /**
     * 创建多媒体（网页）消息对象。
     *
     * @return 多媒体（网页）消息对象。
     */
    private WebpageObject getWebpageObj(String title, String description, String actionUrl, Bitmap bitmap) {
        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title = title;
        mediaObject.description = description;
        // 设置缩略图
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, THUMB_SIZE, THUMB_SIZE, true);
        bitmap.recycle();
        mediaObject.setThumbImage(thumbBmp);
        mediaObject.actionUrl = actionUrl;
        mediaObject.defaultText = "Webpage 默认文案";
        return mediaObject;
    }

    /***
     * 创建多图
     * @return
     * assets目录下
     * getExternalFilesDir(null)+"/aaa.png"
     */
    private MultiImageObject getMultiImageObject(List<String> paths) {
        MultiImageObject multiImageObject = new MultiImageObject();
        //pathList设置的是本地本件的路径,并且是当前应用可以访问的路径，现在不支持网络路径（多图分享依靠微博最新版本的支持，所以当分享到低版本的微博应用时，多图分享失效
        // 可以通过WbSdk.hasSupportMultiImage 方法判断是否支持多图分享,h5分享微博暂时不支持多图）多图分享接入程序必须有文件读写权限，否则会造成分享失败
        ArrayList<Uri> pathList = new ArrayList<Uri>();
        for (int i = 0; i < paths.size(); i++) {
            pathList.add(Uri.fromFile(new File(paths.get(i))));
        }
        multiImageObject.setImageList(pathList);

        return multiImageObject;
    }

    private VideoSourceObject getVideoObject(String videoPath) {
        //获取视频
        VideoSourceObject videoSourceObject = new VideoSourceObject();
        videoSourceObject.videoPath = Uri.fromFile(new File(videoPath));
        return videoSourceObject;
    }

}
