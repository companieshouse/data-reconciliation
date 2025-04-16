provider "aws" {
  region = var.aws_region
}

terraform {
  backend "s3" {
  }
  required_version = "~> 1.3"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 4.54.0"
    }
    vault = {
      source  = "hashicorp/vault"
      version = "~> 3.18.0"
    }
  }
}

module "secrets" {
  source = "git@github.com:companieshouse/terraform-modules//aws/ecs/secrets?ref=1.0.315"

  name_prefix = "${local.service_name}-${var.environment}"
  environment = var.environment
  kms_key_id  = data.aws_kms_key.kms_key.id
  secrets     = nonsensitive(local.service_secrets)
}

module "ecs-service" {
  source = "git@github.com:companieshouse/terraform-modules//aws/ecs/ecs-service?ref=1.0.315"
  


  # Environmental configuration
  environment                     = var.environment
  aws_region                      = var.aws_region
  aws_profile                     = var.aws_profile
  vpc_id                          = data.aws_vpc.vpc.id
  ecs_cluster_id                  = data.aws_ecs_cluster.ecs_cluster.id
  ecs_cluster_arn                 = data.aws_ecs_cluster.ecs_cluster.arn
  task_execution_role_arn         = data.aws_iam_role.ecs_cluster_iam_role.arn
  eventbridge_scheduler_role_arn  = data.aws_iam_role.eventbridge_role.arn
  batch_service                   = true
  

  # ECS Task container health check
  use_task_container_healthcheck    = true
  healthcheck_command               = "pgrep -q java; [[ $? -ne 1 ]] || exit 1"
  healthcheck_path                  = local.healthcheck_path
  healthcheck_matcher               = local.healthcheck_matcher
  health_check_grace_period_seconds = 240
  

  # Docker container details
  docker_registry   = var.docker_registry
  docker_repo       = local.docker_repo
  container_version = var.data_reconciliation_version
  container_port    = local.container_port

  # Service configuration
  service_name                       = local.service_name
  name_prefix                        = local.name_prefix
  desired_task_count                 = var.desired_task_count
  max_task_count                     = var.max_task_count
  min_task_count                     = var.min_task_count
  required_cpus                      = var.required_cpus
  required_memory                    = var.required_memory
  service_autoscale_enabled          = var.service_autoscale_enabled
  service_autoscale_target_value_cpu = var.service_autoscale_target_value_cpu
  service_scaledown_schedule         = var.service_scaledown_schedule
  service_scaleup_schedule           = var.service_scaleup_schedule
  use_capacity_provider              = var.use_capacity_provider
  use_fargate                        = var.use_fargate
  fargate_subnets                    = local.application_subnet_ids
  read_only_root_filesystem          = false

  # Scheduler configuration
  enable_eventbridge_scheduler                   = var.enable_eventbridge_scheduler
  eventbridge_group_name                         = local.name_prefix
  startup_eventbridge_scheduler_cron             = var.startup_eventbridge_scheduler_cron
  shutdown_eventbridge_scheduler_cron            = var.shutdown_eventbridge_scheduler_cron

  # Service environment variable and secret configs
  task_environment          = local.task_environment
  task_secrets              = local.task_secrets
  app_environment_filename  = local.app_environment_filename
  use_set_environment_files = local.use_set_environment_files
}


module "lambda" {
  source = "git@github.com:companieshouse/terraform-modules.git//aws/lambda?ref=1.0.315"

  # Lambda function configuration
  environment                   = var.environment
  function_name                 = "${var.environment}-${local.service_name}-ecs-task-stopper"
  lambda_runtime                = "python3.9" # var.lambda_runtime
  lambda_handler                = "lambda_function.lambda_handler"      # var.lambda_handler_name 

  lambda_code_s3_bucket         = "test-bucket" # this needs looking at
  lambda_code_s3_key            = "path/to/ecs-task-stopper.zip" # Your zipped Lambda code
  
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
        Resource = "arn:aws:ecs:${var.region}:${local.account_id}:service/${local.name_prefix}-cluster/${var.environment}-${local.service_name}",
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
          clusterArn = ["arn:aws:ecs:${var.region}:${local.account_id}:cluster/${local.name_prefix}-cluster"],
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
      source_arn   = "arn:aws:events:${var.region}:${local.account_id}:rule/${var.environment}-${local.service_name}-ecs-task-stopped-event"
    }
  ]
}