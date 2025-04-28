package it.unimib.winedine.model;

import java.util.List;

public abstract class Result {

    private Result() {
    }
    public boolean isSuccess() {
        return !(this instanceof Error);
    }

    public static final class WineSuccess extends Result {
        private final WineAPIResponse wineAPIResponse;

        public WineSuccess(WineAPIResponse wineAPIResponse) {
            this.wineAPIResponse = wineAPIResponse;
        }

        public WineAPIResponse getData() {
            return wineAPIResponse;
        }
    }

    public static final class RecipesSuccess extends Result {
        private final List<Recipe> recipes;

        public RecipesSuccess(List<Recipe> recipes) {
            this.recipes = recipes;
        }

        public List<Recipe> getRecipes() {
            return recipes;
        }
    }

    public static final class PairingSuccess extends Result {
       private final PairingAPIResponse pairingAPIResponse;

       public PairingSuccess(PairingAPIResponse pairingAPIResponse){
           this.pairingAPIResponse = pairingAPIResponse;
       }
       public PairingAPIResponse getPairing() {
           return pairingAPIResponse;
       }
    }

    public static final class DishSuccess extends Result {
        private final DishAPIResponse dishAPIResponse;

       public DishSuccess(DishAPIResponse dishAPIResponse) {
           this.dishAPIResponse = dishAPIResponse;
       }

       public DishAPIResponse getDish() {
           return dishAPIResponse;
       }
    }

    public static class Loading extends Result {}

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



