# Create the namespace
resource "kubernetes_namespace" "microservice_ns" {
  metadata {
    name = var.kubernetes_namespace
  }
}

# Config repo ConfigMap
resource "kubernetes_config_map" "config_repo" {
  metadata {
    name      = "config-repo-data"
    namespace = kubernetes_namespace.microservice_ns.metadata[0].name
  }

  data = {
    "application.yml" = <<-EOT
    eureka:
      instance:
        instanceId: $${spring.application.name}:$${spring.application.instance_id:$${random.value}}
        prefer-ip-address: true
      client:
        serviceUrl:
          defaultZone: $${EUREKA_CLIENT_SERVICEURL_DEFAULTZONE:http://discovery-service:8761/eureka/}
    
    management:
      endpoints:
        web:
          exposure:
            include: health,info
    EOT

    "sample-service.yml" = <<-EOT
    server:
      port: 8060
      servlet:
        context-path: /api/sample
    
    spring:
      datasource:
        url: jdbc:h2:mem:userdb
        driverClassName: org.h2.Driver
        username: sa
        password: password
    
      h2:
        console:
          enabled: true
          path: /h2-console
    
      jpa:
        database-platform: org.hibernate.dialect.H2Dialect
        hibernate:
          ddl-auto: update
        show-sql: true
    EOT

    "gateway-service.yml" = <<-EOT
    server:
      port: 8040
    
    spring:
      cloud:
        gateway:
          globalcors:
            corsConfigurations:
              '[/**]':
                allowedOrigins: "*"
                allowedMethods: "*"
                allowedHeaders: "*"
          routes:
            - id: sample-service
              uri: lb://sample-service
              predicates:
                - Path=/api/sample/**
              filters:
                - RewritePath=/api/sample/(?<path>.*), /api/sample/$\{path}
                
            - id: discovery-service-static
              uri: http://localhost:8061
              predicates:
                - Path=/eureka/web
              filters:
                - SetPath=/
                
            - id: discovery-service-resources
              uri: http://localhost:8061
              predicates:
                - Path=/eureka/**
              
    management:
      endpoints:
        web:
          exposure:
            include: "*"
    EOT

    "discovery-service.yml" = <<-EOT
    server:
      port: 8761
    
    eureka:
      client:
        register-with-eureka: false
        fetch-registry: false
      server:
        wait-time-in-ms-when-sync-empty: 0 
    EOT
  }
}

# Config Service
resource "kubernetes_deployment" "config_service" {
  metadata {
    name      = "config-service"
    namespace = kubernetes_namespace.microservice_ns.metadata[0].name
  }

  spec {
    replicas = 1

    selector {
      match_labels = {
        app = "config-service"
      }
    }

    template {
      metadata {
        labels = {
          app = "config-service"
        }
      }

      spec {
        container {
          name  = "config-service"
          image = "gcr.io/${var.project_id}/config-service:latest"
          
          port {
            container_port = 8888
          }
          
          env {
            name  = "SPRING_PROFILES_ACTIVE"
            value = "native"
          }
          
          env {
            name  = "SPRING_CLOUD_CONFIG_SERVER_NATIVE_SEARCH-LOCATIONS"
            value = "file:/config-repo"
          }
          
          volume_mount {
            name       = "config-repo"
            mount_path = "/config-repo"
          }
          
          readiness_probe {
            http_get {
              path = "/actuator/health"
              port = 8888
            }
            initial_delay_seconds = 30
            period_seconds       = 10
            timeout_seconds      = 5
            failure_threshold    = 5
          }
          
          liveness_probe {
            http_get {
              path = "/actuator/health"
              port = 8888
            }
            initial_delay_seconds = 30
            period_seconds       = 10
            timeout_seconds      = 5
            failure_threshold    = 5
          }
        }
        
        volume {
          name = "config-repo"
          config_map {
            name = kubernetes_config_map.config_repo.metadata[0].name
          }
        }
      }
    }
  }
}

resource "kubernetes_service" "config_service" {
  metadata {
    name      = "config-service"
    namespace = kubernetes_namespace.microservice_ns.metadata[0].name
  }
  
  spec {
    selector = {
      app = "config-service"
    }
    
    port {
      port        = 8888
      target_port = 8888
    }
  }
}

# Discovery Service
resource "kubernetes_deployment" "discovery_service" {
  metadata {
    name      = "discovery-service"
    namespace = kubernetes_namespace.microservice_ns.metadata[0].name
  }

  spec {
    replicas = 1

    selector {
      match_labels = {
        app = "discovery-service"
      }
    }

    template {
      metadata {
        labels = {
          app = "discovery-service"
        }
      }

      spec {
        container {
          name  = "discovery-service"
          image = "gcr.io/${var.project_id}/discovery-service:latest"
          
          port {
            container_port = 8761
          }
          
          env {
            name  = "SPRING_CLOUD_CONFIG_URI"
            value = "http://config-service:8888"
          }
          
          readiness_probe {
            http_get {
              path = "/actuator/health"
              port = 8761
            }
            initial_delay_seconds = 30
            period_seconds       = 10
            timeout_seconds      = 5
            failure_threshold    = 5
          }
          
          liveness_probe {
            http_get {
              path = "/actuator/health"
              port = 8761
            }
            initial_delay_seconds = 30
            period_seconds       = 10
            timeout_seconds      = 5
            failure_threshold    = 5
          }
        }
      }
    }
  }

  depends_on = [
    kubernetes_deployment.config_service
  ]
}

resource "kubernetes_service" "discovery_service" {
  metadata {
    name      = "discovery-service"
    namespace = kubernetes_namespace.microservice_ns.metadata[0].name
  }
  
  spec {
    selector = {
      app = "discovery-service"
    }
    
    port {
      port        = 8761
      target_port = 8761
    }
  }
}

# Sample Service
resource "kubernetes_deployment" "sample_service" {
  metadata {
    name      = "sample-service"
    namespace = kubernetes_namespace.microservice_ns.metadata[0].name
  }

  spec {
    replicas = 1

    selector {
      match_labels = {
        app = "sample-service"
      }
    }

    template {
      metadata {
        labels = {
          app = "sample-service"
        }
      }

      spec {
        container {
          name  = "sample-service"
          image = "gcr.io/${var.project_id}/sample-service:latest"

          port {
            container_port = 8060
          }

          env {
            name  = "SPRING_CLOUD_CONFIG_URI"
            value = "http://config-service:8888"
          }

          readiness_probe {
            http_get {
              path = "/api/sample/actuator/health"
              port = 8060
            }
            initial_delay_seconds = 30
            period_seconds       = 10
            timeout_seconds      = 5
            failure_threshold    = 5
          }

          liveness_probe {
            http_get {
              path = "/api/sample/actuator/health"
              port = 8060
            }
            initial_delay_seconds = 30
            period_seconds       = 10
            timeout_seconds      = 5
            failure_threshold    = 5
          }
        }
      }
    }
  }

  depends_on = [
    kubernetes_deployment.discovery_service
  ]
}

resource "kubernetes_service" "sample_service" {
  metadata {
    name      = "sample-service"
    namespace = kubernetes_namespace.microservice_ns.metadata[0].name
  }

  spec {
    selector = {
      app = "sample-service"
    }

    port {
      port        = 8060
      target_port = 8060
    }
  }
}

# Gateway Service
resource "kubernetes_deployment" "gateway_service" {
  metadata {
    name      = "gateway-service"
    namespace = kubernetes_namespace.microservice_ns.metadata[0].name
  }

  spec {
    replicas = 1

    selector {
      match_labels = {
        app = "gateway-service"
      }
    }

    template {
      metadata {
        labels = {
          app = "gateway-service"
        }
      }

      spec {
        container {
          name  = "gateway-service"
          image = "gcr.io/${var.project_id}/gateway-service:latest"

          port {
            container_port = 8040
          }

          env {
            name  = "SPRING_CLOUD_CONFIG_URI"
            value = "http://config-service:8888"
          }

          readiness_probe {
            http_get {
              path = "/actuator/health"
              port = 8040
            }
            initial_delay_seconds = 30
            period_seconds       = 10
            timeout_seconds      = 5
            failure_threshold    = 5
          }

          liveness_probe {
            http_get {
              path = "/actuator/health"
              port = 8040
            }
            initial_delay_seconds = 30
            period_seconds       = 10
            timeout_seconds      = 5
            failure_threshold    = 5
          }
        }
      }
    }
  }

  depends_on = [
    kubernetes_deployment.sample_service
  ]
}

resource "kubernetes_service" "gateway_service" {
  metadata {
    name      = "gateway-service"
    namespace = kubernetes_namespace.microservice_ns.metadata[0].name
  }

  spec {
    selector = {
      app = "gateway-service"
    }

    type = "LoadBalancer"

    port {
      port        = 80
      target_port = 8040
    }
  }
}