package group503.devicemanager;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * 设备总管类
 *
 * @author 刘恩坚
 */
public class DeviceManager {
// HaspMap遍历，http://www.cnblogs.com/fczjuever/archive/2013/04/07/3005997.html

    Map<String, Device> allDevice = new LinkedHashMap<String, Device>();// 设备类表
    ArrayList<DeviceStatus> allDeviceStatus = new ArrayList<DeviceStatus>();// 设备状态表
    Map<Integer, DeviceInfoMap> processInfo = new LinkedHashMap<Integer, DeviceInfoMap>();// 进程占用设备表
    Queue<QueueElem> d_Queue = new LinkedList<QueueElem>();// 设备等待队列
    public DevicePanel showPanel;// 设备信息展示界面

    /*//以上这些属性，应设置为private，提供方法供外部(仅)读取，不能改变
     private Map<String, Device> allDevice = new LinkedHashMap<String, Device>();// 设备类表
     private ArrayList<DeviceStatus> allDeviceStatus = new ArrayList<DeviceStatus>();// 设备状态表
     private Map<Integer, DeviceInfoMap> processInfo = new LinkedHashMap<Integer, DeviceInfoMap>();// 进程占用设备表
     private Queue<QueueElem> d_Queue = new LinkedList<QueueElem>();// 设备等待队列
     private DevicePanel showPanel;// 设备信息展示界面

     public Map<String, Device> getAllDevice() {
     return allDevice;
     }

     public ArrayList<DeviceStatus> getAllDeviceStatus() {
     return allDeviceStatus;
     }

     public Map<Integer, DeviceInfoMap> getProcessInfo() {
     return processInfo;
     }

     public Queue<QueueElem> getD_Queue() {
     return d_Queue;
     }

     public DevicePanel getDevicePanel() {
     return showPanel;
     }
     */
    /**
     * 无参构造方法
     */
    public DeviceManager() {
        // 初始化设备类、设备状态表
        initDevice();
        initDeviceStatus();
        showPanel = new DevicePanel(allDevice, allDeviceStatus, processInfo, d_Queue);
    }

    /**
     * 初始化设备类表
     */
    private void initDevice() {
        /*//题目要求
         allDevice.put("A", new Device("A", 1, 0));
         allDevice.put("B", new Device("B", 2, 1));
         allDevice.put("C", new Device("C", 2, 3));
         */
        // 书本例题
        allDevice.put("A", new Device("A", 10, 0));
        allDevice.put("B", new Device("B", 5, 10));
        allDevice.put("C", new Device("C", 7, 15));

    }

    /**
     * 初始化设备状态表
     */
    private void initDeviceStatus() {

        for (String key : allDevice.keySet()) {
            Device value = allDevice.get(key);
            for (int i = value.r_address; i < value.r_address + value.amount; i++) {
                allDeviceStatus.add(new DeviceStatus(value.name, i, i - value.r_address));
            }
        }
    }

    /**
     * 验证申请设备信息（设备名）是否合法（存在）
     *
     * @param info
     * @return
     */
    private boolean verifyBorrowInfo(DeviceInfoMap info) {

        for (String dName : info.keySet()) {
            if (!allDevice.containsKey(dName)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 格式化设备信息
     *
     * @param info （需要格式化的）设备信息
     * @return 格式化的设备信息
     */
    private DeviceInfoMap formatDeviceInfo(DeviceInfoMap info) {
        for (String dName : allDevice.keySet()) {
            if (info.containsKey(dName)) {// info设备表中已存在的设备信息，跳过
                continue;
            }
            info.add(dName, 0);// info设备中未有的，设备数量都置0
        }

        return info;
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
        for (String dName : borrowInfo.keySet()) {// dName -> borrowInfo.get(dName)
            int value = borrowInfo.get(dName);
            ocpI.add(dName, value);// 组合进程占用的设备信息

            allDevice.get(dName).left -= value;// 更新 设备类表

            // 更新 设备状态表
            int c_alloc = 0;// 成功分配该设备个数
            for (int i = 0; i < allDeviceStatus.size(); i++) {

                DeviceStatus e = allDeviceStatus.get(i);
                if (e.name.equals(dName) && !e.isAllocate) {// 目标设备，且未分配

                    allDeviceStatus.get(i).isAllocate = true;
                    allDeviceStatus.get(i).process_ID = process_ID;
                    c_alloc++;
                }

                if (c_alloc == value) {// 设备分配够了
                    break;
                }
            }
        }

        ocpI = formatDeviceInfo(ocpI);// 格式化进程占用设备信息

        // 如果该进程已有占用设备，则  新占用设备 = 已占用的设备 + 新申请的设备
        if (processInfo.containsKey(process_ID)) {
            DeviceInfoMap old_PcID = processInfo.get(process_ID);
            for (String dName : old_PcID.keySet()) {
                ocpI.add(dName, ocpI.get(dName) + old_PcID.get(dName));
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
     * @return -2申请的设备不存在 -1申请设备超出总数 0不安全不可分配 1安全可分配 2安全但等待
     */
    public int allocate(int process_ID, DeviceInfoMap borrowInfo) {
        // 检查borrowInfo中设备是否存在
        if (!verifyBorrowInfo(borrowInfo)) {
            return -2;
        }
        // 检查borrowInfo中设备数量是否合法
        if (!checkBorrow_All(borrowInfo)) {
            return -1;
        }
        // 预处理borrowInfo，格式化
        borrowInfo = formatDeviceInfo(borrowInfo);

        if (processInfo.isEmpty()) {// 进程占用设备表为空
            // 第1个进程申请设备，直接分配
            allocateDevice(process_ID, borrowInfo);
            return 1;
        } else {
            //System.out.println("进程占用设备表不为空");
            // 判断是否可分配设备给当前进程元素（cur=new队列元素类）
            QueueElem cur = new QueueElem(process_ID, borrowInfo);
            return toBankJudge(cur);// 调用toBankJudge()
        }
    }

    /**
     * 释放设备
     *
     * @return
     */
    public void deAllocate(int process_ID) {

        DeviceInfoMap curPc = processInfo.get(process_ID);// 获取进程ID为process_ID的进程占用设备信息
        for (String dName : curPc.keySet()) {
            // 更新总设备类表
            allDevice.get(dName).left += curPc.get(dName);
        }

        for (int i = 0; i < allDeviceStatus.size(); i++) {
            // 遍历，查找目标进程
            DeviceStatus e = allDeviceStatus.get(i);
            if (e.process_ID == process_ID) {// 目标进程

                // 更新 设备状态表
                allDeviceStatus.get(i).isAllocate = false;
                allDeviceStatus.get(i).process_ID = -1;
                break;
            }
        }
        processInfo.remove(process_ID);// 更新 进程占用设备表
        //printDev();// 打印输出
        activate();// 调用activate（）
    }

    /**
     * 释放设备后，toBankJudge(cur)，判断是否能够分配设备给设备等待队列首的进程
     */
    private void activate() {
        // 判断是否可分配设备给当前进程元素（cur=队列首元素）
        QueueElem cur = d_Queue.peek();
        if(cur != null){// 当等待队列不空
            toBankJudge(cur);// 调用toBankJudge()
        }
    }

    /**
     * 判断申请是否合理（申请数不超过总数）
     *
     * @param borrowInfo 设备申请信息
     * @return
     */
    private boolean checkBorrow_All(DeviceInfoMap borrowInfo) {
        boolean isLegal = true;
        for (String key : borrowInfo.keySet()) {// dName -> borrowInfo.get(dName)

            if (borrowInfo.get(key) > allDevice.get(key).amount) {
                // 对应key的设备，申请数量 > 总数量
                System.out.println("##checkBorrow_All  --  " + key + "设备，申请数量" + borrowInfo.get(key) + " > 总数量" + allDevice.get(key).amount);
                isLegal = false;
                break;
            }
        }

        if (!isLegal) {
            return false;
        }

        System.out.println("##checkBorrow_All  --  " + "设备申请信息borrowInfo，通过合法性(总数)检查！");
        return true;
    }

    /**
     * 组装“银行”环境，调用银行家算法。 根据返回结果，（分配设备，并）将（安全或不安全的）序列入设备等待队列
     * @param cur 当前申请设备的进程信息，QueueElem队列元素
     * @return 0不安全不可分配 1安全可分配 2安全但等待
     */
    private int toBankJudge(QueueElem cur) {
        DeviceInfoMap Avaliable = new DeviceInfoMap();// 剩余设备信息
        Map<Integer, BKProcess> bkProcessES = new LinkedHashMap<Integer, BKProcess>();// 进程集合
        
        // 组装Avaliable剩余设备信息
        for (String dName : allDevice.keySet()) {
            Avaliable.add(dName, allDevice.get(dName).left);
        }
        // 组装bkProcessES进程集合
        BKProcess bkpc = new BKProcess(processInfo.get(cur.process_ID), cur.borrowInfo);// (进程已占用设备信息，申请设备信息)
        // cur为第1个进程信息，入进程集
        bkProcessES.put(cur.process_ID, bkpc);
        
        while (!d_Queue.isEmpty()) {// 设备等待队列中的进程，入进程集
            QueueElem e = d_Queue.remove();
            if (e.process_ID == cur.process_ID) {// 排除设备等待队列中与cur相同的进程
                System.out.println("##toAllocate  --  " + "队列中存在相同进程，排除");
                continue;
            }
            bkpc = new BKProcess(processInfo.get(e.process_ID), e.borrowInfo);// (进程已占用设备信息，申请设备信息)
            bkProcessES.put(e.process_ID, bkpc);
        }
        Bank bk = new Bank(Avaliable, bkProcessES);// 初始化“银行”环境
    //********************************************************************************************以上初始化“银行”环境
        int status = bk.Safety();// 调用银行家算法，返回0/1/2

        System.out.println("*******安全系列如下********");
        for (Integer pc_ID : bk.SafeSet) {

            System.out.print(pc_ID + ",");
        }
        System.out.println();
        
        //int status = bk.Safety();// 调用银行家算法，返回0/1/2
        QueueElem e = null;// 根据返回值，（分配设备，并）更新设备等待队列
        if (status == 0) {
            // bkProcessES中的进程重新入队列
            for (Integer p_ID : bk.bkProcessES.keySet()) {
                e = new QueueElem(p_ID, bk.bkProcessES.get(p_ID).Need);
                d_Queue.add(e);
            }
        } else {
            if (status == 1) {
                allocateDevice(cur.process_ID, cur.borrowInfo);// 将设备分配给cur.process_ID进程
                deviceWatcher.allocatedDeviceTo(cur.process_ID, status);// 通知该进程 
                bk.SafeSet.remove(0);// 除去序列首
                // 剩下的进程对应序列顺序，入队列等待
                for (Integer p_ID : bk.SafeSet) {
                    e = new QueueElem(p_ID, bk.bkProcessES.get(p_ID).Need);
                    d_Queue.add(e);
                }
            } else {//status == 2
                // 进程按序列顺序入队列等待
                for (Integer p_ID : bk.SafeSet) {
                    e = new QueueElem(p_ID, bk.bkProcessES.get(p_ID).Need);
                    d_Queue.add(e);
                }
            }
            showPanel.update(allDevice, allDeviceStatus, processInfo, d_Queue);// 更新设备信息展示Panel信息
        }
        return status;// 返回0/1/2
    }

    /**
     * 展示 设备分配表，设备等待队列
     */
    public void printDev() {
        System.out.println("##printDev  --  " + "打印 设备类表，设备状态表，设备等待队列！");
        System.out.println("设备类表");
        System.out.println("设备名\t总数\t空闲数\t相对地址");
        for (String key : allDevice.keySet()) {
            Device value = allDevice.get(key);

            System.out.println(value.name + "\t" + value.amount + "\t" + value.left + "\t" + value.r_address);
        }

        System.out.println("-------------------------------------");
        System.out.println("设备状态表");
        System.out.println("设备名\t设备ID\t是否分配\t占用进程\t相对地址号");
        for (int i = 0; i < allDeviceStatus.size(); i++) {
            DeviceStatus e = allDeviceStatus.get(i);

            System.out.println(e.name + "\t" + e.ID + "\t" + e.isAllocate + "\t" + e.process_ID + "\t" + e.r_ID);
        }

        System.out.println("-------------------------------------");
        if (!processInfo.isEmpty()) {

            System.out.println("进程占用设备表");
            System.out.println("进程ID\t占用设备信息");
            for (Integer process_ID : processInfo.keySet()) {
                DeviceInfoMap ocpI = processInfo.get(process_ID);

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
            System.out.println("进程ID\t设备申请信息");
            int queueSize = d_Queue.size();
            for (int i = 0; i < queueSize; i++) {
                QueueElem e = d_Queue.remove();

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
    //**************************************************************************以下为监听器
    private DeviceWatcherImpl deviceWatcher;// 监听器实例
    //Tips:谁被监听，谁就得有那个用于监听的接口实例；
    //     具体通知什么内容，由被监听者发送（即此处的int process_ID）；
    //     接口的方法，即是通知观察者时，观察者处理的地方

    /**
     * （观察者的）进程监听器（监听设备，设备分配给进程后就通知观察者）
     */
    public void addDeviceWatcher(DeviceWatcherImpl deviceWatcher) {
        this.deviceWatcher = deviceWatcher;// 由观察者注册得到
    }
}