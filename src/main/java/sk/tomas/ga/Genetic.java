package sk.tomas.ga;

import sk.tomas.neural.InputException;
import sk.tomas.neural.Network;

import java.util.Random;

public class Genetic {

    private final double CROSS_RATE = 0.7; //crossing probability 0.7 - 1.0
    private final double MUTATION_RATE = 0.05; //mutation of each gene probability
    private final int POPULATION_SIZE = 50; //number of individuals in population
    private final int HIDDEN_LAYER_DEEP = 1;
    private final int OUTPUT_LAYER_DEEP = 2;

    private Population population;

    public void run() throws InputException {
        Random crossingRandom = new Random();
        Random parentRandom = new Random();
        Random selectionRandom = new Random();
        Random mutationRandom = new Random();


        double bestScore = 0;
        Population newPopulation;
        while (bestScore < 200) {

            newPopulation = new Population();

            if (population == null) { //population zero
                population = new Population(POPULATION_SIZE, 2, 2, 1);
                population.execute();//calculate fitness of each individual
            }

            //TODO subtract from all fitnesses value of individual with minimal fitness value -> this individual will never be picked up

            //crossing
            for (int i = 0; i < population.getPopulation().size() / 2; i++) {
                Individual parent1 = selection(population, selectionRandom); //selection
                Individual parent2 = selection(population, selectionRandom); //selection
                System.out.print(parent1.getFitness() + " " + parent1.getFitness() + " ");
                Network[] children = cross(parent1.getNetwork(), parent2.getNetwork(), crossingRandom, parentRandom);
                //children[0] = mutate(children[0], mutationRandom);
                //children[1] = mutate(children[1], mutationRandom);
                newPopulation.getPopulation().add(new Individual(children[0]));
                newPopulation.getPopulation().add(new Individual(children[1]));
            }
            System.out.println();

            //mutation
            for (Individual individual : newPopulation.getPopulation()) {
                individual.setNetwork(mutate(individual.getNetwork(), mutationRandom));
            }

            newPopulation.execute();//calculate fitness of each individual

            bestScore++;
            System.out.println("before: " + population.getBest().getFitness() + " after: " + newPopulation.getBest().getFitness());
            newPopulation.getPopulation().set(0, population.getBest()); //elitism
            population = newPopulation;
        }
        Network best = population.getBest().getNetwork();

        double[] input1 = new double[]{0, 1};
        double[] input2 = new double[]{1, 0};

        System.out.println();
        System.out.println(best.run(input1)[0]);
        System.out.println(best.run(input2)[0]);

    }

    //roulette selection algorithm
    private Individual selection(Population population, Random selectionRandom) {
        double sum = 0;
        double fitnessPoint = selectionRandom.nextDouble() * population.getSumFitness();
        for (Individual individual : population.getPopulation()) {
            sum += individual.getFitness();
            if (sum > fitnessPoint) {
                return individual;
            }
        }
        throw new RuntimeException("wrong selection -> this should not happen at all");
    }

    private Network[] cross(Network parent1, Network parent2, Random crossingRandom, Random parentRandom) {
        Network child1 = parent1.getClone();
        Network child2 = parent1.getClone();
        Network[] children = new Network[]{child1, child2};

        if (crossingRandom.nextDouble() < CROSS_RATE) {
            children = cross(parent1, parent2, children, parent1.getHiddenLayer(), parent1.getInputLayer(), HIDDEN_LAYER_DEEP, parentRandom);
            children = cross(parent1, parent2, children, parent1.getOutputLayer(), parent1.getHiddenLayer(), OUTPUT_LAYER_DEEP, parentRandom);
        }

        return children;
    }

    private Network mutate(Network network, Random mutationRandom) {

        network = mutate(network, network.getHiddenLayer(), network.getInputLayer(), HIDDEN_LAYER_DEEP, mutationRandom);
        network = mutate(network, network.getOutputLayer(), network.getHiddenLayer(), OUTPUT_LAYER_DEEP, mutationRandom);

        return network;
    }

    private Network[] cross(Network parent1, Network parent2, Network[] children, int bottomLayer, int upperLayer, int bottomLayerDeep, Random parentRandom) {
        for (int i = 0; i < bottomLayer; i++) {
            for (int j = 0; j < upperLayer; j++) {
                if (parentRandom.nextBoolean()) { //get from parent1
                    children[0].setWeight(bottomLayerDeep, i, j, parent1.getWeight(bottomLayerDeep, i, j));
                    children[1].setWeight(bottomLayerDeep, i, j, parent2.getWeight(bottomLayerDeep, i, j));
                } else { //get from parent2
                    children[0].setWeight(bottomLayerDeep, i, j, parent2.getWeight(bottomLayerDeep, i, j));
                    children[1].setWeight(bottomLayerDeep, i, j, parent1.getWeight(bottomLayerDeep, i, j));
                }
            }
        }
        return children;
    }

    private Network mutate(Network network, int bottomLayer, int upperLayer, int bottomLayerDeep, Random mutationRandom) {
        double weight;
        for (int i = 0; i < bottomLayer; i++) {
            for (int j = 0; j < upperLayer; j++) {
                if (mutationRandom.nextDouble() < MUTATION_RATE) {
                    weight = network.getWeight(bottomLayerDeep, i, j);
                    weight += Util.normalDistributionForMutation();
                    if (weight >= -1 && weight <= 1) {
                        network.setWeight(bottomLayerDeep, i, j, weight);
                    }
                }
            }
        }
        return network;
    }

}
