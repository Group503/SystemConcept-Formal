package group503.devicemanager;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * （银行）进程属性类
 *
 * @author 刘恩坚
 */
class BKProcess {

    DeviceInfoMap Allocation = new DeviceInfoMap();// 进程已占用的设备信息
    DeviceInfoMap Need = new DeviceInfoMap();// 进程申请设备的信息
    boolean Finished = false;// 进程是否处于安全序列
    
    BKProcess(DeviceInfoMap Allocation, DeviceInfoMap Need){
        this.Allocation = Allocation;
        this.Need = Need;
    }
}
