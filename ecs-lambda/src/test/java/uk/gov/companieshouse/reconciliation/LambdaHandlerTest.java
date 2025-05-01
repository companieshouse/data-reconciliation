package uk.gov.companieshouse.reconciliation;

import com.amazonaws.services.lambda.runtime.Context;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.model.UpdateServiceRequest;
import software.amazon.awssdk.services.ecs.model.UpdateServiceResponse;
import software.amazon.awssdk.services.ecs.model.Service;


import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class LambdaHandlerTest {

    @Mock
    private EcsClient ecsClient;

    @Mock
    private Context context;

    private LambdaHandler lambdaHandler;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        lambdaHandler = new LambdaHandler(ecsClient);
    }

    @Test
    public void testHandleRequest_TaskStoppedWithEssentialContainerExited() {
        // Arrange
        Map<String, Object> event = createEcsTaskStoppedEvent(
                "arn:aws:ecs:us-west-2:123456789012:cluster/dev-cluster",
                "service:dev-my-service",
                "Essential container in task exited"
        );

        Service mockService = Service.builder()
                .serviceArn("arn:aws:ecs:us-west-2:123456789012:service/dev-cluster/dev-my-service")
                .build();
        
        UpdateServiceResponse mockResponse = UpdateServiceResponse.builder()
                .service(mockService)
                .build();
                
        when(ecsClient.updateService(any(UpdateServiceRequest.class))).thenReturn(mockResponse);

        // Act
        String result = lambdaHandler.handleRequest(event, context);

        // Assert
        verify(ecsClient, atLeastOnce()).updateService(any(UpdateServiceRequest.class));
        assertEquals("Service dev-my-service in cluster dev-cluster updated with desired count 0", result);
    }

    @Test
    public void testHandleRequest_TaskStoppedWithDifferentReason() {
        // Arrange
        Map<String, Object> event = createEcsTaskStoppedEvent(
                "arn:aws:ecs:us-west-2:123456789012:cluster/dev-cluster",
                "service:dev-my-service",
                "User initiated stop"
        );

        // Act
        String result = lambdaHandler.handleRequest(event, context);

        // Assert
        verify(ecsClient, never()).updateService(any(UpdateServiceRequest.class));
        assertEquals("Event processed but no action taken", result);
    }

    @Test
    public void testHandleRequest_NotTaskStateChangeEvent() {
        // Arrange
        Map<String, Object> event = new HashMap<>();
        event.put("source", "aws.ecs");
        event.put("detail-type", "Some Other Event Type");
        event.put("detail", new HashMap<String, Object>());
        
        // Act
        String result = lambdaHandler.handleRequest(event, context);

        // Assert
        verify(ecsClient, never()).updateService(any(UpdateServiceRequest.class));
        assertEquals("Event processed but no action taken", result);
    }

    @Test
    public void testHandleRequest_TaskNotStopped() {
        // Arrange
        Map<String, Object> event = new HashMap<>();
        event.put("source", "aws.ecs");
        event.put("detail-type", "ECS Task State Change");
        
        Map<String, Object> detail = new HashMap<>();
        detail.put("clusterArn", "arn:aws:ecs:us-west-2:123456789012:cluster/dev-cluster");
        detail.put("group", "service:dev-my-service");
        detail.put("lastStatus", "RUNNING");  // Not STOPPED
        
        event.put("detail", detail);
        
        // Act
        String result = lambdaHandler.handleRequest(event, context);

        // Assert
        verify(ecsClient, never()).updateService(any(UpdateServiceRequest.class));
        assertEquals("Event processed but no action taken", result);
    }

    private Map<String, Object> createEcsTaskStoppedEvent(String clusterArn, String group, String stoppedReason) {
        Map<String, Object> event = new HashMap<>();
        event.put("source", "aws.ecs");
        event.put("detail-type", "ECS Task State Change");
        
        Map<String, Object> detail = new HashMap<>();
        detail.put("clusterArn", clusterArn);
        detail.put("group", group);
        detail.put("lastStatus", "STOPPED");
        detail.put("stoppedReason", stoppedReason);
        
        event.put("detail", detail);
        
        return event;
    }
}