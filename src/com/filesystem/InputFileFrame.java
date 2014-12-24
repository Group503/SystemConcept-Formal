package com.filesystem;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;

class InputFileFrame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String content;
	JTextArea jTextArea;

	public InputFileFrame() {
		// setLayout(null);

		jTextArea = new JTextArea();
		jTextArea.setLineWrap(true);
		jTextArea.setFont(new Font("SansSerif", Font.BOLD, 16));
		JScrollPane jScrollPane = new JScrollPane(jTextArea);
		jScrollPane.setBorder(new LineBorder(Color.white, 10));

		// jScrollPane.setBounds(50, 10, 300, 100);
		add(jScrollPane);

		JPanel jPanel = new JPanel();
		jPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 5));
		JButton ensurebt = new JButton("确定");
		JButton cancelbt = new JButton("取消");

		ensurebt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				content = jTextArea.getText();
				System.out.println(content);
				System.out.println("length : " + content.length());
				// setVisible(false);
				callBack.returnInputFile(content);
				dispose();
			}
		});

		cancelbt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				// setVisible(false);
				dispose();
			}
		});

		jPanel.add(ensurebt);
		jPanel.add(cancelbt);
		add(jPanel, BorderLayout.SOUTH);

		setTitle("InputFileFrame");
		setSize(400, 200);
		setResizable(false);
		// setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);

		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
				// super.windowClosing(e);
			}

		});
	}

	// public void
	public interface InputFileCallBack {
		public void returnInputFile(String str);
	}

	private InputFileCallBack callBack;

	public void registerInputFileCallback(InputFileCallBack callBack) {
		this.callBack = callBack;
	}
}
