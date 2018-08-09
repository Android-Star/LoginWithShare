package com.example.wilson.loginwithshare;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ShareDialog {

    /**
     * 分享
     *
     * @param activity activity
     * @param listener 监听
     */
    public static void showSharedDialog(Activity activity, final SharedListener listener) {
        final Dialog bottomDialog = new Dialog(activity, R.style.BottomDialog);
        View contentView = LayoutInflater.from(activity).inflate(R.layout.dlg_layout_shared, null, false);
        LinearLayout llWX = contentView.findViewById(R.id.dlg_shared_wx_friend);
        LinearLayout llWXC = contentView.findViewById(R.id.dlg_shared_wx_friend_circle);
        LinearLayout llQQ = contentView.findViewById(R.id.dlg_shared_qq_friend);
        LinearLayout llQQZone = contentView.findViewById(R.id.dlg_shared_qq_friend_zone);
        LinearLayout llSina = contentView.findViewById(R.id.dlg_shared_sina);
        LinearLayout llWXCollect = contentView.findViewById(R.id.dlg_shared_wx_collect);
        TextView cancel = contentView.findViewById(R.id.dlg_shared_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomDialog.dismiss();
            }
        });
        llWX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.sharedToWXFriend();
                bottomDialog.dismiss();
            }
        });
        llWXC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.sharedToWXFriendCircle();
                bottomDialog.dismiss();
            }
        });
        llWXCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.sharedToWXCollect();
                bottomDialog.dismiss();
            }
        });
        llQQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.sharedToQQFriend();
                bottomDialog.dismiss();
            }
        });
        llQQZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.sharedToQQZone();
                bottomDialog.dismiss();
            }
        });
        llSina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.sharedToSina();
                bottomDialog.dismiss();
            }
        });
        bottomDialog.setContentView(contentView);
        ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
        layoutParams.width = activity.getResources().getDisplayMetrics().widthPixels;
        contentView.setLayoutParams(layoutParams);
        bottomDialog.setCanceledOnTouchOutside(true);
        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        bottomDialog.show();

    }

    public interface SharedListener {

        void sharedToWXFriend();

        void sharedToWXFriendCircle();

        void sharedToWXCollect();

        void sharedToQQFriend();

        void sharedToQQZone();

        void sharedToSina();
    }


}
