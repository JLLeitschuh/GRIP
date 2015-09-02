package edu.wpi.grip.core.operations.opencv;

import com.google.common.eventbus.EventBus;
import edu.wpi.grip.core.MethodSignature;
import edu.wpi.grip.core.Operation;
import edu.wpi.grip.core.Socket;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import static com.google.common.base.Preconditions.checkNotNull;


public class AddOperation extends Operation {
    public static final String OPENCV_CORE_METHOD = "add";
    public static final List<MethodSignature> methodSignatures;
    public static final MethodSignature defaultSignature;
    static {
        // Safely define initialization order.
        methodSignatures = MethodSignature.getMethodSignatures(opencv_core.class, OPENCV_CORE_METHOD);
        defaultSignature = getDefaultSignature();
    }

    private static final MethodSignature getDefaultSignature(){
        //TODO Remove me. I'm really only here for testing purposes at the moment.
        MethodSignature possibleSignature = null;
        for(MethodSignature signature : methodSignatures){
            if( signature.paramSignatures[0].paramType.equals(Mat.class) && signature.paramSignatures[1].paramType.equals(Mat.class) && signature.getMethod().getParameterCount() == 2){
                possibleSignature = signature;
                break;
            }
        }
        return possibleSignature;
    }

    private final MethodSignature signature;

    public AddOperation(MethodSignature signature) throws IllegalArgumentException {
        checkNotNull(signature, "Method Signature can not be null");
        if(!methodSignatures.contains(signature) ){
            throw new IllegalArgumentException("Invalid Method signature provided");
        }
        this.signature = signature;
    }

    public AddOperation() {
        this(defaultSignature);
    }

    @Override
    public Socket[] createInputSockets(EventBus eventBus) {
        return Socket.socketListFromHints(eventBus, signature.getParametersSocketHint());
    }

    @Override
    public Socket[] createOutputSockets(EventBus eventBus) {
        return Socket.socketListFromHints(eventBus, signature.getReturnSocketHint());
    }

    @Override
    public void perform(Socket[] inputs, Socket[] outputs) {
        Socket sum = outputs[0];
        try {
            //Null because it is a static method call
            sum.setValue(signature.getMethod().invoke(null, Socket.getSocketValuesList(inputs)));
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
