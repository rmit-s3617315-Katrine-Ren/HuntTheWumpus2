// HuntTheWumpus.java
// Driver and test class for COSC 370, Project 1
// Spring 2013
// Alan C. Jamieson
// Latest Revision: February 12th, 2013

// This driver will ask the user for some information in regards to the format of the Hunt the Wumpus game (credit: Gregory Yob).
// This information will then be passed to the WumpusAgent class (user provided), then randomly assign wumpi, pits, and gold.
// The driver will simulate and provide sensory data as specified in the project document.

// Note: there are very few self-error checks as part of this program.

import java.util.Scanner;
import java.util.Random;

public class HuntTheWumpus {
	
	//Method to generate Stench percept
	public static boolean isStinky(char[][] g, int x, int y, int n){
		if(x != 0 && g[x-1][y] == 'w')
			return true;
		if(x != n-1 && g[x+1][y] == 'w')
			return true;
		if(y != 0 && g[x][y-1] == 'w')
			return true;
		if(y != n-1 && g[x][y+1] == 'w')
			return true;
		return false;
	}
	
	//Method to generate Breeze percept
	public static boolean isBreezy(char[][] g, int x, int y, int n){
		if(x != 0 && g[x-1][y] == 'p')
			return true;
		if(x != n-1 && g[x+1][y] == 'p')
			return true;
		if(y != 0 && g[x][y-1] == 'p')
			return true;
		if(y != n-1 && g[x][y+1] == 'p')
			return true;
		return false;
	}
	
	//Method to generate Glitter percept
	public static boolean isSparkly(int x, int y, int goldx, int goldy){
		if(x == goldx && y == goldy)
			return true;
		return false;
	}
	
	//Quick method to check location
	public static boolean checkWin(int x, int y, int sx, int sy){
		if (x == sx && y == sy)
			return true;
		return false;
	}
	
	public static void main(String[] args) {
		int type = 0, arrows, wumpi, iterations, successes = 0;
		boolean flag = true;
		boolean hasGold = false;
		Scanner s = new Scanner(System.in);
		Random r = new Random(System.nanoTime());
		
		System.out.print("Enter the type, 0 for non-moving Wumpi, 1 for moving wumpi: ");
		while(flag){
			type = s.nextInt();
			if(type == 0 || type == 1){
				flag = false;
			}else{
				System.out.println("Invalid type entry, try again.");
				System.out.print("Enter the type, 0 for non-moving Wumpi, 1 for moving wumpi: ");
			}
		}
		
		System.out.print("Enter the number of wumpi: ");
		wumpi = s.nextInt();

		System.out.print("Enter the number of arrows: ");
		arrows = s.nextInt();
		
		System.out.print("Enter the number of iterations: ");
		iterations = s.nextInt();
		
		//prep and play an Hunt the Wumpus game
		for(int i = 0; i<iterations; i++){
			//create character array to hold the game board
			int n = r.nextInt(200)+wumpi+2;
			char[][] grid = new char[n][n];
			
			//initialize the game board
			for(int j = 0; j<n; j++)
				for(int k = 0; k<n; k++)
					grid[j][k] = 'x';
			
			//prep wumpi list
			Wumpus w[] = new Wumpus[wumpi];
			
			//characters -
			// i - entry/exit
			// g - gold
			// w - wumpus
			// p - pit
			
			//prep game board with initial state
			for (int j = 0; j<wumpi; j++){
				int y = r.nextInt(n);
				int z = r.nextInt(n);
				if (grid[y][z] == 'x'){
					grid[y][z] = 'w';
					w[j] = new Wumpus(y, z);
				}
				else
					j--;
			}		
			
			for (int j = 0; j<n; j++){
				int y = r.nextInt(n);
				int z = r.nextInt(n);
				if(grid[y][z] == 'x')
					grid[y][z] = 'p';
			}
			
			int goldx = -1;
			int goldy = -1;
			flag = true;
			while(flag){
				goldx = r.nextInt(n);
				goldy = r.nextInt(n);
				if (grid[goldx][goldy] != 'w' && grid[goldx][goldy] != 'p')
					flag = false;
			}
			
			//prep entry and exit position
			int x = r.nextInt(n);
			int y = r.nextInt(n);
			if (grid[x][y] == 'w' || grid[x][y] == 'p'){
				System.out.println("You died!");
				continue;
			}
			int initx = x;
			int inity = y;
			grid[x][y] = 'i';
			
			//get initial percept sequence
			String percept = "";
			if(isStinky(grid, x, y, n))
				percept += "S";
			
			if(isBreezy(grid, x, y, n))
				percept += "B";
			
			if(isSparkly(x, y, goldx, goldy))
				percept += "G";
			
			//create agent
			WumpusAgent wa = new WumpusAgent(type, arrows, wumpi);
			int arrowsleft = arrows;
			hasGold = false;
			
			//while loop for the game loop
			while(true){
				//get move from agent
				//if(!percept.equals(""))
				//	System.out.println(percept);
				String move = wa.getMove(percept);
				boolean b = false;
				percept = "";
				
				//process move and check legality
				boolean illegal = false;
				if (move.equals("SN") || move.equals("SS") || move.equals("SE") || move.equals("SW")){
					if (arrowsleft == 0){
						System.out.println("You tried to shoot an arrow you don't have!");
						illegal = true;
					}
				}
				
				//movement
				if(move.equals("N")){
					if(x == 0)
						b = true;
					else
						x--;
				}
					
				if(move.equals("S")){
					if(x == n-1)
						b = true;
					else
						x++;
				}
					
				if(move.equals("E")){
					if(y == n-1)
						b = true;
					else
						y++;
				}
					
				if(move.equals("W")){
					if(y == 0)
						b = true;
					else
						y--;
				}
				
				//grab gold
				if (move.equals("G")){
					if(x == goldx && y == goldy)
						hasGold = true;
					else
						System.out.println("No gold here!");
				}
				
				//check for win
				if (move.equals("C")){
					if (hasGold && checkWin(x, y, initx, inity)){
						successes++;
						System.out.println("You won!");
						break;
					}else{
						System.out.println("Tried to climb out without the gold OR not at the exit!");
					}
				}
				
				//check for death
				if(grid[x][y] == 'w' || grid[x][y] == 'p'){
					System.out.println("You suffered a gruesome death!");
					break;
				}
				
				//update percept sequence
				if(isStinky(grid, x, y, n))
					percept += "S";
				
				if(isBreezy(grid, x, y, n))
					percept += "B";
				
				if(isSparkly(x, y, goldx, goldy))
					percept += "G";
				
				//generate bump
				if(b)
					percept += "U";
				
				//check for arrow kill
				if (!illegal){
					boolean deadly = false;
					
					if (move.equals("SN")){
						arrowsleft--;
						for(int q = x; q>=0; q--){
							if(grid[q][y] == 'w'){
								deadly = true;
								grid[q][y] = 'x';
								for(Wumpus temp : w){
									if(temp.x == q && temp.y == y){
										temp.dead = true;
									}
								}
							}
						}
					}
						
					if (move.equals("SS")){
						arrowsleft--;
						for(int q = x; q<n; q++){
							if(grid[q][y] == 'w'){
								deadly = true;
								grid[q][y] = 'x';
								for(Wumpus temp : w){
									if(temp.x == q && temp.y == y){
										temp.dead = true;
									}
								}
							}
						}
					}
						
					if (move.equals("SW")){
						arrowsleft--;
						for(int q = y; q>=0; q--){
							if(grid[x][q] == 'w'){
								deadly = true;
								grid[x][q] = 'x';
								for(Wumpus temp : w){
									if(temp.x == x && temp.y == q){
										temp.dead = true;
									}
								}
							}
						}
					}
						
					if (move.equals("SE")){
						arrowsleft--;
						for(int q = y; q<n; q++){
							if(grid[x][q] == 'w'){
								deadly = true;
								grid[x][q] = 'x';
								for(Wumpus temp : w){
									if(temp.x == x && temp.y == q){
										temp.dead = true;
									}
								}
							}
						}
					}
					
					if(deadly){
						System.out.println("You hear the death scream of a wumpus! AD");
						percept += "C";
					}
					
				}
				
				//move wumpi
				if(type == 1){
					//clear wumpi entries in the grid
					for(Wumpus temp : w){
						grid[temp.x][temp.y] = 'x';
					}
					
					for(Wumpus temp : w){
						if(temp.dead)
							continue;
						int d = r.nextInt(5);
						if (d == 0 && temp.x != n-1)
							temp.x++;
						if (d == 1 && temp.x != 0)
							temp.x--;
						if (d == 2 && temp.y != n-1)
							temp.y++;
						if (d == 3 && temp.y != 0)
							temp.y--;
					}
					
					//check for pit death
					for(Wumpus temp : w){
						if(temp.dead)
							continue;
						if(grid[temp.x][temp.y] == 'p'){
							temp.dead = true;
							if(!percept.contains("C")){
								System.out.println("You hear the death scream of a wumpus! PD");
								percept += "C";
							}	
						}
					}
					
					//update grid
					for(Wumpus temp : w){
						if(temp.dead)
							continue;
						grid[temp.x][temp.y] = 'w';
					}
					
					//check for agent death via wumpus again
					if(grid[x][y] == 'w'){
						System.out.println("You suffered a gruesome death!");
						break;
					}
				}
			}
		}
		System.out.println("Successes: " + successes);
		float rate = ((float) successes/(float) iterations) * 100;
		System.out.println("Success Rate: " + rate + " percent.");
	}

}
