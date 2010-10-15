package ecprac.tbr440;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.Layer;
import org.neuroph.core.Neuron;
import org.neuroph.util.NeuronProperties;

import java.util.Random;

public class EvoNN extends NeuralNetwork {
    

    private Layer inputLayer;
    private Layer outputLayer;
    private Layer hiddenLayer;

    private Neuron angleInputNeuron;
    private Neuron[] trackInputNeurons;
    private Neuron speedXInputNeuron;
    private Neuron speedYInputNeuron;

    private Neuron accelerateOutputNeuron;
    private Neuron steeringOutputNeuron;

    EvoNN() {
        setupInputLayer();
        setupHiddenLayer();
        setupOutputLayer();
        connect();
        randomizeWeights();
        debugPrintWeights();
    }

    /**
    * mutates a child with double probability per weight
    * -probability must be between 0 and 1
    *
    *  pWeights: propability a weight gets mutated
    *  sigmaWeigts: standardeviation of the Gaussian mutation of weights
    *  pAddN, pRemN, pAddC, pRemC: propability of an strucutural mutation
    */
    public void mutate(double pWeights, double sigmaWeights, double pAddN,
            double pRemN, double pAddC, double pRemC) {
        mutateWeights(pWeights, sigmaWeights);
        mutateStructure(pAddN, pRemN, pAddC, pRemC);
    }

    /**
    * Creates one child by whole arithmetic crossover with another Neural Network
    * see page 51 of book or slides of chapter 3
    * - double alpha must be between 0 and 1.
    */
    public EvoNN crossOver(EvoNN partner, double alpha) {
        //TODO
        return this;
    }
    
    /**
     /* Calculates the neural network after the input neurons are set
     */
    public void calculate() {
        for (Neuron n : inputLayer.getNeurons()) {
            n.calculate();
        }
        for (Neuron n : hiddenLayer.getNeurons()) {
            n.calculate();
        }
        for (Neuron n : outputLayer.getNeurons()) {
            n.calculate();
        }
    }
    
    /**
     * get an output from the network
     */
    public int getAccelerate() {
        double acc = accelerateOutputNeuron.getOutput();
        //System.out.printf("getacc %f\n", acc);
        //System.out.printf("speed %f\n", speedInputNeuron.getOutput());
        if (acc < 0) return 1; else return 0;
    }
    public double getSteering() {
        double steering = steeringOutputNeuron.getNetInput();
        return Math.sin(steering);
    }
    
    /**
     * set an input neuron
     */
    public void setSpeedX(double speedX) {
        //preprocess?
        speedXInputNeuron.setInput(speedX);
    }
    public void setSpeedY(double speedY) {
        //preprocess?
        speedYInputNeuron.setInput(speedY);
    }
    public void setTrack(int[] trackSensors) {
        //preprocess?
        for (int i=0; i < trackSensors.length; i++) {
            trackInputNeurons[i].setInput(trackSensors[i]);
        }
    }
    public void setAngle(double angle) {
        //preprocess?
        angleInputNeuron.setInput(angle);
    }

    // DEBUG FUNCTIONS
    public void debugPrintWeights() {
        for (Neuron n : inputLayer.getNeurons()) {
            for (org.neuroph.core.Weight w : n.getWeightsVector()) {
                System.out.printf("i:%f", w.getValue());
            }
            System.out.printf("\n");
        }
        for (Neuron n : hiddenLayer.getNeurons()) {
            for (org.neuroph.core.Weight w : n.getWeightsVector()) {
                System.out.printf("h%n:%f", w.getValue());
            }
            System.out.printf("\n");
        }
        for (Neuron n : outputLayer.getNeurons()) {
            for (org.neuroph.core.Weight w : n.getWeightsVector()) {
                System.out.printf("o:%f", w.getValue());
            }
            System.out.printf("\n");
        }
    }
    public void debugPrintValues() {
        for (Neuron n : inputLayer.getNeurons()) {
            System.out.printf("%f : %f", n.getNetInput(), n.getOutput());
        }
        System.out.printf("\n");
        for (Neuron n : hiddenLayer.getNeurons()) {
            System.out.printf("%f : %f", n.getNetInput(), n.getOutput());
        }
        System.out.printf("\n");
        for (Neuron n : outputLayer.getNeurons()) {
            System.out.printf("%f:%f; ", n.getNetInput(), n.getOutput());
        }
        System.out.printf("\n");
    }
    private void setupInputLayer() {
        NeuronProperties props = new NeuronProperties(
            org.neuroph.util.WeightsFunctionType.WEIGHTED_INPUT,
            org.neuroph.util.SummingFunctionType.SUM,
            org.neuroph.util.TransferFunctionType.LINEAR);
        inputLayer = new Layer(23, props);
        angleInputNeuron = inputLayer.getNeuronAt(0);
        speedXInputNeuron = inputLayer.getNeuronAt(1);
        speedYInputNeuron = inputLayer.getNeuronAt(2);
        trackInputNeurons = new Neuron[20];
        for(int i=0; i<20; i++) {
            trackInputNeurons[i] = inputLayer.getNeuronAt(i+3);
        }
        setInputNeurons(inputLayer.getNeurons());
    }

    private void setupOutputLayer() {
        outputLayer = new Layer();
        accelerateOutputNeuron = new Neuron(
            new org.neuroph.core.input.InputFunction(
                new org.neuroph.core.input.WeightedInput(),
                new org.neuroph.core.input.Sum()),
            new org.neuroph.core.transfer.Sgn());
        outputLayer.addNeuron(accelerateOutputNeuron);
        setOutputNeurons(outputLayer.getNeurons());
    }

    private void setupHiddenLayer() {
        hiddenLayer = new Layer();
        Neuron n = new EvoNeuron(
            new org.neuroph.core.input.InputFunction(
                new org.neuroph.core.input.WeightedInput(),
                new org.neuroph.core.input.Sum()),
            new org.neuroph.core.transfer.Sgn());
        hiddenLayer.addNeuron(n);
    }

    private void connect() {
        for (Neuron inputNeuron : inputLayer.getNeurons()) {
            for (Neuron hiddenNeuron : hiddenLayer.getNeurons()) {
                hiddenNeuron.addInputConnection(inputNeuron);
            }
        }
        for (Neuron hiddenNeuron : hiddenLayer.getNeurons()) {
            for (Neuron outputNeuron : outputLayer.getNeurons()) {
                outputNeuron.addInputConnection(hiddenNeuron);
            }
        }
    }


    private void mutateStructure(double pAddN, double pRemN, double pAddC, double pRemC) {
    	Random r = new Random();
        if (r.nextDouble() < pAddN) {
            addNeuron();
        }
        if (r.nextDouble() < pRemN) {
            removeNeuron();
        }
        if (r.nextDouble() < pAddC) {
            addConnection();
        }
        if (r.nextDouble() < pRemC) {
            removeConnection();
        }
    }

    private void removeNeuron() {
        //TODO
    }

    private void addConnection() {
        randomOutputNeuron().addInputConnection(randomInputNeuron());
    }

    private void removeConnection() {
        //randomOutputNeuron.
    }

    private void addNeuron() {
        Neuron n = new Neuron(
            new org.neuroph.core.input.InputFunction(
                new org.neuroph.core.input.WeightedInput(),
                new org.neuroph.core.input.Sum()),
            new org.neuroph.core.transfer.Sgn());
        hiddenLayer.addNeuron(n);
        n.addInputConnection(randomInputNeuron());
        randomOutputNeuron().addInputConnection(n);
    }

    private Neuron randomInputNeuron() {
    	Random r = new Random();
        int neuronNumber = r.nextInt(hiddenLayer.getNeuronsCount() + inputLayer.getNeuronsCount());
        if(neuronNumber < hiddenLayer.getNeuronsCount()) {
            return hiddenLayer.getNeuronAt(neuronNumber);
        } else {
            return inputLayer.getNeuronAt(neuronNumber-hiddenLayer.getNeuronsCount());
        }
    }

    private Neuron randomOutputNeuron() {
    	Random r = new Random();
        int neuronNumber = r.nextInt(hiddenLayer.getNeuronsCount() + outputLayer.getNeuronsCount());
        if(neuronNumber < hiddenLayer.getNeuronsCount()) {
            return hiddenLayer.getNeuronAt(neuronNumber);
        } else {
            return outputLayer.getNeuronAt(neuronNumber-hiddenLayer.getNeuronsCount());
        }
    }

    private void mutateWeights(double probability, double sigma) {     
    	Random r = new Random();
    	double mutation = 0;
    	
        for (Neuron n : inputLayer.getNeurons()) {
            for (org.neuroph.core.Weight w : n.getWeightsVector()) {            	
            	if (r.nextDouble() < probability) {
            		mutation = w.getValue()+(r.nextGaussian()*sigma); 
            		System.out.print("mutation from i: " + w.getValue()); //debug
            		w.setValue(mutation);
            		System.out.println(" to " + w.getValue()); //debug
            	}
            }
        }
        
        for (Neuron n : hiddenLayer.getNeurons()) {
            for (org.neuroph.core.Weight w : n.getWeightsVector()) {
            	if (r.nextDouble() < probability) {
            		mutation = w.getValue()+(r.nextGaussian()*sigma); 
            		System.out.print("mutation from h: " + w.getValue()); //debug
            		w.setValue(mutation);
            		System.out.println(" to " + w.getValue()); //debug
            	}
            }
        } 
        
        for (Neuron n : outputLayer.getNeurons()) {
            for (org.neuroph.core.Weight w : n.getWeightsVector()) {            	
            	if (r.nextDouble() < probability) {            		
            		mutation = w.getValue()+(r.nextGaussian()*sigma); 
            		System.out.print("mutation from 0: " + w.getValue()); //debug
            		w.setValue(mutation);
            		System.out.println(" to " + w.getValue()); //debug
            	}
            }
        }        
    }
    
            
}
