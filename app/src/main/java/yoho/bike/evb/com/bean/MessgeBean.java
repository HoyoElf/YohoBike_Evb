package yoho.bike.evb.com.bean;

/**
 * 类名
 *
 * @author Yoho https://github.com/YohoElf
 * @description Service发送给观察者的数据实体类。观察者通过这个类可以获取到想要的数据
 * @date Created in 2018/6/6 下午5:40
 * @modified By Yoho
 */
public class MessgeBean {

   public  enum Actions {
       /**连接失败**/
        CONNECT_FAILED,
       /**16进制数据返回**/
       BEL_DATA,
       /**服务挂了**/
       SERVICE_FAILED
    }

    /**
     * 广播ID
     * **/
    public Actions action;
    /**
     * 16进制的数据
     * **/
    public String HexData = "";
    /**当有需要提示的信息时使用**/
    public String ToastMessage= "";
}
