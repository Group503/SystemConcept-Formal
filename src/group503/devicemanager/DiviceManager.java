/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package group503.devicemanager;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author yonss
 */
public class DiviceManager {
    Map<String, Device> allDevice = new HashMap<String, Device>();// 设备类表
    DeviceStatus[] allDeviceStatus;// 设备状态表

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
        
        int amount = 0;// 记录设备总数
        
        // HaspMap遍历，http://www.cnblogs.com/fczjuever/archive/2013/04/07/3005997.html
        for (String key : allDevice.keySet()) {
            Device value = allDevice.get(key);
            amount += value.amount;
        }

        allDeviceStatus = new DeviceStatus[amount];

        for (int i = 0; i < amount; i++) {
            allDeviceStatus[i] = new DeviceStatus(i);
            System.out.println(allDeviceStatus[i].ID);
        }
    }
    
    /**
     * 申请设备
     * @param process_ID
     * @param devStr
     * @return 
     */
    public boolean allocate(int process_ID, String devStr){
        
        
        return false;
    }
    
    /**
     * 释放设备
     * @return 
     */
    public boolean deAllocate(){
        
        return false;
    }
    
    /**
     * 判断是否可分配设备给当前进程元素（cur=new队列元素类）
     * @param cur
     * @return 
     */
    public boolean toAlocate(QueueElem cur){
        
        return false;
    }
    
    /**
     * toAlocate调用的内层方法to_2_Alocate
     * @return 
     */
    private boolean to_2_Alocate(){
        
        return false;
    }
    
    /**
     * 判断是否可分配设备给队列头的进程元素
     * @return 
     */
    public boolean toAlocate(){
        
        return false;
    }
    
    /**
     * 银行家算法
     * @return 
     */
    private boolean bankJudge(){
        
        return false;
    }
    
    /**
     * 展示 设备分配表，设备等待队列
     */
    public void printDev(){
        
    }
}
