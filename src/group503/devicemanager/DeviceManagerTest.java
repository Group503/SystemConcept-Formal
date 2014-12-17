package group503.devicemanager;

import java.util.HashMap;
import java.util.Map;

/**
 * 设备测试类
 * @author 刘恩坚
 */
public class DeviceManagerTest {

    public static void main(String args[]) {
        
        DeviceManager deviceManager = new DeviceManager();
        DeviceInfoMap brI = new DeviceInfoMap();
        brI.add(deviceManager.allDevice.get("A").name, 1);
        brI.add(deviceManager.allDevice.get("B").name, 2);
        int process_ID = 1233;
        int status = deviceManager.allocate(process_ID, brI);
        if(status == -1){
            System.out.println("进程" + process_ID + "申请（+已占用）设备总量超过总数！");
        }else if(status == 0){
            System.out.println("进程" + process_ID + "未通过银行家算法，进程不安全不可分配");
        }else if(status == 1){
            System.out.println("进程" + process_ID + "通过银行家算法，进程安全可分配！");
        }else if(status == 2){
            System.out.println("进程" + process_ID + "通过银行家算法，进程安全但需要等待！");
        }
        //deviceManager.printDev();
        brI = new DeviceInfoMap();
        brI.add(deviceManager.allDevice.get("C").name, 1);
        
        status = deviceManager.allocate(process_ID, brI);
        if(status == -1){
            System.out.println("进程" + process_ID + "申请（+已占用）设备总量超过总数！");
        }else if(status == 0){
            System.out.println("进程" + process_ID + "未通过银行家算法，进程不安全不可分配");
        }else if(status == 1){
            System.out.println("进程" + process_ID + "通过银行家算法，进程安全可分配！");
        }else if(status == 2){
            System.out.println("进程" + process_ID + "通过银行家算法，进程安全但需要等待！");
        }
        //deviceManager.printDev();
        
//        deviceManager.deAllocate(process_ID);
//        System.out.println("进程" + process_ID + "已释放设备！");
//        deviceManager.printDev();
    }
}
