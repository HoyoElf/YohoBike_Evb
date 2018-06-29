package yoho.bike.evb.com.ui;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.widget.Button;

import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.util.Observable;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.bgabanner.BGABanner;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import yoho.bike.evb.com.R;
import yoho.bike.evb.com.base.LocalBaseActivity;
import yoho.bike.evb.com.base.YohoEvbApplication;
import yoho.bike.evb.com.util.ThreadPoolProxyFactory;

/**
 * @author yoho
 */
public class StartActivity extends LocalBaseActivity {

    @BindView(R.id.banner_guide_bgabanner)
    BGABanner bannerGuideBgabanner;
    @BindView(R.id.yoho_guide_skip_btn)
    Button yohoGuideSkipBtn;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
           switch (msg.what){
               case 1:
                   ToastUtils.showLong("蓝牙开启成功");
                   //开始验证其他功能
                   ThreadPoolProxyFactory.getNormalThreadPoolProxy().execute(new Runnable() {
                       @Override
                       public void run() {
                           if (YohoEvbApplication.getApplication().yohoservice != null) {
                               ThreadPoolProxyFactory.getNormalThreadPoolProxy().remove(this);
                               YohoActivity.doIntent(activity,true);
                           }
                       }
                   });
                   break;
               case 2:
                   ToastUtils.showLong("您的设备不支持ble功能");
                   break;
               case 3:
                   ToastUtils.showLong("蓝牙服务已开启");
                   break;
               case 4:
                   ToastUtils.showLong("蓝牙服务开启失败");
                   break;
                   default:
           }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);
    }

    @Override
    public void initDataHDB() {
        super.initDataHDB();
        registerReceiver(broadcastReceiver,new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
    }

    @Override
    public void initViewHDB(@Nullable Bundle savedInstanceState) {
        super.initViewHDB(savedInstanceState);
        //设置数据来源
    }


    @Override
    public void onPermissionRejectionHDB(String[] permissions) {
        super.onPermissionRejectionHDB(permissions);
        io.reactivex.Observable.timer(3, TimeUnit.SECONDS).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(aLong -> {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter != null){
                if (!bluetoothAdapter.isEnabled()){
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }else {
                    handler.sendEmptyMessage(1);
                }
            } else {
                ToastUtils.showLong("很抱歉，您的设备暂不支持蓝牙功能");
                finish();
            }
        });

    }

    @OnClick(R.id.yoho_guide_skip_btn)
    public void onViewClicked() {

    }

    @Override
    public void upObserverData(Observable o, Object arg) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()){
            unregisterReceiver(broadcastReceiver);
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ObjectUtils.isNotEmpty(intent) && ObjectUtils.isNotEmpty(intent.getAction())){
                switch (intent.getAction()){
                    case BluetoothAdapter.ACTION_STATE_CHANGED:
                        int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,0);
                        switch (blueState){
                            case BluetoothAdapter.STATE_TURNING_ON:
                                break;
                            case BluetoothAdapter.STATE_ON:
                                handler.sendEmptyMessage(1);
                                break;
                            case BluetoothAdapter.STATE_TURNING_OFF:
                                break;
                            case BluetoothAdapter.STATE_OFF:
                                break;
                        }
                        break;
                        default:
                }
            }
        }
    };
}
