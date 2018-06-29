package yoho.bike.evb.com.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.blankj.utilcode.util.ToastUtils;
import com.handy.base.utils.IntentUtils;

import java.util.Observable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import yoho.bike.evb.com.R;
import yoho.bike.evb.com.base.LocalBaseActivity;
import yoho.bike.evb.com.base.YohoEvbApplication;
import yoho.bike.evb.com.bean.MessgeBean;

/**
 * 主界面
 *
 * @author yoho
 **/
public class YohoActivity extends LocalBaseActivity {


    @BindView(R.id.btn)
    Button btn;
    @BindView(R.id.btn1)
    Button btn1;


    public static void doIntent(Activity activity,boolean isFinish){
        IntentUtils.openActivity(activity,YohoActivity.class,isFinish);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yoho);
        ButterKnife.bind(this);
        YohoEvbApplication.getApplication().yohoservice.addObserver(this);
    }

    @Override
    public void upObserverData(Observable o, Object arg) {
        MessgeBean messgeBean = (MessgeBean) arg;
        ToastUtils.showLong("我已经收到被观察者的数据了" + messgeBean.ToastMessage);
    }


    @OnClick({R.id.btn, R.id.btn1})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn:

                break;
            case R.id.btn1:
                YohoEvbApplication.getApplication().yohoservice.test();
                break;
        }
    }
}
