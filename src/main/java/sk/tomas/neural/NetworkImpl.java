package sk.tomas.neural;

import java.util.ArrayList;
import java.util.List;

public class NetworkImpl implements Network {

    private List<List<Neural>> network;

    public NetworkImpl() {
        init();
        randomizeWeights();
        addBias();
    }

    private void init() {
        network = new ArrayList<>();
        for (int i = 0; i < Config.networkSize.length; i++) {
            List<Neural> layer = new ArrayList<>();
            for (int j = 0; j < Config.networkSize[i]; j++) {
                //input layer does not have ancestors
                List<NeuralInput> neuralInputs = new ArrayList<>();
                if (i < 1) {
                    neuralInputs.add(new NeuralInput(null));
                } else {
                    for (int k = 0; k < Config.networkSize[i - 1]; k++) {
                        neuralInputs.add(new NeuralInput(network.get(i - 1).get(k)));
                    }
                }
                layer.add(new Neural(neuralInputs));
            }
            network.add(layer);
        }
    }

    private void randomizeWeights() {
        for (List<Neural> innerList : network) {
            for (Neural neural : innerList) {
                neural.randomizeWeights();
            }
        }
    }

    private void addBias() {
        for (int i = 1; i < Config.networkSize.length; i++) {
            addBias(i);
        }
    }

    private void addBias(int layerNumber) {
        Neural bias = new Neural(new ArrayList<>());
        for (Neural neural : network.get(layerNumber)) {
            neural.addBias(bias);
        }

    }

    @Override
    public void teach(double[] input, double[] expected) throws InputException {
        if (Config.networkSize[0] != input.length) {
            throw new InputException("wrong input length");
        }
        for (int i = 0; i < network.get(0).size(); i++) {
            network.get(0).get(i).setLastValue(input[i]);
        }
        double[] run = run();
        for (double v : run) {
            System.out.print(v + " ");
        }
    }

    private double[] run() {
        for (int i = 1; i < network.size(); i++) {
            for (Neural neural : network.get(i)) {
                for (NeuralInput neuralInput : neural.getInputs()) {
                    neuralInput.setX(neuralInput.getAncestor().getLastValue());
                }
                neural.forwardPropagation();
            }
        }
        double[] result = new double[Config.networkSize[Config.networkSize.length - 1]];
        for (int i = 0; i < result.length; i++) {
            result[i] = network.get(network.size() - 1).get(i).getLastValue();
        }
        return result;
    }

}