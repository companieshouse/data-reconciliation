module "lambda" {
  source = "git@github.com:companieshouse/terraform-modules.git//aws/lambda?ref=1.0.316"

  # Lambda function configuration
  environment                   = var.environment
  function_name                 = "${local.service_name}-ecs-task-stopper"
  lambda_runtime                = var.lambda_runtime
  lambda_handler                = var.lambda_handler_name 

  lambda_code_s3_bucket         = var.release_bucket_name
  lambda_code_s3_key            = var.release_artifact_key

  lambda_memory_size            = var.lambda_memory_size
  lambda_timeout_seconds        = var.lambda_timeout_seconds
  lambda_logs_retention_days    = var.lambda_logs_retention_days

  # VPC configuration
  lambda_vpc_id                 = data.aws_vpc.vpc.id
  lambda_vpc_access_subnet_ids  = local.application_subnet_ids

  # Security group egress rules
  lambda_sg_egress_rule = {
    from_port   = -1
    to_port     = -1
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # Add necessary permissions for the Lambda to update ECS services
  additional_policies = [
    jsonencode({
      Version = "2012-10-17",
      Statement = [{
        Action   = "ecs:UpdateService",
        Resource = "arn:aws:ecs:${var.aws_region}:${local.account_id}:service/${local.name_prefix}-cluster/${local.service_name}",
        Effect   = "Allow"
      }]
    })
  ]

  # CloudWatch Event Rule for ECS Task State Change
  lambda_cloudwatch_event_rules = [
    {
      name        = "${var.environment}-${local.service_name}-ecs-task-stopped-event"
      description = "Trigger when ECS task in the service stops"
      event_pattern = jsonencode({
        source      = ["aws.ecs"],
        detail-type = ["ECS Task State Change"],
        detail = {
          clusterArn = ["arn:aws:ecs:${var.aws_region}:${local.account_id}:cluster/${local.name_prefix}-cluster"],
          lastStatus = ["STOPPED"],
          group      = ["service:${var.environment}-${local.service_name}"]
        }
      })
    }
  ]

  # Permissions
  lambda_permissions = [
    {
      statement_id = "AllowExecutionFromCloudWatch"
      action       = "lambda:InvokeFunction"
      principal    = "events.amazonaws.com"
      source_arn   = "arn:aws:events:${var.aws_region}:${local.account_id}:rule/${local.service_name}-ecs-task-stopped-event"
    }
  ]
}