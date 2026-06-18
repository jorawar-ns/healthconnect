package com.healthconnect.claims.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Publishes claim lifecycle events to Kafka.
 *
 * Fire-and-forget — Kafka failures are logged but NEVER propagate back to
 * the HTTP caller. The claim is persisted regardless of Kafka availability.
 * Downstream consumers (ERA service, notification service) must handle
 * idempotency via the claimNumber key.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ClaimEventProducer {

    private static final String TOPIC_CLAIM_SUBMITTED   = "claim.submitted";
    private static final String TOPIC_CLAIM_ADJUDICATED = "claim.adjudicated";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publishClaimSubmitted(String claimNumber, String memberId, String payerId, String claimType) {
        publish(TOPIC_CLAIM_SUBMITTED, claimNumber, buildPayload(
                "eventType", "CLAIM_SUBMITTED",
                "claimNumber", claimNumber,
                "memberId", memberId,
                "payerId", payerId,
                "claimType", claimType,
                "occurredAt", Instant.now().toString()
        ));
    }

    public void publishClaimAdjudicated(String claimNumber, String memberId, String status, String icn) {
        publish(TOPIC_CLAIM_ADJUDICATED, claimNumber, buildPayload(
                "eventType", "CLAIM_ADJUDICATED",
                "claimNumber", claimNumber,
                "memberId", memberId,
                "status", status,
                "icn", icn,
                "occurredAt", Instant.now().toString()
        ));
    }

    // ── Internal ──────────────────────────────────────────────────────────────

    private void publish(String topic, String key, String payload) {
        kafkaTemplate.send(topic, key, payload)
                .addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
                    @Override
                    public void onSuccess(SendResult<String, String> result) {
                        log.debug("Published to {} key={} offset={}",
                                topic, key, result.getRecordMetadata().offset());
                    }

                    @Override
                    public void onFailure(Throwable ex) {
                        // Intentional: Kafka failure must NOT fail the HTTP response.
                        // Claims are persisted in Postgres regardless.
                        // Ops alert configured in Datadog (HCEP-1102).
                        log.error("Failed to publish Kafka event topic={} key={}: {}",
                                topic, key, ex.getMessage());
                    }
                });
    }

    private String buildPayload(String... kvPairs) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < kvPairs.length - 1; i += 2) {
            map.put(kvPairs[i], kvPairs[i + 1]);
        }
        try {
            return objectMapper.writeValueAsString(map);
        } catch (Exception e) {
            log.error("Failed to serialize Kafka payload: {}", e.getMessage());
            return "{}";
        }
    }
}
