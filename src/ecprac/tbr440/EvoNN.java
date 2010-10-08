package ecprac.tbr440;

import org.neuroph.core.NeuralNetwork;

public class EvoNN extends NeuralNetwork {
       
    EvoNN() {
        //TODO
        //initialize
        //create neurons
        //make conncetions
        //give random weights
    }
    /**
    * Create a child by mutation
    */
    public EvoNN mutate(int mutationRate) {
        //TODO
        return this;
    }
    
    /**
    * Create a child by crossover with another Neural Network
    */
    public EvoNN crossOver(EvoNN partner) {
        //TODO
        return this;
    }
}
