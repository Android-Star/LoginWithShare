package com.example.wilson.loginwithshare.wechat;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
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

import junit.framework.Assert;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
     * @param title       分享的标题
     * @param openUrl     点击分享item打开的网页地址url
     * @param description 网页的描述
     * @param icon        分享item的图片
     * @param scene       0表示为分享到微信好友  1表示为分享到朋友圈 2表示微信收藏
     */
    public void sendToWeiXin(String title, String openUrl, String description, Bitmap icon, int scene) {
        //初始化一个WXWebpageObject对象，填写url
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = openUrl;
        //Y用WXWebpageObject对象初始化一个WXMediaMessage对象，填写标题、描述
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;//网页标题
        msg.description = description;//网页描述
        msg.setThumbImage(Bitmap.createScaledBitmap(icon, THUMB_SIZE, THUMB_SIZE, true));
        //构建一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = "supplier";
        req.message = msg;
        req.scene = scene;
        api.sendReq(req);
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
            WXImageObject imageObject = new WXImageObject(bitmap);

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


    /**
     * 根据一个网络连接(String)获取bitmap图像
     *
     * @param imageUri
     * @return
     * @throws MalformedURLException
     */
    public static Bitmap getbitmap(Context context,String imageUri) {
        Log.v(WechatUtils.class.getSimpleName(), "getbitmap:" + imageUri);
        // 显示网络上的图片
        String fileName = createShareFile(context);
        try {
            URL myFileUrl = new URL(imageUri);
            HttpURLConnection conn = (HttpURLConnection) myFileUrl
                    .openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();

            File file = new File(fileName);
            FileOutputStream outputStream = new FileOutputStream(file);
            int len = 0;
            byte[] bs = new byte[1024];
            while ((len = is.read(bs)) != -1) {
                outputStream.write(bs, 0, len);
            }

            is.close();
            Log.v(WechatUtils.class.getSimpleName(), "image download finished." + imageUri);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            Log.v(WechatUtils.class.getSimpleName(), "getbitmap bmp fail---");
        }
        return extractThumbNail(fileName, 200, 200, false);
    }

    private static final int MAX_DECODE_PICTURE_SIZE = 1920 * 1440;
    private static final String TAG = "extractThumbNail";

    public static Bitmap extractThumbNail(final String path, final int height, final int width, final boolean crop) {
        Assert.assertTrue(path != null && !path.equals("") && height > 0 && width > 0);

        BitmapFactory.Options options = new BitmapFactory.Options();

        try {
            options.inJustDecodeBounds = true;
            Bitmap tmp = BitmapFactory.decodeFile(path, options);
            if (tmp != null) {
                tmp.recycle();
                tmp = null;
            }

            Log.d(TAG, "extractThumbNail: round=" + width + "x" + height + ", crop=" + crop);
            final double beY = options.outHeight * 1.0 / height;
            final double beX = options.outWidth * 1.0 / width;
            Log.d(TAG, "extractThumbNail: extract beX = " + beX + ", beY = " + beY);
            options.inSampleSize = (int) (crop ? (beY > beX ? beX : beY) : (beY < beX ? beX : beY));
            if (options.inSampleSize <= 1) {
                options.inSampleSize = 1;
            }

            // NOTE: out of memory error
            while (options.outHeight * options.outWidth / options.inSampleSize > MAX_DECODE_PICTURE_SIZE) {
                options.inSampleSize++;
            }

            int newHeight = height;
            int newWidth = width;
            if (crop) {
                if (beY > beX) {
                    newHeight = (int) (newWidth * 1.0 * options.outHeight / options.outWidth);
                } else {
                    newWidth = (int) (newHeight * 1.0 * options.outWidth / options.outHeight);
                }
            } else {
                if (beY < beX) {
                    newHeight = (int) (newWidth * 1.0 * options.outHeight / options.outWidth);
                } else {
                    newWidth = (int) (newHeight * 1.0 * options.outWidth / options.outHeight);
                }
            }

            options.inJustDecodeBounds = false;

            Log.i(TAG, "bitmap required size=" + newWidth + "x" + newHeight + ", orig=" + options.outWidth + "x" + options.outHeight + ", sample=" + options.inSampleSize);
            Bitmap bm = BitmapFactory.decodeFile(path, options);
            if (bm == null) {
                Log.e(TAG, "bitmap decode failed");
                return null;
            }

            Log.i(TAG, "bitmap decoded size=" + bm.getWidth() + "x" + bm.getHeight());
            final Bitmap scale = Bitmap.createScaledBitmap(bm, newWidth, newHeight, true);
            if (scale != null) {
                bm.recycle();
                bm = scale;
            }

            if (crop) {
                final Bitmap cropped = Bitmap.createBitmap(bm, (bm.getWidth() - width) >> 1, (bm.getHeight() - height) >> 1, width, height);
                if (cropped == null) {
                    return bm;
                }

                bm.recycle();
                bm = cropped;
                Log.i(TAG, "bitmap croped size=" + bm.getWidth() + "x" + bm.getHeight());
            }
            return bm;

        } catch (final OutOfMemoryError e) {
            Log.e(TAG, "decode bitmap failed: " + e.getMessage());
            options = null;
        }

        return null;
    }

    /**
     * 创建分享的头像照片保存路径
     */
    public static String createShareFile(Context context) {
        String path = "";
        File file = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            file = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "kuxiuShareImg");
        } else {
            file = new File(context.getFilesDir().getPath() + File.separator + "kuxiuShareImg");
        }
        if (file != null) {
            if (!file.exists()) {
                file.mkdir();
            }
            File output = new File(file, System.currentTimeMillis() + ".png");
            try {
                if (output.exists()) {
                    output.delete();
                } else {
                    output.createNewFile();
                }
                path = output.getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return path;
    }
}
