package net.sf.xfresh.auth;

/**
 * Author: Olga Bolshakova (obolshakova@yandex-team.ru)
 * Date: 02.01.11 17:45
 */
public class UserCreationException extends RuntimeException {

    private final Type type;

    public UserCreationException(final Type type) {
        super();
        this.type = type;
    }

    public UserCreationException(final String message, final Type type) {
        super(message);
        this.type = type;
    }

    public UserCreationException(final String message, final Throwable cause, final Type type) {
        super(message, cause);
        this.type = type;
    }

    public UserCreationException(final Throwable cause, final Type type) {
        super(cause);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public static enum Type {
        ALREADY_EXISTS,
        INTERNAL_ERROR
    }
}
