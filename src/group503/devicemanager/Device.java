/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package group503.devicemanager;

/**
 *
 * @author yonss
 */
public class Device {
    String name;// 设备名称
    int amount;// 总设备数
    int left;// 空闲设备数
    int r_address;// 相对地址
    
    /**
     * 有参构造函数
     * @param name 设备名称
     * @param amount 总设备数
     * @param r_address  相对地址
     */
    Device(String name, int amount, int r_address){
        this.name = name;
        this.amount = amount;
        this.left = amount;// 起始，空闲设备数 = 总设备数
        this.r_address = r_address;
    }
}
