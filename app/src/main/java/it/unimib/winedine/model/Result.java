package it.unimib.winedine.model;

public abstract class Result {

    private Result() {
    }
    public boolean isSuccess() {
        return !(this instanceof Error);
    }

    /**
     * Class that represents a successful action during the interaction
     * with a Web Service or a local database.
     */
    public static final class WineSuccess extends Result {
        private final WineAPIResponse wineAPIResponse;

        public WineSuccess(WineAPIResponse wineAPIResponse) {
            this.wineAPIResponse = wineAPIResponse;
        }

        public WineAPIResponse getData() {
            return wineAPIResponse;
        }
    }

    public static final class UserSuccess extends Result {
        private final User user;
        public UserSuccess(User user) {
            this.user = user;
        }
        public User getData() {
            return user;
        }
    }
    public static final class Error extends Result {
        private final String message;
        public Error(String message) {
            this.message = message;
        }
        public String getMessage() {
            return message;
        }
    }
}


