# ------------------------------------------------------------------------------
# Environment
# ------------------------------------------------------------------------------

variable "environment" {
  type        = string
  description = "The environment name, defined in environments vars."
}
variable "aws_region" {
  default     = "eu-west-2"
  type        = string
  description = "The AWS region for deployment."
}
variable "aws_profile" {
  default     = "development-eu-west-2"
  type        = string
  description = "The AWS profile to use for deployment."
}

# ------------------------------------------------------------------------------
# Docker Container
# ------------------------------------------------------------------------------
variable "docker_registry" {
  type        = string
  description = "The FQDN of the Docker registry."
}

# ------------------------------------------------------------------------------
# Service performance and scaling configs
# ------------------------------------------------------------------------------
variable "desired_task_count" {
  type = number
  description = "The desired ECS task count for this service"
  default = 1 # defaulted low for dev environments, override for production
}
variable "required_cpus" {
  type = number
  description = "The required cpu resource for this service. 1024 here is 1 vCPU"
  default = 1024 # defaulted low for dev environments, override for production
}
variable "required_memory" {
  type = number
  description = "The required memory for this service"
  default = 8192 # defaulted low for node service in dev environments, override for production
}

variable "max_task_count" {
  type        = number
  description = "The maximum number of tasks for this service."
  default     = 1
}

variable "min_task_count" {
  default     = 1
  type        = number
  description = "The minimum number of tasks for this service."
}

variable "use_fargate" {
  type        = bool
  description = "If true, sets the required capabilities for all containers in the task definition to use FARGATE, false uses EC2"
  default     = true
}
variable "use_capacity_provider" {
  type        = bool
  description = "Whether to use a capacity provider instead of setting a launch type for the service"
  default     = true
}
variable "service_autoscale_enabled" {
  type        = bool
  description = "Whether to enable service autoscaling, including scheduled autoscaling"
  default     = true
}
variable "service_autoscale_target_value_cpu" {
  type        = number
  description = "Target CPU percentage for the ECS Service to autoscale on"
  default     = 100 # 100 disables autoscaling using CPU as a metric
}
variable "service_scaledown_schedule" {
  type        = string
  description = "The schedule to use when scaling down the number of tasks to zero."
  # Typically used to stop all tasks in a service to save resource costs overnight.
  # E.g. a value of '55 19 * * ? *' would be Mon-Sun 7:55pm.  An empty string indicates that no schedule should be created.

  default     = ""
}
variable "service_scaleup_schedule" {
  type        = string
  description = "The schedule to use when scaling up the number of tasks to their normal desired level."
  # Typically used to start all tasks in a service after it has been shutdown overnight.
  # E.g. a value of '5 6 * * ? *' would be Mon-Sun 6:05am.  An empty string indicates that no schedule should be created.

  default     = ""
}

# ----------------------------------------------------------------------
# Cloudwatch alerts
# ----------------------------------------------------------------------
variable "cloudwatch_alarms_enabled" {
  description = "Whether to create a standard set of cloudwatch alarms for the service.  Requires an SNS topic to have already been created for the stack."
  type        = bool
  default     = true
}

# ------------------------------------------------------------------------------
# Service environment variable configs
# ------------------------------------------------------------------------------
variable "ssm_version_prefix" {
  type        = string
  description = "String to use as a prefix to the names of the variables containing variables and secrets version."
  default     = "SSM_VERSION_"
}

variable "use_set_environment_files" {
  type        = bool
  default     = false
  description = "Toggle default global and shared environment files"
}

variable "data_reconciliation_version" {
  type        = string
  description = "The version of the data-reconciliation container to run."
}

variable "use_task_container_healthcheck" {
  type        = bool
  description = "If true, sets the ECS Tasks' container health check"
  default     = false
}
variable "healthcheck_command" {
  type        = string
  description = "Custom healthcheck command"
  default     = ""
}


# ------------------------------------------------------------------------------
# Scheduler variables
# ------------------------------------------------------------------------------
variable "enable_scale_down_eventbridge_scheduler" {
  default     = false
  description = "Whether to enable the scale down EventBridge scheduler for the ECS service"
  type        = bool
}

variable "enable_scale_up_eventbridge_scheduler" {
  default     = false
  description = "Whether to enable the scale up EventBridge scheduler for the ECS service"
  type        = bool
}

variable "eventbridge_group_name" {
  default     = ""
  description = "Group of the eventbridge schedulers"
  type        = string
}

variable "shutdown_eventbridge_scheduler_cron" {
  default     = "" 
  description = "Cron expression for the scheduler"
  type        = string
}

variable "startup_eventbridge_scheduler_cron" {
  default     = "" 
  description = "Cron expression for the scheduler"
  type        = string
}

# ------------------------------------------------------------------------------
# Lambda variable configs
# ------------------------------------------------------------------------------

variable "lambda_memory_size" {
  default     = "128"
  description = "The amount of memory made available to the Lambda function at runtime in megabytes"
  type        = string
}

variable "lambda_timeout_seconds" {
  default     = "600"
  description = "The amount of time the lambda function is allowed to run before being stopped"
  type        = string
}

variable "lambda_logs_retention_days" {
  default     = "7"
  description = "Number of days to keep AWS logs around in specific log group."
  type        = string
}

variable "lambda_handler_name" {
  type        = string
  description = "The lambda function entrypoint"
  default     = "uk.gov.companieshouse::handleRequest"
}

variable "lambda_runtime" {
  type        = string
  description = "The lambda runtime to use for the function"
  default     = "java21"
}

variable "release_bucket_name" {
  type        = string
  description = "The name of the S3 bucket containing the release artefact for the Lambda function"
}

variable "release_artifact_key" {
  type        = string
  description = "The release artifact key for the Lambda function"
}