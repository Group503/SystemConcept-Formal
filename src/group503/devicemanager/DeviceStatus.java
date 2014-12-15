/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package group503.devicemanager;

/**
 *
 * @author yonss
 */
public class DeviceStatus {
    int ID;// 设备ID
    boolean isAllocate=false;// 是否已分配，默认为false
    int process_ID;// 占用进程ID
    int r_ID;// 设备类相对号
    
    /**
     * 有参构造函数
     * @param ID 设备ID
     */
    DeviceStatus(int ID){
        
        this.ID = ID;
    }
}
