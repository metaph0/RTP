package biz.donvi.jakesRTP.argsChecker;

import java.util.List;

public abstract class DynamicArgsMap {
    private List<String> result;

    public void setResult(List<String> result) throws ResultAlreadySetException {
        if (this.result != null)
            throw new ResultAlreadySetException();
        this.result = result;
    }

    List<String> runner(String[] path) throws Exception {
        this.result = null;
        try {
            getPotential(path);
        } catch (ResultAlreadySetException resultAlreadySetException) {}
        if (this.result != null)
            return this.result;
        StringBuilder pathBuilder = new StringBuilder();
        for (int i = 0; i < path.length; i++) {
            if (i != 0)
                pathBuilder.append(' ');
            pathBuilder.append(path[i]);
        }
        String pathAsString = pathBuilder.toString();
        try {
            getPotential(pathAsString);
        } catch (ResultAlreadySetException resultAlreadySetException) {}
        if (this.result != null)
            return this.result;
        throw new Exception("Path led nowhere: " + pathAsString);
    }

    public abstract void getPotential(String paramString) throws ResultAlreadySetException;

    public abstract void getPotential(String[] paramArrayOfString) throws ResultAlreadySetException;

    public static class ResultAlreadySetException extends Exception {}
}
