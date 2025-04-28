import json
import boto3
import os
import logging

# Configure logging
logger = logging.getLogger()
logger.setLevel(logging.INFO)

# Initialize AWS clients
ecs_client = boto3.client('ecs')

def handleRequest(event, context):
    """
    Lambda function that checks if an ECS task stopped due to 'Essential container in task exited'
    and sets the service's desired count to 0 to prevent further task launches.
    
    Args:
        event: The event from CloudWatch Events/EventBridge
        context: Lambda execution context
    
    Returns:
        dict: Response indicating action taken
    """
    logger.info(f"Received event: {json.dumps(event)}")
    
    # Check if this is an ECS Task State Change event
    if 'detail-type' not in event or event['detail-type'] != 'ECS Task State Change':
        logger.info("Not an ECS Task State Change event, ignoring")
        return {"statusCode": 200, "message": "Not an ECS Task State Change event"}
    
    # Extract task details
    detail = event.get('detail', {})
    cluster_arn = detail.get('clusterArn')
    task_arn = detail.get('taskArn')
    last_status = detail.get('lastStatus')
    group = detail.get('group', '')
    
    # Only proceed if we have a service group (starts with 'service:')
    if not group.startswith('service:'):
        logger.info(f"Task is not part of a service group: {group}")
        return {"statusCode": 200, "message": "Task not part of a service"}
    
    # Extract service name from group
    service_name = group.split('service:')[1]
    
    # Only proceed if the task has stopped
    if last_status != 'STOPPED':
        logger.info(f"Task status is {last_status}, not STOPPED, ignoring")
        return {"statusCode": 200, "message": f"Task status is {last_status}, not STOPPED"}
    
    # Check if essential container exited
    try:
        # Get the task details to check for stopped reason
        task_response = ecs_client.describe_tasks(
            cluster=cluster_arn,
            tasks=[task_arn]
        )
        
        if not task_response.get('tasks'):
            logger.warning(f"Could not find task {task_arn} in cluster {cluster_arn}")
            return {"statusCode": 404, "message": "Task not found"}
        
        task = task_response['tasks'][0]
        containers = task.get('containers', [])
        
        # Check for essential containers that exited
        essential_container_exited = False
        for container in containers:
            reason = container.get('reason', '')
            if reason and 'Essential container in task exited' in reason:
                essential_container_exited = True
                logger.info(f"Found essential container that exited: {reason}")
                break
        
        if not essential_container_exited:
            logger.info("No essential container exited, ignoring")
            return {"statusCode": 200, "message": "No essential container exited"}
        
        # Get the current service settings
        service_response = ecs_client.describe_services(
            cluster=cluster_arn,
            services=[service_name]
        )
        
        if not service_response.get('services'):
            logger.warning(f"Could not find service {service_name} in cluster {cluster_arn}")
            return {"statusCode": 404, "message": "Service not found"}
        
        service = service_response['services'][0]
        current_desired_count = service.get('desiredCount', 0)
        
        logger.info(f"Service {service_name} current desired count: {current_desired_count}")
        
        # Only update if the desired count is greater than 0
        if current_desired_count > 0:
            # Update the service to set desired count to 0
            update_response = ecs_client.update_service(
                cluster=cluster_arn,
                service=service_name,
                desiredCount=0
            )
            
            logger.info(f"Updated service {service_name} desired count to 0")
            return {
                "statusCode": 200,
                "message": f"Updated service {service_name} desired count from {current_desired_count} to 0",
                "service": service_name,
                "cluster": cluster_arn
            }
        else:
            logger.info(f"Service {service_name} desired count is already {current_desired_count}, no action needed")
            return {
                "statusCode": 200,
                "message": f"Service desired count already set to {current_desired_count}, no action needed",
                "service": service_name,
                "cluster": cluster_arn
            }
            
    except Exception as e:
        logger.error(f"Error processing task: {str(e)}")
        return {
            "statusCode": 500,
            "message": f"Error: {str(e)}"
        }