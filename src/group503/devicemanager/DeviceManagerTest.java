package group503.devicemanager;

import java.util.HashMap;
import java.util.Map;

/**
 * 设备测试类
 *
 * @author 刘恩坚
 */
public class DeviceManagerTest {

    public static void main(String args[]) {

        DeviceManager deviceManager = new DeviceManager();
        DeviceInfoMap info;
        DeviceInfoMap br;
        QueueElem e;
        int pcs;

        deviceManager.allDevice.get("A").left = 3;
        deviceManager.allDevice.get("B").left = 3;
        deviceManager.allDevice.get("C").left = 2;

        pcs = 0;
        //P0，占用010，申请743
        info = new DeviceInfoMap();
        br = new DeviceInfoMap();
        info.add("A", 0);
        info.add("B", 1);
        info.add("C", 0);
        deviceManager.processInfo.put(0, info);//占用1个设备A
        br.add("A", 7);
        br.add("B", 4);
        br.add("C", 3);
        e = new QueueElem(0, br);//申请1个设备B
        deviceManager.d_Queue.add(e);//加入等待队列

        pcs = 2;
        //P2，占用302，申请600
        info = new DeviceInfoMap();
        br = new DeviceInfoMap();
        info.add("A", 3);
        info.add("B", 0);
        info.add("C", 2);
        deviceManager.processInfo.put(2, info);//占用1个设备A
        br.add("A", 6);
        br.add("B", 0);
        br.add("C", 0);
        e = new QueueElem(2, br);//申请1个设备B
        deviceManager.d_Queue.add(e);//加入等待队列

        pcs = 3;
        //P3，占用211，申请011
        info = new DeviceInfoMap();
        br = new DeviceInfoMap();
        info.add("A", 2);
        info.add("B", 1);
        info.add("C", 1);
        deviceManager.processInfo.put(3, info);//占用1个设备A
        br.add("A", 0);
        br.add("B", 1);
        br.add("C", 1);
        e = new QueueElem(3, br);//申请1个设备B
        deviceManager.d_Queue.add(e);//加入等待队列

        pcs = 4;
        //P4，占用002，申请431
        info = new DeviceInfoMap();
        br = new DeviceInfoMap();
        info.add("A", 0);
        info.add("B", 0);
        info.add("C", 2);
        deviceManager.processInfo.put(4, info);//占用1个设备A
        br.add("A", 4);
        br.add("B", 3);
        br.add("C", 1);
        e = new QueueElem(4, br);//申请1个设备B
        deviceManager.d_Queue.add(e);//加入等待队列

        pcs = 1;
        //******P1
        info = new DeviceInfoMap();
        br = new DeviceInfoMap();
        info.add("A", 2);
        info.add("B", 0);
        info.add("C", 0);
        deviceManager.processInfo.put(1, info);//占用1个设备A

        br.add("A", 1);
        br.add("B", 2);
        br.add("C", 2);
        e = new QueueElem(1, br);//申请1个设备B
        deviceManager.d_Queue.add(e);//加入等待队列

        int status = deviceManager.allocate(pcs, br);//模拟进程33申请2个B设备
        if (status == -1) {
            System.out.println("进程P" + pcs + "申请（+已占用）设备总量超过总数！");
        } else if (status == 0) {
            System.out.println("进程P" + pcs + "未通过银行家算法，进程不安全不可分配");
        } else if (status == 1) {
            System.out.println("进程P" + pcs + "通过银行家算法，进程安全可分配！");
        } else if (status == 2) {
            System.out.println("进程P" + pcs + "通过银行家算法，进程安全但需要等待！");
        }
        deviceManager.printDev();
        
        // 将进程1释放
        deviceManager.deAllocate(pcs);//模拟进程33申请2个B设备
        
        deviceManager.printDev();
    }
}
