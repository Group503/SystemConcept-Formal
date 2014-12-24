package com.filesystem;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import com.filesystem.Disk.ExeContentCallback;
import com.filesystem.Disk.FlushFileTreeCallback;
import com.filesystem.Disk.FlushUsageCallback;

public class FileSystemManager {
	Disk disk;
	FileTree fileTree;
	ShowUsagePanel showUsagePanel;
	JTextField textField;
	public JPanel filePanel;

	public FileSystemManager() {
		filePanel = new JPanel();
		filePanel.setLayout(new BorderLayout());

		// ShowAttributeFrame frame = new ShowAttributeFrame(null);

		try {
			disk = new Disk();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		disk.registerFlushFileTreeCallback(new FlushFileTreeCallback() {
			// 注册回调刷新文件树接口
			@Override
			public void flushFileTree(String nodePath, int index) {
				System.out.println("filetree nodePath : " + nodePath);
				// int index = disk.getDirectoryItemAddress(nodePath);
				fileTree.flushNode(nodePath, index);
			}
		});
		disk.registerFlushUsageCallback(new FlushUsageCallback() {
			// 注册回调刷新磁盘使用情况接口
			@Override
			public void flushUsage() {
				showUsagePanel.repaint();
			}
		});
		disk.registerExeContentCallback(new ExeContentCallback() {

			@Override
			public void executeFile(String content) {
				executeFileCallback.executeFile(content);
			}
		});

		textField = new JTextField(20);
		textField.setFont(new Font("SansSerif", Font.BOLD, 16));
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {

				super.keyPressed(e);
				if (e.getKeyChar() == KeyEvent.VK_ENTER) {
					String input = textField.getText();
					try {
						disk.execute(input);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		JButton buttonExecute = new JButton("执行");
		buttonExecute.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				String input = textField.getText();

				try {
					disk.execute(input);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});

		// 用户接口
		JPanel panelNorth = new JPanel();
		panelNorth.add(textField);
		panelNorth.add(buttonExecute);
		Border border = new TitledBorder("用户命令接口");
		panelNorth.setBorder(border);
		// panelNorth.setBounds(2, 0, 400, 70);
		panelNorth.setPreferredSize(new Dimension(400, 70));
		filePanel.add(panelNorth, BorderLayout.NORTH);

		// 磁盘目录
		JPanel panelCenter = new JPanel();
		panelCenter.setLayout(null);
		border = new TitledBorder("磁盘目录结构");
		panelCenter.setBorder(border);
		// panelCenter.setBounds(2, 70, 400, 300);

		fileTree = new FileTree(disk, 300, 260);
		fileTree.setBounds(50, 30, 300, 260);
		panelCenter.add(fileTree);
		panelCenter.setPreferredSize(new Dimension(400, 300));
		filePanel.add(panelCenter, BorderLayout.CENTER);

		// 磁盘使用
		JPanel panelSouth = new JPanel();
		border = new TitledBorder("磁盘使用情况");
		panelSouth.setBorder(border);
		// panelSouth.setBounds(2, 370, 400, 230);
		panelSouth.setLayout(null);

		showUsagePanel = new ShowUsagePanel(disk.bytes);
		showUsagePanel.setBounds(10, 20, 380, 200);
		panelSouth.add(showUsagePanel);
		panelSouth.setPreferredSize(new Dimension(400, 230));
		filePanel.add(panelSouth, BorderLayout.SOUTH);

	}

	/************************** 回调返回exe文件执行的内容 ******************************/
	public interface ExecuteFileCallback {
		public void executeFile(String content);
	}

	private ExecuteFileCallback executeFileCallback;

	public void registerExecuteFileCallback(ExecuteFileCallback callback) {
		this.executeFileCallback = callback;
	}
}
