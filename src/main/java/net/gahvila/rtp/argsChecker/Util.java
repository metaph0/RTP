package net.gahvila.rtp.argsChecker;

import java.util.Map;

public class Util {
    public static Map<String, Object> getImpliedMap(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Map)
            try {
                return (Map<String, Object>)value;
            } catch (ClassCastException classCastException) {}
        return null;
    }
}
