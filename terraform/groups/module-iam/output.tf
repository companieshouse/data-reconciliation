output "access_key_id" {
  description = "Access key that will be used to connect to S3"
  value = aws_iam_access_key.iam-user.id
}

output "secret_access_key" {
  description = "Secret access key that will be used to connect to S3"
  value = aws_iam_access_key.iam-user.secret
}