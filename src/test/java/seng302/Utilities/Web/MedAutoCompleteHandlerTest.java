package seng302.Utilities.Web;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for the medication autocompletion handler. This handler connects to the MAPI drug autocompletion web API, so
 * tests will break if internet access is not enabled on the system running the tests.
 */
public class MedAutoCompleteHandlerTest {

    private MedAutoCompleteHandler handler;

    @Before
    public void setUp() {
        handler = new MedAutoCompleteHandler();
    }

    /**
     * Test for query string "res" that expects the results shown below.
     */
    @Test
    public void getSuggestionsTest1() {
        final List<String> expected = Arrays
                .asList("Reserpine", "Resectisol", "Resectisol in plastic container", "Restoril", "Rescriptor",
                        "Restasis", "Rescula", "Reserpine and hydrochlorothiazide",
                        "Reserpine, hydralazine hydrochloride and hydrochlorothiazide",
                        "Reserpine, hydrochlorothiazide, and hydralazine hydrochloride",
                        "Reserpine and hydrochlorothiazide-50", "Reserpine and hydroflumethiazide", "Resporal");

        List<String> actual = handler.getSuggestions("res");

        // Do this instead of assertEquals so that if more matching drugs are added to the API, our test doesn't break.
        assertTrue(actual.containsAll(expected));
    }

    /**
     * Test for query string "panda" that expects no results.
     */
    @Test
    public void getSuggestionsTest2() {
        final List<String> expected = Collections.emptyList();

        List<String> actual = handler.getSuggestions("panda");

        assertEquals(expected, actual);
    }
}
