resource "aws_iam_role" "ecs_task_role" {
  name               = "${local.name_prefix}-${local.service_name}-ecs-task-role"
  assume_role_policy = data.aws_iam_policy_document.ecs_task_policy_data_reconciliation.json
}

data "aws_iam_policy_document" "ecs_task_policy_data_reconciliation" {
  statement {
    actions = ["sts:AssumeRole"]

    principals {
      type = "Service"
      identifiers = [
        "ecs-tasks.amazonaws.com"
      ]
    }
  }
}

data "aws_iam_policy_document" "bucket_access_policy" {
  statement {
    sid = "S3Read"
    effect = "Allow"
    actions = [
      "s3:ListBucket",
      "s3:GetBucketLocation"
    ]

    resources = [
      "arn:aws:s3:::${var.data_reconciliation_results_bucket_name}"
    ]
  }

  statement {
    sid = "AccessS3Bucket"
    effect = "Allow"
    actions = [
      "s3:ListObjectsV2",
      "s3:ListObject",
      "s3:PutObject",
      "s3:PutObjectAcl",
      "s3:GetObject",
      "s3:GetObjectAcl",
      "s3:DeleteObject"
    ]

    resources = [
      "arn:aws:s3:::${var.data_reconciliation_results_bucket_name}/*"
    ]
  }
}

resource "aws_iam_role_policy" "bucket_access_policy" {
  name   = "bucket-access-role-policy"
  role   = aws_iam_role.ecs_task_role.id
  policy = data.aws_iam_policy_document.bucket_access_policy.json
}