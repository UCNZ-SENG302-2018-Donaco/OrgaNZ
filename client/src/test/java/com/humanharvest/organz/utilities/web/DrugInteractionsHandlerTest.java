package com.humanharvest.organz.utilities.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import com.humanharvest.organz.BaseTest;
import com.humanharvest.organz.Client;
import com.humanharvest.organz.utilities.enums.Gender;
import com.humanharvest.organz.utilities.exceptions.BadDrugNameException;
import com.humanharvest.organz.utilities.exceptions.BadGatewayException;

import com.google.api.client.testing.http.MockHttpTransport;
import org.junit.Test;

public class DrugInteractionsHandlerTest extends BaseTest {

    public static final String EXPECTED_RESPONSE_BODY = "{\"age_interaction\":{\"0-1\":[\"foetal exposure during "
            + "pregnancy\","
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
    public static final String DRUG1 = "leflunomide";
    public static final String DRUG2 = "prednisone";

    @Test
    public void testValidDrugInteractions() {
        MockCacheManager.Create();

        MockHttpTransport mockTransport = MockHelper.makeMockHttpTransport(EXPECTED_RESPONSE_BODY);
        DrugInteractionsHandler handler = new DrugInteractionsHandler(mockTransport);

        Client client = new Client("first", null, "last", LocalDate.now().minusYears(32).minusDays(10), 0);
        client.setGender(Gender.FEMALE);

        List<String> interactions = Collections.emptyList();
        try {
            interactions = handler.getInteractions(client, DRUG1, DRUG2);
        } catch (IOException | BadDrugNameException | BadGatewayException e) {
            fail(e.getMessage());
        }
        assertEquals(7, interactions.size());
        assertEquals("pain (1 - 6 months, 2 - 5 years)", interactions.get(0));
        assertEquals("fatigue", interactions.get(1));
        assertEquals("arthralgia (2 - 5 years)", interactions.get(2));
        assertEquals("pain in extremity", interactions.get(3));
        assertEquals("drug ineffective (1 month - 5 years, 10+ years)", interactions.get(4));
        assertEquals("headache", interactions.get(5));
        assertEquals("nausea (1 - 12 months)", interactions.get(6));
    }
}
