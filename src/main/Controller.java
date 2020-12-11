package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import enums.Direction;
import enums.State;

public class Controller implements KeyListener {
	private Player player;
	private boolean[] isHolding = new boolean[] {
			false, // W
			false, // A
			false, // S
			false, // D
			false  // Shift
	};
	
	public Controller(Player player) {
		this.player = player;
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		if (code == KeyEvent.VK_W) {
			player.setDirection(Direction.UP);
			isHolding[0] = true;
		}
		else if (code == KeyEvent.VK_A) {
			player.setDirection(Direction.LEFT);
			isHolding[1] = true;
		}
		else if (code == KeyEvent.VK_S) {
			player.setDirection(Direction.DOWN);
			isHolding[2] = true;
		}
		else if (code == KeyEvent.VK_D) {
			player.setDirection(Direction.RIGHT);
			isHolding[3] = true;
		} else if (code == KeyEvent.VK_SHIFT) {
			player.addCrossing();
			isHolding[4] = true;
		}
		
		if (isHolding[4]) player.setState(State.CROSSING);
		else if (!isHolding[4] && player.getState() != State.REVERSING) player.setState(State.MOVING);
//		System.out.println(player.getDirection() + " " + player.getState());
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int code = e.getKeyCode();
		if (code == KeyEvent.VK_W) isHolding[0] = false;
		else if (code == KeyEvent.VK_A) isHolding[1] = false;
		else if (code == KeyEvent.VK_S) isHolding[2] = false;
		else if (code == KeyEvent.VK_D) isHolding[3] = false;
		else if (code == KeyEvent.VK_SHIFT) {
			if (player.getState() == State.CROSSING) player.setState(State.REVERSING);
			isHolding[4] = false;
		}

		boolean notHolding = true;
		for (int i = 0; i < isHolding.length - 1; i++)
			if (isHolding[i] == true) {
				notHolding = false;
				break;
			}
		if (notHolding && player.getState() == State.MOVING) player.setState(State.STOP);
	}

	@Override
	public void keyTyped(KeyEvent e) {}
}
