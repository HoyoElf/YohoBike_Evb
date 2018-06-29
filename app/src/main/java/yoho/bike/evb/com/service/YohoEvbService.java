package yoho.bike.evb.com.service;

import android.app.Service;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.ble.api.DataUtil;
import com.ble.ble.BleCallBack;
import com.ble.ble.BleService;

import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

import yoho.bike.evb.com.bean.MessgeBean;

/***
 * 后台服务处理类。
 * 主要完成，车辆的连接工作。同时接受数据，并将数据已广播的形式发送出去。
 *
 * @author yoho**/
public class YohoEvbService extends Service {
    private YohoBinder INSTANCE = new YohoBinder();
    //被观察者
    private YohoObservable mObservable;
    private BleService mLeService;
    private ServiceConnection connection;
    private String Tag = "YohoEvbService";
    public YohoEvbService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return INSTANCE;
    }


    public class YohoBinder extends Binder {
        public YohoEvbService getService() {
            return YohoEvbService.this;
        }
    }

    /**
     *
     * **/
    @Override
    public void onCreate() {
        super.onCreate();
        mObservable = new YohoObservable();
        if (connection == null || mLeService == null) {
            getBleService();
        }
    }

    /**
     * 每次通过StartService()方法启动service时会调用
     **/
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mObservable == null){
            mObservable = new YohoObservable();
        }
        if (connection == null || mLeService == null) {
            getBleService();
        }
        return super.onStartCommand(intent, flags, startId);
    }


    /**
     * 服务销毁时会回调
     **/
    @Override
    public void onDestroy() {
        super.onDestroy();
        //在销毁service时需要关闭蓝牙操作器
        mLeService.removeBleCallBack(mBleCallBack);
    }


    private void getBleService() {
        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                if (mLeService == null) {
                    // BleCallBack是个关键参数
                    mLeService = ((BleService.LocalBinder) service).getService(mBleCallBack);
                    // 必须调用初始化函数
                    mLeService.initialize();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mLeService = null;
            }
        };
    }

    private final BleCallBack mBleCallBack = new BleCallBack() {
        @Override
        public void onConnected(String s) {
            super.onConnected(s);
            //车辆已经连接，可以进行蓝牙连接操作
            Log.i(Tag,"onConnected--->"+s);
        }

        @Override
        public void onConnectTimeout(String s) {

            super.onConnectTimeout(s);
            //车辆连接超时
            Log.i(Tag,"onConnectTimeout--->"+s);
        }

        @Override
        public void onConnectionError(String s, int i, int i1) {
            super.onConnectionError(s, i, i1);
            //连接车辆出现异常
            Log.i(Tag,"onConnectionError--->"+s);
        }

        @Override
        public void onDisconnected(String s) {
            super.onDisconnected(s);
            //已经处于断开连接状态
            Log.i(Tag,"onDisconnected--->"+s);
        }

        @Override
        public void onServicesDiscovered(String s) {
            super.onServicesDiscovered(s);
            //此时已经可以发送连接指令进行 车辆控制器连接
            Log.i(Tag,"onServicesDiscovered--->"+s);
        }

        @Override
        public void onServicesUndiscovered(String s, int i) {
            super.onServicesUndiscovered(s, i);
            Log.i(Tag,"onServicesUndiscovered--->"+s);
        }

        @Override
        public void onCharacteristicRead(String s, byte[] bytes, int i) {
            super.onCharacteristicRead(s, bytes, i);
            Log.i(Tag,"onCharacteristicRead--->"+s);
        }

        @Override
        public void onCharacteristicRead(String s, BluetoothGattCharacteristic bluetoothGattCharacteristic, int i) {
            super.onCharacteristicRead(s, bluetoothGattCharacteristic, i);
            Log.i(Tag,"onCharacteristicRead--->"+s);
        }

        @Override
        public void onRegRead(String s, String s1, int i, int i1) {
            super.onRegRead(s, s1, i, i1);
            Log.i(Tag,"onRegRead--->"+s);
        }

        @Override
        public void onCharacteristicChanged(String s, byte[] bytes) {
            super.onCharacteristicChanged(s, bytes);
            Log.i(Tag,"onCharacteristicChanged--byte->"+s);
        }

        @Override
        public void onCharacteristicChanged(String s, BluetoothGattCharacteristic bluetoothGattCharacteristic) {
            super.onCharacteristicChanged(s, bluetoothGattCharacteristic);
            Log.i(Tag,"onCharacteristicChanged--->"+s);
            //接受控制返回回来的数据
            String hexData = DataUtil.byteArrayToHex(bluetoothGattCharacteristic.getValue());
            if (!String.valueOf(hexData.charAt(0)).equalsIgnoreCase("F")) {
                //这种情况说明，控制返回的回来的数据有错误，不可进行下一步操作
            }
            MessgeBean messgeBean = new MessgeBean();
            messgeBean.action = MessgeBean.Actions.BEL_DATA;
            messgeBean.HexData = hexData;
            mObservable.notifyChanged(messgeBean);
        }

        @Override
        public void onCharacteristicWrite(String s, BluetoothGattCharacteristic bluetoothGattCharacteristic, int i) {
            super.onCharacteristicWrite(s, bluetoothGattCharacteristic, i);
            Log.i(Tag,"onCharacteristicWrite--->"+s);
        }

        @Override
        public void onDescriptorRead(String s, BluetoothGattDescriptor bluetoothGattDescriptor, int i) {
            super.onDescriptorRead(s, bluetoothGattDescriptor, i);
            Log.i(Tag,"onDescriptorRead--->"+s);
        }

        @Override
        public void onDescriptorWrite(String s, BluetoothGattDescriptor bluetoothGattDescriptor, int i) {
            super.onDescriptorWrite(s, bluetoothGattDescriptor, i);
            Log.i(Tag,"onDescriptorWrite--->"+s);
        }

        @Override
        public void onReadRemoteRssi(String s, int i, int i1) {
            super.onReadRemoteRssi(s, i, i1);
            //此处处理蓝牙的连接信息内容。表示蓝牙处于连接状态的信号
            Log.i(Tag,"onReadRemoteRssi--->"+s);
        }

        @Override
        public void onNotifyStateRead(UUID uuid, UUID uuid1, boolean b) {
            super.onNotifyStateRead(uuid, uuid1, b);
            Log.i(Tag,"onNotifyStateRead--->"+uuid);
        }

        @Override
        public void onMtuChanged(String s, int i, int i1) {
            super.onMtuChanged(s, i, i1);
            Log.i(Tag,"onMtuChanged--->"+s);
        }
    };

    /**
     * 发送蓝牙指令与车里进行信息交互
     * 发送蓝牙指令方法
     * @param mac 蓝牙地址
     * @param data 发送的数据，16进制码
     **/
    public void sendKey(String mac, String data) {
        if (mLeService != null) {
            mLeService.send(mac, DataUtil.hexToByteArray(data), false);
        }else {
            //此时需要发送一个服务没有正常启动的广播
            //此时需要发送一个服务没有正常启动的广播。
            MessgeBean messgeBean = new MessgeBean();
            messgeBean.action = MessgeBean.Actions.SERVICE_FAILED;
            messgeBean.ToastMessage = "服务启动失败，请重新关联";
            mObservable.notifyChanged(messgeBean);
        }
    }

    /**
     * 连接车里控制器
     * 发送连接指令方法
     * @param mac 蓝牙地址
     **/
    public void sendConnectKey(String mac) {
        if (mLeService != null) {
            mLeService.connect(mac, true);
        } else {
            //此时需要发送一个服务没有正常启动的广播。
            MessgeBean messgeBean = new MessgeBean();
            messgeBean.action = MessgeBean.Actions.SERVICE_FAILED;
            messgeBean.ToastMessage = "服务启动失败，请重新关联";
            mObservable.notifyChanged(messgeBean);
        }

    }

    /**
     * 添加观察者
     * @param observer
     */
    public void addObserver(Observer observer) {
        mObservable.addObserver(observer);
    }

    public class YohoObservable extends Observable {

        public void notifyChanged(Object object) {
            this.setChanged();
            this.notifyObservers(object);
        }
    }

    public void test(){
        MessgeBean messgeBean = new MessgeBean();
        messgeBean.action = MessgeBean.Actions.SERVICE_FAILED;
        messgeBean.ToastMessage = "我是测试数据";
        mObservable.notifyChanged(messgeBean);
    }
}
