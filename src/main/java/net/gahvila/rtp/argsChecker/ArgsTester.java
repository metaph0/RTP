package net.gahvila.rtp.argsChecker;

import java.util.*;

public abstract class ArgsTester {
    public static void printTree(Map<String, Object> mapToPrint) {
        printTree(mapToPrint, 0);
    }

    private static void printTree(Map<String, Object> mapToPrint, int depth) {
        for (String key : mapToPrint.keySet()) {
            System.out.print(depth + " | " + numSpaces(depth) + key + ":");
            if (mapToPrint.get(key) instanceof Map) {
                System.out.print('\n');
                printTree((Map<String, Object>)mapToPrint.get(key), depth + 1);
                continue;
            }
            System.out.print(' ');
            System.out.println((String)mapToPrint.get(key));
        }
    }

    private static String numSpaces(int count) {
        StringBuilder spaces = new StringBuilder();
        for (int i = 0; i < count; ) {
            spaces.append(' ');
            i++;
        }
        return spaces.toString();
    }

    public static List<String> nextCompleteInTree(String[] searchFor, Map<String, Object> map, DynamicArgsMap dam) {
        if (searchFor[searchFor.length - 1] == null)
            searchFor[searchFor.length - 1] = "";
        try {
            return nextCompleteInTree(searchFor, map, dam, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static List<String> nextCompleteInTree(String[] searchFor, Map<String, Object> map, DynamicArgsMap dam, int depth) throws Exception {
        ArrayList<String> completes = new ArrayList<>();
        if (searchFor.length > depth + 1) {
            for (String key : map.keySet()) {
                if (map.get(key) != null && (searchFor[depth].equalsIgnoreCase(key) || key.contains("!dynamic")))
                    return nextCompleteInTree(searchFor, (Map<String, Object>)map.get(key), dam, depth + 1);
            }
        } else {
            String lastInSearch = searchFor[searchFor.length - 1];
            for (String key : map.keySet()) {
                if (key.contains("!dynamic")) {
                    for (String dynamicKey : dam.runner(Arrays.<String>copyOf(searchFor, depth))) {
                        if (dynamicKey.toLowerCase().contains(lastInSearch.toLowerCase()))
                            completes.add(dynamicKey);
                    }
                    continue;
                }
                if (key.contains("!any"))
                    return Collections.singletonList('<' + key.substring(0, key.indexOf("!") - 1) + '>');
                if (key.toLowerCase().contains(lastInSearch.toLowerCase()))
                    completes.add(key);
            }
        }
        return completes;
    }
}
