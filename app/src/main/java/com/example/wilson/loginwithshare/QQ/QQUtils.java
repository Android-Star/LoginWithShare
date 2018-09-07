package com.example.wilson.loginwithshare.QQ;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.sina.weibo.sdk.utils.LogUtil;
import com.tencent.connect.auth.QQToken;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzonePublish;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;

import java.util.ArrayList;

import static com.example.wilson.loginwithshare.base.Config.QQ_APP_ID;

/**
 * Created by ggg on 2018/8/2.
 */

public class QQUtils {

    private static QQUtils instance;
    private Tencent mTencent;

    private int shareType;
    private Handler mManinHandler;
    private static Object mMainHandlerLock = new Object();


    public static QQUtils getInstance() {
        if (instance == null) {
            instance = new QQUtils();
        }
        return instance;
    }

    public void qqLogin(Activity activity, String scope, IUiListener listener) {
        if (mTencent == null) {
            mTencent = Tencent.createInstance(QQ_APP_ID, activity);
        }
        if (mTencent.isSessionValid()) {
            qqLogout(activity);
        }
        mTencent.login(activity, scope, listener);
    }

    /**
     * 退出登陆
     */
    public void qqLogout(Activity activity) {
        if (null != mTencent && mTencent.isSessionValid()) {
            LogUtil.e("logout", "退出登陆");
            mTencent.logout(activity);
        }
    }

    public void setAccessTokenWithId(String token, String expires, String openId) {
        if (null != mTencent){
            mTencent.setAccessToken(token, expires);
            mTencent.setOpenId(openId);
        }
    }

    public QQToken getQQToken(){
        return mTencent.getQQToken();
    }

    /**
     * 分享图文消息
     *
     * @param title         分享的标题, 最长30个字符
     * @param targetUrl     这条分享消息被好友点击后的跳转URL
     * @param summary       分享的消息摘要，最长40个字
     * @param imageUrl      分享图片的URL或者本地路径
     * @param appName       手Q客户端顶部，替换“返回”按钮文字，如果为空，用返回代替
     * @param shareListener 分享结果回调
     */
    public void shareImageTextToQ(final Activity context,
            String title,
            String targetUrl,
            String summary,
            String imageUrl,
            String appName,
            boolean showZoneDialog,
            String arkStr,
            final IUiListener shareListener
    ) {
        shareType = QQShare.SHARE_TO_QQ_TYPE_DEFAULT;

        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, shareType);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, summary);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, targetUrl);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imageUrl);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, appName);

        //分享额外选项，两种类型可选（默认是不隐藏分享到QZone按钮且不自动打开分享到QZone的对话框）：
        //QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN，分享时自动打开分享到QZone的对话框。
        //QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE，分享时隐藏分享到QZone按钮
        if (showZoneDialog) {
            params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
        } else {
            params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE);
        }

        if (!TextUtils.isEmpty(arkStr)) {
            params.putString(QQShare.SHARE_TO_QQ_ARK_INFO, arkStr);
        }

        // QQ分享要在主线程做
        getMainHandler().post(new Runnable() {

            @Override
            public void run() {
                if (null == mTencent) {
                    mTencent = Tencent.createInstance(QQ_APP_ID, context.getApplicationContext());
                }
                mTencent.shareToQQ(context, params, shareListener);
            }
        });

    }

    /**
     * 分享纯图片
     *
     * @param imageUrl(必须是本地图片的路径)
     * @param appName
     * @param shareListener
     */
    public void shareImageToQ(final Activity context,
            String imageUrl,
            String appName,
            boolean showZoneDialog,
            String arkStr,
            final IUiListener shareListener) {
        shareType = QQShare.SHARE_TO_QQ_TYPE_IMAGE;

        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, shareType);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, imageUrl);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, appName);

        //分享额外选项，两种类型可选（默认是不隐藏分享到QZone按钮且不自动打开分享到QZone的对话框）：
        //QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN，分享时自动打开分享到QZone的对话框。
        //QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE，分享时隐藏分享到QZone按钮
        if (showZoneDialog) {
            params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
        } else {
            params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE);
        }

        if (!TextUtils.isEmpty(arkStr)) {
            params.putString(QQShare.SHARE_TO_QQ_ARK_INFO, arkStr);
        }

        // QQ分享要在主线程做
        getMainHandler().post(new Runnable() {

            @Override
            public void run() {
                if (null == mTencent) {
                    mTencent = Tencent.createInstance(QQ_APP_ID, context.getApplicationContext());
                }
                mTencent.shareToQQ(context, params, shareListener);
            }
        });
    }

    /**
     * 分享音乐
     *
     * @param title
     * @param targetUrl
     * @param summary
     * @param imageUrl
     * @param appName
     * @param audioUrl      音乐文件的远程链接, 以URL的形式传入, 不支持本地音乐
     * @param shareListener
     */
    public void shareMusicToQ(final Activity context,
            String title,
            String targetUrl,
            String summary,
            String imageUrl,
            String appName,
            String audioUrl,
            boolean showZoneDialog,
            String arkStr,
            final IUiListener shareListener
    ) {
        shareType = QQShare.SHARE_TO_QQ_TYPE_AUDIO;

        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, shareType);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, summary);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, targetUrl);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imageUrl);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, appName);
        params.putString(QQShare.SHARE_TO_QQ_AUDIO_URL, audioUrl);

        //分享额外选项，两种类型可选（默认是不隐藏分享到QZone按钮且不自动打开分享到QZone的对话框）：
        //QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN，分享时自动打开分享到QZone的对话框。
        //QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE，分享时隐藏分享到QZone按钮
        if (showZoneDialog) {
            params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
        } else {
            params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE);
        }

        if (!TextUtils.isEmpty(arkStr)) {
            params.putString(QQShare.SHARE_TO_QQ_ARK_INFO, arkStr);
        }

        // QQ分享要在主线程做
        getMainHandler().post(new Runnable() {

            @Override
            public void run() {
                if (null == mTencent) {
                    mTencent = Tencent.createInstance(QQ_APP_ID, context.getApplicationContext());
                }
                mTencent.shareToQQ(context, params, shareListener);
            }
        });
    }

    /**
     * 分享应用
     *
     * @param title
     * @param targetUrl
     * @param summary
     * @param imageUrl
     * @param appName
     * @param shareListener
     */
    public void shareApplicationToQ(final Activity context,
            String title,
            String targetUrl,
            String summary,
            String imageUrl,
            String appName,
            boolean showZoneDialog,
            String arkStr,
            final IUiListener shareListener
    ) {
        shareType = QQShare.SHARE_TO_QQ_TYPE_APP;

        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, shareType);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, summary);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, targetUrl);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imageUrl);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, appName);

        //分享额外选项，两种类型可选（默认是不隐藏分享到QZone按钮且不自动打开分享到QZone的对话框）：
        //QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN，分享时自动打开分享到QZone的对话框。
        //QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE，分享时隐藏分享到QZone按钮
        if (showZoneDialog) {
            params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
        } else {
            params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE);
        }

        if (!TextUtils.isEmpty(arkStr)) {
            params.putString(QQShare.SHARE_TO_QQ_ARK_INFO, arkStr);
        }
        // QQ分享要在主线程做
        getMainHandler().post(new Runnable() {

            @Override
            public void run() {
                if (null == mTencent) {
                    mTencent = Tencent.createInstance(QQ_APP_ID, context.getApplicationContext());
                }
                mTencent.shareToQQ(context, params, shareListener);
            }
        });
    }

    /**
     * 分享图文消息
     *
     * @param title         分享的标题, 最长30个字符
     * @param targetUrl     这条分享消息被好友点击后的跳转URL
     * @param summary       分享的消息摘要，最长40个字 (选填)
     * @param imageUrls     分享的图片, 以ArrayList<String>的类型传入，以便支持多张图片（注：图片最多支持9张图片，多余的图片会被丢弃.只支持本地图片）
     * @param shareListener 分享结果回调
     */
    public void shareImageTextToQzone(final Activity context,
            String title,
            String targetUrl,
            String summary,
            ArrayList<String> imageUrls,
            final IUiListener shareListener
    ) {
        shareType = QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT;

        final Bundle params = new Bundle();
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, shareType);

        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, summary);
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, targetUrl);

        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);

        getMainHandler().post(new Runnable() {

            @Override
            public void run() {
                if (null == mTencent) {
                    mTencent = Tencent.createInstance(QQ_APP_ID, context.getApplicationContext());
                }
                mTencent.shareToQzone(context, params, shareListener);
            }
        });

    }

    /**
     * 发布说说(图片)
     *
     * @param imageUrls     说说的图片, 以ArrayList<String>的类型传入，以便支持多张图片（注：<=9张图片为发表说说，>9张为上传图片到相册），只支持本地图片
     * @param summary       说说正文
     * @param shareListener
     */
    public void shareImageToQzone(final Activity context,
            String summary,
            ArrayList<String> imageUrls,
            final IUiListener shareListener) {
        shareType = QzonePublish.PUBLISH_TO_QZONE_TYPE_PUBLISHMOOD;

        final Bundle params = new Bundle();
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, shareType);
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, summary);
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
        params.putString(QzonePublish.PUBLISH_TO_QZONE_VIDEO_PATH, "本地视频地址");

        // QQ空间分享要在主线程做
        getMainHandler().post(new Runnable() {

            @Override
            public void run() {
                if (null == mTencent) {
                    mTencent = Tencent.createInstance(QQ_APP_ID, context.getApplicationContext());
                }
                mTencent.publishToQzone(context, params, shareListener);
            }
        });
    }

    /**
     * 发布说说视频
     *
     * @param videoUrl 发表的视频，只支持本地地址，发表视频时必填；上传视频的大小最好控制在100M以内（因为QQ普通用户上传视频必须在100M以内，黄钻用户可上传1G以内视频，大于1G会直接报错。）
     */
    public void shareVideoToQzone(final Activity context,
            String summary,
            String videoUrl,
            final IUiListener shareListener
    ) {
        shareType = QzonePublish.PUBLISH_TO_QZONE_TYPE_PUBLISHVIDEO;

        final Bundle params = new Bundle();
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, shareType);
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, summary);
        params.putString(QzonePublish.PUBLISH_TO_QZONE_VIDEO_PATH, videoUrl);

        // QQ分享要在主线程做
        getMainHandler().post(new Runnable() {

            @Override
            public void run() {
                if (null == mTencent) {
                    mTencent = Tencent.createInstance(QQ_APP_ID, context.getApplicationContext());
                }
                mTencent.publishToQzone(context, params, shareListener);
            }
        });
    }

    private Handler getMainHandler() {
        if (mManinHandler == null) {
            synchronized (mMainHandlerLock) {
                if (mManinHandler == null) {
                    mManinHandler = new Handler(Looper.getMainLooper());
                }
            }
        }
        return mManinHandler;
    }


}
