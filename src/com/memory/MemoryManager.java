package com.memory;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

public class MemoryManager extends JPanel {

	private final int LEFT = 45;// 内存示意图左上角x坐标
	private final int TOP = 60;// 内存示意图左上角y坐标
	private final static int FIRST_FIT = 1;
	private final static int NEXT_FIT = 2;
	private final static int BEST_FIT = 3;
	private final static int WORST_FIT = 4;
	private int currentFit = 1;
	private Memory memory = new Memory();// 定义内存

	public MemoryManager() {
		JRadioButton FF = new JRadioButton("首次适配", true);
		JRadioButton NF = new JRadioButton("下次适配");
		JRadioButton BF = new JRadioButton("最佳适配");
		JRadioButton WF = new JRadioButton("最差适配");
		ButtonGroup group = new ButtonGroup();
		group.add(FF);
		group.add(NF);
		group.add(BF);
		group.add(WF);
		this.add(FF);
		this.add(NF);
		this.add(BF);
		this.add(WF);
		FF.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentFit = MemoryManager.FIRST_FIT;
			}
		});
		NF.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentFit = MemoryManager.NEXT_FIT;
			}
		});
		BF.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentFit = MemoryManager.BEST_FIT;
			}
		});
		WF.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentFit = MemoryManager.WORST_FIT;
			}
		});
		
		TitledBorder border = new TitledBorder("主存用户区");
		setBorder(border);
		setPreferredSize(new Dimension(600,150));
	}
	
	public boolean allocMemory(int id,int size) {
		Process process = new Process(id, size);
		if(allocMemory(process)){
			return true;
		}
		return false;
	}

	// 申请空间
	private boolean allocMemory(Process process) {
		switch (this.currentFit) {
		case MemoryManager.FIRST_FIT:
			if (this.memory.firstFit(process)) {
				repaint();
				return true;
			}
			break;
		case MemoryManager.NEXT_FIT:
			if (this.memory.nextFit(process)) {
				repaint();
				return true;
			}
			break;
		case MemoryManager.BEST_FIT:
			if (this.memory.bestFit(process)) {
				repaint();
				return true;
			}
			break;
		case MemoryManager.WORST_FIT:
			if (this.memory.worstFit(process)) {
				repaint();
				return true;
			}
			break;
		default:
			break;
		}
		return false;
	}

	// 释放进程
	public boolean aleaseMemory(int id) {
		if (this.memory.free(id)) {
			repaint();
			return true;
		}
		return false;
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(new Color(76, 130, 230));

		g.drawRect(LEFT, TOP, 514, 51);
		for (Process process : memory.getMemAllocationList()) {
			g.setColor(process.color);
			g.fillRect(LEFT + 1 + process.startAddress, TOP + 1, process.size,
					50);
		}

		g.setColor(new Color(76, 130, 230));
		g.fillRect(LEFT, TOP + 60, 15, 15);
		g.drawString("正在使用 " + this.memory.getUsingSpace() + "B", LEFT + 20,
				TOP + 72);
		g.drawRect(LEFT + 150, TOP + 60, 14, 14);
		g.drawString("可用 " + this.memory.getUnusedSpace() + "B", LEFT + 170,
				TOP + 72);
	}
}
