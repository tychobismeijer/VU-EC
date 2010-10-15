package ecprac.tbr440;

import org.neuroph.core.Neuron;
import org.neuroph.core.input.InputFunction;
import org.neuroph.core.transfer.TransferFunction;
import java.util.Random;

public class EvoNeuron extends Neuron {
    int structureId;

    EvoNeuron() {
        super();
        Random r = new Random();
        structureId = r.nextInt();
    }
    EvoNeuron(InputFunction inputFunction, TransferFunction transferFunction) {
        super(inputFunction, transferFunction);
        Random r = new Random();
        structureId = r.nextInt();
    }
}
