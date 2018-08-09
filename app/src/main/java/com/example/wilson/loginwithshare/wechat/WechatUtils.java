package com.example.wilson.loginwithshare.wechat;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.example.wilson.loginwithshare.base.Util;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXMiniProgramObject;
import com.tencent.mm.opensdk.modelmsg.WXMusicObject;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXVideoObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.List;

import static com.example.wilson.loginwithshare.base.Config.APP_ID_WX;

/**
 * Created by ggg on 2018/8/3.
 */

public class WechatUtils {
    private static final int THUMB_SIZE = 150;

    private static WechatUtils wechatUtils;

    private IWXAPI api;
    private Context context;

    public static WechatUtils getInstance(Context context) {
        if (wechatUtils == null) {
            wechatUtils = new WechatUtils(context);
        }
        return wechatUtils;
    }


    public WechatUtils(Context mContext) {
        this.context = mContext;
        api = WXAPIFactory.createWXAPI(context, APP_ID_WX, true);
    }

    public void weichatLogin() {
        if (isWeChatAppInstalled(context)) {
            final SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            req.state = "wechat_sdk_demo_test";
            api.sendReq(req);
        } else {
            Toast.makeText(context, "请安装微信后重试", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 判断微信客户端是否存在
     *
     * @return true安装, false未安装
     */
    public boolean isWeChatAppInstalled(Context context) {
        if (api.isWXAppInstalled() && api.isWXAppSupportAPI()) {
            return true;
        } else {
            final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
            List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
            if (pinfo != null) {
                for (int i = 0; i < pinfo.size(); i++) {
                    String pn = pinfo.get(i).packageName;
                    if (pn.equalsIgnoreCase("com.tencent.mm")) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    /**
     * @param text  分享的内容
     * @param scene 0表示为分享到微信好友  1表示为分享到朋友圈 2表示微信收藏
     */
    public void sendTextToWeiXin(String text, int scene) {
        if (isWeChatAppInstalled(context)) {

        } else {
            Toast.makeText(context, "请安装微信后重试", Toast.LENGTH_SHORT).show();
        }
        //初始化一个WXWebpageObject对象，填写url
        WXTextObject textObject = new WXTextObject();
        textObject.text = text;

        //Y用WXWebpageObject对象初始化一个WXMediaMessage对象，
        WXMediaMessage msg = new WXMediaMessage(textObject);
        msg.description = text;

        //构建一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = "supplier";

        req.message = msg;
        req.scene = scene;
        api.sendReq(req);
    }

    /**
     * @param bitmap 分享的内容
     * @param scene  0表示为分享到微信好友  1表示为分享到朋友圈 2表示微信收藏
     */
    public void sendImageToWeiXin(Bitmap bitmap, int scene) {
        if (isWeChatAppInstalled(context)) {
            if (bitmap == null)
                return;
            //初始化一个WXWebpageObject对象，填写url
            WXImageObject imageObject = new WXImageObject();

            //Y用WXWebpageObject对象初始化一个WXMediaMessage对象，
            WXMediaMessage msg = new WXMediaMessage(imageObject);
            // 设置缩略图
            Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, THUMB_SIZE, THUMB_SIZE, true);
            bitmap.recycle();
            msg.thumbData = Util.bmpToByteArray(thumbBmp, true);

            //构建一个Req
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = "supplier";

            req.message = msg;
            req.scene = scene;
            api.sendReq(req);
        } else {
            Toast.makeText(context, "请安装微信后重试", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 分享音乐
     *
     * @param bitmap 分享的内容
     * @param scene  0表示为分享到微信好友  1表示为分享到朋友圈 2表示微信收藏
     */
    public void sendMusicToWeiXin(String musicUrl, Bitmap bitmap, int scene) {
        if (isWeChatAppInstalled(context)) {
            //初始化一个WXWebpageObject对象，填写url
            WXMusicObject music = new WXMusicObject();
            music.musicUrl = musicUrl;

            WXMediaMessage msg = new WXMediaMessage();
            msg.mediaObject = music;
            msg.title = "Music Title";
            msg.description = "Music Album";

            if (bitmap != null) {
                Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, THUMB_SIZE, THUMB_SIZE, true);
                msg.thumbData = Util.bmpToByteArray(thumbBmp, true);
            }
            //构建一个Req
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = "supplier";

            req.message = msg;
            req.scene = scene;
            api.sendReq(req);
        } else {
            Toast.makeText(context, "请安装微信后重试", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * @param scene 0表示为分享到微信好友  1表示为分享到朋友圈 2表示微信收藏
     */
    public void sendVideoToWeiXin(String videoUrl, Bitmap bitmap, String title, String description, int scene) {
        if (isWeChatAppInstalled(context)) {
            //初始化一个WXWebpageObject对象，填写url
            WXVideoObject video = new WXVideoObject();
            video.videoUrl = videoUrl;

            WXMediaMessage msg = new WXMediaMessage(video);
            msg.title = title;
            msg.description = description;
            if (bitmap != null) {
                Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, THUMB_SIZE, THUMB_SIZE, true);
                msg.thumbData = Util.bmpToByteArray(thumbBmp, true);
            }

            //构建一个Req
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = "supplier";

            req.message = msg;
            req.scene = scene;
            api.sendReq(req);
        } else {
            Toast.makeText(context, "请安装微信后重试", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * @param scene 0表示为分享到微信好友  1表示为分享到朋友圈 2表示微信收藏
     */
    public void sendPageToWeiXin(String pageUrl, Bitmap bitmap, String title, String description, int scene) {
        if (isWeChatAppInstalled(context)) {
            //初始化一个WXWebpageObject对象，填写url
            WXWebpageObject webpage = new WXWebpageObject();
            webpage.webpageUrl = pageUrl;

            WXMediaMessage msg = new WXMediaMessage(webpage);
            msg.title = title;
            msg.description = description;

            if (bitmap != null) {
                Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, THUMB_SIZE, THUMB_SIZE, true);
                msg.thumbData = Util.bmpToByteArray(thumbBmp, true);
            }

            //构建一个Req
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = "supplier";

            req.message = msg;
            req.scene = scene;
            api.sendReq(req);
        } else {
            Toast.makeText(context, "请安装微信后重试", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * @param miniPtype // 正式版:0，测试版:1，体验版:2
     * @param miniPid   小程序原始id
     * @param path      小程序页面路径
     * @param scene     0表示为分享到微信好友  1表示为分享到朋友圈 2表示微信收藏
     */
    public void sendMiniProgramToWeiXin(String pageUrl, int miniPtype, String miniPid, String path, Bitmap bitmap, String title, String description, int scene) {
        if (isWeChatAppInstalled(context)) {
            WXMiniProgramObject miniProgramObj = new WXMiniProgramObject();
            miniProgramObj.webpageUrl = pageUrl;
            miniProgramObj.miniprogramType = miniPtype;
            miniProgramObj.userName = miniPid;
            miniProgramObj.path = path;
            WXMediaMessage msg = new WXMediaMessage(miniProgramObj);
            msg.title = title;
            msg.description = description;

            if (bitmap != null) {
                Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, THUMB_SIZE, THUMB_SIZE, true);
                msg.thumbData = Util.bmpToByteArray(thumbBmp, true);
            }

            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = "supplier";
            req.message = msg;
            scene = 0;
            req.scene = scene;  // 目前支持会话
            api.sendReq(req);
        } else {
            Toast.makeText(context, "请安装微信后重试", Toast.LENGTH_SHORT).show();
        }

    }
}
