package group503.devicemanager;

import java.util.Locale;
import javax.swing.JFrame;

/**
 * 设备测试类
 *
 * @author 刘恩坚
 */
public class DeviceManagerTest {

    public static void main(String args[]) {

// *********************************************************************测试书本上的例题
        DeviceManager deviceManager = new DeviceManager();// 设备管理实例
        
        // 测试监听器
        deviceManager.addDeviceWatcher(new DeviceWatcherImpl() {
            @Override
            public void allocatedDeviceTo(int process_ID, int status) {
                System.out.println("监听器收到通知了！");
            }
        });
        DeviceInfoMap info;
        DeviceInfoMap br;// 申请设备信息实例
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
        deviceManager.allDeviceStatus.get(10).isAllocate = true;
        deviceManager.allDeviceStatus.get(10).process_ID = 0;
        deviceManager.allDeviceStatus.get(10).r_ID = 0;
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
        deviceManager.allDeviceStatus.get(0).isAllocate = true;deviceManager.allDeviceStatus.get(1).isAllocate = true;deviceManager.allDeviceStatus.get(2).isAllocate = true;
        deviceManager.allDeviceStatus.get(0).process_ID = 2;deviceManager.allDeviceStatus.get(1).process_ID = 2;deviceManager.allDeviceStatus.get(2).process_ID = 2;
        deviceManager.allDeviceStatus.get(0).r_ID = 0;deviceManager.allDeviceStatus.get(1).r_ID = 1;deviceManager.allDeviceStatus.get(2).r_ID = 2;
        info.add("B", 0);
        info.add("C", 2);
        deviceManager.allDeviceStatus.get(15).isAllocate = true;deviceManager.allDeviceStatus.get(16).isAllocate = true;
        deviceManager.allDeviceStatus.get(15).process_ID = 2;deviceManager.allDeviceStatus.get(16).process_ID = 2;
        deviceManager.allDeviceStatus.get(15).r_ID = 0;deviceManager.allDeviceStatus.get(16).r_ID = 1;
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
        deviceManager.allDeviceStatus.get(3).isAllocate = true;deviceManager.allDeviceStatus.get(4).isAllocate = true;
        deviceManager.allDeviceStatus.get(3).process_ID = 3;deviceManager.allDeviceStatus.get(4).process_ID = 3;
        deviceManager.allDeviceStatus.get(3).r_ID = 3;deviceManager.allDeviceStatus.get(4).r_ID = 4;
        info.add("B", 1);
        deviceManager.allDeviceStatus.get(11).isAllocate = true;
        deviceManager.allDeviceStatus.get(11).process_ID = 3;
        deviceManager.allDeviceStatus.get(11).r_ID = 1;
        info.add("C", 1);
        deviceManager.allDeviceStatus.get(17).isAllocate = true;
        deviceManager.allDeviceStatus.get(17).process_ID = 3;
        deviceManager.allDeviceStatus.get(17).r_ID = 2;
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
        deviceManager.allDeviceStatus.get(18).isAllocate = true;deviceManager.allDeviceStatus.get(19).isAllocate = true;
        deviceManager.allDeviceStatus.get(18).process_ID = 4;deviceManager.allDeviceStatus.get(19).process_ID = 4;
        deviceManager.allDeviceStatus.get(18).r_ID = 3;deviceManager.allDeviceStatus.get(19).r_ID = 4;
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
        deviceManager.allDeviceStatus.get(5).isAllocate = true;deviceManager.allDeviceStatus.get(6).isAllocate = true;
        deviceManager.allDeviceStatus.get(5).process_ID = 1;deviceManager.allDeviceStatus.get(6).process_ID = 1;
        deviceManager.allDeviceStatus.get(5).r_ID = 5;deviceManager.allDeviceStatus.get(6).r_ID = 6;
        info.add("B", 0);
        info.add("C", 0);
        deviceManager.processInfo.put(1, info);//占用1个设备A
// ****************************************************************以上为例题初始环境模拟*********************************
        
// ***************************以下为模拟进程P1申请设备A1,B2,C2（此处模拟P1是为了方便以PPT上例题的解为参照）********************
        br.add("A", 1);// 借1个A设备
        br.add("B", 2);// 借2个B设备
        br.add("C", 2);// 借2个C设备
        //e = new QueueElem(1, br);//申请1个设备B
        //deviceManager.d_Queue.add(e);//加入等待队列

        int status = deviceManager.allocate(pcs, br);//模拟进程pcs=1申请1A,2B,2C
        if (status == -1) {
            System.out.println("进程P" + pcs + "申请（+已占用）设备总量超过总数！");
        } else if (status == 0) {
            System.out.println("进程P" + pcs + "未通过银行家算法，进程不安全不可分配");
        } else if (status == 1) {
            System.out.println("进程P" + pcs + "通过银行家算法，进程安全可分配！");
        } else if (status == 2) {
            System.out.println("进程P" + pcs + "通过银行家算法，进程安全但需要等待！");
        }
        deviceManager.printDev();// 打印输出
        
        
        
        // 测试展示界面deviceManager.showPanel
        JFrame tFrame = new JFrame();
        tFrame.add(deviceManager.showPanel);// 设备情况展示panel，直接使用，会实时更新

        tFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        tFrame.setLocationRelativeTo(null);
        tFrame.setSize(360, 300);
        tFrame.setVisible(true);

        // 将进程1释放，测试展示界面是否实时更新
        deviceManager.deAllocate(pcs);// 释放进程pcs=1占用的设备
        deviceManager.printDev();// 打印输出
    }
}
