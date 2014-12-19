
package group503.devicemanager;

import java.util.HashMap;
import java.util.Map;

/**
 * 设备等待队列元素类
 * @author 刘恩坚
 */
public class QueueElem {
    int process_ID;// 进程ID
    DeviceInfoMap borrowInfo = new DeviceInfoMap();// 设备申请信息
    //Map<String,Integer> borrowInfo = new LinkedHashMap<String,Integer>();// 设备申请信息
    /*
      {
          进程ID,
          设备申请信息{
               设备名：数量,
               设备名：数量
               设备名：数量
          },
      }
     */
    
    /**
     * 有参构造函数
     * @param process_ID 进程ID
     * @param borrowInfo 设备申请信息
     */
    QueueElem(int process_ID, DeviceInfoMap borrowInfo){
        this.process_ID = process_ID;
        this.borrowInfo = borrowInfo;
    }
}
