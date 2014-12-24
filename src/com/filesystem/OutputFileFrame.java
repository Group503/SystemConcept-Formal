package com.filesystem;

import java.awt.BorderLayout;
import java.awt.Color;
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

class OutputFileFrame extends JFrame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public  OutputFileFrame( String content) {
		
	    JTextArea jTextArea = new JTextArea();
		jTextArea.setLineWrap(true);
		jTextArea.setFont(new Font("SansSerif", Font.BOLD, 16));
		jTextArea.setText(content);
		jTextArea.setEditable(false);
		JScrollPane jScrollPane = new JScrollPane(jTextArea);
		jScrollPane.setBorder(new LineBorder(Color.white, 10));
	
		add( jScrollPane);
		
		JPanel jPanel = new JPanel();
		JButton closebt = new JButton("关闭");
		closebt.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		jPanel.add(closebt);
		add(jPanel , BorderLayout.SOUTH);
		
		setTitle("OutputFileFrame");
		setSize(400, 200);
		setResizable(false);
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
		
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
				//super.windowClosing(e);
			}
			
		});
		
	}
}
