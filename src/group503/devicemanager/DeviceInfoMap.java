/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package group503.devicemanager;

import java.util.HashMap;

/**
 *
 * @author yonss
 */
public class DeviceInfoMap extends HashMap<String,Integer> {
    
    /**
     * 设备信息 对象构造函数
     */
    public DeviceInfoMap() {
        super();//调用父类方法
    }
    
    /**
     * 添加设备信息情况，如(B,2)
     * @param deviceName 设备名
     * @param num 数量
     */
    public void add(String deviceName, Integer num){
        this.put(deviceName, num);
    }
    
}
