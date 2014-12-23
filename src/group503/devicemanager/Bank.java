package group503.devicemanager;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 模拟银行类
 *
 * @author 刘恩坚
 */
public class Bank {

    DeviceInfoMap Avaliable = new DeviceInfoMap();// 剩余设备信息
    DeviceInfoMap Work = new DeviceInfoMap();// 可分配使用设备的信息，初始为Avaliable
    Map<Integer, BKProcess> bkProcessES = new LinkedHashMap<Integer, BKProcess>();// 进程集合
    // 系统状态，0不存在安全系列，1存在安全系列且能立即满足序列首==当前进程的申请，2存在安全序列但当前进程需要等待
    private static int isSafe = -999;// 初始-999目的是作为递归循环继续的标志
    ArrayList<Integer> SafeSet = new ArrayList<Integer>();// 安全系列，没有安全序列则清空
    private static ArrayList<Integer> invalidOrder = new ArrayList<Integer>();// 记录全排列中，已经确定无效的排列

    /**
     * 有参构造方法
     */
    Bank(DeviceInfoMap Avaliable, Map<Integer, BKProcess> bkProcessES) {
        // 初始化Avaliable(和Work)，进程集合bkProcess
        this.Avaliable = Avaliable;
        //this.Work = Avaliable;// 不能这样赋值，会使得Work和Avaliable索引相同
        //initWork();// 初始化Work
        this.bkProcessES = bkProcessES;
    }

    /*
     * 初始化Work
     */
    private void initWork() {
        //this.Work = this.Avaliable;// 不能这样赋值，会使得Work和Avaliable索引相同
        // 所以采用如下遍历的方式
        for (String dName : Avaliable.keySet()) {
            Work.put(dName, Avaliable.get(dName));
        }
    }

    /**
     * 判断是否所有进程已经完成
     *
     * @return
     */
    private boolean isAllFinished() {
        for (Integer process_ID : bkProcessES.keySet()) {
            BKProcess pcsI = bkProcessES.get(process_ID);
            if (pcsI.Finished == false) {
                return false;
            }
        }

        return true;
    }

    /**
     * 将所有进程的Finished置false
     */
    private void setUnFinished() {
        for (Integer process_ID : bkProcessES.keySet()) {
            bkProcessES.get(process_ID).Finished = false;
        }
    }

    /**
     * 过滤掉已经确定无效的排列，剪枝
     *
     * @param list
     * @return
     */
    private boolean FindInvalid(Integer list[]) {
        boolean flag = false;

        //System.out.println("----------------****************---------------");
        for (int i = 0; i < invalidOrder.size(); i++) {
            System.out.print(invalidOrder.get(i) + ",");

            if (invalidOrder.get(i) != list[i]) {
                flag = true;
                break;
            }
        }
        //System.out.println("*----------------*");

        return flag;
    }

    /**
     * 交换全排列因子顺序
     *
     * @param list
     * @param left
     * @param right
     */
    private void swap(Integer list[], int left, int right) {
        Integer temp;
        temp = list[left];
        list[left] = list[right];
        list[right] = temp;
    }

    /**
     * 递归寻找安全系列
     *
     * @param list 全排列因子，此处为进程ID数组
     * @param k
     * @param m
     * @return -999/0/1/2
     */
    private int FindSafeSerial(Integer list[], int k, int m) {
        if (k == m) {
            initWork();// 初始化Work
            int i;
            for (i = 0; i <= m; i++) {// 检查该排列的序列是否安全
                boolean isLegal = true;// 序列安全的标志
                Integer curPc_ID = list[i];
                System.out.print(curPc_ID + ",");

                // 直接记录该进程ID，组成排列。如果合法，最后即为此序列；如果非法，赋值给invalidOrder后清空
                SafeSet.add(curPc_ID);

                BKProcess testPc = bkProcessES.get(curPc_ID);//当前试探性分配的进程
                for (String dName : Work.keySet()) {// 循环每一种设备，判断Work是否满足该进程的Need
                    Integer dNum = Work.get(dName);
                    if (dNum < testPc.Need.get(dName)) {
                        isLegal = false;
                        break;
                    }
                }
                if (!isLegal) {
                    break;// 终止该不可能的系列
                } else {// Work都 > Need，满足，可分配给该进程(只管分配。如果这个系列不安全，全部抛弃，“从头”再来)
                    this.bkProcessES.get(curPc_ID).Finished = true;// 将设进程Finished设为true，表示进入安全系列候选
                    for (String dName : Work.keySet()) {// 循环每一种设备

                        Work.put(dName, Work.get(dName) + testPc.Allocation.get(dName));// 更新Work
                    }
                }
            }
            System.out.println();

            if (i > m) {// 存在安全系列，可以返回了，isAllFinished()
                for (Integer process_ID : bkProcessES.keySet()) {
                    if (list[0] == process_ID) {// 判断序列首进程是否为当前申请设备的进程
                        isSafe = 1;// 进程可以立即分配得到设备
                    } else {
                        isSafe = 2;// 进程需要等待
                    }
                    return isSafe;
                }
            } else {// 当前这种排列的序列不安全

                setUnFinished();// 进程的Finished全置为false
                if (!SafeSet.isEmpty()) {
                    invalidOrder = SafeSet;// 将已确定的无效的排列赋给invalidOrder
                    SafeSet.clear();// 安全系列记录表清空
                }
            }
        } else {
            for (int i = k; i <= m; i++) {
                if (isSafe != -999) {// 如果已经找到1个安全系列，停止递归，返回
                    return isSafe;
                }
                // invalidOrder不空（排除掉第1次），剪枝
                if (!invalidOrder.isEmpty() && FindInvalid(list)) {
                    continue;
                }
                swap(list, k, i);
                FindSafeSerial(list, k + 1, m);//递归
                swap(list, k, i);
            }
        }
        return isSafe;
    }

    /**
     * 检查安全性
     *
     * @return
     */
    public int Safety() {
        int id = 0;// 计数
        Integer[] list = new Integer[bkProcessES.size()];
        for (Integer pc_ID : bkProcessES.keySet()) {
            // 将进程名当成 全排列 的因子，初始化排列
            list[id++] = pc_ID;
        }

        invalidOrder.clear();// 无效排列，初始化
        isSafe = -999;// 初始化，-999为递归继续的标志
        int status = FindSafeSerial(list, 0, list.length - 1);// 递归，可能返回-999，0，1，2

        if (status == -999 || status == 0) {// -999和0，都返回0，说明无安全系列
            return 0;
        } else {
            return status;
        }
    }
}
