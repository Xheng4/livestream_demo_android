package cn.ucai.live.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import cn.ucai.live.R;
import cn.ucai.live.ui.activity.LoginActivity;
import cn.ucai.live.ui.activity.MainActivity;
import cn.ucai.live.ui.activity.RegisterActivity;


/**
 * Created by xheng on 2017/3/29.
 */

public class MFGT {
    public static void startActivity(Activity activity, Class cla) {
        activity.startActivity(new Intent(activity, cla));
        activity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    public static void startActivityZ(Activity activity, Class cla) {
        activity.startActivity(new Intent(activity, cla));
        activity.overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
    }

    public static void startActivityForResult(Activity activity, Intent intent, int requestCode) {
        activity.startActivityForResult(intent, requestCode);
        activity.overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    public static void startActivity(Activity activity, Intent intent) {
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    public static void finish(Activity activity) {
        activity.overridePendingTransition(R.anim.push_left_out, R.anim.push_left_in);
        activity.finish();
    }

    public static void finishZ(Activity activity) {
        activity.overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
        activity.finish();
    }

    public static void gotoMain(Activity activity) {
        startActivity(activity, MainActivity.class);
    }

    public static void gotoLogin(Activity activity) {
        startActivity(activity, LoginActivity.class);
    }

    public static void logoutToLogin(Activity activity) {
        startActivity(activity, new Intent(activity, LoginActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    public static void gotoRegister(Activity activity) {
        startActivity(activity, RegisterActivity.class);
    }


}
