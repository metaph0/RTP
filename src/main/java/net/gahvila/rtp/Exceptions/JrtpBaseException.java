package net.gahvila.rtp.Exceptions;

/**
 * An exception for organizational purposes.
 */
public class JrtpBaseException extends Exception {

    public JrtpBaseException() { super(); }

    public JrtpBaseException(String message) { super(message); }

    public JrtpBaseException(String message, Throwable cause) { super(message, cause); }

    public JrtpBaseException(Throwable cause) { super(cause); }

    public static class PluginDisabledException extends JrtpBaseException {}

    public static class NotPermittedException extends JrtpBaseException {
        public NotPermittedException(String message) { super(message); }
    }

    public static class ConfigurationException extends JrtpBaseException {
        public ConfigurationException(String message) {super(message);}
    }
}
