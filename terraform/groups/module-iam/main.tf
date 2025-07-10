# IAM user responsible for uploading results to S3
resource "aws_iam_user" "iam-user" {
  name = "${var.name_prefix}-iam-user"
  tags = {
    Name        = "${var.name_prefix}-iam-user"
    Environment = var.environment
    Service     = var.service_name
  }
}

resource "aws_iam_access_key" "iam-user" {
  user = aws_iam_user.iam-user.name
}

resource "aws_iam_user_policy" "iam-user" {
  name      = "${var.name_prefix}-iam-user"
  user      = aws_iam_user.iam-user.name
  policy    = data.aws_iam_policy_document.iam-user.json
}

data "aws_iam_policy_document" "iam-user" {
  statement {
    effect = "Allow"
    actions = [
      "s3:ListBucket",
      "s3:GetBucketLocation"
    ]
    resources = [
      var.result_bucket_arn
    ]
  }

  statement {
    effect = "Allow"
    actions = [
      "s3:ListObjectsV2",
      "s3:PutObject",
      "s3:PutObjectAcl",
      "s3:GetObject",
      "s3:GetObjectAcl",
      "s3:DeleteObject"
    ]
    resources = [
      "${var.result_bucket_arn}/*"
    ]
  }
}