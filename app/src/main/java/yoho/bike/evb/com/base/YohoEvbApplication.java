package yoho.bike.evb.com.base;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.blankj.utilcode.util.AppUtils;
import com.handy.base.app.BaseApplication;
import com.handy.base.config.BuglyConfig;
import com.handy.base.utils.PermissionsUtils;
import com.handy.sweetalert.SweetDialogClient;
import com.handy.sweetalert.SweetDialogUtil;

import java.util.ArrayList;

import yoho.bike.evb.com.service.YohoEvbService;

/**
 * 类名
 *
 * @author Yoho https://github.com/YohoElf
 * @description 类功能内容
 * @date Created in 2018/6/4 上午11:29
 * @modified By Yoho
 */
public class YohoEvbApplication extends BaseApplication {

    private static YohoEvbApplication yohoEvbApplication = null;
    public YohoEvbService yohoservice = null;
    private ActivityLifecycleCallbacks activityLifecycleCallbacks;
    public Vibrator mVibrator;

    {
        buglyID = "aa9fd86a26";
        PermissionsUtils.setPermissions(new ArrayList<String>() {
            {
                add(Manifest.permission.READ_EXTERNAL_STORAGE);
                add(Manifest.permission.ACCESS_COARSE_LOCATION);
                add(Manifest.permission.ACCESS_NETWORK_STATE);
                add(Manifest.permission.INTERNET);
                add(Manifest.permission.READ_PHONE_STATE);
                add(Manifest.permission.ACCESS_WIFI_STATE);
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                add(Manifest.permission.RECORD_AUDIO);
                add(Manifest.permission.GET_TASKS);
                add(Manifest.permission.ACCESS_FINE_LOCATION);
                add(Manifest.permission.BLUETOOTH);
                add(Manifest.permission.BLUETOOTH_ADMIN);
                add(Manifest.permission.CAMERA);
            }
        });


    }

    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            YohoEvbService.YohoBinder binder = (YohoEvbService.YohoBinder) service;
            yohoservice = binder.getService();
            Log.e("----", "启动服务成功");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            yohoservice = null;
            Log.e("---", "启动服务失败");
        }
    };

    public static YohoEvbApplication getApplication() {
        return yohoEvbApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        yohoEvbApplication = this;
        try {
            mVibrator = (Vibrator) getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
            registCallback();

        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(this, YohoEvbService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
        Log.e("---", "启动服务");
    }


    @Override
    protected BuglyConfig resetBuglyConfig(BuglyConfig buglyConfig) {
        buglyConfig.setDevelopmentDevice(true);
        buglyConfig.setAppChannel(AppUtils.isAppDebug() ? "测试版本" : "正式版本");
        return buglyConfig;

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    private void registCallback() {
        registerActivityLifecycleCallbacks(activityLifecycleCallbacks = new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                SweetDialogClient.connect(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {
                SweetDialogClient.connect(activity);
            }

            @Override
            public void onActivityResumed(Activity activity) {
                SweetDialogClient.connect(activity);
            }

            @Override
            public void onActivityPaused(Activity activity) {
                if (activity != null && activity.isFinishing()) {
                    SweetDialogUtil.getInstance().dismissAll();
                }
            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks);
        /**
         * 关闭服务
         * **/
        unbindService(connection);
    }
}
