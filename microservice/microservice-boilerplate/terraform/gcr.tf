# Enable container registry API
resource "google_project_service" "container_registry" {
  project = var.project_id
  service = "containerregistry.googleapis.com"

  disable_dependent_services = true
  disable_on_destroy         = false
}

# Create a Cloud Storage bucket for the container registry
resource "google_storage_bucket" "container_registry" {
  name     = "${var.project_id}-container-registry"
  location = var.region
  depends_on = [google_project_service.container_registry]
} 