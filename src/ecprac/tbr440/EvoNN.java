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
    private static final int NR_HIDDENLAYER_NEURONS = 10;

    private Neuron speedInputNeuron;

    private Neuron accelerateOutputNeuron;

    EvoNN() {
        System.out.println("create NN; ");
        setupInputLayer();
        NeuronProperties hiddenLayerProperties = new NeuronProperties(
            org.neuroph.util.WeightsFunctionType.WEIGHTED_INPUT,
            org.neuroph.util.SummingFunctionType.SUM,
            org.neuroph.util.TransferFunctionType.STEP);
        hiddenLayer = new Layer(NR_HIDDENLAYER_NEURONS, hiddenLayerProperties);
        setupOutputLayer();
        connect();
        initializeWeights(-1., 1.);
        randomizeWeights();
        debugPrintWeights();
        System.out.println("NN created\n");
    }

    private void setupInputLayer() {
        NeuronProperties props = new NeuronProperties(
            org.neuroph.util.WeightsFunctionType.WEIGHTED_INPUT,
            org.neuroph.util.SummingFunctionType.SUM,
            org.neuroph.util.TransferFunctionType.LINEAR);
        inputLayer = new Layer(1, props);
        speedInputNeuron = inputLayer.getNeuronAt(0);
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
        NeuronProperties props = new NeuronProperties(
            org.neuroph.util.WeightsFunctionType.WEIGHTED_INPUT,
            org.neuroph.util.SummingFunctionType.SUM,
            org.neuroph.util.TransferFunctionType.LINEAR);
        hiddenLayer = new Layer(NR_HIDDENLAYER_NEURONS, props);
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

    public void debugPrintWeights() {
        for (Neuron n : inputLayer.getNeurons()) {
            for (org.neuroph.core.Weight w : n.getWeightsVector()) {
                System.out.printf("i:%f", w.getValue());
            }
            System.out.printf("\n");
        }
        for (Neuron n : hiddenLayer.getNeurons()) {
            for (org.neuroph.core.Weight w : n.getWeightsVector()) {
                System.out.printf("h:%f", w.getValue());
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
    /**
    * mutates a child with double probability per weight
    * -probability must be between 0 and 1
    */
    public void mutate(double probability) {
        //TODO: decide on value of change of weight: probabilistic?
    	//      
    	Random r = new Random();
    	double mutation = 0;
    	
        for (Neuron n : inputLayer.getNeurons()) {
            for (org.neuroph.core.Weight w : n.getWeightsVector()) {            	
            	if (r.nextInt(100) < (int)(probability*100)){
            		mutation = w.getValue()+ 0.05; 
            		System.out.print("mutation from i: " + w.getValue()); //debug
            		w.setValue(mutation);
            		System.out.println(" to " + w.getValue()); //debug
            	}
            }
        }
        
        for (Neuron n : hiddenLayer.getNeurons()) {
            for (org.neuroph.core.Weight w : n.getWeightsVector()) {
            	if (r.nextInt(100) < (int)(probability*100)){
            		mutation = w.getValue()+ 0.05; 
            		System.out.print("mutation from h: " + w.getValue()); //debug
            		w.setValue(mutation);
            		System.out.println(" to " + w.getValue()); //debug
            	}
            }
        } 
        
        for (Neuron n : outputLayer.getNeurons()) {
            for (org.neuroph.core.Weight w : n.getWeightsVector()) {            	
            	if (r.nextInt(100) < (int)(probability*100)){            		
            		mutation = w.getValue()+ 0.05; 
            		System.out.print("mutation from 0: " + w.getValue()); //debug
            		w.setValue(mutation);
            		System.out.println(" to " + w.getValue()); //debug
            	}
            }
        }        
    }
    
    /**
    * Creates one child by whole arithmetic crossover with another Neural Network
    * see page 51 of book or slides of chapter 3
    * - double alpha must be between 0 and 1.
    */
    public EvoNN crossOver(EvoNN partner, double alpha) {
        //TODO improve: 3 EvoNN objects has to be iterated at once: this, partner and result
    	
    	EvoNN result = new EvoNN();
    	double recombination = 0;
    	
        for (Neuron n : inputLayer.getNeurons()) {
            for (org.neuroph.core.Weight w : n.getWeightsVector()) {
            	recombination = w.getValue()*alpha + (1-alpha)*partner.w.getValue(); 
            	result.w.setValue(recombination);
            }
        }
        
        for (Neuron n : hiddenLayer.getNeurons()) {
            for (org.neuroph.core.Weight w : n.getWeightsVector()) {
            	recombination = w.getValue()*alpha + (1-alpha)*partner.getValue(); 
            	result.w.setValue(recombination);
            }
        }
        
        for (Neuron n : outputLayer.getNeurons()) {
            for (org.neuroph.core.Weight w : n.getWeightsVector()) {
            	recombination = w.getValue()*alpha + (1-alpha)*partner.w.getValue(); 
            	result.w.setValue(recombination);
            }
        } 
        
        return result;
    }
            
    public void calculate() {
        for (Neuron n : inputLayer.getNeurons()) {
            //n.calculate();
        }
        for (Neuron n : hiddenLayer.getNeurons()) {
            n.calculate();
        }
        for (Neuron n : outputLayer.getNeurons()) {
            n.calculate();
        }
    }

    public int getAccelerate() {
        double acc = accelerateOutputNeuron.getNetInput();
        //System.out.printf("getacc %f\n", acc);
        //System.out.printf("speed %f\n", speedInputNeuron.getOutput());
        if (acc < 0) return 1; else return 0;
    }
    
    public void setSpeed(double speed) {
        //preprocess?
        speedInputNeuron.setOutput(speed);
    }
}
