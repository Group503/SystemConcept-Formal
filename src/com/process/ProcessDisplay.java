package com.process;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class ProcessDisplay extends JPanel {
	private JPanel readyPanel;
	private JTable readyTable;
	DefaultTableModel readyTableModel;

	private JPanel blockPanel;
	private JTable blockTable;
	DefaultTableModel blockTableModel;

	private JTable midJTable;
	DefaultTableModel midTableModel;

	JTextArea resultArea;

	JTextField systemTimeField;// 系统时间
	JTextField rrField;// 轮转时间
	JTextField processField;// 当前进程
	JTextField irField;// 当前指令

	JComboBox<String> aglComboBox;
	String[] agl = new String[] { "FCFS", "RR", "SPN", "HRRN" };
	boolean isonce = false;// 用于Item改变时调用

	// 位置不够初始化要在加上这个面板
	public JPanel otherJPanel = new JPanel();

	public ProcessDisplay() {

		/*
		 * Initialize panels
		 */
		initPanels();

	}

	/**
	 * Initialize panels
	 */
	private void initPanels() {
		setLayout(new BorderLayout());

		JPanel northPanel = new JPanel();
		northPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

		JPanel tmp;// 临时

		systemTimeField = new JTextField(15);
		systemTimeField.setEnabled(false);
		systemTimeField.setFont(new Font("SansSerif", Font.BOLD, 16));
		tmp = new JPanel();
		tmp.add(systemTimeField);
		tmp.setBorder(new TitledBorder("系统时间"));
		northPanel.add(tmp);
		Timer systemTimer = new Timer(1000, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				systemTimeField.setText(String.valueOf(System
						.currentTimeMillis()));
			}
		});
		systemTimer.start();

		aglComboBox = new JComboBox<String>(agl);
		tmp = new JPanel();
		tmp.add(aglComboBox);
		tmp.setBorder(new TitledBorder("调度算法"));
		northPanel.add(tmp);

		aglComboBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (isonce) {
					System.out.println("Changed agl selected." + e.getItem()
							+ " ,index=" + aglComboBox.getSelectedIndex());
					isonce = false;
					// 回调给ProcessControll
					if (callback != null) {
						callback.algChanged(aglComboBox.getSelectedIndex());
					}
				} else {
					isonce = true;
				}

			}
		});

		rrField = new JTextField(5);
		rrField.setEnabled(false);
		rrField.setFont(new Font("SansSerif", Font.BOLD, 16));
		tmp = new JPanel();
		tmp.add(rrField);
		tmp.setBorder(new TitledBorder("轮转时间"));
		northPanel.add(tmp);
		rrField.setText(" 5");

		processField = new JTextField(5);
		processField.setEnabled(false);
		processField.setFont(new Font("SansSerif", Font.BOLD, 16));
		tmp = new JPanel();
		tmp.add(processField);
		tmp.setBorder(new TitledBorder("当前执行进程"));
		northPanel.add(tmp);

		irField = new JTextField(15);
		irField.setEnabled(false);
		irField.setFont(new Font("SansSerif", Font.BOLD, 16));
		tmp = new JPanel();
		tmp.add(irField);
		tmp.setBorder(new TitledBorder("当前执行指令"));
		northPanel.add(tmp);

		add(northPanel, BorderLayout.NORTH);

		JPanel centerPanel = new JPanel();
		/*
		 * Initialize ready panel.
		 */
		readyPanel = new JPanel();

		readyTable = new JTable();
		readyTable.setEnabled(false);
		JScrollPane rJScrollPane = new JScrollPane(readyTable);
		rJScrollPane.setPreferredSize(new Dimension(450, 183));
		readyPanel.add(rJScrollPane);
		readyPanel.setBorder(new TitledBorder("就绪进程队列"));
		// readyPanel.setPreferredSize(new Dimension(470, 220));
		centerPanel.add(readyPanel, BorderLayout.WEST);
		initReadyTable();

		/*
		 * Initialize block panel.
		 */
		blockPanel = new JPanel();
		blockTable = new JTable();
		blockTable.setEnabled(false);
		JScrollPane bJScrollPane = new JScrollPane(blockTable);
		bJScrollPane.setPreferredSize(new Dimension(450, 183));
		blockPanel.add(bJScrollPane);
		// blockPanel.setPreferredSize(new Dimension(470, 220));
		blockPanel.setBorder(new TitledBorder("阻塞进程队列"));
		centerPanel.add(blockPanel, BorderLayout.EAST);
		initBlockTable();

		// initialize reg show panel and process log.
		initRegShowPanel();

		add(centerPanel, BorderLayout.CENTER);

	}

	/**
	 * 初始化就绪队列
	 */
	private void initReadyTable() {
		Vector tmpColumnNames;
		Vector tmpRowVector;
		Vector tmpAllRowVector;

		readyTableModel = new DefaultTableModel();
		tmpColumnNames = new Vector(Arrays.asList(new String[] { "进程ID",
				"到达时间", "服务时间", "占用内存" }));// 列名
		tmpAllRowVector = new Vector();
		for (int i = 0; i < 10; i++) {
			tmpRowVector = new Vector();
			tmpRowVector.add("");
			tmpRowVector.add("");
			tmpRowVector.add("");
			tmpRowVector.add("");

			tmpAllRowVector.add(tmpRowVector);

		}
		readyTableModel.setDataVector(tmpAllRowVector, tmpColumnNames);
		readyTable.setModel(readyTableModel);
	}

	/**
	 * 初始化block队列
	 */
	private void initBlockTable() {
		Vector tmpColumnNames;
		Vector tmpRowVector;
		Vector tmpAllRowVector;

		blockTableModel = new DefaultTableModel();
		tmpColumnNames = new Vector(Arrays.asList(new String[] { "进程ID",
				"到达时间", "服务时间", "占用内存", "阻塞原因" }));// 列名
		tmpAllRowVector = new Vector();
		for (int i = 0; i < 10; i++) {
			tmpRowVector = new Vector();
			tmpRowVector.add("");
			tmpRowVector.add("");
			tmpRowVector.add("");
			tmpRowVector.add("");
			tmpRowVector.add("");

			tmpAllRowVector.add(tmpRowVector);

		}
		blockTableModel.setDataVector(tmpAllRowVector, tmpColumnNames);
		blockTable.setModel(blockTableModel);
	}

	/**
	 * 因为版面规划不够好，将本个面板在父类中选择添加进去（这样不好，但时间关系先不调整） initialize reg show panel and
	 * process log.
	 */
	private void initRegShowPanel() {
		JPanel tmp;// 临时
		Vector tmpColumnNames;
		Vector tmpRowVector;
		Vector tmpAllRowVector;

		otherJPanel.setLayout(new BorderLayout());
		tmp = new JPanel();
		midJTable = new JTable();
		midJTable.setEnabled(false);
		JScrollPane midJScrollPane = new JScrollPane(midJTable);
		midJScrollPane.setPreferredSize(new Dimension(200, 150));
		tmp.add(midJScrollPane);
		tmp.setBorder(new TitledBorder("寄存器值"));
		otherJPanel.add(tmp, BorderLayout.NORTH);

		midTableModel = new DefaultTableModel();
		tmpColumnNames = new Vector(Arrays.asList(new String[] { "寄存器", "值" }));// 列名
		tmpAllRowVector = new Vector();
		tmpRowVector = new Vector();
		tmpRowVector.add("AX");
		tmpRowVector.add(0);
		tmpAllRowVector.add(tmpRowVector);
		tmpRowVector = new Vector();
		tmpRowVector.add("BX");
		tmpRowVector.add(0);
		tmpAllRowVector.add(tmpRowVector);
		tmpRowVector = new Vector();
		tmpRowVector.add("CX");
		tmpRowVector.add(0);
		tmpAllRowVector.add(tmpRowVector);
		tmpRowVector = new Vector();
		tmpRowVector.add("DX");
		tmpRowVector.add(0);
		tmpAllRowVector.add(tmpRowVector);
		midTableModel.setDataVector(tmpAllRowVector, tmpColumnNames);
		midJTable.setModel(midTableModel);

		resultArea = new JTextArea(5, 20);
		resultArea.setEnabled(false);
		resultArea.setFont(new Font("SansSerif", Font.BOLD, 16));
		resultArea.setLineWrap(true);
		resultArea.setPreferredSize(new Dimension(200,100));
		tmp = new JPanel();
		tmp.setBorder(new TitledBorder("进程日志"));
		tmp.add(resultArea);
		otherJPanel.add(tmp, BorderLayout.CENTER);
	}
	
	void updataBlockTable(List<PCB> pcbList){
		int i;
		int size = pcbList.size();
		PCB pcb;
		for(i=0;i<size;i++){
			pcb = pcbList.get(i);
			blockTableModel.setValueAt(pcb.getId(), i, 0);
			blockTableModel.setValueAt(pcb.getArriveTime(), i, 1);
			blockTableModel.setValueAt(pcb.getSerivceTime(), i, 2);
			blockTableModel.setValueAt(pcb.getMenorySize(), i, 3);
			blockTableModel.setValueAt(pcb.getBlockReason(), i, 4);
		}
		
		for(;i<10;i++){
			blockTableModel.setValueAt("", i, 0);
			blockTableModel.setValueAt("", i, 1);
			blockTableModel.setValueAt("", i, 2);
			blockTableModel.setValueAt("", i, 3);
			blockTableModel.setValueAt("", i, 4);
		}
	}
	
	void updataReadyTable(List<PCB> pcbList){
		int i;
		int size = pcbList.size();
		PCB pcb;
		for(i=0;i<size-1;i++){
			pcb = pcbList.get(i);
			readyTableModel.setValueAt(pcb.getId(), i, 0);
			readyTableModel.setValueAt(pcb.getArriveTime(), i, 1);
			readyTableModel.setValueAt(pcb.getSerivceTime(), i, 2);
			readyTableModel.setValueAt(pcb.getMenorySize(), i, 3);
		}
		
		for(;i<10;i++){
			readyTableModel.setValueAt("", i, 0);
			readyTableModel.setValueAt("", i, 1);
			readyTableModel.setValueAt("", i, 2);
			readyTableModel.setValueAt("", i, 3);
		}
	}

	// ////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 回调给ProcessController，通知其调度算法选择的改变
	 */
	public interface AlgChangedCallback {
		public void algChanged(int alg);
	}

	private AlgChangedCallback callback;

	public void registerAlgChangedCallback(AlgChangedCallback callback) {
		this.callback = callback;
	}
	
	
	

}
