package com.healthconnect.eligibility.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthconnect.common.constants.HealthConnectConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class EligibilityEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Publishes eligibility.checked event after a successful inquiry.
     * Key = controlNumber so consumers can partition by transaction.
     */
    public void publishEligibilityChecked(String controlNumber, String memberId,
                                          String tradingPartnerId, String status) {
        Map<String, String> event = new HashMap<>();
        event.put("controlNumber", controlNumber);
        event.put("memberId", memberId);
        event.put("tradingPartnerId", tradingPartnerId);
        event.put("status", status);
        event.put("eventType", "ELIGIBILITY_CHECKED");

        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(HealthConnectConstants.TOPIC_ELIGIBILITY_CHECKED, controlNumber, payload)
                    .addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
                        @Override
                        public void onSuccess(SendResult<String, String> result) {
                            log.info("Published eligibility.checked for controlNumber={} partition={}",
                                    controlNumber,
                                    result.getRecordMetadata().partition());
                        }

                        @Override
                        public void onFailure(Throwable ex) {
                            // Log and continue — Kafka failure must not fail the HTTP response
                            log.error("Failed to publish eligibility.checked for controlNumber={}: {}",
                                    controlNumber, ex.getMessage());
                        }
                    });
        } catch (JsonProcessingException e) {
            log.error("Could not serialise eligibility event for controlNumber={}", controlNumber, e);
        }
    }
}
