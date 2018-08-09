package com.example.wilson.loginwithshare.wxapi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.wilson.loginwithshare.base.Config;
import com.example.wilson.loginwithshare.entity.WXAccessTokenEntity;
import com.example.wilson.loginwithshare.entity.WXBaseRespEntity;
import com.example.wilson.loginwithshare.entity.WXUserInfo;
import com.google.gson.Gson;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    /**
     * 微信登录相关
     */
    private IWXAPI api;
    private static final String WEICHAT_APP_ID = "wxd930ea5d5a258f4f";

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //通过WXAPIFactory工厂获取IWXApI的示例
        api = WXAPIFactory.createWXAPI(this, WEICHAT_APP_ID, true);
        //将应用的appid注册到微信
        api.registerApp(WEICHAT_APP_ID);
        Log.d("------------------------------------", "");
        //注意：
        //第三方开发者如果使用透明界面来实现WXEntryActivity，需要判断handleIntent的返回值，如果返回值为false，
        // 则说明入参不合法未被SDK处理，应finish当前透明界面，避免外部通过传递非法参数的Intent导致停留在透明界面，引起用户的疑惑
        try {
            boolean result = api.handleIntent(getIntent(), this);
            if (!result) {
                Log.d("------------------------------------", "参数不合法，未被SDK处理，退出");
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        api.handleIntent(data, this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
        finish();
    }


    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        Log.d("baseResp:", baseResp.toString());
        Log.d("baseResp:", baseResp.errStr + "," + baseResp.openId + "," + baseResp.transaction + "," + baseResp.errCode);
        String result = "";
        WXBaseRespEntity entity = new Gson().fromJson(new Gson().toJson(baseResp), WXBaseRespEntity.class);
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = "发送成功";
//                showMsg(1, result);
                OkHttpUtils.get().url("https://api.weixin.qq.com/sns/oauth2/access_token")
                        .addParams("appid", Config.APP_ID_WX)
                        .addParams("secret", Config.APP_SECRET_WX)
                        .addParams("code", entity.getCode())
                        .addParams("grant_type", "authorization_code")
                        .build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(okhttp3.Call call, Exception e, int id) {
                                Log.d("wechatlogin", "请求错误..");
                                finish();
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                Log.d("wechatlogin", "response:" + response);
                                WXAccessTokenEntity accessTokenEntity = new Gson().fromJson(response, WXAccessTokenEntity.class);
                                if (accessTokenEntity != null) {
                                    getUserInfo(accessTokenEntity);
                                } else {
                                    Log.d("wechatlogin", "获取失败");
                                    finish();
                                }
                            }
                        });
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = "发送取消";
//                showMsg(2, result);
                finish();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = "发送被拒绝";
//                showMsg(1, result);
                finish();
                break;
            default:
                result = "发送返回";
//                showMsg(0, result);
                finish();
                break;
        }
        Toast.makeText(this, result, Toast.LENGTH_LONG).show();
    }

    /**
     * 获取个人信息
     *
     * @param accessTokenEntity
     */
    private void getUserInfo(WXAccessTokenEntity accessTokenEntity) {
        //https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID
        OkHttpUtils.get()
                .url("https://api.weixin.qq.com/sns/userinfo")
                .addParams("access_token", accessTokenEntity.getAccess_token())
                .addParams("openid", accessTokenEntity.getOpenid())//openid:授权用户唯一标识
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(okhttp3.Call call, Exception e, int id) {
                        Log.d("wechatlogin", "获取错误..");
                        finish();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d("wechatlogin", "userInfo:" + response);
                        WXUserInfo wxResponse = new Gson().fromJson(response, WXUserInfo.class);
                        Log.d("wechatlogin", "微信登录资料已获取，后续未完成");
                        String headUrl = wxResponse.getHeadimgurl();
                        String name = wxResponse.getNickname();
                        Log.d("wechatlogin", "头像Url:" + headUrl);
                        Intent intent = getIntent();
                        intent.putExtra("headUrl", headUrl);
                        intent.putExtra("nickname", name);
                        WXEntryActivity.this.setResult(0, intent);
                        finish();
                    }
                });
    }
}