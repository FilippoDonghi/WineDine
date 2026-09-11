package it.unimib.winedine.repository.user;

import static org.junit.Assert.*;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import org.junit.Rule;
import org.junit.Test;
import java.util.List;
import it.unimib.winedine.model.Bottle;
import it.unimib.winedine.model.Result;
import it.unimib.winedine.model.User;
import it.unimib.winedine.source.user.BaseUserAuthenticationRemoteDataSource;
import it.unimib.winedine.source.user.BaseUserDataRemoteDataSource;
import it.unimib.winedine.source.wine.BaseBottleLocalDataSource;

public class UserRepositoryTest {
    @Rule public final InstantTaskExecutorRule instantTaskExecutorRule =
            new InstantTaskExecutorRule();

    @Test public void failedAuthenticationCanRetryWithoutReplayingOldError() {
        FakeAuth auth = new FakeAuth();
        UserRepository repository = repository(auth);
        MutableLiveData<Result> first = repository.getUser("demo@example.com", "wrong", true);
        auth.fail();
        assertTrue(first.getValue() instanceof Result.Error);

        MutableLiveData<Result> retry = repository.getUser("demo@example.com", "correct", true);
        assertNotSame(first, retry);
        assertNull(retry.getValue());
        auth.succeed();
        assertTrue(retry.getValue() instanceof Result.UserSuccess);
        assertTrue(first.getValue() instanceof Result.Error);
        assertEquals(2, auth.calls);
    }

    @Test public void duplicateInFlightRequestDoesNotStartAnotherAuthentication() {
        FakeAuth auth = new FakeAuth();
        UserRepository repository = repository(auth);
        MutableLiveData<Result> first = repository.getUser("demo@example.com", "password", true);
        assertSame(first, repository.getUser("demo@example.com", "password", true));
        assertEquals(1, auth.calls);
    }

    @Test public void synchronousLogoutCallbackIsNotLost() {
        UserRepository repository = repository(new FakeAuth());
        assertTrue(repository.logout().getValue() instanceof Result.UserSuccess);
    }

    private UserRepository repository(FakeAuth auth) {
        return new UserRepository(auth, new BaseUserDataRemoteDataSource() {
            @Override public void saveUserData(User user) {
                userResponseCallback.onSuccessFromRemoteDatabase(user);
            }
            @Override public void saveUserFavoriteWines(String id, Bottle bottle) {}
            @Override public void getUserFavoriteWines(String id) {}
            @Override public void getUserPreferences(String id) {}
            @Override public void saveUserPreferences(String id) {}
        }, new BaseBottleLocalDataSource() {
            @Override public void insertWines(List<Bottle> bottles) {}
            @Override public void insertBottle(Bottle bottle) {}
            @Override public void getBottlesBySelectedWine(String wine) {}
            @Override public void updateWine(Bottle bottle) {}
            @Override public void getFavoriteWines() {}
            @Override public void deleteFavoriteWines(Bottle bottle) {}
        });
    }

    private static class FakeAuth extends BaseUserAuthenticationRemoteDataSource {
        int calls;
        @Override public User getLoggedUser() { return null; }
        @Override public void signIn(String email, String password) { calls++; }
        @Override public void signUp(String email, String password) { calls++; }
        @Override public void signInWithGoogle(String token) { calls++; }
        @Override public void logout() { userResponseCallback.onSuccessLogout(); }
        void fail() { userResponseCallback.onFailureFromAuthentication("invalid credentials"); }
        void succeed() {
            userResponseCallback.onSuccessFromAuthentication(
                    new User("Demo", "demo@example.com", "demo-id"));
        }
    }
}
