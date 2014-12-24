package com.process;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import com.device.DeviceInfoMap;
import com.operatingsystem.OperatingSystem;
import com.process.ProcessDisplay.AlgChangedCallback;

/**
 * 管理进程 调度进程
 */
public class ProcessController extends Thread {
	private List<PCB> readyProcessesList = new ArrayList<PCB>(); // 就绪队列
	private List<PCB> blockProcessesList = new ArrayList<PCB>(); // 阻塞队列
	private List<PCB> pcbList = new ArrayList<PCB>(); // PCB 队列
	private PCB runningProcess; // 正在运行的进程
	private int schedulingALG = 0;
	private boolean isAdd;

	public final static int TIME_SLOT = 600; // 单位时间, 用于 sleep();
	public final static int TIME_CHECK = 200; // 定义闲逛进程多久检查是否有新进程加入

	public ProcessDisplay processDisplay;

	/**
	 * 创建进程控制类
	 * 
	 * @param readyProcessesList
	 * @param blockProcessesList
	 * @param pcbList
	 * @param schedulingALG
	 */
	public ProcessController() {
		// 初始情况, 加入一个闲逛进程
		this.createWanderProcess();
		// System.out.println("-------------" + this.pcbList);
		System.out.println(this.readyProcessesList);
		// this.isAdd = true;
		// this.schedulingALG = 1;

		processDisplay = new ProcessDisplay();

		schedulingALG = processDisplay.aglComboBox.getSelectedIndex();// 跟界面的对应起来，时间关系先这么做

		processDisplay.registerAlgChangedCallback(new AlgChangedCallback() {

			@Override
			public void algChanged(int alg) {
				schedulingALG = alg;
			}
		});

	}

	// public static void main(String[] args) {

	// String code =
	// "ax=1;bx=2;cx=ax+bx;dx=cx+cx;dx=dx+dx;dx=dx+dx;ax=20;bx=20;cx=20;dx=20;ax=1;";
	// String code1 = "ax=999;bx=999;cx=999;dx=999";

	/*
	 * String[] codes = {
	 * "ax=1;bx=2;cx=ax+bx;dx=cx+cx;dx=dx+dx;dx=dx+dx;ax=20;bx=20;cx=20;dx=20;ax=1"
	 * ,
	 * "ax=1;bx=2;cx=ax+bx;dx=cx+cx;dx=dx+dx;dx=dx+dx;ax=20;bx=20;cx=20;dx=20",
	 * "ax=1;bx=2;cx=ax+bx;dx=cx+cx;dx=dx+dx;dx=dx+dx;ax=20;bx=20;cx=20",
	 * "ax=1;bx=2;cx=ax+bx;dx=cx+cx;dx=dx+dx;dx=dx+dx;ax=20;bx=20",
	 * "ax=1;bx=2;cx=ax+bx;dx=cx+cx;dx=dx+dx;dx=dx+dx;ax=20",
	 * "ax=1;bx=2;cx=ax+bx;dx=cx+cx;dx=dx+dx;dx=dx+dx",
	 * "ax=1;bx=2;cx=ax+bx;dx=cx+cx;dx=dx+dx", "ax=1;bx=2;cx=ax+bx;dx=cx+cx",
	 * "ax=1;bx=2;cx=ax+bx", "ax=1;bx=2" };
	 */

	/*
	 * String[] codes = {
	 * "ax=1;bx=2;cx=ax+bx;dx=cx+cx;dx=dx+dx;dx=dx+dx;ax=20;bx=20;cx=20;dx=20;ax=1"
	 * , "ax=1;bx=2;cx=ax+bx;dx=cx+cx;dx=dx+dx;dx=dx+dx;ax=20;",
	 * "ax=1;bx=2;cx=ax+bx;dx=cx+cx;dx=dx+dx;dx=dx+dx;ax=20;bx=20;cx=20;",
	 * "ax=1;bx=2;cx=ax+bx;dx=cx+cx;",
	 * "ax=1;bx=2;cx=ax+bx;dx=cx+cx;dx=dx+dx;dx=dx+dx;ax=20;bx=20;cx=20;dx=20;"
	 * , "ax=1;bx=2;", "ax=1;bx=2;cx=ax+bx;dx=cx+cx;dx=dx+dx;",
	 * "ax=1;bx=2;cx=ax+bx;dx=cx+cx;dx=dx+dx;dx=dx+dx;ax=20;bx=20;",
	 * "ax=1;bx=2;cx=ax+bx;", "ax=1;bx=2;cx=ax+bx;dx=cx+cx;dx=dx+dx;dx=dx+dx;"};
	 */

	// int menorySize = 123;
	/*
	 * ProcessController pc = new ProcessController();
	 * 
	 * for (int i = 0; i < 10; i++) { try { sleep(100); } catch
	 * (InterruptedException e) { e.printStackTrace(); } //long tmpSerivceTime =
	 * 1000 + ((long) (Math.random() * 1000)); pc.create(codes[i]); }
	 * 
	 * pc.execute();
	 */
	// pc.create(code1, menorySize);
	// System.out.println("\n----- readyProcessList ----\n");
	// System.out.println(pc.readyProcessesList);
	// pc.dispatch(pc.getSchedulingALG());

	/*
	 * for (int i = 0; i < 10; i++) { try { sleep(100); } catch
	 * (InterruptedException e) { e.printStackTrace(); } // long tmpSerivceTime
	 * = 1000 + ((long) (Math.random() * 1000)); pc.create(code, menorySize);
	 * 
	 * }
	 */
	/*
	 * PCB aPcb = pc.pcbList.get(0); pc.block(aPcb);
	 * System.out.println("\n----- pcbList ----\n");
	 * System.out.println(pc.pcbList);
	 * System.out.println("\n----- blockProcessList ----\n");
	 * System.out.println(pc.blockProcessesList);
	 * System.out.println("\n----- readyProcessList ----\n");
	 * System.out.println(pc.readyProcessesList);
	 */
	// System.out.println("done");
	// }

	/**
	 * 考虑是否需要延时 保持新创建的进程在闲逛进程之前
	 * 
	 * @param serivceTime
	 */
	public void create(String code) {
		if (pcbList.size() > 10) {
			System.out.println("PCB 队列已满");
			return;
		}

		int[] device = new int[3];
		device[0] = createRandomDevice(3);
		device[1] = createRandomDevice(2);
		device[2] = createRandomDevice(3);

		PCB newPcb = new PCB(device, code);
		this.isAdd = true;
		/*
		 * 申请内存如果不成功则返回
		 */
		if (!OperatingSystem.getInstance().memoryManager.allocMemory(
				newPcb.getId(), newPcb.getMenorySize())) {
			System.out.println("Accloc memory successfully.");
			return;
		}

		System.out.println(pcbList.size());
		System.out.println(pcbList);
		pcbList.add(pcbList.size() - 1, newPcb);
		readyProcessesList.add(readyProcessesList.size() - 1, newPcb);

		if (processDisplay.resultArea.getLineCount() <= 5) {
			processDisplay.resultArea.append("进程 " + newPcb.getId() + " 创建\n");
		} else {
			processDisplay.resultArea.setText("进程 " + newPcb.getId() + " 创建\n");
		}
		
		/*ArrayList<PCB> clonePcbList;
		synchronized (readyProcessesList) {
			clonePcbList = (ArrayList<PCB>) ((ArrayList<PCB>)readyProcessesList).clone();
		}
		if(clonePcbList!=null){
			processDisplay.updataReadyTable(clonePcbList);
		}
		 */
		processDisplay.updataReadyTable(readyProcessesList);
	}

	private static int createRandomDevice(int max) {
		double rand = Math.random();
		if (max == 2) {
			if (rand > 0.50) {
				return 1;
			}
		} else if (max == 3) {
			if (rand > 0.66666) {
				return 2;
			} else if (rand > 0.33333) {
				return 1;
			}
		}

		return 0;
	}

	/**
	 * 具有特殊的id : -1
	 */
	private void createWanderProcess() {
		PCB newPcb = new PCB(true);
		pcbList.add(pcbList.size(), newPcb);
		readyProcessesList.add(readyProcessesList.size(), newPcb);
	}

	/**
	 * 销毁进程, 即将其移出pcbList
	 */
	public void destroy(PCB endPCB) {
		endPCB.setStatus(PCB.STATUS_DONE);
		// 回收设备, 回收内存
		OperatingSystem.getInstance().memoryManager
				.aleaseMemory(endPCB.getId());

		if (endPCB.isApplyResource()) {

			// 释放设备
			OperatingSystem.getInstance().deviceManager.deAllocate(endPCB
					.getId());
			System.out.println("进程 :" + endPCB.getId() + " 已被销毁\n");
		}

		
		if (processDisplay.resultArea.getLineCount() <= 5) {
			processDisplay.resultArea.append("进程 :" + endPCB.getId()
					+ " 已被销毁\n");
		} else {
			processDisplay.resultArea.setText("进程 :" + endPCB.getId()
					+ " 已被销毁\n");
		}

		pcbList.remove(endPCB);
		
		processDisplay.updataReadyTable(readyProcessesList);
	}

	/**
	 * 阻塞进程
	 * 
	 * 1.保存运行进程的CPU现场 2.修改进程状态 3.将进程链入对应的阻塞队列,然后转向进程调度 。
	 * 
	 */
	public void block(PCB blockPCB) {
		// readyProcessesList.remove(blockPCB);
		blockPCB.setStatus(PCB.STATUS_BLOCK);
		blockPCB.setBlockReason("申请设备失败");
		blockProcessesList.add(blockPCB);
		if (processDisplay.resultArea.getLineCount() <= 5) {
			processDisplay.resultArea.append("进程 : " + blockPCB.getId()
					+ "被阻塞!\n");
		} else {
			processDisplay.resultArea.setText("进程 : " + blockPCB.getId()
					+ "被阻塞!\n");
		}
		processDisplay.updataReadyTable(readyProcessesList);
		processDisplay.updataBlockTable(blockProcessesList);
	}

	public void awake(int processId) {
		for (int i = 0; i < blockProcessesList.size(); i++) {
			if (blockProcessesList.get(i).getId() == processId) {
				PCB tmPcb = blockProcessesList.get(i);
				blockProcessesList.remove(i);
				tmPcb.setStatus(PCB.STATUS_READY);
				readyProcessesList.add(readyProcessesList.size() - 1, tmPcb);
				if (processDisplay.resultArea.getLineCount() <= 5) {
					processDisplay.resultArea.append("进程 : " + tmPcb.getId()
							+ "被唤醒!\n");
				} else {
					processDisplay.resultArea.setText("进程 : " + tmPcb.getId()
							+ "被唤醒!\n");
				}
				break;
			}
		}
		
		processDisplay.updataBlockTable(blockProcessesList);
		processDisplay.updataReadyTable(readyProcessesList);

	}

	// 从就绪队列中移除
	// 模拟执行指令, 暂定只有 +,- (整型)
	// time 为时间片
	public void exeAfterDispatch(PCB runningPCB, long time) {
		processDisplay.updataReadyTable(readyProcessesList);
		if (runningPCB.isApplyResource()&&runningPCB.isOnceApply()) { // 申请设备
			int[] tmpRes = runningPCB.getApplyResource();
			DeviceInfoMap borrowInfo = new DeviceInfoMap();
			borrowInfo.add("A", tmpRes[0]);
			borrowInfo.add("B", tmpRes[1]);
			borrowInfo.add("C", tmpRes[2]);
			
			int status = OperatingSystem.getInstance().deviceManager.allocate(
					runningPCB.getId(), borrowInfo);
			switch (status) {
			case -2:

				break;
			case -1:
				
				break;
			case 1:
				OperatingSystem.getInstance().deviceManager.printDev();
				runningPCB.setOnceApply(false);
				break;
			case 0:
			case 2:
				runningPCB.setOnceApply(false);
				block(runningPCB);
				return;

			default:
				break;
			}
		}

		runningPCB.setStatus(PCB.STATUS_RUNNING);
		this.runningProcess = runningPCB;

		processDisplay.processField.setText(String.valueOf(runningProcess
				.getId()));

		System.out.println("进程 : " + runningPCB + "正在执行! 执行时间为 : " + time);
		if (processDisplay.resultArea.getLineCount() <= 5) {
			processDisplay.resultArea.append("进程 : " + runningPCB.getId()
					+ "正在执行!\n");
		} else {
			processDisplay.resultArea.setText("进程 : " + runningPCB.getId()
					+ "正在执行!\n");
		}

		if (runningPCB.getId() != PCB.WANDER_PCB_ID) {
			try {
				// 执行代码
				// 这里输出是为了获取中间变量.
				for (int i = 0; i < time; i++) {
					exeInstruction(runningPCB);
					sleep(ProcessController.TIME_SLOT);
				}
				runningPCB.setLeafTime(runningPCB.getLeafTime() - time);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (runningPCB.getLeafTime() <= 0) {
				System.out.println("进程 : " + runningPCB + " 执行完成");
				if (processDisplay.resultArea.getLineCount() <= 5) {
					processDisplay.resultArea.append("进程 : "
							+ runningPCB.getId() + " 执行完成\n");
				} else {
					processDisplay.resultArea.setText("进程 : "
							+ runningPCB.getId() + " 执行完成\n");
				}

				destroy(runningPCB);
			} else {
				readyProcessesList.add(readyProcessesList.size() - 1,
						runningPCB);
			}
		}

	}

	// 执行
	public void execute() {

		while (true) {
			// 只有闲逛进程
			if (readyProcessesList.size() <= 1) {
				this.isAdd = false;
				exeAfterDispatch(readyProcessesList.get(0), readyProcessesList
						.get(0).getSerivceTime());
				while (true) {
					if (!isAdd) {
						try {
							sleep(ProcessController.TIME_CHECK);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						break;
					}
				}
			} else {
				// 执行调度
				System.out.println("-----!!!!!!!!!!!!!!!!!!!!!!--------");
				dispatch(schedulingALG);
			}
		}
	}

	/**
	 * 
	 * @param schedulingALG
	 *            0 -> FCFS 1 -> RR 2 -> SPN 3 -> HRRN
	 * 
	 */
	public void dispatch(int schedulingALG) {
		switch (schedulingALG) {
		case 0:
			FCFS();
			break;
		case 1:
			RR(5);
			break;
		case 2:
			SPN();
			break;
		case 3:
			HRRN();
			break;
		default:
			break;
		}

	}

	// 调度算法
	/**
	 * FCFS 调度算法 非抢占
	 */
	private void FCFS() {
		/*
		 * while (true) { if (readyProcessesList.size() <= 0) break;
		 * 
		 * PCB runningPCB = readyProcessesList.get(0); if (runningPCB.getId() !=
		 * PCB.WANDER_PCB_ID) readyProcessesList.remove(runningPCB); // 输出到界面、
		 * processDisplay
		 * .processField.setText(String.valueOf(runningPCB.getId()));
		 * execute(runningPCB, runningPCB.getSerivceTime());
		 * 
		 * if (runningPCB.getLeafTime() <= 0 && runningPCB.getId() !=
		 * PCB.WANDER_PCB_ID) { System.out.println("进程 : " + runningPCB +
		 * " 执行完成"); destroy(runningPCB); }
		 * 
		 * // readyProcessesList.add(runningPCB); }
		 */
		PCB runningPCB = readyProcessesList.get(0);
		if (runningPCB.getId() != PCB.WANDER_PCB_ID) {
			readyProcessesList.remove(runningPCB);
		}

		exeAfterDispatch(runningPCB, runningPCB.getSerivceTime());

	}

	/**
	 * RR 调度 非抢占 时间片为 q
	 * 
	 * if(进程的serviceTime > q) {即执行 q 个单位时间, 然后将其加入就绪队列队尾} else 执行进程 serviceTime;
	 * 
	 * @param q
	 */
	private void RR(int q) {
		/*
		 * while (true) { if (readyProcessesList.size() <= 0) break;
		 * 
		 * PCB runningPCB = readyProcessesList.get(0);
		 * 
		 * long exeTime = runningPCB.getLeafTime() > q ? q : runningPCB
		 * .getLeafTime(); System.out.println("-------" + exeTime +
		 * "---------"); if (runningPCB.getId() != PCB.WANDER_PCB_ID)
		 * readyProcessesList.remove(runningPCB); // 输出到界面、
		 * processDisplay.processField
		 * .setText(String.valueOf(runningPCB.getId())); execute(runningPCB,
		 * exeTime);
		 * 
		 * if (runningPCB.getLeafTime() <= 0 && runningPCB.getId() !=
		 * PCB.WANDER_PCB_ID) { System.out.println("进程 : " + runningPCB +
		 * " 执行完成"); destroy(runningPCB); } else {
		 * readyProcessesList.add(readyProcessesList.size() - 1, runningPCB); }
		 * 
		 * }
		 */
		PCB runningPCB = readyProcessesList.get(0);
		long exeTime = runningPCB.getLeafTime() > q ? q : runningPCB
				.getLeafTime();
		// System.out.println("-------" + exeTime + "---------");
		if (runningPCB.getId() != PCB.WANDER_PCB_ID) {
			readyProcessesList.remove(runningPCB);
		}

		exeAfterDispatch(runningPCB, exeTime);
	}

	/**
	 * SPN 调度算法 非抢占
	 */
	private void SPN() {

		/*
		 * while (true) { if (readyProcessesList.size() <= 0) break; int index =
		 * getSPN(); PCB runningPCB = readyProcessesList.get(index); //
		 * System.out.println("---------I'm the runningPCB : " + // runningPCB);
		 * // System.out.println("---------Index: "+ //
		 * readyProcessesList.indexOf(runningPCB)); if (runningPCB.getId() !=
		 * PCB.WANDER_PCB_ID) readyProcessesList.remove(runningPCB); // 输出到界面、
		 * processDisplay
		 * .processField.setText(String.valueOf(runningPCB.getId()));
		 * execute(runningPCB, runningPCB.getLeafTime());
		 * 
		 * if (runningPCB.getId() != PCB.WANDER_PCB_ID) {
		 * System.out.println("进程 : " + runningPCB + " 执行完成");
		 * destroy(runningPCB); } }
		 */
		int index = getSPN();
		PCB runningPCB = readyProcessesList.get(index);
		if (runningPCB.getId() != PCB.WANDER_PCB_ID) {
			readyProcessesList.remove(runningPCB);
		}
		exeAfterDispatch(runningPCB, runningPCB.getSerivceTime());
	}

	/**
	 * HRRN 调度算法 非抢占
	 */
	private void HRRN() {
		/*
		 * while (true) { if (readyProcessesList.size() <= 0) break; int index =
		 * getHRRN(); // System.out.println("HRRNNNNNNNNNNNN"); PCB runningPCB =
		 * readyProcessesList.get(index); //
		 * System.out.println("running ID : "+runningPCB.getId()); if
		 * (runningPCB.getId() != PCB.WANDER_PCB_ID)
		 * readyProcessesList.remove(runningPCB); // 输出到界面、
		 * processDisplay.processField
		 * .setText(String.valueOf(runningPCB.getId())); execute(runningPCB,
		 * runningPCB.getLeafTime());
		 * 
		 * if (runningPCB.getLeafTime() <= 0) { System.out.println("进程 : " +
		 * runningPCB + " 执行完成"); destroy(runningPCB); } }
		 */
		int index = getHRRN();
		// System.out.println("HRRNNNNNNNNNNNN");
		PCB runningPCB = readyProcessesList.get(index);
		// System.out.println("running ID : "+runningPCB.getId());
		if (runningPCB.getId() != PCB.WANDER_PCB_ID) {
			readyProcessesList.remove(runningPCB);
		}
		exeAfterDispatch(runningPCB, runningPCB.getSerivceTime());
	}

	/**
	 * 得到下一个应该调度的进程的下标.
	 * 
	 * @return
	 */
	private int getSPN() {
		int index = 0;
		for (int i = 1; i < readyProcessesList.size(); i++) {
			if (readyProcessesList.get(i).getSerivceTime() != -1) {
				if (readyProcessesList.get(index).getSerivceTime() > readyProcessesList
						.get(i).getSerivceTime()) {
					index = i;
				}
			}
		}
		return index;
	}

	/**
	 * 返回下一个应该调用的进程
	 * 
	 * @return
	 */
	private int getHRRN() {
		int index = 0;
		long currentTime = System.currentTimeMillis();
		double max = functionHRRN(index, currentTime);
		for (int i = 1; i < readyProcessesList.size(); i++) {
			double tmp = functionHRRN(i, currentTime);
			// System.out.println("----id = "+
			// readyProcessesList.get(i).getId()+ ", hrrnTime = " + tmp);
			// System.out.println("tmp:" + tmp + ", max: " + max );
			if (tmp > max) {
				index = i;
				max = tmp;
			}
		}
		// System.out.println("index: " + index);
		return index;
	}

	/**
	 * 给定需要计算的下标(readyProcessesList)
	 * 
	 * @param index
	 * @return
	 */
	private double functionHRRN(int index, long currentTime) {
		double tmp;
		if (readyProcessesList.get(index).getId() == -1) {
			tmp = -1.0;
		} else {
			double w = readyProcessesList.get(index).getArriveTime();
			w = currentTime - w;
			double s = readyProcessesList.get(index).getSerivceTime() * 1000.0;
			tmp = (w + s) / s;
		}
		return tmp;
	}

	/**
	 * 执行单条指令 只有 加,减 只能允许 赋值 形式如 ax=2;计算 cx=ax+bx; 不能有空格;
	 */
	private void exeInstruction(PCB pcb) {
		int codeInd = pcb.getCodeIndex();
		String[] codes = pcb.getCodes();
		String runningCode = codes[codeInd];
		int flag1 = runningCode.indexOf('+');
		int flag2 = runningCode.indexOf('-');
		int flag3 = runningCode.indexOf("=");
		System.out.println("------" + runningCode);
		processDisplay.irField.setText(runningCode);
		pcb.setCodeIndex(codeInd + 1);
		// 如果都不是, 即非 +,-
		if (flag3 != -1) {
			if (flag1 == -1 && flag2 == -1) {
				String[] regs = runningCode.split("=");
				System.out.println("!!!!!!!!!!" + regs[1] + "-------");
				System.out.println("----------" + regs[1].trim() + "-------");
				int des = -1;

				int src = Integer.parseInt(regs[1].trim());
				/*
				 * switch (regs[0]) { case "ax": des = 0; break; case "bx": des
				 * = 1; break; case "cx": des = 2; break; case "dx": des = 3;
				 * break; default: des = -1; break; }
				 */
				des = getRegIndex(regs[0]);
				// 执行
				int[] tmpReg = pcb.getReg();
				tmpReg[des] = src;
				pcb.setReg(tmpReg);
			} else {
				String[] regs = runningCode.split("=");
				int des = -1;
				/*
				 * // int src = Integer.parseInt(regs[1]); switch (regs[0]) {
				 * case "ax": des = 0; break; case "bx": des = 1; break; case
				 * "cx": des = 2; break; case "dx": des = 3; break; default: des
				 * = -1; break; }
				 */

				int src1 = -1, src2 = -1;
				flag1 = regs[1].indexOf("+");
				if (flag1 != -1) {
					flag2 = 0;
				} else {
					flag2 = 1;
				}

				String[] srcReg = regs[1].split("[+-]");

				/*
				 * switch (srcReg[0]) { case "ax": src1 = 0; break; case "bx":
				 * src1 = 1; break; case "cx": src1 = 2; break; case "dx": src1
				 * = 3; break; default: src1 = -1; break; }
				 */

				/*
				 * switch (srcReg[1]) { case "ax": src2 = 0; break; case "bx":
				 * src2 = 1; break; case "cx": src2 = 2; break; case "dx": src2
				 * = 3; break; default: src2 = -1; break; }
				 */
				des = getRegIndex(regs[0]);
				src1 = getRegIndex(srcReg[0]);
				src2 = getRegIndex(srcReg[1]);
				// 执行
				int[] tmpReg = pcb.getReg();
				if (flag2 == 0) {
					tmpReg[des] = tmpReg[src1] + tmpReg[src2];
				} else {
					tmpReg[des] = tmpReg[src1] - tmpReg[src2];
				}
				pcb.setReg(tmpReg);

			}
			int[] tmpReg = pcb.getReg();
			for (int i = 0; i < 4; i++) {
				processDisplay.midTableModel.setValueAt(tmpReg[i], i, 1);
			}
		}
	}

	private int getRegIndex(String reg) {
		switch (reg) {
		case "ax":
			return 0;

		case "bx":
			return 1;

		case "cx":
			return 2;

		case "dx":
			return 3;

		default:
			return 0;

		}
	}

	public int getSchedulingALG() {
		return schedulingALG;
	}

	public void setSchedulingALG(int schedulingALG) {
		this.schedulingALG = schedulingALG;
	}

	public PCB getRunningProcess() {
		return runningProcess;
	}

	public void setRunningProcess(PCB runningProcess) {
		this.runningProcess = runningProcess;
	}

}
