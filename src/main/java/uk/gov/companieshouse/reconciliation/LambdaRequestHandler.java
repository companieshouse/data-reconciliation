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





// package uk.gov.companieshouse.reconciliation;

// import com.amazonaws.services.lambda.runtime.Context;
// import com.amazonaws.services.lambda.runtime.RequestHandler;
// import com.amazonaws.services.lambda.runtime.LambdaLogger;
// import com.amazonaws.services.ecs.AmazonECS;
// import com.amazonaws.services.ecs.AmazonECSClientBuilder;
// import com.amazonaws.services.ecs.model.UpdateServiceRequest;
// import com.amazonaws.services.ecs.model.UpdateServiceResult;
// import com.google.gson.Gson;
// import com.google.gson.GsonBuilder;
// import java.util.Map;

// public class EcsTaskStopper implements RequestHandler<Map<String, Object>, Map<String, Object>> {
//     private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
//     private final AmazonECS ecsClient = AmazonECSClientBuilder.standard().build();

//     @Override
//     public Map<String, Object> handleRequest(Map<String, Object> event, Context context) {
//         LambdaLogger logger = context.getLogger();
//         logger.log("Event received: " + GSON.toJson(event));

//         try {
//             // Extract cluster and service name from the event
//             Map<String, Object> detail = (Map<String, Object>) event.get("detail");
//             String taskArn = (String) detail.get("taskArn");
//             String clusterArn = (String) detail.get("clusterArn");
//             String serviceNameFull = ((String) detail.get("group")).replace("service:", "");

//             logger.log("Task " + taskArn + " stopped in service " + serviceNameFull);

//             // Set desired count to 0
//             UpdateServiceRequest updateServiceRequest = new UpdateServiceRequest()
//                 .withCluster(clusterArn)
//                 .withService(serviceNameFull)
//                 .withDesiredCount(0);

//             UpdateServiceResult result = ecsClient.updateService(updateServiceRequest);
//             logger.log("Successfully set desired count to 0: " + GSON.toJson(result));
            
//             // Return success response
//             Map<String, Object> response = Map.of(
//                 "statusCode", 200,
//                 "body", "Success"
//             );
//             return response;
            
//         } catch (Exception e) {
//             logger.log("Error updating service: " + e.getMessage());
//             throw new RuntimeException("Failed to update ECS service", e);
//         }
//     }
// }





// xml<!-- For Maven pom.xml -->
// <dependencies>
//     <dependency>
//         <groupId>com.amazonaws</groupId>
//         <artifactId>aws-lambda-java-core</artifactId>
//         <version>1.2.2</version>
//     </dependency>
//     <dependency>
//         <groupId>com.amazonaws</groupId>
//         <artifactId>aws-java-sdk-ecs</artifactId>
//         <version>1.12.472</version>
//     </dependency>
//     <dependency>
//         <groupId>com.google.code.gson</groupId>
//         <artifactId>gson</artifactId>
//         <version>2.10.1</version>
//     </dependency>
// </dependencies>