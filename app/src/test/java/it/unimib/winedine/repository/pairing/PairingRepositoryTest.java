package it.unimib.winedine.repository.pairing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Rule;
import org.junit.Test;

import it.unimib.winedine.model.PairingAPIResponse;
import it.unimib.winedine.model.Result;
import it.unimib.winedine.source.pairing.BasePairingRemoteDataSource;

public class PairingRepositoryTest {
    @Rule
    public final InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Test
    public void getPairingPublishesSuccessfulResponse() {
        FakePairingDataSource dataSource = new FakePairingDataSource(false);
        PairingRepository repository = new PairingRepository(dataSource);

        Result result = repository.getPairing("merlot").getValue();

        assertTrue(result instanceof Result.PairingSuccess);
        PairingAPIResponse response = ((Result.PairingSuccess) result).getPairing();
        assertEquals("steak", response.getPairings()[0]);
    }

    @Test
    public void getPairingPublishesFailure() {
        PairingRepository repository = new PairingRepository(new FakePairingDataSource(true));

        Result result = repository.getPairing("merlot").getValue();

        assertTrue(result instanceof Result.Error);
        assertEquals("network unavailable", ((Result.Error) result).getMessage());
    }

    private static final class FakePairingDataSource extends BasePairingRemoteDataSource {
        private final boolean shouldFail;

        private FakePairingDataSource(boolean shouldFail) {
            this.shouldFail = shouldFail;
        }

        @Override
        public void getPairing(String wine) {
            if (shouldFail) {
                responseCallback.onFailure(new Exception("network unavailable"));
                return;
            }

            PairingAPIResponse response = new PairingAPIResponse();
            response.setPairings(new String[]{"steak", "mushrooms"});
            response.setText("Demo pairing");
            responseCallback.onPairingSuccess(response);
        }
    }
}
