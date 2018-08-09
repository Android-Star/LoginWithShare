package com.example.wilson.loginwithshare.wxapi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    /**
     * ΢�ŵ�¼���
     */
    private IWXAPI api;
    private static final String WEICHAT_APP_ID = "wxd930ea5d5a258f4f";

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ͨ��WXAPIFactory������ȡIWXApI��ʾ��
        api = WXAPIFactory.createWXAPI(this, WEICHAT_APP_ID, true);
        //��Ӧ�õ�appidע�ᵽ΢��
        api.registerApp(WEICHAT_APP_ID);
        Log.d("------------------------------------", "");
        //ע�⣺
        //���������������ʹ��͸��������ʵ��WXEntryActivity����Ҫ�ж�handleIntent�ķ���ֵ���������ֵΪfalse����˵����β��Ϸ�δ��SDK����Ӧfinish��ǰ͸�����棬�����ⲿͨ�����ݷǷ�������Intent����ͣ����͸�����棬�����û����ɻ�
        try {
            boolean result = api.handleIntent(getIntent(), this);
            if (!result) {
                Log.d("------------------------------------", "�������Ϸ���δ��SDK�����˳�");
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
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = "���ͳɹ�";
//                showMsg(1, result);
                finish();
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = "����ȡ��";
//                showMsg(2, result);
                finish();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = "���ͱ��ܾ�";
//                showMsg(1, result);
                finish();
                break;
            default:
                result = "���ͷ���";
//                showMsg(0, result);
                finish();
                break;
        }
        Toast.makeText(this, result, Toast.LENGTH_LONG).show();
    }
}