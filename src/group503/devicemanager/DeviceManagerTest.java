package group503.devicemanager;

import java.util.HashMap;
import java.util.Map;

public class DeviceManagerTest {

    public static void main(String args[]) {

        Map<String, Device> allDevice = new HashMap<String, Device>();// 设备类表
        DeviceStatus[] allDeviceStatus;// 设备状态表

        allDevice.put("A", new Device("A", 1, 0));
        allDevice.put("B", new Device("B", 2, 1));
        allDevice.put("C", new Device("C", 2, 3));

        int amount = 0;// 记录设备总数
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
}
