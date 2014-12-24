package com.filesystem;

import java.awt.GridLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;


class ShowAttributeFrame extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ShowAttributeFrame(String[] args){//文件名、扩展名、属性、文件长度、起始磁盘
		
		setLayout(null);
			
		JLabel icon = new JLabel();
		JLabel name = new JLabel();
		JLabel extension = new JLabel();
		JLabel attribute = new JLabel();
		JLabel fileLenght = new JLabel();
		JLabel startblock = new JLabel();
		
		
		if(args[1].equals("n")){
			icon.setIcon(new ImageIcon("directory.png"));
			name.setText("目录名 ： " + args[0]);
			extension.setText("扩展名 ： 无");
			attribute.setText("属性 ： 目录");
			fileLenght.setText("子项个数 ： "+args[3]);
			startblock.setText("起始盘号 ： "+args[4]);
		}else {
			icon.setIcon(new ImageIcon("file.png"));
			name.setText("文件名 ： " + args[0]);
			extension.setText("扩展名 ： "+args[1]);
			attribute.setText("属性 ： 文件");
			fileLenght.setText("文件大小 ： "+Integer.parseInt(args[3])*64 +" kb");
			startblock.setText("起始盘号 ： "+args[4]);
		}
		
		icon.setBounds(86, 10, icon.getPreferredSize().width, icon.getPreferredSize().height);
		
		JPanel  downPanel = new JPanel();
		downPanel.setBounds(0, 150, 300, 250);
		downPanel.setLayout(new GridLayout(6,1));
		downPanel.add(name);
		downPanel.add(extension);
		downPanel.add(attribute);
		downPanel.add(fileLenght);
		downPanel.add(startblock);
		
		
		add(icon);
		/*add(name);
		add(extension);
		add(attribute);
		add(fileLenght);
		add(startblock);*/
		add(downPanel);
		
		name.setHorizontalAlignment(SwingConstants.CENTER);
		extension.setHorizontalAlignment(SwingConstants.CENTER);
		attribute.setHorizontalAlignment(SwingConstants.CENTER);
		fileLenght.setHorizontalAlignment(SwingConstants.CENTER);
		startblock.setHorizontalAlignment(SwingConstants.CENTER);
		
		setTitle("ShowAttributeFrame");
		setSize(300, 400);
		setResizable(false);
		setLocationRelativeTo(null);
		setVisible(true);
		
	}
	
	
}
