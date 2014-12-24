package com.filesystem;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

class FileTree extends JPanel {

	Disk disk;
	byte[] bytes;
	final int ITEMLENGTH = 8;
	final int BLOCKLENGTH = 64;
	JScrollPane scrollPane;
	JTree jTree;
	int width, height;

	DefaultMutableTreeNode root;// 我的电脑
	DefaultMutableTreeNode c, d, e; // c d e 盘

	public FileTree(Disk disk, int width, int height) {
		this.disk = disk;
		setLayout(null);
		this.width = width;
		this.height = height;
		init();
		// System.out.println(scrollPane.getWidth());
	}

	// 初始化
	public void init() {
		// System.out.println("init");
		bytes = disk.bytes;

		if (jTree != null) {
			remove(scrollPane);
			// invalidate();
			// repaint();
			System.out.println("remove");
		}
		root = new DefaultMutableTreeNode("我的电脑");

		// 增加3个根目录 C: 、D: 、E:
		c = new DefaultMutableTreeNode("C:") {
			public boolean isLeaf() { // 显示为非子节点
				return false;
			}
		};
		d = new DefaultMutableTreeNode("D:") {
			public boolean isLeaf() {
				return false;
			}
		};
		e = new DefaultMutableTreeNode("E:") {
			public boolean isLeaf() {
				return false;
			}
		};
		root.add(c);
		root.add(d);
		root.add(e);

		loadTreenode(c, 128);
		loadTreenode(d, 136);
		loadTreenode(e, 144);

		jTree = new JTree(root);
		// jTree.setRootVisible(false);
		scrollPane = new JScrollPane();
		scrollPane.setViewportView(jTree);
		scrollPane.setBounds(0, 0, width, height);

		add(scrollPane);
		System.out.println("add");

		// validate();
		updateUI();
		// repaint();
	}
	
	public void flushNode(String nodePath, int index) {
		if (nodePath.equals("我的电脑")) {
			c.removeAllChildren();
			d.removeAllChildren();
			e.removeAllChildren();
		} else {
			String[] subPath = nodePath.split("\\\\");
			subPath[0] = subPath[0].toUpperCase();// 盘符转大写
			DefaultMutableTreeNode node = root;

			for (int i = 0; i < subPath.length; i++) {
				int childCount = node.getChildCount();
				for (int j = 0; j < childCount; j++) {

					String childName = (String) ((DefaultMutableTreeNode) node
							.getChildAt(j)).getUserObject();
					if (childName.equals(subPath[i])) {
						node = (DefaultMutableTreeNode) node.getChildAt(j);
						break;
					}
				}
			}
			node.removeAllChildren();
			loadTreenode(node, index);

		}

		jTree.updateUI();
		updateUI();
	}

	// 加载文件树
	public void loadTreenode(DefaultMutableTreeNode root, int startIndex) {// startindex为目录项开始下标
		byte startBloack = bytes[startIndex + ITEMLENGTH - 1]; // 起始盘号
		if (startBloack > 2 && bytes[startIndex + 3] == 'n') { // 非空 (
																// 前三个盘块系统占用) 且
																// 为目录
			int subStartIndex = startBloack * BLOCKLENGTH;// 子目录项开始下标
			int fileLength = bytes[startIndex + 6];

			for (int i = 0; i < fileLength; i++, subStartIndex += 8) {
				char[] filename = { (char) bytes[subStartIndex],
						(char) bytes[subStartIndex + 1],
						(char) bytes[subStartIndex + 2] };
				String nameString = String.valueOf(filename).replaceAll("\\$",
						"");
				DefaultMutableTreeNode child;

				if (bytes[subStartIndex + 3] == 'n') {// 子目录项为目录
					if (bytes[subStartIndex + ITEMLENGTH - 1] == -1) { // 空目录
						child = new DefaultMutableTreeNode(nameString) {
							public boolean isLeaf() {
								return false;
							}
						};
						root.add(child);
					} else { // 非空目录
						child = new DefaultMutableTreeNode(nameString);
						loadTreenode(child, subStartIndex); // 递归加载
						root.add(child);

					}
				} else if (bytes[subStartIndex + 3] == 'e') { // 文件
					nameString = nameString.concat(".e");
					child = new DefaultMutableTreeNode(nameString);
					root.add(child);
				}

			}
		}

	}
}
