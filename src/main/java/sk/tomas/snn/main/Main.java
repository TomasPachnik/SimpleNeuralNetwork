package sk.tomas.snn.main;

import sk.tomas.snn.core.NeuralNetwork;

public class Main {

    public static void main(String[] args) {
        NeuralNetwork neuralNetwork = new NeuralNetwork();

        neuralNetwork.randomizeWeights();
        neuralNetwork.run(0, 1);
    }

}
