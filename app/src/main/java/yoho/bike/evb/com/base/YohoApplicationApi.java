package yoho.bike.evb.com.base;

import java.util.Observable;

/**
 * 类名
 *
 * @author Yoho https://github.com/YohoElf
 * @description 类功能内容
 * @date Created in 2018/6/7 上午9:28
 * @modified By Yoho
 */
public class YohoApplicationApi {
    interface YohoBaseActivityApi{
        /**
         * 观察者接受被观察者发送出来的数据
         * **/
        void upObserverData(Observable o, Object arg);
    }
}
