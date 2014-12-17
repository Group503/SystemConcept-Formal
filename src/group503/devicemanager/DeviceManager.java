package group503.devicemanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * 设备总管类
 *
 * @author 刘恩坚
 */
public class DeviceManager {

    Map<String, Device> allDevice = new HashMap<String, Device>();// 设备类表
    ArrayList<DeviceStatus> allDeviceStatus = new ArrayList<DeviceStatus>();// 设备状态表
    Map<Integer, DeviceInfoMap> processInfo = new HashMap<Integer, DeviceInfoMap>();// 进程占用设备表
    Queue<QueueElem> d_Queue = new LinkedList<QueueElem>();// 设备等待队列

    DeviceManager() {
        // 初始化设备类、设备状态表
        initDevice();
        initDeviceStatus();

    }

    /**
     * 初始化设备类表
     */
    private void initDevice() {

        allDevice.put("A", new Device("A", 1, 0));
        allDevice.put("B", new Device("B", 2, 1));
        allDevice.put("C", new Device("C", 2, 3));
    }

    /**
     * 初始化设备状态表
     */
    private void initDeviceStatus() {

        // HaspMap遍历，http://www.cnblogs.com/fczjuever/archive/2013/04/07/3005997.html
//        for (String key : allDevice.keySet()) {
//            Device value = allDevice.get(key);
//            amount += value.amount;
//        }

        for (String key : allDevice.keySet()) {
            Device value = allDevice.get(key);
            for (int i = value.r_address; i < value.r_address + value.amount; i++) {
                allDeviceStatus.add(new DeviceStatus(value.name, i, i - value.r_address));
            }
        }
//        for (DeviceStatus e : allDeviceStatus) {
//
//            System.out.println(e.name + " - " + e.ID + " - " + e.r_ID);
//        }
    }

    /**
     * 分配设备，更新 设备类表，设备状态表，进程占用设备表 PS：调用该方法默认能够分配设备，不加其它可行性检查！
     *
     * @param process_ID 进程ID
     * @param borrowInfo 设备借用信息
     */
    private void allocateDevice(int process_ID, DeviceInfoMap borrowInfo) {
        DeviceInfoMap ocpI = new DeviceInfoMap();
        // 将申请的设备分配给该进程，更新 设备类表 和 设备状态表
        for (String key : borrowInfo.keySet()) {// key -> borrowInfo.get(key)
            int value = borrowInfo.get(key);
            ocpI.add(key, value);// 组合进程占用的设备信息

            allDevice.get(key).left -= value;// 更新 设备类表

            // 更新 设备状态表
            int c_alloc = 0;// 成功分配该设备个数
            for (int i = 0; i < allDeviceStatus.size(); i++) {

                DeviceStatus e = allDeviceStatus.get(i);
                if (e.name.equals(key) && !e.isAllocate) {// 目标设备，且未分配

                    allDeviceStatus.get(i).isAllocate = true;
                    allDeviceStatus.get(i).process_ID = process_ID;
                    c_alloc++;
                }

                if (c_alloc == value) {// 设备分配够了
                    break;
                }
            }
        }
        processInfo.put(process_ID, ocpI);// 更新 进程占用设备表
        System.out.println("##allocateDevice  --  " + "设备成功分配给该进程！");
        //printDev();// 打印输出
    }

    /**
     * 申请设备
     *
     * @param process_ID 进程ID
     * @param borrowInfo 设备申请信息
     * @return 申请设备超出总数-1 不安全不可分配0 安全可分配1 安全但等待2
     */
    public int allocate(int process_ID, DeviceInfoMap borrowInfo) {
//       判断申请是否合理（不超出总数）
//          合理，判断[设备等待队列]是否空
//               是，第1个进程占用设备，直接分配，更新 设备分配表
//               否，判断是否可分配设备给当前进程元素（cur=new队列元素类）
//                    可以分配，返回true
//                    不可分配，返回false
        if (!checkBorrow_All(borrowInfo)) {
            return -1;
        }

        if (processInfo.isEmpty()) {// 进程占用设备表为空
            // 第1个进程申请设备，直接分配
            allocateDevice(process_ID, borrowInfo);
            return 1;
        } else {
            System.out.println("进程占用设备表不为空");
            // 判断是否可分配设备给当前进程元素（cur=new队列元素类）
            QueueElem cur = new QueueElem(process_ID, borrowInfo);
            return toAlocate(cur);
        }

    }

    /**
     * 释放设备
     *
     * @return
     */
    public void deAllocate(int process_ID) {

        for (int i = 0; i < allDeviceStatus.size(); i++) {

            DeviceStatus e = allDeviceStatus.get(i);
            if (e.process_ID == process_ID) {// 目标进程
                allDevice.get(e.name).left++;// 更新 设备类表

                // 更新 设备状态表
                allDeviceStatus.get(i).isAllocate = false;
                allDeviceStatus.get(i).process_ID = -1;
            }
        }
        processInfo.remove(process_ID);// 更新 进程占用设备表
        //printDev();// 打印输出
    }

    /**
     * 判断申请是否合理（申请数不超过总数）
     *
     * @param borrowInfo 设备申请信息
     * @return
     */
    private boolean checkBorrow_All(DeviceInfoMap borrowInfo) {
        boolean isLegal = true;
        for (String key : borrowInfo.keySet()) {// key -> borrowInfo.get(key)

            if (borrowInfo.get(key) > allDevice.get(key).amount) {
                // 对应key的设备，申请数量 > 总数量
                System.out.println("##checkBorrow_All  --  " + key + "设备，申请数量" + borrowInfo.get(key) + " > 总数量" + allDevice.get(key).amount);
                isLegal = false;
                break;
                
            }
        }

        if(!isLegal){
            return false;
        }
        
        System.out.println("##checkBorrow_All  --  " + "设备申请信息borrowInfo，通过合法性(总数)检查！");
        return true;
    }

    /**
     * 判断是否可分配设备给当前进程元素（cur=new队列元素类）
     *
     * @param cur
     * @return
     */
    private int toAlocate(QueueElem cur) {
        //检查该进程是否已占用有设备（即同一进程重复申请设备）(总设备有1A+2B+2C)
        //情况如：进程1233已占用1A+2B，再申请1A，如不判断，则[进程1233，申请1A]会进队列，但事实上不可能满足（因为申请数>总数）

        //检查cur.process_ID在processInfo表中存在
        //  存在，即已占用设备的进程，再次申请
        //      取出该进程占用的设备信息+再次申请的设备信息，判断总设备数量是否合法
        //          合法，调用银行家算法
        //          不合法，return -1;
        //  不存在
        //      判断设备申请信息是否合法
        //          合法，调用银行家算法
        //          不合法，return -1;

        boolean isLegal = true;// 设备申请总数是否合法标记
        if (processInfo.containsKey(cur.process_ID)) {
            System.out.println("有这个进程ID！");

            DeviceInfoMap ocpI = processInfo.get(cur.process_ID);// 已存在进程中的占用设备信息
            DeviceInfoMap brI = cur.borrowInfo;// 总占用设备信息

            for (String dName : ocpI.keySet()) {// 组合总占用设备信息到brI
                if (brI.containsKey(dName)) {// cur.borrowInfo包含已存在进程ocpI中的占用设备

                    brI.add(dName, ocpI.get(dName) + brI.get(dName));// 新的总量，覆盖
                } else {// 不包含，申请了新的设备总类

                    brI.add(dName, ocpI.get(dName));// 增加
                }
            }

            if (!checkBorrow_All(brI)) {// 不合法
                isLegal = false;
            }
        }else{
            if (!checkBorrow_All(cur.borrowInfo)) {// 不合法
                isLegal = false;
            }
        }

        if (!isLegal) {// 不合法
            return -1;
        }else{
            // 调用银行家算法
            
            return 0;// 不安全，不可分配
        }
    }

    /**
     * toAlocate调用的内层方法to_2_Alocate
     *
     * @return
     */
    private boolean to_2_Alocate() {

        return false;
    }

    /**
     * 银行家算法
     *
     * @return
     */
    private boolean bankJudge() {

        return false;
    }

    /**
     * 展示 设备分配表，设备等待队列
     */
    public void printDev() {
        System.out.println("##printDev  --  " + "打印 设备类表，设备状态表，设备等待队列！");
        System.out.println("设备类表");
        for (String key : allDevice.keySet()) {
            Device value = allDevice.get(key);
            System.out.println("设备名\t总数\t空闲数\t相对地址");
            System.out.println(value.name + "\t" + value.amount + "\t" + value.left + "\t" + value.r_address);
        }

        System.out.println("-------------------------------------");
        System.out.println("设备状态表");
        for (int i = 0; i < allDeviceStatus.size(); i++) {
            DeviceStatus e = allDeviceStatus.get(i);
            System.out.println("设备名\t设备ID\t是否分配\t占用进程\t相对地址号");
            System.out.println(e.name + "\t" + e.ID + "\t" + e.isAllocate + "\t" + e.process_ID + "\t" + e.r_ID);
        }

        System.out.println("-------------------------------------");
        if (!processInfo.isEmpty()) {

            System.out.println("进程占用设备表");
            for (Integer process_ID : processInfo.keySet()) {
                DeviceInfoMap ocpI = processInfo.get(process_ID);
                System.out.println("进程ID\t占用设备信息");
                System.out.println(process_ID);
                for (String key : ocpI.keySet()) {
                    System.out.println("\t" + key + ":" + ocpI.get(key));
                }
            }
        } else {
            System.out.println("进程占用设备表，空！");
        }

        System.out.println("-------------------------------------");
        if (!d_Queue.isEmpty()) {

            System.out.println("设备等待队列");
            int queueSize = d_Queue.size();
            for (int i = 0; i < queueSize; i++) {
                QueueElem e = d_Queue.remove();
                System.out.println("进程ID\t设备申请信息");
                System.out.print(e.process_ID + "\t");

                DeviceInfoMap brI = e.borrowInfo;
                for (String key : brI.keySet()) {
                    System.out.print(key + ":" + brI.get(key) + " ");
                }
                System.out.println();

                d_Queue.add(e);
            }
        } else {
            System.out.println("设备等待队列，空！");
        }
    }
}