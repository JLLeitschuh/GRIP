package edu.wpi.grip.core;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class MethodSignature {

    public class ParamSignature {
        public final String paramName;
        public final Class<?> paramType;

        ParamSignature(Parameter param, char defaultParam){
            paramType = param.getType();
            // If compiled without the `-parameters` flag this will be false
            if(param.isNamePresent()){
                paramName = param.getName();
            } else {
                paramName = String.valueOf(defaultParam);
            }
        }

        public SocketHint<?> getParamSocketHint(){
            return new SocketHint(this.paramName, this.paramType);
        }
    }

    public final Class<?> returnType;
    public final ParamSignature[] paramSignatures;
    public final String name;
    private final Method method;
    public MethodSignature(Method method){
        this.name = method.getName();
        this.returnType = method.getReturnType();
        List<ParamSignature> paramSignatures = new ArrayList<>();
        //If there isn't a param provided by reflection then we have to provide our own.
        char defaultParam = 'a';
        for(Parameter param : method.getParameters()){
            paramSignatures.add(new ParamSignature(param, defaultParam));
            defaultParam ++;
        }
        this.paramSignatures = paramSignatures.toArray(new ParamSignature[paramSignatures.size()]);
        this.method = method;
    }

    public Method getMethod(){ return method; }


    public SocketHint<?>[] getParametersSocketHint(){
        List<SocketHint<?>> socketHints = new ArrayList<>();
        for(ParamSignature signature : paramSignatures){
            socketHints.add(signature.getParamSocketHint());
        }
        return socketHints.toArray(new SocketHint[socketHints.size()]);
    }

    /**
     * @return A new Socket hint with the name of the function + `'ed'` as the identifier.
     * Example: 'add' becomes 'added'
     */
    public SocketHint getReturnSocketHint() {
        return new SocketHint(this.name + "ed", this.returnType);
    }

    public static final List<MethodSignature> getMethodSignatures(Class<?> clazz, String methodName){
        List<MethodSignature> methodSignatures = new ArrayList<>();
        for (Class<?> c = clazz; c != null; c = c.getSuperclass()) {
            for (Method method : c.getDeclaredMethods()) {
                if(method.getName().equals(methodName)){
                    methodSignatures.add(new MethodSignature(method));
                }
            }
        }

        return methodSignatures;
    }
}
