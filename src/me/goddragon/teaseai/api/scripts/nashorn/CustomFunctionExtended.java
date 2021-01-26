package me.goddragon.teaseai.api.scripts.nashorn;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import me.goddragon.teaseai.utils.TeaseLogger;

public abstract class CustomFunctionExtended extends CustomFunction {
    private static final String HANDLER_METHOD_NAME = "onCall";

    protected CustomFunctionExtended(String... functionName) {
        super(functionName);
    }

    @Override
    public Object call(Object object, Object... args) {
        super.call(object, args);

        final Class<?>[] argTypeList = objectListToTypeList(args);

        Object result = null;
        String faultMessage = null;

        try {
            final Method method = getClass().getDeclaredMethod(HANDLER_METHOD_NAME, argTypeList);
            result = method.invoke(this, args);
        } catch (NoSuchMethodException e) {
            faultMessage = "No match for";
        } catch (SecurityException e) {
            faultMessage = "Inaccessible";
        } catch (IllegalAccessException e) {
            faultMessage = "Illegal access to";
        } catch (IllegalArgumentException e) {
            faultMessage = "Illegal argument passed to";
        } catch (InvocationTargetException e) {
            faultMessage = "Invocation exception during";
        }

        if (faultMessage != null) {
            TeaseLogger.getLogger().log(Level.SEVERE,
                    String.format("%s function call %s(%s)", faultMessage, getFunctionName(),
                            typeListToString(argTypeList)));

            logCandidateFunctions();
        }

        return result;
    }

    private Class<?>[] objectListToTypeList(Object[] args) {
        final Class<?>[] typeList = new Class<?>[ args.length ];
        for (int i = 0; i < args.length; ++i) {
            if (args[i] != null) {
                typeList[i] = args[i].getClass();
            } else {
                typeList[i] = Void.TYPE;
            }
        }

        return typeList;
    }

    private void logCandidateFunctions() {
        TeaseLogger.getLogger().log(Level.INFO, "Candidate functions are:");

        final List<String> listOfEachMethodsArguments = new ArrayList<>();
        for (Method method : getClass().getDeclaredMethods()) {
            if (method.getName().contentEquals(HANDLER_METHOD_NAME) && method.canAccess(this)) {
                listOfEachMethodsArguments.add(typeListToString(method.getParameterTypes()));
            }
        }

        listOfEachMethodsArguments.sort(String::compareTo);

        for (String methodArguments : listOfEachMethodsArguments) {
            TeaseLogger.getLogger().log(
                    Level.INFO, String.format("    %s(%s)", getFunctionName(), methodArguments));
        }
    }

    private String typeListToString(Class<?>[] typeList) {
        StringBuilder text = new StringBuilder();
        boolean isFirstParameter = true;

        for (Class<?> pType : typeList) {
            if (isFirstParameter)
                isFirstParameter = false;
            else
                text.append(", ");

            if (pType == Void.TYPE) {
                text.append("null");
            } else {
                text.append(getSimplifiedTypeName(pType.getName()));
            }
        }

        return text.toString();
    }

    private String getSimplifiedTypeName(String typeName) {
        final int lastSplit = typeName.lastIndexOf('.');
        if (lastSplit != -1) {
            return typeName.substring(lastSplit + 1);
        } else {
            return typeName;
        }
    }
}
