package net.rulerz.netatmoToTado.tado;

public class TadoException extends RuntimeException {
    public TadoException(String message) {
        super(message);
    }

    public TadoException(Exception exception) {
        super(exception);
    }

    public TadoException(String message, Exception exception) {
        super(message, exception);
    }
}
