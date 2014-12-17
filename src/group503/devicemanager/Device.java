
package group503.devicemanager;

/**
 * 设备类
 * @author 刘恩坚
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
