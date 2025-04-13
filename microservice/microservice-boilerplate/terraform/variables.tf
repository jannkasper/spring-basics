variable "project_id" {
  description = "The Google Cloud Project ID"
  type        = string
}

variable "region" {
  description = "The GCP region to deploy resources"
  type        = string
  default     = "us-central1"
}

variable "zone" {
  description = "The GCP zone to deploy resources"
  type        = string
  default     = "us-central1-a"
}

variable "cluster_name" {
  description = "The name of the GKE cluster"
  type        = string
  default     = "microservice-cluster"
}

variable "node_count" {
  description = "Number of nodes in the GKE node pool"
  type        = number
  default     = 3
}

variable "machine_type" {
  description = "Machine type for GKE nodes"
  type        = string
  default     = "e2-standard-2"
}

variable "preemptible" {
  description = "Use preemptible nodes (lower cost, but can be terminated anytime)"
  type        = bool
  default     = true
}

variable "node_service_account" {
  description = "Service account for GKE nodes"
  type        = string
  default     = null
}

variable "kubernetes_namespace" {
  description = "Kubernetes namespace for microservices"
  type        = string
  default     = "microservice-ns"
} 