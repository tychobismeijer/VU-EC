package ecprac.tbr440;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.Layer;
import org.neuroph.core.Neuron;
import org.neuroph.nnet.comp.BiasNeuron;
import org.neuroph.util.NeuronProperties;
import org.neuroph.core.Connection;

import java.util.Random;


public class EvoNN extends NeuralNetwork{
    
    static final long serialVersionUID = 2002396243941905560L;
    private Layer inputLayer;
    private Layer outputLayer;
    private Layer hiddenLayer;

    private Neuron angleInputNeuron;
    private Neuron[] trackInputNeurons;
    //private Neuron[] opponentInputNeurons;
    private Neuron trackPositionInputNeuron;
    private Neuron speedXInputNeuron;
    private Neuron speedYInputNeuron;

    private Neuron accelerateOutputNeuron;
    private Neuron steeringOutputNeuron;
    //private Neuron breakOutputNeuron;

    private Neuron biasNeuron = new BiasNeuron();
	
	private EvoNN parent;
    
    final static int NR_OF_TRACK_SENSORS = 19;
    				 //NR_OF_OPPONENT_SENSORS = 36;

    EvoNN() {
        super();
        setupInputLayer();
        setupHiddenLayer();
        setupOutputLayer();
        connect();
        randomizeWeights();
        //debugPrintWeights();
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
    public double getAccelerate() {
        double acc = accelerateOutputNeuron.getOutput();
        //System.out.printf("getacc %f\n", acc);
        //System.out.printf("speed %f\n", speedInputNeuron.getOutput());
        //System.out.println("Accelerate output is: " + acc); //debug
        return (Math.sin(acc)+1)/2;
    }
    public double getSteering() {
        double steering = steeringOutputNeuron.getNetInput();
        
        //System.out.println("steering output is: " + steering + " steering output Math.sin: " + Math.sin(steering)); //debug
        return Math.sin(steering);
    }
    
    
    /*public double getBreak() {
        double breaking = breakOutputNeuron.getNetInput();
       
        //System.out.println("steering output is: " + steering + " steering output Math.sin: " + Math.sin(steering)); //debug
        if(breaking < 0) return 0;
        else return 1;
    }*/
    
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
    public void setTrack(double[] trackSensors) {
        //preprocess?
        for (int i=0; i < trackSensors.length; i++) {
            trackInputNeurons[i].setInput(trackSensors[i]);
        }
    }
    /*public void setOpponent(double[] opponentSensors) {
        //preprocess?
        for (int i=0; i < opponentSensors.length; i++) {
        	opponentInputNeurons[i].setInput(opponentSensors[i]);
        }
    } */   
    public void setAngle(double angle) {
        //preprocess?
        angleInputNeuron.setInput(angle);
    }    
    public void setTrackPosition(double trackPosion) {
        //preprocess?
    	trackPositionInputNeuron.setInput(trackPosion);
    }
	
	public void setPossibleParent(EvoNN parent) {
        if (similarity(parent) > 10) {
            this.parent = parent;
        } else {
            this.parent = this;
        }
    }

    //**
    // Compatability distance
    // from NEAT
    // Evolving Neural Networks through Augmenting Topologies by Kenneth O.
    // Stanly and Risto Miikkulainen. 2002. Evolutionary Computation 10(2)
    // pg. 110
    public double similarity(EvoNN other) {
        double c1 = 1.0;
        double c2 = 1.0;
        double c3 = 1.0;
        int E = Math.abs(this.getNeuronsCount() - other.getNeuronsCount()); 
        int N = this.getNeuronsCount();
        int J = 0;
        double W_total = 0;
        int W_n = 0;
        for (Neuron neuron : hiddenLayer.getNeurons()) {
            if (neuron instanceof EvoNeuron) {
            for (Neuron other_neuron : other.hiddenLayer.getNeurons()) {
                if ((other_neuron instanceof EvoNeuron) &&
                        ((EvoNeuron) neuron).structureId ==
                        ((EvoNeuron) other_neuron).structureId) {
                    for (int i=0; i<inputLayer.getNeuronsCount(); i++) {
                        Connection c = 
                            neuron.getConnectionFrom(inputLayer.getNeuronAt(i));
                        Connection c_other =
                            other_neuron.getConnectionFrom(other.inputLayer.getNeuronAt(i));
                        if (c != null && c_other != null) {
                            W_total = c.getWeight().getValue() -
                                c_other.getWeight().getValue();
                            W_n++;
                        }
                    }
                    for (int i=0; i<outputLayer.getNeuronsCount(); i++) {
                        Connection c = 
                            outputLayer.getNeuronAt(i).getConnectionFrom(neuron);
                        Connection c_other =
                            other.outputLayer.getNeuronAt(i).getConnectionFrom(other_neuron);
                        if (c != null && c_other != null) {
                            W_total = c.getWeight().getValue() -
                                c_other.getWeight().getValue();
                            W_n++;
                        }
                    }
                    J++;
                }
            }}
        }
        double W = W_total / W_n;
        int D = N - J;
        System.out.printf("D:%d E:%d W:%f", J, E, W);
        return ((c1*E)/N)+((c2*D)/N)+(c3*W);
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
	
	int getNeuronsCount() {
        return hiddenLayer.getNeuronsCount();
    }

    private void setupInputLayer() {
        NeuronProperties props = new NeuronProperties(
            org.neuroph.util.WeightsFunctionType.WEIGHTED_INPUT,
            org.neuroph.util.SummingFunctionType.SUM,
            org.neuroph.util.TransferFunctionType.LINEAR);
        inputLayer = new Layer(NR_OF_TRACK_SENSORS+4, props);
        angleInputNeuron = inputLayer.getNeuronAt(0);
        speedXInputNeuron = inputLayer.getNeuronAt(1);
        speedYInputNeuron = inputLayer.getNeuronAt(2);
        trackPositionInputNeuron = inputLayer.getNeuronAt(3);
        trackInputNeurons = new Neuron[NR_OF_TRACK_SENSORS]; 
        for(int i=0; i<NR_OF_TRACK_SENSORS; i++) {
            trackInputNeurons[i] = inputLayer.getNeuronAt(i+4);
        }
        /*opponentInputNeurons = new Neuron[NR_OF_OPPONENT_SENSORS];
        for(int i=0; i<NR_OF_OPPONENT_SENSORS; i++) {
        	opponentInputNeurons[i] = inputLayer.getNeuronAt(i+NR_OF_TRACK_SENSORS+4);
        }*/
        setInputNeurons(inputLayer.getNeurons());
        this.addLayer(inputLayer);
    }

    private void setupOutputLayer() {
        outputLayer = new Layer();
        accelerateOutputNeuron = new Neuron(
            new org.neuroph.core.input.InputFunction(
                new org.neuroph.core.input.WeightedInput(),
                new org.neuroph.core.input.Sum()),
            new org.neuroph.core.transfer.Linear());
        steeringOutputNeuron = new Neuron(
            new org.neuroph.core.input.InputFunction(
                new org.neuroph.core.input.WeightedInput(),
                new org.neuroph.core.input.Sum()),
            new org.neuroph.core.transfer.Linear());
        /*breakOutputNeuron = new Neuron(
            new org.neuroph.core.input.InputFunction(
                new org.neuroph.core.input.WeightedInput(),
                new org.neuroph.core.input.Sum()),
            new org.neuroph.core.transfer.Sgn());*/      
        outputLayer.addNeuron(accelerateOutputNeuron);
        outputLayer.addNeuron(steeringOutputNeuron);
        //outputLayer.addNeuron(breakOutputNeuron);
        setOutputNeurons(outputLayer.getNeurons());
        this.addLayer(outputLayer);
    }

    private void setupHiddenLayer() {
        hiddenLayer = new Layer();
        Neuron n = new EvoNeuron(
            new org.neuroph.core.input.InputFunction(
                new org.neuroph.core.input.WeightedInput(),
                new org.neuroph.core.input.Sum()),
            new org.neuroph.core.transfer.Sgn());
        hiddenLayer.addNeuron(n);
        this.addLayer(hiddenLayer);
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
            hiddenNeuron.addInputConnection(biasNeuron);
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
        //TODO
    }

    private void addNeuron() {
        Neuron n = new Neuron(
            new org.neuroph.core.input.InputFunction(
                new org.neuroph.core.input.WeightedInput(),
                new org.neuroph.core.input.Sum()),
            new org.neuroph.core.transfer.Sgn());
        hiddenLayer.addNeuron(n);
        n.addInputConnection(randomInputNeuron());
        n.addInputConnection(biasNeuron);
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
            		//System.out.print("mutation from i: " + w.getValue()); //debug
            		w.setValue(mutation);
            		//System.out.println(" to " + w.getValue()); //debug
            	}
            }
        }
        
        for (Neuron n : hiddenLayer.getNeurons()) {
            for (org.neuroph.core.Weight w : n.getWeightsVector()) {
            	if (r.nextDouble() < probability) {
            		mutation = w.getValue()+(r.nextGaussian()*sigma); 
            		//System.out.print("mutation from h: " + w.getValue()); //debug
            		w.setValue(mutation);
            		//System.out.println(" to " + w.getValue()); //debug
            	}
            }
        } 
        
        for (Neuron n : outputLayer.getNeurons()) {
            for (org.neuroph.core.Weight w : n.getWeightsVector()) {            	
            	if (r.nextDouble() < probability) {            		
            		mutation = w.getValue()+(r.nextGaussian()*sigma); 
            		//System.out.print("mutation from 0: " + w.getValue()); //debug
            		w.setValue(mutation);
            		//System.out.println(" to " + w.getValue()); //debug
            	}
            }
        }
    }
}
