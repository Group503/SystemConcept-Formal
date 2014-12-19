
package group503.devicemanager;

/**
 * 监听设备是否可用，接口（通知进程）
 * @author 刘恩坚
 */
public interface DeviceWatcherImpl {
    
    // 通知进程，已分配设备给它
    public int allocatedDeviceTo(int process_ID);
}
