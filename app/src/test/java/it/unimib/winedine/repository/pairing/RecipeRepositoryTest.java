package it.unimib.winedine.repository.pairing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Rule;
import org.junit.Test;

import java.util.Collections;

import it.unimib.winedine.model.Recipe;
import it.unimib.winedine.model.Result;
import it.unimib.winedine.source.pairing.BaseRecipeRemoteDataSource;

public class RecipeRepositoryTest {
    @Rule
    public final InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Test
    public void suppressesDuplicateRequestsUntilCurrentRequestCompletes() {
        FakeRecipeDataSource dataSource = new FakeRecipeDataSource();
        RecipeRepository repository = new RecipeRepository(dataSource);

        repository.getRecipes(new String[]{"steak"});
        repository.getRecipes(new String[]{"pasta"});
        assertEquals(1, dataSource.requestCount);

        dataSource.succeed();
        assertTrue(repository.getRecipes(new String[]{"salad"}).getValue()
                instanceof Result.RecipesSuccess);
        assertEquals(2, dataSource.requestCount);
    }

    @Test
    public void cancelDelegatesAndAllowsAnotherRequest() {
        FakeRecipeDataSource dataSource = new FakeRecipeDataSource();
        RecipeRepository repository = new RecipeRepository(dataSource);

        repository.getRecipes(new String[]{"steak"});
        repository.cancelPendingRequest();
        repository.getRecipes(new String[]{"pasta"});

        assertTrue(dataSource.cancelled);
        assertEquals(2, dataSource.requestCount);
    }

    private static final class FakeRecipeDataSource extends BaseRecipeRemoteDataSource {
        private int requestCount;
        private boolean cancelled;

        @Override
        public void getRecipesForPairings(String[] ingredients) {
            requestCount++;
        }

        @Override
        public void cancelPendingRequests() {
            cancelled = true;
        }

        private void succeed() {
            responseCallback.onRecipesSuccess(Collections.emptyList());
        }
    }
}
