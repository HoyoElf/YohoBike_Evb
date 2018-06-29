package yoho.bike.evb.com.base;

import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import com.blankj.utilcode.util.ToastUtils;
import com.handy.base.app.BaseActivity;
import com.handy.base.utils.ActivityStackUtils;
import com.handy.base.utils.PermissionsUtils;
import com.handy.sweetalert.SweetDialogUtil;

import java.util.Observable;
import java.util.Observer;


/**
 * <pre>
 *  author : LiuJie
 *  desc :
 *  blog : https://github.com/liujie045
 *  createtime : 2017/4/28 11:23
 *  updatetime : 2017/4/28 11:23
 * </pre>
 */
public abstract class LocalBaseActivity extends BaseActivity implements Observer,YohoApplicationApi.YohoBaseActivityApi{

    private String version = "";
    private String pakgeName = "";

    @Override
    public void onPermissionRejectionHDB(String[] permissions) {
        super.onPermissionRejectionHDB(permissions);
        SweetDialogUtil.getInstance().showNormal("发现未启用权限", "为保障应用正常使用，请开启应用权限", "开启", "退出", sweetAlertDialog -> {
            ToastUtils.showLong("请在手机设置权限管理中启用开启此应用系统权限");
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 100);
            sweetAlertDialog.dismiss();
        }, sweetAlertDialog -> {
            sweetAlertDialog.dismiss();
            ActivityStackUtils.appExit();
        }).setCancelable(false);
    }

    //TODO: 2016/7/21 若从设置界面返回，重新扫描权限
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            PermissionsUtils.checkDeniedPermissions(activity,true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SweetDialogUtil.getInstance().dismissAll();
    }


    @Override
    public void update(Observable o, Object arg) {

        upObserverData(o, arg);

    }


}
