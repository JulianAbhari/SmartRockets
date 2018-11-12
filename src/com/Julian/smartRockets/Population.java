package com.Julian.smartRockets;

import java.awt.BorderLayout;

import com.Julian.shapes.JEllipse;
import com.Julian.shapes.JRect;
import com.Julian.window.JBackground;
import com.Julian.window.JWindow;

public class Population {
	int popSize;
	Rocket[] rockets;
	JBackground background;
	JEllipse target;
	JRect obsticle;

	public Population(int popSize) {
		// This is instantiating the target, obsticle, and rockets in the
		// constructor so that when the population object is instantiated in the
		// GeneticRockets class, the locations for these objects have also
		// already been instantiated.
		target = new JEllipse((int) GeneticRockets.END_POINT.getX(), (int) GeneticRockets.END_POINT.getY(), 30, 30, 255,
				255, 255, 255, true);
		obsticle = new JRect((int) GeneticRockets.OBS_POINT.getX(), (int) GeneticRockets.OBS_POINT.getY(), 300, 20, 255,
				255, 255, 255, true);
		rockets = new Rocket[popSize];
		// This is looping through the popSize and instantiating every rocket in
		// the rocket array.
		for (int i = 0; i < popSize; i += 1) {
			rockets[i] = new Rocket();
		}
	}

	public void run(JWindow window) {
		// This is creating a new JBackground everytime this method is called so
		// it re positon the rockets in the JPanel. Because the JBackground is
		// instantiated here and it will keep being re instantiated,
		// unfortunately that means that all solid object that won't be moving
		// also have to be added as if they were moving.
		background = new JBackground(0, 0, 0);
		background.addComponent(target, BorderLayout.CENTER);
		window.addComponent(background.getPanel());
		background.addComponent(obsticle, BorderLayout.CENTER);
		window.addComponent(background.getPanel());
		// What this is doing is displaying and updating the rockets in the
		// rocket array which is of size 'popSize'. The rockets are then added
		// to the background which is immediately added to the window.
		for (int i = 0; i < rockets.length; i += 1) {
			rockets[i].display();
			rockets[i].update();
			background.addComponent(rockets[i].getRocket(), BorderLayout.CENTER);
			window.addComponent(background.getPanel());
		}
	}

	// This is a simple bubble sort algorithm sorting from greatest to least.
	// What's going on is that first a for loop of int h has to run these two
	// other for loops to sort every index of the array
	// The array in this case is the 'chromosomes' array. In for loop of int i
	// it starts at the end of the array
	// to see if any value before that (at index j) might be smaller (might have
	// a worse fitness score)
	// in for loop of int j it stops at i because in for loop at i it has
	// already run j to see if index i is the smallest fitness score
	// basically the reason j stops at i is because i is already sorted so it
	// doesn't need to test the other values.
	// in for loop of int j it tests to see if the fitness score of j is < the
	// fitness score of i.
	// If the condition is true, it swaps the values.
	public void sortRocketsByFitness() {
		for (int i = rockets.length - 1; i > 0; i -= 1) {
			for (int j = 0; j < i; j += 1) {
				// The condition tests to see if the fitness score of j is <
				// the fitness score i. If the condition is true
				// it has to swap the values by storing one of the values in
				// a temporary variable.
				// I would like to point out that to be 100% certain
				// that there will be no errors,
				// you might want to TEST j and i
				// but you'd want to SWAP j and j + 1, not j and i.
				if (rockets[j].getFitness() < rockets[i].getFitness()) {
					Rocket temp = rockets[j];
					rockets[j] = rockets[i];
					rockets[i] = temp;
				}
			}
		}
	}

	public Rocket[] getRockets() {
		return rockets;
	}
}