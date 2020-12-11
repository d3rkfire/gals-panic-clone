package main;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Main {
	private JFrame frame;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
					window.frame.setVisible(true);
				} catch (Exception e) {e.printStackTrace();}
			}
		});
	}

	public Main() {
		initialize();
	}

	private void initialize() {
		int frameW = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		int frameH = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		int startX = 25;
		int startY = 25;
		int speed = 5;
		File background = null;
		while (background == null) background = openImage();
		
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setBounds(0, 0, frameW, frameH);
		frame.getContentPane().setBackground(Color.DARK_GRAY);
		frame.setUndecorated(true);
		frame.setLayout(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Screen screen = new Screen(startX, startY, frameW, frameH, speed, background);
		screen.setBounds(0, 0, frameW, frameH);
		frame.getContentPane().add(screen);
			
		frame.addKeyListener(new Controller(screen.getPlayer()));
	}
	
	private File openImage() {
		JFileChooser fc = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "bmp", "gif");
		fc.addChoosableFileFilter(filter);
		fc.setAcceptAllFileFilterUsed(false);
		fc.setMultiSelectionEnabled(false);
		fc.setCurrentDirectory(new File(System.getProperty("user.home") + "/Desktop"));

		int result = fc.showOpenDialog(frame);
		if (result == JFileChooser.APPROVE_OPTION) {
			return fc.getSelectedFile();
		} else return null;
	}
}
