package com.operatingsystem;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.xml.stream.events.StartDocument;

import com.device.DeviceManager;
import com.device.DeviceWatcherImpl;
import com.filesystem.FileSystemManager;
import com.filesystem.FileSystemManager.ExecuteFileCallback;
import com.memory.MemoryManager;
import com.process.ProcessController;

/**
 * 模拟操作系统
 * 
 * @author Administrator
 *
 */
public class OperatingSystem extends JFrame {
	private static final long serialVersionUID = 1L;
	private static final String TAG = OperatingSystem.class.getSimpleName();

	// 操作系统实例
	private volatile static OperatingSystem instance;
	// 文件系统管理者
	public FileSystemManager fileSystemManager;
	// 设备管理者
	public DeviceManager deviceManager;
	// 内存管理者
	public MemoryManager memoryManager;
	// 进程管理者
	public ProcessController processController;

	/**
	 * 
	 * @return 返回操作系统的实例
	 */
	public static OperatingSystem getInstance() {
		if (instance == null) {
			synchronized (OperatingSystem.class) {
				if (instance == null) {
					instance = new OperatingSystem();
				}
			}
		}
		return instance;
	}

	private OperatingSystem() {

		/*
		 * 文件管理系统
		 */
		fileSystemManager = new FileSystemManager();
		fileSystemManager
				.registerExecuteFileCallback(new ExecuteFileCallback() {

					@Override
					public void executeFile(String content) {
						processController.create(content);
					}
				});
		/*
		 * 进程管理
		 */
		processController = new ProcessController();
		/*
		 * 内存管理
		 */
		memoryManager = new MemoryManager();
		/*
		 * 设备管理
		 */
		deviceManager = new DeviceManager();
		deviceManager.addDeviceWatcher(new DeviceWatcherImpl() {

			@Override
			public void allocatedDeviceTo(int process_ID, int status) {
				processController.awake(process_ID);
			}
		});

		initView();

		/*
		 * 最后调用，能确保绘画主界面时其他所有的子界面都以经添加到主界面上
		 */
		initMainView();
	}

	/**
	 * 初始化各管理界面
	 */
	private void initView() {
		setLayout(new BorderLayout());
		//
		add(new JScrollPane(fileSystemManager.filePanel), BorderLayout.EAST);// 加入文件系统面板

		// 布局左边用的JPanel
		JPanel panel = new JPanel();

		JPanel centerPanel = new JPanel();
		centerPanel.add(processController.processDisplay);
		panel.add(centerPanel);

		JPanel southPanel = new JPanel();
		southPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BorderLayout());
		leftPanel.add(memoryManager, BorderLayout.NORTH);
		leftPanel.add(deviceManager.showPanel, BorderLayout.CENTER);
		southPanel.add(leftPanel);

		southPanel.add(processController.processDisplay.otherJPanel);

		panel.add(southPanel, BorderLayout.SOUTH);

		add(panel, BorderLayout.CENTER);

	}

	/**
	 * 初始化界面
	 */
	private void initMainView() {
		// 全屏显示（不包括Windows底部任务栏）
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle bounds = new Rectangle(screenSize);
		Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(
				getGraphicsConfiguration());
		bounds.width -= insets.left + insets.right;
		bounds.height -= insets.top + insets.bottom;
		// 窗口设置
		setBounds(bounds);
		setTitle("Operating System");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	/**
	 * 模拟操作系统运行
	 */
	public void start() {
		processController.execute();
	}

	/*
	 * ==========================================================================
	 */
	public static void main(String[] args) {
		OperatingSystem.getInstance().start();
	}

}
