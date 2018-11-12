package com.Julian.smartRockets;

import java.util.Random;

import com.Julian.math.JMath;
import com.Julian.shapes.JRect;
import com.Julian.shapes.JVector;

public class Rocket {
	JVector location;
	JVector velocity;
	JVector acceleration;
	JRect rocket;
	Random ran = new Random();
	JVector[] genes;
	int count = 0;
	float rocketFitness;

	// This rocket constructor is instantiating the velocity, acceleration,
	// location, and genes.
	public Rocket() {
		velocity = new JVector(0, 0);
		acceleration = new JVector(0, 0);
		location = new JVector(GeneticRockets.START_POINT.getX(), GeneticRockets.START_POINT.getY());
		// This is setting the genes array to the MAX_COUNT of the GeneticRocket
		// class.
		genes = new JVector[GeneticRockets.MAX_COUNT];
		// This loops through the genes array instantiating the array[i] to a
		// new JVector object, whose X is the output of the randomNumber
		// function, and whose Y is also a new output of the randomNumber
		// function.
		for (int i = 0; i < genes.length; i += 1) {
			genes[i] = new JVector(randomNumber(), randomNumber());
		}
	}

	// This function takes in nothing and outputs a randomNumber between -1 to
	// 1.
	public float randomNumber() {
		// This is setting ranNum to a random number between 0-1.
		float ranNum = (float) Math.random();
		// This is setting ranFiftyNum to a random int from 0-1.
		// This has a 50% chance of 0 or 1.
		int ranFiftyNum = ran.nextInt(2);
		// if the ranFiftyNum is 0 than the ranNum will be negative, otherwise
		// positive.
		if (ranFiftyNum == 0) {
			return ranNum * -1;
		} else {
			return ranNum;
		}
	}

	// This function calculates the rocket's fitness and returns it's fitness.
	public float calculateFitness() {
		// This finds the distance from the rocket's location from the END_POINT
		// by caling the JMath dist function.
		float distance = JMath.dist(location.getX(), location.getY(), GeneticRockets.END_POINT.getX(),
				GeneticRockets.END_POINT.getY());
		// This takes the distance and maps it from a range of 0-600 to the
		// inverted value of 600-0. If any rocket's distance is above 600, or
		// below the new mapped value, 0, than it is set to 1 to get rid of any
		// negative numbers.
		float rocketFitness = JMath.map(distance, 0, GeneticRockets.window.getWidth(), GeneticRockets.window.getWidth(),
				0);
		// If the rocket's fitness is below 0 than it's set to 1 so that if
		// there's a generation where all rocket's fitness scores are negative,
		// it would still be able to work.
		if (rocketFitness < 0) {
			rocketFitness = 1;
		}
		// If the rocket has crashed then it's score is divided by 10.
		if (hasCrashed()) {
			rocketFitness /= 10;
		}
		return rocketFitness;
	}

	// This sets the global variable rocketFitness to the actual calculated
	// fitness and is returned.
	public float getFitness() {
		this.rocketFitness = calculateFitness();
		return rocketFitness;
	}

	// This returns the genes[].
	public JVector[] getGenes() {
		return this.genes;
	}

	// This returns the JComponent Rocket rectangle object. The point is so that
	// the JBackground can add the rocket (JComponent rectangle) to the
	// background.
	public JRect getRocket() {
		return rocket;
	}

	// This tells whether or not the rocket has crashed into the obsticle.
	public boolean hasCrashed() {
		if (location.getX() >= GeneticRockets.OBS_POINT.getX() - 150
				&& location.getX() <= GeneticRockets.OBS_POINT.getX() + 150
				&& location.getY() >= GeneticRockets.OBS_POINT.getY() - 10
				&& location.getY() <= GeneticRockets.OBS_POINT.getY() + 10) {
			return true;
		}
		return false;
	}

	// This tells whether the rocket has completed by seeing if it is near the
	// maximum fitness minus 20 or if the count is >= the MAX_COUNT.
	public boolean hasCompleted() {
		if (calculateFitness() >= GeneticRockets.window.getWidth() - 20) {
			return true;
		} else if (count >= GeneticRockets.MAX_COUNT) {
			return true;
		}
		return false;
	}

	// This tells whether the rocket has completed or crahsed, if it hasn't done
	// either than it will update.
	public void update() {
		if (!hasCompleted() && !hasCrashed()) {
			// It first adds the force, which is the genes.getX and the
			// genes.getY (which is a number between -1 and 1) to the
			// acceleration vector.
			addForce(genes[count].getX(), genes[count].getY());
			// After it adds the genes to the acceleration, it adds them to the
			// velocity, which is hen added the location.
			velocity.add(acceleration.getX(), acceleration.getY());
			location.add(velocity.getX(), velocity.getY());
			// Lastly the acceleration is cleared but the velocity isn't. So the
			// velocity keeps gaining momentom from the acceleration. But the
			// acceleration could be negative or positive.
			acceleration.mult(0, 0);
			// The count is then increased because once this update method is
			// completed, then the next gene in the gene[] is used. Since the
			// gene is adding the count which is the current position in the
			// genes.
			count += 1;
		}
	}

	// This takes in a forceX and a forceY and adds them to the acceleration.
	public void addForce(double forceX, double forceY) {
		acceleration.add(forceX, forceY);
	}

	// This function will only be called in the population run method, because
	// it needs to constantly be reinstantiated everytimme a new tick is
	// reached. So it re draws a new rectnagle at the new location.x and
	// location.y.
	public void display() {
		rocket = new JRect((int) location.getX(), (int) location.getY(), 5, 25, 255, 255, 255, 170, true);
	}
}
