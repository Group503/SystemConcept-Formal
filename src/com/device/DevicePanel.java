/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.device;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Queue;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

/**
 * 设备情况展示Panel
 *
 * @author 刘恩坚
 */
public class DevicePanel extends JPanel {

	// 总设备类表
	private DefaultTableModel tableModel_Device = new DefaultTableModel(null,
			new String[] {}) {
		// 内部类重写isCellEditable方法，设置JTable不可编辑
		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};
	private JTable jTable_Device = new JTable(tableModel_Device);
	// 设备状态表
	private DefaultTableModel tableModel_DeviceStatus = new DefaultTableModel(
			null, new String[] {}) {
		// 内部类重写isCellEditable方法，设置JTable不可编辑
		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};
	private JTable jTable_DeviceStatus = new JTable(tableModel_DeviceStatus);
	// 进程占用设备表
	private DefaultTableModel tableModel_ProcessInfo = new DefaultTableModel(
			null, new String[] {}) {
		// 内部类重写isCellEditable方法，设置JTable不可编辑
		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};
	private JTable jTable_ProcessInfo = new JTable(tableModel_ProcessInfo);
	// 设备等待队列表
	private DefaultTableModel tableModel_Queue = new DefaultTableModel(null,
			new String[] {}) {
		// 内部类重写isCellEditable方法，设置JTable不可编辑
		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};
	private JTable jTable_Queue = new JTable(tableModel_Queue);

	private JTextArea jTx_Queue = new JTextArea();

	/**
	 * 有参构造方法
	 * 
	 * @param allDevice
	 *            总设备类表
	 * @param allDeviceStatus
	 *            设备状态表
	 * @param processInfo
	 *            进程占用设备信息表
	 * @param d_Queue
	 *            设备等待队列
	 */
	DevicePanel(Map<String, Device> allDevice,
			ArrayList<DeviceStatus> allDeviceStatus,
			Map<Integer, DeviceInfoMap> processInfo, Queue<QueueElem> d_Queue) {
		setLayout(new BorderLayout());// BorderLayout布局
		add(jTx_Queue, BorderLayout.SOUTH);// 设备等待队列信息

		JTabbedPane jtbp = new JTabbedPane();
		JScrollPane scroll_Device = new JScrollPane(jTable_Device);
		jtbp.addTab("总设备", scroll_Device);
		JScrollPane scroll_DeviceStatus = new JScrollPane(jTable_DeviceStatus);
		jtbp.addTab("设备状态", scroll_DeviceStatus);
		JScrollPane scroll_ProcessInfo = new JScrollPane(jTable_ProcessInfo);
		jtbp.addTab("进程占用设备", scroll_ProcessInfo);
		JScrollPane scroll_Queue = new JScrollPane(jTable_Queue);
		jtbp.addTab("进程等待设备", scroll_Queue);

		add(jtbp, BorderLayout.CENTER);// 选项卡。总设备，设备状态，进程占用设备，设备等待队列
		update(allDevice, allDeviceStatus, processInfo, d_Queue);

		setPreferredSize(new Dimension(600, 200));
	}

	/**
	 * 总更新方法
	 * 
	 * @param allDevice
	 *            总设备类表
	 * @param allDeviceStatus
	 *            设备状态表
	 * @param processInfo
	 *            进程占用设备信息表
	 * @param d_Queue
	 *            设备等待队列
	 */
	void update(Map<String, Device> allDevice,
			ArrayList<DeviceStatus> allDeviceStatus,
			Map<Integer, DeviceInfoMap> processInfo, Queue<QueueElem> d_Queue) {
		updateQueue(d_Queue);// 设备等待队列表
		updateDevice(allDevice);// 总设备类表
		updateDeviceStatus(allDeviceStatus);// 设备状态表
		updateProcessInfo(processInfo);// 设备状态
	}

	/**
	 * 更新设备等待队列，表
	 * 
	 * @param d_Queue
	 *            设备等待队列
	 */
	private void updateQueue(Queue<QueueElem> d_Queue) {
		String text = "";

		if (!d_Queue.isEmpty()) {
			int count = 0;
			for (QueueElem e : d_Queue) {
				count++;
				text += e.process_ID;
				if (count != d_Queue.size()) {
					text += " <-- ";
				}
			}
		} else {
			text = "空！";
		}

		text = "设备等待队列:\n\t" + text;

		jTx_Queue.setText(text);
		// ***************************************************************以上为更新Queue_Text
		Vector columnNames = new Vector(Arrays.asList(new String[] { "进程ID",
				"_A", "_B", "_C" }));// 列名
		Vector allRowVector = new Vector();// JTable总数据

		for (QueueElem e : d_Queue) {

			Vector columnDate = new Vector();// 一行的列数据
			columnDate.add(e.process_ID);
			DeviceInfoMap dev = e.borrowInfo;
			for (String dName : dev.keySet()) {
				columnDate.add(dev.get(dName));
			}

			allRowVector.add(columnDate);
		}

		tableModel_Queue.setDataVector(allRowVector, columnNames);

		//jTable_Queue.repaint();// 刷新
	}

	/**
	 * 更新总设备类表
	 * 
	 * @param allDevice
	 *            设备类表
	 */
	private void updateDevice(Map<String, Device> allDevice) {
		Vector columnNames = new Vector(Arrays.asList(new String[] { "设备名",
				"总设备数", "空闲设备数", "相对地址" }));// 列名
		Vector allRowVector = new Vector();// JTable总数据

		for (String dName : allDevice.keySet()) {
			Device dev = allDevice.get(dName);
			Vector columnDate = new Vector();// 一行的列数据
			columnDate.add(dev.name);
			columnDate.add(dev.amount);
			columnDate.add(dev.left);
			columnDate.add(dev.r_address);
			allRowVector.add(columnDate);
		}

		tableModel_Device.setDataVector(allRowVector, columnNames);

		//jTable_Device.repaint();// 刷新
	}

	/**
	 * 更新设备状态表
	 * 
	 * @param allDeviceStatus
	 *            设备状态表
	 */
	private void updateDeviceStatus(ArrayList<DeviceStatus> allDeviceStatus) {
		Vector columnNames = new Vector(Arrays.asList(new String[] { "设备名",
				"设备ID", "是否分配", "占用进程ID", "相对地址号" }));// 列名
		Vector allRowVector = new Vector();// JTable总数据

		for (int i = 0; i < allDeviceStatus.size(); i++) {
			Vector columnDate = new Vector();// 一行的列数据
			DeviceStatus dStatus = allDeviceStatus.get(i);

			columnDate.add(dStatus.name);
			columnDate.add(dStatus.ID);
			if (dStatus.isAllocate) {
				columnDate.add("√");
			} else {
				columnDate.add("-");
			}
			if (dStatus.process_ID == -1) {
				columnDate.add("-");
			} else {
				columnDate.add(dStatus.process_ID);
			}
			columnDate.add(dStatus.r_ID);

			allRowVector.add(columnDate);
		}

		tableModel_DeviceStatus.setDataVector(allRowVector, columnNames);

		//jTable_DeviceStatus.repaint();// 刷新
	}

	/**
	 * 更新进程占用设备表
	 * 
	 * @param processInfo
	 *            进程占用设备信息
	 */
	private void updateProcessInfo(Map<Integer, DeviceInfoMap> processInfo) {
		Vector columnNames = new Vector(Arrays.asList(new String[] { "进程ID",
				"A", "B", "C" }));// 列名
		Vector allRowVector = new Vector();// JTable总数据

		for (Integer process_ID : processInfo.keySet()) {

			Vector columnDate = new Vector();// 一行的列数据
			columnDate.add(process_ID);
			DeviceInfoMap dev = processInfo.get(process_ID);
			for (String dName : dev.keySet()) {
				columnDate.add(dev.get(dName));
			}

			allRowVector.add(columnDate);
		}

		tableModel_ProcessInfo.setDataVector(allRowVector, columnNames);

		//jTable_ProcessInfo.repaint();// 刷新
	}
}
