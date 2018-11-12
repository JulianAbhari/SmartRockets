package com.Julian.smartRockets;

import com.Julian.math.JMath;
import com.Julian.shapes.JVector;
import com.Julian.window.JWindow;

/**
 * Smart Rockets 11/26/16
 * 
 * @author Julian Abhari
 */

public class GeneticRockets implements Runnable {

	public static float maxFitness = 0;
	public static JWindow window;
	public Population popul;
	public int count = 0;
	public int generation = 0;
	public boolean isRunning;

	public static final int POPULATION_SIZE = 20;
	public static final float MUTATION_RATE = (float) 0.01;
	public static final int TOURNAMENT_SELECTION_SIZE = 4;
	public static final int MAX_COUNT = 200;
	public static JVector END_POINT;
	public static JVector START_POINT;
	public static JVector OBS_POINT;

	// This constructor is instatiating the variables that influence the
	// graphics. Which is why the points have to be instantiated before the
	// population. The population needs to use these variables for the graphics.
	public GeneticRockets() {
		window = new JWindow(600, 600, "Smart Rockets");
		END_POINT = new JVector(window.getWidth() / 2, 100);
		START_POINT = new JVector(window.getWidth() / 2, (window.getHeight() - 100));
		OBS_POINT = new JVector(window.getWidth() / 2, window.getHeight() / 2);
		popul = new Population(POPULATION_SIZE);
	}

	// This function takes in a population and returns a mutated crossovered
	// version of that population.
	public Population evolvePopulation(Population population) {
		return mutatePopulation(crossoverPopulation(population));
	}

	// The mutatePopulation takes in a population and returns a mutated version
	// of that population.
	private Population mutatePopulation(Population population) {
		// This is creating a new population object which will have the given
		// population's rockets applied with mutation.
		Population mutatedPopulation = new Population(population.getRockets().length);
		// This is looping through the population's rockets and grapping that
		// rocket, mutating it, and adding it to the new population.
		for (int i = 0; i < population.getRockets().length; i += 1) {
			// This grabs the populations rocket, applies the mutateRocket
			// function to it, and adds the mutated version to the new
			// mutatedPopulation.
			mutatedPopulation.getRockets()[i] = mutateRocket(population.getRockets()[i]);
		}
		return mutatedPopulation;
	}

	// This mutateRocket function takes in a rocket, and returns the same rocket
	// however, within the mutation rate, there could be a chance that the new
	// rockets genes might be mutated.
	private Rocket mutateRocket(Rocket rocket) {
		// This is creating a new rocket which will get the given rockets genes
		// with some mutation.
		Rocket mutatedRocket = new Rocket();
		// This is looping through the given rockets genes, and if a random
		// number is <= the MUTATION_RATE then that gene[i] (which is a vector)
		// will have it's X and Y overriden by a randomNumber (between -1 and
		// 1).
		for (int i = 0; i < rocket.getGenes().length; i += 1) {
			if (Math.random() <= MUTATION_RATE) {
				// This is giving a mutated version of the given rockets genes
				// to the mutatedRocket. Since the genes are JVectors it
				// override the JVectors X and Y with a randomNumber. The
				// randomNumber function is found within the rocket class, so it
				// just calls the new rocket to input it's randomNumber output.
				mutatedRocket.getGenes()[i] = new JVector(mutatedRocket.randomNumber(), mutatedRocket.randomNumber());
			} else {
				// If no mutation is <= the MUTATION_RATE then the mutated
				// version of the given rocket's genes are just the rocket's
				// genes.
				mutatedRocket.getGenes()[i] = rocket.getGenes()[i];
			}
		}
		return mutatedRocket;
	}

	// This function takes in a population and returns a crossovered version of
	// that population.
	private Population crossoverPopulation(Population population) {
		// This is creating the crossoverPopulation, whose population size is
		// the same as the given population.
		Population crossoverPopulation = new Population(population.getRockets().length);
		// This maxFitness needs to be set to 0 everytime this function is
		// called, because if it wasn't then it would be the same as the last
		// generation's maxFitness, unless the new generation's fitness is
		// higher. Basically if the given generation has a lower maxFitness
		// than the previous, then it would still think that the maxFitness is
		// the maxFitness of the previous generation. The maxFitness variable is
		// a global variable because it will be called in other functions.
		maxFitness = 0;
		// This is looping through the given generation's rockets and seeing if
		// any of their fitness scores are higher than the maxFitness (which is
		// 0), if it is, then the maxFitness is set to that fitness.
		for (int i = 0; i < population.getRockets().length; i += 1) {
			if (population.getRockets()[i].getFitness() > maxFitness) {
				// This is declaring the maxFitness to be that rocket's fitness
				// score.
				maxFitness = population.getRockets()[i].getFitness();
			}
		}
		// This is looping through the given population's rockets and crossing
		// over two rockets from the matingPool.
		for (int i = 0; i < population.getRockets().length; i += 1) {
			// This is setting rocket1 to the best rocket out of the
			// matingPool's population.
			Rocket rocket1 = matingPoolByFitness(population).getRockets()[0];
			// This is setting rocket2 to the best rocket out of the
			// matingPool's population.
			Rocket rocket2 = matingPoolByFitness(population).getRockets()[0];
			// This setting the new crossoverPopulation's rocket at index i to
			// be a crossovered rocket of rocket1 and rocket2.
			crossoverPopulation.getRockets()[i] = crossoverRocket(rocket1, rocket2);
		}
		return crossoverPopulation;
	}

	// This function returns a rocket but takes in two rockets.
	private Rocket crossoverRocket(Rocket rocket1, Rocket rocket2) {
		// This creates new rocket whose genes will be overriden by the rocket1
		// and/or rocket2 genes.
		Rocket crossoverRocket = new Rocket();
		// This is looping through the length of rocket1 (and rocket2)'s genes.
		for (int i = 0; i < rocket1.getGenes().length; i += 1) {
			// If a random number between 0.0 - 1.0 is < 0.5, then the new
			// rocket will have rocket1's genes. Otherwise it will have
			// rocket2's genes.
			if (Math.random() < 0.5) {
				crossoverRocket.getGenes()[i] = rocket1.getGenes()[i];
			} else {
				crossoverRocket.getGenes()[i] = rocket2.getGenes()[i];
			}
		}
		return crossoverRocket;
	}

	// This function returns a population and takes in a population. This is the
	// natural selection of the program. What it does is it picks out a random
	// Rocket from the population, and thinks of a random number between
	// 0-maxFitness. If the rocket's fitness score is >= to the randomNumber
	// than that rocket will go in the population.
	private Population matingPoolByFitness(Population population) {
		// This is creating a new matingPool population of the TOURNAMENT_SIZE.
		Population matingPool = new Population(TOURNAMENT_SELECTION_SIZE);
		// This loops through the new population and testing the randomly picked
		// rockets. If a rocket has succesfully passed the test than the it's
		// added to the pool and the while loop stops, but while it hasn't it
		// will be picking a new rocket and testing it.
		for (int i = 0; i < TOURNAMENT_SELECTION_SIZE; i += 1) {
			// This is picking a randomRocket by mapping the randomNumber
			// between 0-1 to 0-population's size.
			Rocket randomRocket = population.getRockets()[(int) (JMath.map((float) Math.random(), 0, 1, 0,
					population.getRockets().length - 1))];
			// This is picking out a randomNumber between 0-1 and mapping it to
			// 0-maxFitness
			float randomNumber = JMath.map((float) Math.random(), 0, 1, 0, maxFitness);
			// While it hasn't picked out a rocket that passed the test it will
			// pick a new rocket and keep testing, however if it does
			// successfully pick out a rocket then that rocket will be added to
			// the mating pool and the while loop will stop.
			while (true) {
				// This is testing the randomRocket
				if (randomNumber <= randomRocket.getFitness()) {
					// This is adding the rocket to the new population
					matingPool.getRockets()[i] = randomRocket;
					// This stopping the loop if the randomRocket passed the if
					// statement's conditional.
					break;
				}
				// This is picking out a new randomRocket
				randomRocket = population.getRockets()[(int) (JMath.map((float) Math.random(), 0, 1, 0,
						population.getRockets().length))];
			}
		}
		// This is sorting the rocket so the crossoverPopulation picks the best
		// rocket that passed the test out of the population's size (or
		// TOURNAMENT_SIZE which as of now is 4).
		matingPool.sortRocketsByFitness();
		return matingPool;
	} 

	// Every time this method is called the population's run method is called
	// and the population is sorted and count increases.
	public void update() {
		popul.run(window);
		popul.sortRocketsByFitness();
		count += 1;

		// While the count is >= the MAX_COUNT (200), that means that the
		// population's rockets have successfully reached the end of their genes
		// and it means that this generation is over.
		while (count >= MAX_COUNT) {
			// The stats for the generations are then printed to the console.
			System.out.println("-------------------------------------------------");
			System.out
					.println("Generation # " + generation + " | Fittest Rocket: " + popul.getRockets()[0].getFitness());
			System.out.println("-------------------------------------------------");
			for (int i = 0; i < popul.getRockets().length; i += 1) {
				System.out.println("Rocket # " + i + " Fitness : " + popul.getRockets()[i].getFitness());
			}
			generation += 1;
			count = 0;
			// This takes the prevous population and sets it to a new evolved
			// version of that population.
			popul = evolvePopulation(popul);
		}
	}

	// This run method is using an algorithm to run the program 1/60th a second
	// or tick. This program could run as fast as the processor can handle but
	// that would make the processor very hot and the program wuld run extremely
	// fast then slow down, which it still does for some computers but this
	// should be a more fixed running experience.
	public void run() {
		isRunning = true;
		long lastTime = System.nanoTime();
		double nsPerTick = 1000000000.0 / 60.0;

		long lastTimer = System.currentTimeMillis();
		double delta = 0;

		while (isRunning) {
			long now = System.nanoTime();
			delta += (now - lastTime) / nsPerTick;
			lastTime = now;
			boolean shouldUpdate = false;

			while (delta >= 1) {
				delta -= 1;
				shouldUpdate = true;
			}

			try {
				Thread.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// When it's time for the processor to update/render the whole
			// program, it will call the update function. The update function
			// moves the ball and handles whenever it has hits an edge of the
			// screen.
			if (shouldUpdate == true) {
				update();
				shouldUpdate = false;
			}

			if (System.currentTimeMillis() - lastTimer >= 1000) {
				lastTimer += 1000;
			}
		}
	}

	// This sets isRunning to false so the while loop in the run method doesn't
	// crash.
	public void stop() {
		isRunning = false;
	}

	public static void main(String[] args) {
		new GeneticRockets().run();
	}
}