package ecprac.tbr440;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.Layer;
import org.neuroph.core.Neuron;
import org.neuroph.util.NeuronProperties;

import java.util.Random;

public class EvoNeuron extends Neuron {
    int structureId;

    EvoNeuron() {
        Random r = new Random();
        structureId = r.nextInt();
        super();
    }
    EvoNeuron(InputFunction inputFunction, TransferFunction transferFunction) {
        Random r = new Random();
        structureId = r.nextInt();
        super(InputFunction inputFunction, TransferFunction transferFunction);
    }
}
