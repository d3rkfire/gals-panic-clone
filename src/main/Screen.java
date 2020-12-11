package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.Timer;

import enums.Position;

@SuppressWarnings("serial")
public class Screen extends JLabel {
	private int startX;
	private int startY;
	private int width;
	private int height;
	private int gap;
	private Position[][] positions;
	private BufferedImage background;
	private Player player;
	
	public Screen(int startX, int startY, int frameW, int frameH, int gap, File backgroundFile) {
		this.startX = startX;
		this.startY = startY;
		this.gap = gap;
		
		width = (frameW - startX * 2) / gap * gap;
		height = (frameH - startY * 2) / gap * gap;
		
		positions = new Position[width/gap][height/gap];
		for (int x = 1; x < width/gap - 1; x++)
			for (int y = 1; y < height/gap - 1; y++)
				positions[x][y] = Position.CLEAR;
		for (int x = 0; x < width/gap; x++)
		{
			positions[x][0] = Position.BORDER;
			positions[x][height/gap-1] = Position.BORDER;
		}
		for (int y = 0; y < height/gap; y++)
		{
			positions[0][y] = Position.BORDER;
			positions[width/gap-1][y] = Position.BORDER;
		}
		
		player = new Player(startX, startY, gap, 25, this);
		background = loadBackground(backgroundFile);
		
		//60 frames / 1 second => 1 frame = 16.6 mSec
		Timer timer = new Timer(16, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				player.move();
			}
		});
		timer.start();
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		// draw background
		if (background != null)
			g.drawImage(background,
					startX, startY, startX + width, startY + height,
					0, 0, width, height,
					null);
		
		// draw lines
		for (int x = 0; x < width/gap; x++)
			for (int y = 0; y < height/gap; y++) {
				if (positions[x][y] == Position.CLEAR) g.setColor(Color.BLACK);
				else if (positions[x][y] == Position.FILL) continue;
				else if (positions[x][y] == Position.LINE) g.setColor(Color.LIGHT_GRAY);
				else if (positions[x][y] == Position.BORDER) g.setColor(Color.WHITE);
				else if (positions[x][y] == Position.CROSSING) g.setColor(Color.GREEN);
				else if (positions[x][y] == Position.TESTFILL1) g.setColor(Color.YELLOW);	// If you see this, there is a bug.
				else if (positions[x][y] == Position.TESTFILL2) g.setColor(Color.MAGENTA);	// If you see this, there is a bug.

				g.fillRect(startX + gap * x, startY + gap * y, gap, gap);
			}
		
		// draw player
		g.setColor(Color.RED);
		g.fillOval(player.getX() - player.getSize()/2 + gap/2, player.getY() - player.getSize()/2 + gap/2, player.getSize(), player.getSize());
	}
	private BufferedImage loadBackground(File file) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(file);
			float ratio = (float) width/image.getWidth();
			AffineTransform at = new AffineTransform();
			at.scale(ratio, ratio);
			AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
			image = scaleOp.filter(image, null);
		} catch (IOException e) {e.printStackTrace();}
		return image;
	}
	
	public Player getPlayer() {
		return player;
	}
	public Position[][] getPositions() {
		return positions;
	}
	
	public int getStartX() {
		return startX;
	}
	
	public int getStartY() {
		return startY;
	}
	public int getPosWidth() {
		return width/gap;
	}
	public int getPosHeight() {
		return height/gap;
	}
}