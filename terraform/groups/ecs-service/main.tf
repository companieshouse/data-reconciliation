provider "aws" {
  region = var.aws_region
}

terraform {
  backend "s3" {
  }
  required_version = ">= 1.3.0, < 2.0.0"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = ">= 5.94.0, < 6.0"
    }

    vault = {
      source  = "hashicorp/vault"
      version = ">= 3.18.0, < 5.0"
    }
  }
}

module "secrets" {
  # source = "git@github.com:companieshouse/terraform-modules//aws/ecs/secrets?ref=1.0.316"
  source = "git@github.com:companieshouse/terraform-modules//aws/ecs/secrets?ref=feature/JU-954-improve-scheduler"

  name_prefix = "${local.service_name}-${var.environment}"
  environment = var.environment
  kms_key_id  = data.aws_kms_key.kms_key.id
  secrets     = nonsensitive(local.service_secrets)
}

module "ecs-service" {
  # source = "git@github.com:companieshouse/terraform-modules//aws/ecs/ecs-service?ref=1.0.316"
  source = "git@github.com:companieshouse/terraform-modules//aws/ecs/secrets?ref=feature/JU-954-improve-scheduler"
  


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
  enable_scale_up_eventbridge_scheduler                  = var.enable_scale_up_eventbridge_scheduler
  eventbridge_group_name                                 = local.name_prefix
  startup_eventbridge_scheduler_cron                     = var.startup_eventbridge_scheduler_cron
  # shutdown_eventbridge_scheduler_cron            = var.shutdown_eventbridge_scheduler_cron

  # Service environment variable and secret configs
  task_environment          = local.task_environment
  task_secrets              = local.task_secrets
  app_environment_filename  = local.app_environment_filename
  use_set_environment_files = local.use_set_environment_files
}
