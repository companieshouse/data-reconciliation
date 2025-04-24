package uk.gov.companieshouse.reconciliation;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.model.UpdateServiceRequest;
import software.amazon.awssdk.services.ecs.model.UpdateServiceResponse;

import java.io.IOException;

public class EcsTaskStopperHandler implements RequestHandler<JsonNode, Void> {

    // Read env vars set by Terraform
    private static final String CLUSTER = System.getenv("ECS_CLUSTER");
    private static final String SERVICE = System.getenv("ECS_SERVICE"); // :contentReference[oaicite:6]{index=6}

    // Jackson mapper
    private final ObjectMapper mapper = new ObjectMapper();

    // ECS client (v2)
    private final EcsClient ecs = EcsClient.create(); // :contentReference[oaicite:7]{index=7}

    @Override
    public Void handleRequest(JsonNode event, Context context) {
        context.getLogger().log("Received event: " + event.toString());

        // Validate event source/type
        String source = event.path("source").asText();
        String detailType = event.path("detail-type").asText();
        if (!"aws.ecs".equals(source) || !"ECS Task State Change".equals(detailType)) {
            context.getLogger().log("Ignoring non-ECS Task State Change event");
            return null;
        }

        // Drill into detail
        JsonNode detail = event.path("detail");
        String lastStatus = detail.path("lastStatus").asText();
        String group = detail.path("group").asText();  // e.g. "service:my-service"

        // Only if the service task stopped
        if ("STOPPED".equals(lastStatus) && ("service:" + SERVICE).equals(group)) {
            context.getLogger().log("Stopping service " + SERVICE + " in cluster " + CLUSTER);
            try {
                UpdateServiceRequest req = UpdateServiceRequest.builder()
                    .cluster(CLUSTER)
                    .service(SERVICE)
                    .desiredCount(0)
                    .build();
                UpdateServiceResponse resp = ecs.updateService(req); // :contentReference[oaicite:8]{index=8}
                context.getLogger().log("UpdateService response: " + resp.toString());
            } catch (Exception e) {
                context.getLogger().log("Failed to update service: " + e.getMessage());
                throw e;
            }
        } else {
            context.getLogger().log("Event does not match STOPPED for service " + SERVICE);
        }
        return null;
    }
}