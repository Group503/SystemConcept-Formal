package com.memory;

import java.awt.Color;
import java.util.LinkedList;

public class Memory {

	private byte[] userArea;// 用户区
	private final int USER_AREA_SIZE = 512;// 用户区长度
	private LinkedList<Process> processesList;// 内存空间分配表
	private int next;// 下次适配起始地址
	private int usingSpace;// 正在使用的空间大小
	private int unusedSpace;// 未被使用的空间大小

	// 构造函数
	public Memory() {
		userArea = new byte[USER_AREA_SIZE];
		processesList = new LinkedList<Process>();
		next = 0;
		this.usingSpace = 0;
		this.unusedSpace = USER_AREA_SIZE;
	}

	// 首次适配
	public boolean firstFit(Process process) {
		int size = 0, address = -1;
		// 寻找第一个可用空间
		for (int i = 0; i < userArea.length; i++) {
			if (userArea[i] == 0) {
				size++;
				if (size >= process.size) {
					address = i - size + 1;
					break;
				}
			} else {
				size = 0;
			}
		}
		// 若找不到
		if (address == -1) {
			return false;
		}
		// 找到
		for (int i = address; i < address + size; i++) {
			userArea[i] = 1;
		}
		process.startAddress = address;
		processesList.add(process);
		next = address + size;
		usingSpace += size;
		unusedSpace -= size;
		return true;
	}

	// 下次适配
	public boolean nextFit(Process process) {
		int size = 0, address = -1;
		// 寻找从last开始的第一个可用空间
		for (int i = next; i < userArea.length; i++) {
			if (userArea[i] == 0) {
				size++;
				if (size >= process.size) {
					address = i - size + 1;
					break;
				}
			} else {
				size = 0;
			}
		}
		// 若找不到
		if (address == -1) {
			return false;
		}
		// 找到
		for (int i = address; i < address + size; i++) {
			userArea[i] = 1;
		}
		process.startAddress = address;
		processesList.add(process);
		next = address + size;
		usingSpace += size;
		unusedSpace -= size;
		return true;
	}

	// 最佳适配
	public boolean bestFit(Process process) {
		int size = 0;
		int address = -1;
		int bestSize = USER_AREA_SIZE + 1;
		for (int i = 0; i < userArea.length; i++) {
			if (userArea[i] == 0) {
				size++;
				if (i + 1 == USER_AREA_SIZE || userArea[i + 1] == 1) {
					if (size < bestSize && size >= process.size) {
						bestSize = size;
						address = i - size + 1;
					}
				}
			} else {
				size = 0;
			}
		}
		// 若找不到
		if (address == -1) {
			return false;
		}
		// 找到
		for (int i = address; i < address + process.size; i++) {
			userArea[i] = 1;
		}
		process.startAddress = address;
		processesList.add(process);
		next = address + process.size;
		usingSpace += process.size;
		unusedSpace -= process.size;
		return true;
	}

	// 最差适配
	public boolean worstFit(Process process) {
		int size = 0;
		int address = -1;
		int worstSize = process.size - 1;
		for (int i = 0; i < userArea.length; i++) {
			if (userArea[i] == 0) {
				size++;
				if (i + 1 == USER_AREA_SIZE || userArea[i + 1] == 1) {
					if (size > worstSize && size >= process.size) {
						worstSize = size;
						address = i - size + 1;
					}
				}
			} else {
				size = 0;
			}
		}
		// 若找不到
		if (address == -1) {
			return false;
		}
		// 找到
		for (int i = address; i < address + process.size; i++) {
			userArea[i] = 1;
		}
		process.startAddress = address;
		processesList.add(process);
		next = address + process.size;
		usingSpace += process.size;
		unusedSpace -= process.size;
		return true;
	}

	// 释放进程
	public boolean free(int id) {

		for (Process process : processesList) {
			if (process.ID == id) {
				return free(process);// true
			}
		}

		return false;
	}

	// 释放进程
	private synchronized boolean free(Process process) {
		if (!this.processesList.remove(process)) {
			return false;
		}
		for (int i = process.startAddress; i < process.startAddress
				+ process.size; i++) {
			this.userArea[i] = 0;
		}
		this.usingSpace -= process.size;
		this.unusedSpace += process.size;
		return true;
	}

	// 返回内存分配表
	public LinkedList<Process> getMemAllocationList() {
		return processesList;
	}

	// 返回正在使用的空间的大小
	public int getUsingSpace() {
		return this.usingSpace;
	}

	// 返回未被使用的空间的大小
	public int getUnusedSpace() {
		return this.unusedSpace;
	}
}

class Process {
	int ID;
	int startAddress;
	int size;
	Color color;

	public Process(int id, int size) {
		this.ID = id;
		this.size = size;
		int rand_r = (int) (Math.random() * 255);
		int rand_g = (int) (Math.random() * 255);
		int rand_b = (int) (Math.random() * 255);
		color = new Color(rand_r,rand_g,rand_b);
	}
}
