
package group503.devicemanager;

/**
 * 设备状态表类
 * @author 刘恩坚
 */
public class DeviceStatus {
    String name;// 设备名
    int ID;// 设备ID
    boolean isAllocate=false;// 是否已分配，默认为false
    int process_ID = -1;// 占用进程ID，-1表示无进程占用
    int r_ID;// 设备类相对号
    
    // 设备相对地址 = ID - r_ID
    
    /**
     * 有参构造函数
     * @param ID 设备ID
     */
    DeviceStatus(String name, int ID, int r_ID){
        this.name = name;
        this.ID = ID;
        this.r_ID = r_ID;
    }
}
