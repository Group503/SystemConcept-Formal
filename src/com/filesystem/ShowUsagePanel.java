package com.filesystem;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

class ShowUsagePanel extends JPanel {

	private byte bytes[];

	public ShowUsagePanel(byte[] bytes) {
		this.bytes = bytes;
	}

	@Override
	protected void paintComponent(Graphics g) {

		super.paintComponent(g);
		double width = getWidth();
		double height = getHeight();

		int recWidth = (int) (width / 16);
		int recHeight = (int) (height * 0.7 / 8);

		g.setColor(new Color(0x93, 0x0, 0x0));
		g.fillRoundRect((int) (width * 0.02), (int) (height * 0.1), recWidth,
				recHeight, 10, 10);
		g.setColor(Color.LIGHT_GRAY);
		g.fillRoundRect((int) (width * 0.4), (int) (height * 0.1), recWidth,
				recHeight, 10, 10);

		g.setColor(Color.WHITE);
		g.drawRoundRect((int) (width * 0.02), (int) (height * 0.1), recWidth,
				recHeight, 10, 10);
		g.drawRoundRect((int) (width * 0.4), (int) (height * 0.1), recWidth,
				recHeight, 10, 10);

		g.setColor(Color.black);
		g.drawString("系统占用", (int) (width * 0.05) + recWidth,
				(int) (height * 0.16));
		g.drawString("空闲", (int) (width * 0.44) + recWidth,
				(int) (height * 0.16));
		g.drawString("其他 ：已使用", (int) (width * 0.7) + recWidth,
				(int) (height * 0.16));

		for (int i = 0; i < 128; i++) {
			if (i < 3) {
				g.setColor(new Color(0x93, 0x0, 0x0));
			} else if (bytes[i] == -1) {
				g.setColor(new Color(i * 111 % 255, i * 222 % 255,
						i * 333 % 255));
				// g.setColor(new Color(0x01, 0x98, 0x58));
			} else if (bytes[i] > 0) {
				// g.setColor(Color.white);
				int j = i;
				while (bytes[j] != -1) {
					j = bytes[j];
				}

				g.setColor(new Color(j * 111 % 255, j * 222 % 255,
						j * 333 % 255));
			} else {
				g.setColor(Color.LIGHT_GRAY);
			}

			g.fillRoundRect((i % 16) * recWidth, (i / 16) * recHeight
					+ (int) (0.3 * height), recWidth, recHeight, 10, 10);

			// g.setColor(new Color(0x84, 0xc1, 0xff));
			g.setColor(Color.white);
			g.drawRoundRect((i % 16) * recWidth, (i / 16) * recHeight
					+ (int) (0.3 * height), recWidth, recHeight, 10, 10);
		}
	}

}
