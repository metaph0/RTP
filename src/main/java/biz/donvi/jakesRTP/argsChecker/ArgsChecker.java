package biz.donvi.jakesRTP.argsChecker;

import java.util.ArrayList;
import java.util.Arrays;

public class ArgsChecker {
    private final String[] inputArgs;

    private boolean matchFound;

    private String[] remainingArgs;

    public ArgsChecker(String[] inputArgs) {
        this.inputArgs = inputArgs;
        this.matchFound = false;
    }

    public boolean matches(boolean exactLength, String... argsToMatch) {
        if (this.matchFound || (exactLength && this.inputArgs.length != argsToMatch.length) || this.inputArgs.length < argsToMatch.length)
            return false;
        for (int i = 0; i < argsToMatch.length; i++) {
            if (argsToMatch[i] != null && !this.inputArgs[i].equalsIgnoreCase(argsToMatch[i]))
                return false;
        }
        ArrayList<String> remainingArgs = new ArrayList<>();
        for (int j = 0; j < argsToMatch.length; j++) {
            if (argsToMatch[j] == null)
                remainingArgs.add(this.inputArgs[j]);
        }
        if (!exactLength)
            remainingArgs.addAll(Arrays.<String>asList(this.inputArgs).subList(argsToMatch.length, this.inputArgs.length));
        this.remainingArgs = remainingArgs.<String>toArray(new String[0]);
        return this.matchFound = true;
    }

    public boolean matches(int minLength, int maxLength, String... argsToMatch) {
        if (argsToMatch.length >= minLength && argsToMatch.length <= maxLength)
            return matches(false, argsToMatch);
        return false;
    }

    public String[] getRemainingArgs() {
        if (this.matchFound)
            return this.remainingArgs;
        throw new Error("Can not find remaining args when no match has been found.");
    }
}
