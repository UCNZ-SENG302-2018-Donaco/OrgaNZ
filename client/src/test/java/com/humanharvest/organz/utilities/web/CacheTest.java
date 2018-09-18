package com.humanharvest.organz.utilities.web;

import static org.junit.Assert.*;

import java.io.EOFException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.humanharvest.organz.BaseTest;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.utilities.CacheManager;
import com.humanharvest.organz.utilities.enums.Gender;
import com.humanharvest.organz.utilities.exceptions.BadDrugNameException;
import com.humanharvest.organz.utilities.exceptions.BadGatewayException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.api.client.testing.http.MockHttpTransport;
import org.junit.Assert;
import org.junit.Test;

public class CacheTest extends BaseTest {

    @Test
    public void testCachingInteractions() throws Exception {
        MockCacheManager mockCacheManager = MockCacheManager.Create();
        String EXPECTED_RESPONSE_BODY = "{\"age_interaction\":{\"0-1\":[\"foetal exposure during pregnancy\","
                + "\"congenital arterial malformation\",\"premature baby\",\"ventricular septal defect\","
                + "\"cytogenetic abnormality\",\"heart disease congenital\",\"pyloric stenosis\"],"
                + "\"10-19\":[\"hodgkin's disease\",\"anaphylactic reaction\",\"drug ineffective\","
                + "\"electrocardiogram qt prolonged\",\"pain\",\"blood pressure decreased\",\"bronchospasm\","
                + "\"cardio-respiratory arrest\",\"hodgkin's disease stage ii\",\"hyperkalaemia\"],"
                + "\"2-9\":[\"cardiac failure\",\"infection\",\"pneumonia\",\"serum sickness\",\"arthritis\","
                + "\"device related infection\",\"drug resistance\",\"epistaxis\",\"multi-organ failure\","
                + "\"renal failure\"],\"20-29\":[\"drug ineffective\",\"pain\",\"infection\","
                + "\"blood pressure increased\",\"stress\",\"weight decreased\",\"tendon operation\","
                + "\"arthralgia\",\"hypertension\",\"sarcoidosis\"],\"30-39\":[\"fatigue\",\"nausea\",\"arthralgia\","
                + "\"headache\",\"drug ineffective\",\"injection site haemorrhage\",\"back pain\",\"pain\","
                + "\"pyrexia\",\"cardiac arrest\"],\"40-49\":[\"drug ineffective\",\"arthralgia\","
                + "\"pain in extremity\",\"joint swelling\",\"pain\",\"peripheral swelling\",\"headache\",\"rash\","
                + "\"sinusitis\",\"infection\"],\"50-59\":[\"drug ineffective\",\"arthralgia\",\"fatigue\",\"pain\","
                + "\"nausea\",\"headache\",\"rash\",\"alopecia\",\"pain in extremity\",\"vomiting\"],"
                + "\"60+\":[\"drug ineffective\",\"fatigue\",\"pain\",\"arthralgia\",\"nausea\",\"rash\","
                + "\"arthropathy\",\"fall\",\"synovitis\",\"pneumonia\"],\"nan\":[\"drug ineffective\","
                + "\"arthralgia\",\"dyspnoea\",\"pain\",\"pneumonia\",\"infection\",\"nausea\",\"pain in extremity\","
                + "\"asthenia\",\"cough\"]},\"co_existing_conditions\":{\"high blood pressure\":322,\"pain\":287},"
                + "\"duration_interaction\":{\"1 - 2 years\":[\"hypertension\",\"pneumonia\",\"arthropathy\","
                + "\"alanine aminotransferase increased\",\"appendicitis perforated\",\"drug ineffective\","
                + "\"edentulous\",\"hepatitis\",\"injection site erythema\",\"injection site pruritus\"],"
                + "\"1 - 6 months\":[\"drug ineffective\",\"nausea\",\"drug intolerance\",\"gait disturbance\","
                + "\"carpal tunnel syndrome\",\"pain\",\"alopecia\",\"vomiting\",\"dyspnoea\",\"arthropathy\"],"
                + "\"10+ years\":[\"cardio-respiratory arrest\",\"anaphylactic reaction\",\"apnoea\",\"coma\","
                + "\"dizziness\",\"drug ineffective\",\"hypotension\",\"meningitis\",\"osteonecrosis of jaw\","
                + "\"pneumocystis jiroveci infection\"],\"2 - 5 years\":[\"drug ineffective\",\"pain\",\"anaemia\","
                + "\"oedema peripheral\",\"abdominal pain\",\"arthralgia\",\"back pain\",\"blood pressure increased\","
                + "\"dizziness\",\"gait disturbance\"],\"5 - 10 years\":[\"myocardial infarction\","
                + "\"atrial fibrillation\",\"leukocytoclastic vasculitis\",\"coronary artery occlusion\","
                + "\"renal failure\",\"alanine aminotransferase increased\",\"aspartate aminotransferase increased\","
                + "\"drug-induced liver injury\",\"palpitations\",\"pruritus generalised\"],"
                + "\"6 - 12 months\":[\"alanine aminotransferase increased\",\"drug ineffective\",\"hepatitis\","
                + "\"injection site erythema\",\"injection site pruritus\",\"pancreatitis acute\",\"renal failure\","
                + "\"death\",\"injury\",\"nausea\"],\"< 1 month\":[\"arrhythmia\",\"blood pressure decreased\","
                + "\"bradycardia\",\"cardiac arrest\",\"cardiac index decreased\",\"circulatory collapse\","
                + "\"hyperlipidaemia\",\"lactic acidosis\",\"multi-organ failure\",\"renal impairment\"],"
                + "\"not specified\":[\"drug ineffective\",\"arthralgia\",\"pain\",\"fatigue\",\"nausea\",\"rash\","
                + "\"pain in extremity\",\"headache\",\"joint swelling\",\"dyspnoea\"]},"
                + "\"gender_interaction\":{\"female\":[\"drug ineffective\",\"pain\",\"arthralgia\",\"fatigue\","
                + "\"nausea\",\"rash\",\"headache\",\"pain in extremity\",\"diarrhoea\",\"fall\"],"
                + "\"male\":[\"drug ineffective\",\"arthralgia\",\"fatigue\",\"pain in extremity\",\"joint swelling\","
                + "\"pneumonia\",\"dyspnoea\",\"pain\",\"cough\",\"weight decreased\"]},\"reports\":{\"amount\":4199}}";

        MockHttpTransport mockTransport = MockHelper.makeMockHttpTransport(EXPECTED_RESPONSE_BODY);
        DrugInteractionsHandler handler = new DrugInteractionsHandler(mockTransport);

        Client client = new Client("first", null, "last", LocalDate.now().minusYears(32).minusDays(10), 0);
        client.setGender(Gender.FEMALE);

        Assert.assertTrue(mockCacheManager.isEmpty());

        handler.getInteractions(client, "leflunomide", "prednisone");

        Assert.assertTrue(!mockCacheManager.isEmpty());
    }

    @Test
    public void testCachingInteractionsMultipleClients() throws Exception {
        MockCacheManager mockCacheManager = MockCacheManager.Create();

        MockHttpTransport mockTransport =
                MockHelper.makeMockHttpTransport(DrugInteractionsHandlerTest.EXPECTED_RESPONSE_BODY);
        DrugInteractionsHandler handler = new DrugInteractionsHandler(mockTransport);

        Client client1 = new Client("first", null, "last", LocalDate.now().minusYears(32).minusDays(10), 0);
        client1.setGender(Gender.FEMALE);

        Client client2 = new Client("first", null, "last", LocalDate.now().minusYears(32).minusDays(10), 0);
        client2.setGender(Gender.MALE);

        assertTrue(mockCacheManager.isEmpty());

        List<String> client1Interactions = handler.getInteractions(client1, DrugInteractionsHandlerTest.DRUG1,
                DrugInteractionsHandlerTest.DRUG2);
        List<String> client2Interactions = handler.getInteractions(client2, DrugInteractionsHandlerTest.DRUG1,
                DrugInteractionsHandlerTest.DRUG2);
        assertNotEquals(client1Interactions, client2Interactions);

        assertTrue(!mockCacheManager.isEmpty());
    }

    @Test
    public void testCachingIngredients() throws IOException {
        MockCacheManager mockCacheManager = MockCacheManager.Create();
        String EXPECTED_RESPONSE_BODY = "[\"Hydralazine hydrochloride; hydrochlorothiazide; reserpine\","
                + "\"Hydrochlorothiazide; reserpine\",\"Hydroflumethiazide; reserpine\",\"Reserpine\"]";

        MockHttpTransport mockTransport = MockHelper.makeMockHttpTransport(EXPECTED_RESPONSE_BODY);
        MedActiveIngredientsHandler handler = new MedActiveIngredientsHandler(mockTransport);

        Assert.assertTrue(mockCacheManager.isEmpty());

        handler.getActiveIngredients("reserpine");

        Assert.assertTrue(!mockCacheManager.isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoArguments() {
        CacheManager mockCacheManager = new MockCacheManager();
        mockCacheManager.addCachedData("test", new Object[0], "test", Optional.empty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoCategory() {
        CacheManager mockCacheManager = new MockCacheManager();
        mockCacheManager.addCachedData(null, new Object[]{"foo"}, "test", Optional.empty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoValue() {
        CacheManager mockCacheManager = new MockCacheManager();
        mockCacheManager.addCachedData("test", new Object[]{"foo"}, null, Optional.empty());
    }

    @Test
    public void testWorks() {
        CacheManager mockCacheManager = new MockCacheManager();
        mockCacheManager.addCachedData("test", new Object[]{"foo"}, "test", Optional.empty());
        Optional<String> value = mockCacheManager.getCachedData("test", new TypeReference<String>() {
        }, new Object[]{"foo"});
        assertTrue(value.isPresent());
        assertEquals("test", value.get());
    }

    @Test
    public void testCacheSaveLoad() throws IOException {
        MockCacheManager mockCacheManager = new MockCacheManager();
        mockCacheManager.addCachedData("test", new Object[]{"foo"}, "test", Optional.empty());
        String result = mockCacheManager.save();

        mockCacheManager.load(result);
        Optional<String> value = mockCacheManager.getCachedData("test", new TypeReference<String>() {
        }, new Object[]{"foo"});
        assertTrue(value.isPresent());
        assertEquals("test", value.get());
    }

    @Test
    public void testRefreshingCache() {
        MockCacheManager mockCacheManager = MockCacheManager.Create();
        String EXPECTED_RESPONSE_BODY = "[\"Hydralazine hydrochloride; hydrochlorothiazide; reserpine\","
                + "\"Hydrochlorothiazide; reserpine\",\"Hydroflumethiazide; reserpine\",\"Reserpine\"]";
        MockHttpTransport mockTransport = MockHelper.makeMockHttpTransport(EXPECTED_RESPONSE_BODY);

        mockCacheManager.addCachedData("com.humanharvest.organz.utilities.web.MedActiveIngredientsHandler",
                new Object[]{"foo"}, "test", Optional.empty());

        // Check the pre-refresh value
        Optional<String> initialValue = mockCacheManager.getCachedData(
                "com.humanharvest.organz.utilities.web.MedActiveIngredientsHandler",
                new TypeReference<String>() {
                }, new Object[]{"foo"});
        assertTrue(initialValue.isPresent());
        assertEquals("test", initialValue.get());

        // Refresh it
        mockCacheManager.refreshCachedData(mockTransport);

        // Check it has the new value
        Optional<String[]> refreshedValue = mockCacheManager.getCachedData(
                "com.humanharvest.organz.utilities.web.MedActiveIngredientsHandler",
                new TypeReference<String[]>() {
                }, new Object[]{"foo"});
        assertTrue(refreshedValue.isPresent());
        assertEquals(EXPECTED_RESPONSE_BODY, "[\"" + String.join("\",\"", refreshedValue.get()) + "\"]");

    }
}