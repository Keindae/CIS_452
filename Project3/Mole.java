/*
Matt Noblett
Whack-a-Mole
Wolffe - GVSU 2017
Project 3
 */

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
	private JPanel centerPanel, southPanel, southPanel2;
	private JLabel score, missed;
	private static int gameScore, missScore;
	private static JTextArea scoreText, missText;
	private static Color moleUp = Color.GREEN;
	private static Color moleDown = Color.WHITE;
	private static String down = "()";
	private static String up = "Mole Up";
	private static final int MAX_MOLES = 5;
	private static Semaphore semaphore = new Semaphore(MAX_MOLES);
	private JButton[][] buttons;
	private Thread[][] moleThreads;
	private static Thread mole;
	Random random = new Random();

	/**************************************************
	 * Constructor of the mole class to set up the GUI.
	 *************************************************/
	public Mole(){
		//Sets the title of the GUI
		this.setTitle("Whack-A-Mole!");
		//sets the closing option
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		centerPanel = new JPanel();
		southPanel = new JPanel(new GridLayout(0,2));
		this.setLayout(new BorderLayout());
		this.addButtons(8);
		this.add(centerPanel, BorderLayout.CENTER);
		this.setSize(400, 450);
		this.setVisible(true);
		this.setupMenus();
		score = new JLabel("SCORE:");
		missed = new JLabel("Missed Clicked Moles:");
		missed.setLabelFor(missText);
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
		this.add(menu, BorderLayout.NORTH);
		this.add(southPanel, BorderLayout.SOUTH);
	}
	/****************************************************
	 * Function used to set up the GUI with buttons
	 * based on what is passed in by the user.
	 ***************************************************/
	public void addButtons(int numberOfButtons){
		buttons = new JButton[numberOfButtons][numberOfButtons];
		centerPanel.setLayout(new GridLayout(numberOfButtons, numberOfButtons));
		for(int i = 0; i<numberOfButtons; i++){
			for(int j = 0; j<numberOfButtons; j++){
				buttons[i][j] = new JButton();
				buttons[i][j].setOpaque(true);
				buttons[i][j].addActionListener(this);
				centerPanel.add(buttons[i][j]);
			}
		}
	}
	//Used for creating the mole threads for the GUI
	private class MoleThread implements Runnable{
		private JButton button;

		public MoleThread(JButton button){
			this.button = button;
			this.button.setBackground(moleDown);
			this.button.setText(down);
		}
		/*
		 * used to generate a random number for the amount
		 * of time to sleep for the threads
		 */
		public int randomNumber(){
			int n = random.nextInt(6000);

			return n;
		}

		/*
		 * Used to check if the thread has acquired a semaphore or not.
		 * If it has acquired a semaphore, then it sleeps and changes the
		 * text of the box. If the thread cannot grab a semaphore,
		 * it will sleep a random amount of time and then attempt to regain
		 * a semaphore.
		 */
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

	//Main program to run the program.
	public static void main(String args[]){
		Mole mole = new Mole();
	}

	//Sets up the user menu for them to start the game
	private void setupMenus(){
		menus = new JMenu("Menu");
		start = new JMenuItem("Start");
		start.addActionListener(this);
		menus.add(start);
		menu = new JMenuBar();
		menu.add(menus);
	}

	/*********************************************************
	 * Actionlistener to fire off events based on what the
	 * user has input.
	 ********************************************************/
	public void actionPerformed(ActionEvent e) {
		//If the user presses "Start", then it will create a thread for each button
		//inside of the array of buttons
		if(e.getSource() == start){
			moleThreads = new Thread[8][8];
			for(int i = 0; i < 8; i++){
				for(int j = 0; j < 8; j++){
					Thread mole = new Thread(new MoleThread(buttons[i][j]));
					buttons[i][j].setText("()");
					moleThreads[i][j] = mole;
					mole.start();
				}
			}
		}
		for(int i = 0; i < 8; i++){
			for(int j = 0; j < 8; j++){
				if(buttons[i][j] == e.getSource()){
					if(buttons[i][j].getText() == up){
						gameScore++;
						scoreText.setText("" + gameScore);
						buttons[i][j].setText(down);
						moleThreads[i][j].interrupt();
					}
					if(buttons[i][j].getText() == down
							&& buttons[i][j].getBackground() == moleDown){
						missScore++;
						missText.setText("" + missScore);
					}else{

					}
				}

			}
		}
	}
}
