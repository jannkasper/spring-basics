output "kubernetes_cluster_name" {
  description = "GKE Cluster Name"
  value       = google_container_cluster.primary.name
}

output "kubernetes_cluster_endpoint" {
  description = "GKE Cluster Host"
  value       = google_container_cluster.primary.endpoint
}

output "kubernetes_cluster_region" {
  description = "GKE Cluster Region"
  value       = google_container_cluster.primary.location
}

output "gke_connect_command" {
  description = "Command to connect to the GKE cluster"
  value       = "gcloud container clusters get-credentials ${google_container_cluster.primary.name} --region ${google_container_cluster.primary.location} --project ${var.project_id}"
}

output "gateway_service_ip" {
  description = "External IP of the Gateway Service"
  value       = kubernetes_service.gateway_service.status.0.load_balancer.0.ingress.0.ip
}