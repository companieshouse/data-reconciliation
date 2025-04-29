package uk.gov.companieshouse.reconciliation;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.model.UpdateServiceRequest;
import software.amazon.awssdk.services.ecs.model.UpdateServiceResponse;
import software.amazon.awssdk.regions.Region;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LambdaHandler implements RequestHandler<Map<String, Object>, String> {
    private static final Logger logger = LoggerFactory.getLogger(LambdaHandler.class);
    private final EcsClient ecsClient;
    private final ObjectMapper objectMapper;

    public LambdaHandler() {
        this.ecsClient = EcsClient.builder()
                .region(Region.of(System.getenv("AWS_REGION")))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    // Used for testing
    LambdaHandler(EcsClient ecsClient) {
        this.ecsClient = ecsClient;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public String handleRequest(Map<String, Object> event, Context context) {
        logger.info("Event received: {}", event);
        
        try {
            // Extract necessary information from the event
            if (event.containsKey("detail")) {
                Map<String, Object> detail = (Map<String, Object>) event.get("detail");
                
                // Check if this is an ECS task state change event
                if (event.containsKey("source") && "aws.ecs".equals(event.get("source")) && 
                    event.containsKey("detail-type") && "ECS Task State Change".equals(event.get("detail-type"))) {
                    
                    // Check if the task stopped and reason contains "Essential container in task exited"
                    String stopReason = (String) detail.get("stoppedReason");
                    String lastStatus = (String) detail.get("lastStatus");
                    
                    if ("STOPPED".equals(lastStatus) && stopReason != null && 
                        stopReason.contains("Essential container in task exited")) {
                        
                        // Extract the cluster and service name
                        String clusterArn = (String) detail.get("clusterArn");
                        String group = (String) detail.get("group");
                        
                        if (group != null && group.startsWith("service:")) {
                            String serviceName = group.substring("service:".length());
                            
                            // Extract the cluster name from the ARN
                            String[] clusterArnParts = clusterArn.split("/");
                            String clusterName = clusterArnParts[clusterArnParts.length - 1];
                            
                            // Set the desired count to 0
                            return updateServiceDesiredCount(clusterName, serviceName, 0);
                        }
                    }
                }
            }
            return "Event processed but no action taken";
        } catch (Exception e) {
            logger.error("Error processing event", e);
            throw new RuntimeException("Error processing event", e);
        }
    }
    
    private String updateServiceDesiredCount(String clusterName, String serviceName, int desiredCount) {
        logger.info("Setting desired count to {} for service {} in cluster {}", desiredCount, serviceName, clusterName);
        
        try {
            UpdateServiceRequest request = UpdateServiceRequest.builder()
                    .cluster(clusterName)
                    .service(serviceName)
                    .desiredCount(desiredCount)
                    .build();
            
            UpdateServiceResponse response = ecsClient.updateService(request);
            logger.info("Service updated successfully: {}", response.service().serviceArn());
            return "Service " + serviceName + " in cluster " + clusterName + " updated with desired count " + desiredCount;
        } catch (Exception e) {
            logger.error("Failed to update service", e);
            throw new RuntimeException("Failed to update service: " + e.getMessage(), e);
        }
    }
}