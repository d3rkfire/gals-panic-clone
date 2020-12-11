package main;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import enums.Direction;
import enums.Position;
import enums.State;

public class Player {
	private Screen screen;
	private Direction direction = Direction.RIGHT;
	private State state = State.STOP;
	private int x;
	private int y;
	private int speed;
	private int size;
	private ArrayList<int[]> lstCrossing;
	private int fillCount;

	public Player(int x, int y, int speed, int size, Screen screen) {
		this.screen = screen;
		this.x = x;
		this.y = y;
		this.speed = speed;
		this.size = size;
		lstCrossing = new ArrayList<>();
	}

	public void move() {
//		System.out.println(direction + " " + state);
		
		Position[][] positions = screen.getPositions();
		int posX = (x - screen.getStartX()) / speed;
		int posY = (y - screen.getStartY()) / speed;
		if (state == State.STOP) {
			// Stop
		}
		else if (state == State.MOVING) {
			// Moving
			if (direction == Direction.LEFT) {
				if (posX - 1 < 0) return;
				if (positions[posX - 1][posY] == Position.BORDER || positions[posX - 1][posY] == Position.LINE) posX -= 1;
			}
			else if (direction == Direction.RIGHT) {
				if (posX + 1 >= screen.getPosWidth()) return;
				if (positions[posX + 1][posY] == Position.BORDER || positions[posX + 1][posY] == Position.LINE) posX += 1;
			}
			else if (direction == Direction.UP) {
				if (posY - 1 < 0) return;
				if (positions[posX][posY - 1] == Position.BORDER || positions[posX][posY - 1] == Position.LINE) posY -= 1;
			}
			else if (direction == Direction.DOWN) {
				if (posY + 1 >= screen.getPosHeight()) return;
				if (positions[posX][posY + 1] == Position.BORDER || positions[posX][posY + 1] == Position.LINE) posY += 1;
			}
		} else if (state == State.CROSSING) {
			// Crossing
			if (direction == Direction.LEFT) {
				if (posX - 1 >= 0) posX -= 1;
			}
			else if (direction == Direction.RIGHT) {
				if (posX + 1 < screen.getPosWidth()) posX += 1;
			}
			else if (direction == Direction.UP) {
				if (posY - 1 >= 0) posY -= 1;
			}
			else if (direction == Direction.DOWN) {
				if (posY + 1 < screen.getPosHeight()) posY += 1;
			}

			if (positions[posX][posY] == Position.CLEAR) {
				lstCrossing.add(new int[] {posX, posY});
				positions[posX][posY] = Position.CROSSING;
			} else if (positions[posX][posY] == Position.BORDER || positions[posX][posY] == Position.LINE) {
				// Set line
				for (int i=1; i < lstCrossing.size(); i++) {
					int crossX = lstCrossing.get(i)[0];
					int crossY = lstCrossing.get(i)[1];
					positions[crossX][crossY] = Position.LINE;
				}
				// Calculate Slice here with Flood fill
				fillCount = 0;
				int count1 = 0;
				int count2 = 0;
				
				if (direction == Direction.LEFT || direction == Direction.RIGHT) {
					int buffer = 0;
					if (direction == Direction.LEFT) buffer = 1;
					else if (direction == Direction.RIGHT) buffer = -1;
					
					FloodFill(positions, posX+buffer, posY-1, Position.TESTFILL1);
					count1 = fillCount;
					fillCount = 0;
					FloodFill(positions, posX+buffer, posY+1, Position.TESTFILL2);
					count2 = fillCount;
				}
				else if (direction == Direction.UP || direction == Direction.DOWN) {
					int buffer = 0;
					if (direction == Direction.UP) buffer = 1;
					else if (direction == Direction.DOWN) buffer = -1;
					
					FloodFill(positions, posX-1, posY+buffer, Position.TESTFILL1);
					count1 = fillCount;
					fillCount = 0;
					FloodFill(positions, posX+1, posY+buffer, Position.TESTFILL2);
					count2 = fillCount;
				}
//				System.out.println("Count 1 = " + count1);
//				System.out.println("Count 2 = " + count2);
				if (count1 < count2) {
					for (int repX = 0; repX < positions.length; repX++)
						for (int repY = 0; repY < positions[0].length; repY++)
						{
							if (positions[repX][repY] == Position.TESTFILL1) positions[repX][repY] = Position.FILL;
							else if (positions[repX][repY] == Position.TESTFILL2) positions[repX][repY] = Position.CLEAR;
						}
				} else if (count1 > count2) {
					for (int repX = 0; repX < positions.length; repX++)
						for (int repY = 0; repY < positions[0].length; repY++)
						{
							if (positions[repX][repY] == Position.TESTFILL1) positions[repX][repY] = Position.CLEAR;
							else if (positions[repX][repY] == Position.TESTFILL2) positions[repX][repY] = Position.FILL;
						}
				}
				
				state = State.MOVING;
				lstCrossing.clear();
			}

		} else if (state == State.REVERSING) {
			if (lstCrossing.size() > 1) {
				int last = lstCrossing.size() - 1;
				posX = lstCrossing.get(last)[0];
				posY = lstCrossing.get(last)[1];
				positions[posX][posY] = Position.CLEAR;
				lstCrossing.remove(last);
			} else if (lstCrossing.size() == 1) {
				posX = lstCrossing.get(0)[0];
				posY = lstCrossing.get(0)[1];
				lstCrossing.remove(0);
				state = State.STOP;
			}
		}

		x = screen.getStartX() + posX * speed;
		y = screen.getStartY() + posY * speed;
		screen.repaint();
	}
	
	private void FloodFill(Position[][] pos, int x, int y, Position target) {
		// Source is Position.CLEAR
		if (pos[x][y] == Position.BORDER || pos[x][y] == Position.LINE) return;
		if (pos[x][y] == target) return;
//		int maxX = pos.length; 
//		int maxY = pos[0].length;
		
		Queue<int[]> queue = new LinkedList<>();
		queue.add(new int[] {x,y});
		
		while (!queue.isEmpty()) {
			int[] values = queue.remove();
			if (pos[values[0]][values[1]] == Position.CLEAR) {
				pos[values[0]][values[1]] = target;
				fillCount += 1;
				
				queue.add(new int[] {values[0]-1, values[1]});
				queue.add(new int[] {values[0]+1, values[1]});
				queue.add(new int[] {values[0], values[1]-1});
				queue.add(new int[] {values[0], values[1]+1});
				
				queue.add(new int[] {values[0]-1, values[1]-1});
				queue.add(new int[] {values[0]-1, values[1]+1});
				queue.add(new int[] {values[0]+1, values[1]-1});
				queue.add(new int[] {values[0]+1, values[1]+1});
			}
		}
	}
	
	
	public void setDirection(Direction dir) {
		direction = dir;
	}
	public Direction getDirection() {
		return direction;
	}
	
	public void setState(State state) {
		this.state = state;
	}
	public State getState() {
		return state;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getSize() {
		return size;
	}
	public void addCrossing() {
		int posX = (x - screen.getStartX()) / speed;
		int posY = (y - screen.getStartY()) / speed;
		lstCrossing.add(new int[] {posX, posY});
	}
}
