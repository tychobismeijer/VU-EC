package ecprac.tbr440;

import org.neuroph.core.Neuron;
import org.neuroph.core.input.InputFunction;
import org.neuroph.core.transfer.TransferFunction;
import java.util.Random;

public class EvoNeuron extends Neuron implements Cloneable{
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
    
    public Object clone(){
    	EvoNeuron clone;

        try{
            clone = (EvoNeuron) super.clone();
        }catch(CloneNotSupportedException e){
        	throw new Error("EvoNeuron not clonable"); 
        }
        
        clone.structureId = structureId; //is this line needed?
        
        return clone;
    }
}
