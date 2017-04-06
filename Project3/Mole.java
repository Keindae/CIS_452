/*******************************************************************************
Matt Noblett
Whack-a-Mole
Wolffe - GVSU 2017
Project 3
 ******************************************************************************/

//The required imports for the project
import javax.naming.SizeLimitExceededException;
import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.concurrent.Semaphore;



public class Mole extends JFrame implements ActionListener{

	//Declaration of the variables and menu items that I am going to be using
	private JMenu menus;
	private JMenuBar menu;
	private JMenuItem start;
	private JPanel centerPanel, southPanel;
	private JLabel score, missed;
	private static int gameScore, missScore;
	private static JTextArea scoreText, missText;
	private static Color moleUp = Color.GREEN;
	private static Color moleDown = Color.WHITE;
	private static String down = "()";
	private static String up = "Mole Up";
	int userResult = 8, userResult3;
	static int userResult2;
	String userSize, userMoles, userTime;
	int gameTime;
	private static Semaphore semaphore;
	private JButton[][] buttons;
	private Thread[][] moleThreads;
	Random random = new Random();

	/*****************************************************************************
	 * Constructor of the mole class to set up the GUI.
	 ****************************************************************************/
	public Mole(){
		//Sets the title of the GUI
		this.setTitle("Whack-A-Mole!");
		//sets the closing option
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		centerPanel = new JPanel();
		southPanel = new JPanel(new GridLayout(0,2));
		this.setLayout(new BorderLayout());
		//Hardcoded the size of the buttons and game
		this.addButtons( userResult);
		this.add(centerPanel, BorderLayout.CENTER);
		this.setSize(400, 450);
		this.setVisible(true);
		//Call to the function to create my menu bar
		this.setupMenus();
		//Creating the score label and the missed mole labels
		//and text areas to store the score
		score = new JLabel("SCORE:");
		missed = new JLabel("Missed Clicked Moles:");
		missed.setVisible(true);
		score.setVisible(true);
		southPanel.add(missed);
		southPanel.add(score);
		missText = new JTextArea(1, 5);
		missText.setEditable(false);
		missText.setVisible(true);
		scoreText = new JTextArea(1,5);
		scoreText.setEditable(false);
		scoreText.setVisible(true);
		southPanel.add(missText);
		southPanel.add(scoreText);
		//Adds the menu created to the northern panel
		this.add(menu, BorderLayout.NORTH);
		//Adds the score items to the south part of the panel
		this.add(southPanel, BorderLayout.SOUTH);
	}
	/*****************************************************************************
	 * Function used to set up the GUI with buttons
	 * based on what is passed in by the user.
	 ****************************************************************************/
	public void addButtons(int numberOfButtons){
		buttons = new JButton[numberOfButtons][numberOfButtons];
		centerPanel.removeAll();
		centerPanel.setLayout(new GridLayout(numberOfButtons, numberOfButtons));
		//Loops through the entire array
		for(int i = 0; i<numberOfButtons; i++){
			for(int j = 0; j<numberOfButtons; j++){
				//creates a new JButton at each location
				buttons[i][j] = new JButton();
				//Setting the opacity to have the correct color show up
				buttons[i][j].setOpaque(true);
				//Add an action listener to each button that is being created
				buttons[i][j].addActionListener(this);
				//Put the created button onto the center panel of my GUI
				centerPanel.add(buttons[i][j]);
			}
		}
	}
	//Used for creating the mole threads for the GUI
	private class MoleThread implements Runnable{
		private JButton button;
		/***************************************************************************
		 * Sets up the default look of the buttons
		 ***************************************************************************/
		public MoleThread(JButton button){
			this.button = button;
			this.button.setBackground(moleDown);
			this.button.setText(down);
		}
		/***************************************************************************
		 * used to generate a random number for the amount
		 * of time to sleep for the threads
		 **************************************************************************/
		public int randomNumber(){
			int n = random.nextInt(6000);

			return n;
		}

		/***************************************************************************
		 * Used to check if the thread has acquired a semaphore or not.
		 * If it has acquired a semaphore, then it sleeps and changes the
		 * text of the box. If the thread cannot grab a semaphore,
		 * it will sleep a random amount of time and then attempt to regain
		 * a semaphore.
		 **************************************************************************/
		public void run() {
			while(true){
				//If the semaphore is able to be acquired, then it sets the text
				//and the background to show that the mole has come out of the hole
				if(semaphore.tryAcquire()){
					this.button.setBackground(moleUp);
					this.button.setText(up);
					try{
						Thread.sleep(randomNumber());
						this.button.setText(down);
						this.button.setBackground(moleDown);
						semaphore.release();
						Thread.sleep(randomNumber());
					}
					//If the button is pressed, then the system send off
					//and interrupt call, and I catch it here, and
					//set the text and button color back to "mole down"
					catch(InterruptedException e){
						this.button.setText(down);
						this.button.setBackground(moleDown);
						semaphore.release();
						try {
							Thread.sleep(randomNumber());
						} catch (InterruptedException e1) {
						}
					}
					//This is used when the thread doesn't have access to a semaphore,
					//it just sleeps a random time and will attempt to regain access
					//to the semaphore.
				}else{
					try {
						Thread.sleep(randomNumber());
						this.button.setText(down);
						this.button.setBackground(moleDown);
					} catch (InterruptedException e) {
						this.button.setBackground(moleDown);
						this.button.setText(down);
					}
				}
			}
		}
	}

	/*****************************************************************************
	 * Main program to run the program.
	 *****************************************************************************/
	public static void main(String args[]){
		Mole mole = new Mole();
	}

	/*****************************************************************************
	 * Sets up the user menu for them to start the game
	 * Very basic only has a start button to get the game rolling
	 *****************************************************************************/
	private void setupMenus(){
		menus = new JMenu("Menu");
		start = new JMenuItem("Start");
		start.addActionListener(this);
		menus.add(start);
		menu = new JMenuBar();
		menu.add(menus);
	}
	/****************************************************************************
	 * Prompts the user over and over again for the correct number format for 
	 * the board size. Once the user inputs it correctly, it will move on to 
	 * mole set up.
	 ***************************************************************************/
	private int setUpBoardSize(String size){
		userSize = JOptionPane.showInputDialog("Please input the size "
				+ "that you want the board to be");
		try{
			userResult = Integer.parseInt(userSize);
		}
		catch(NumberFormatException e1){
			JOptionPane.showMessageDialog(null, "Improper Number format - Integers"
					+ "only! (1-10)");
			return 0;
		}
		return 1;
	}
	
	/****************************************************************************
	 * Prompts the user over and over again for the correct number format for 
	 * the mole number. Once the user inputs it correctly, it will move on and 
	 * prompt the user for the time to play the game
	 ***************************************************************************/
	private int setUpMoleNumber(String mole){
		userMoles = JOptionPane.showInputDialog("Please enter the number "
				+ "of moles you want visible at a time");
		try{
			userResult2 = Integer.parseInt(userMoles);
		}catch(NumberFormatException e1){
			JOptionPane.showMessageDialog(null, "Improper Number format - Integers"
					+ "only! (1-10)");
			return 0;
		}
		return 1;
	}
	
	/****************************************************************************
	 * Prompts the user over and over again for the correct number format for 
	 * the time to play the game. Once the user inputs it correctly, it will 
	 * move on and start the game.
	 ***************************************************************************/
	private int userTime(String time){
		userTime = JOptionPane.showInputDialog("Please enter the ammount of time"
				+ "you want to play the game!");
		try{
			userResult3 = Integer.parseInt(userTime);
		}catch(NumberFormatException e1){
			JOptionPane.showMessageDialog(null, "Improper Number format - Integers"
					+ "only! (1-10)");
			return 0;
		}
		return 1;
	}
	
	
	public void gameTimer(int time){
		Timer gameTimer = new Timer(time * 1000, new ActionListener(){
			public void actionPerformed(ActionEvent evt){
				JOptionPane.showMessageDialog(null, "The time is up!");	
				centerPanel.removeAll();
			}
		});
		gameTimer.setRepeats(false);
		gameTimer.start();
	}

	/*****************************************************************************
	 * Actionlistener to fire off events based on what the
	 * user has input.
	 ****************************************************************************/
	public void actionPerformed(ActionEvent e) {
		//If the user presses "Start", then it will create a thread for each button
		//inside of the array of buttons
		if(e.getSource() == start){
			//JOptionPanes for the user to input the size and the number
			//Moles that they want to play the game.
			while(true){
				if(setUpBoardSize(userSize) == 1){
					break;
				}
			}
			while(true){
				if(setUpMoleNumber(userMoles) == 1){
					break;
				}
			}
			while(true){
				if(userTime(userTime) == 1){
					break;
				}
			}
			this.addButtons(userResult);
			gameTimer(userResult3);
			if(userResult2 > userResult * userResult){
				JOptionPane.showMessageDialog(null, "You entered a mole size larger"
						+ "than the board, setting the mole number to 1");
				semaphore = new Semaphore(1);
			}else{
				semaphore = new Semaphore(userResult2);
			}
			moleThreads = new Thread[userResult][userResult];
			for(int i = 0; i < userResult; i++){
				for(int j = 0; j < userResult; j++){
					Thread mole = new Thread(new MoleThread(buttons[i][j]));
					moleThreads[i][j] = mole;
					mole.start();
				}
			}
		}
		//Loops through the button array and checks to see if the position the user
		//clicked, and then increments based on what was clicked.

		for(int i = 0; i <  userResult; i++){
			for(int j = 0; j <  userResult; j++){
				if(buttons[i][j] == e.getSource()){
					//This is checking to see if the user clicked the "mole up", if so,
					//The thread will then release its semaphore to be accessed by a
					//different thread.
					if(buttons[i][j].getText() == up){
						gameScore++;
						scoreText.setText("" + gameScore);
						buttons[i][j].setText(down);
						moleThreads[i][j].interrupt();
					}
					//If the user clicks on a down thread, then the missed count just
					//gets incremented.
					if(buttons[i][j].getText() == down
							&& buttons[i][j].getBackground() == moleDown){
						missScore++;
						missText.setText("" + missScore); 
						buttons[i][j].setBackground(Color.RED);
					}else{

					}
				}

			}
		}
	}
}
