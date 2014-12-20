
package group503.devicemanager;

/**
 * 监听设备是否可用，接口（通知进程）
 * @author 刘恩坚
 */
public interface DeviceWatcherImpl {
    
    /**
     * 通知进程，已分配（或未分配）设备给它
     * @param process_ID 进程ID
     * @param status 分配状态，-1申请设备超出总数 0不安全不可分配 1安全可分配 2安全但等待
     */
    public void allocatedDeviceTo(int process_ID, int status);
}
