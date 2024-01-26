# Modular CI/CD Builder: Streamlining Jenkins Automation

The Modular CI/CD Builder is a pioneering toolkit that transforms the landscape of Continuous Integration and Continuous
Deployment (CI/CD) within the Jenkins ecosystem. As a highly modular Shared Library conceived for integration with
Jenkins Global Pipeline Libraries, it represents the epitome of innovative automation. Adopting a script-based pipeline
approach, the project emphasizes modularity and customization, serving Jenkins Jobs with unrivaled efficiency. Designed
to provide a flexible and scalable solution, the Modular CI/CD Builder utilizes a range of design patterns to
effectively support complex CI/CD workflows and requirements.

## Why This Project?

In a landscape where efficiency and speed are paramount, the Modular CI/CD Builder emerges as a crucial asset. It's more
than a portfolio piece; it's my commitment to advancing automation technologies. While the project is open for community
input and evolution, its core lies in enabling developers to achieve more with less—less time, less overhead, and fewer
complications.

## Core Features & Highlights

- **Docker Integration**: Deep integration with Docker supports containerization and artifact image CI processes that
  streamline the entire development lifecycle.
- **Ansible Automation**: Harness the power of Ansible for advanced automation and orchestration tasks, enabling
  scalable and manageable infrastructure with minimal effort.
- **Linux Bash and SSH Commands**: Take control with encapsulations of Linux bash and SSH commands, offering flexibility
  that transforms script execution and remote operations.
- **Docker Compose Workflows**: Implement Docker Compose CD processes tailored for multi-container Docker applications,
  simplifying complex deployments.
- **Modularity**: Boasts easy expansion and customization of pipeline stages to suit a variety of needs.
- **Design Patterns**: Utilizes a variety of design patterns, including Hollywood, Decorator, Delegation, and On-Demand
  Loading, to enhance code maintainability and extensibility.
- **Versatility**: Adaptable to a range of deployment scenarios, from simple app rollouts to intricate multiService
architectures.

## Getting Started with Modular CI/CD Builder

### Prerequisites

To utilize the Modular CI/CD Builder, your Jenkins environment must be pre-configured with essential global properties.
Key-Value pairs and Jenkins credentials form the backbone of the framework, enabling its core functionality.

### Jenkinsfile Initiation & Quick Start

The primary method of engaging with the Modular CI/CD Builder is through the creation of a `jenkinsfile`. This file
should reference `ModularArtifactPipeline.groovy` located in the `vars` directory of this project and must be configured
with the necessary parameters to initiate the pipeline. Here's how to get started:

1. Ensure all prerequisites are met within your Jenkins setup.
2. Import the Modular CI/CD Builder shared library into Jenkins.
3. Create a `jenkinsfile` that includes and correctly sets up all required parameters for your build and deployment
   processes.

The `jenkinsfile` acts as the operational core, interfacing with the Modular CI/CD Builder to orchestrate and execute
your CI/CD pipeline.

```groovy
// A snippet showing how to structure the jenkinsfile parameters
// Define images and build arguments
String nextJsImage = '...' // Image for Next.js service
String ansibleImage = '...' // Image for Ansible service
// ... other parameter definitions ...

// Define Docker Compose settings
Map DockerComposeMap = [
        // ... Docker Compose configurations ...
]

// Invoke the Modular Artifact Pipeline script
ModularArtifactPipeline {
    // ... settings and configurations ...
}
```

A complete `jenkinsfile` example, detailing the specific parameters and structures, should be provided in the
documentation to guide users through setting up their own pipelines.

## Setup and Execution

To utilize the Modular CI/CD Builder, users must first fulfill specific Jenkins setup steps, such as defining
environmental variables and credentials. A step-by-step guide will be provided to ensure a seamless integration into the
Jenkins pipeline.

### Configurable Parameters and Their Mappings

- `BUILD_PLATFORM`: Determines the build context (e.g., 'docker'). It maps to a specific Groovy script
  like `src/pipeline/artifact/ci/build/docker.groovy` which contains the logic for Docker-based builds.

- `BUILD_WORK_FLOW_ARGS_LIST`: Supplies the necessary arguments for the specified `BUILD_PLATFORM`. This list contains
  configuration details like Docker image names, Dockerfile templates, and more.

- `BUILD_TYPE`: Indicates the trigger type for the build process (e.g., 'Manual'), allowing for different types of build
  initiation.

- `MODULE`: Specifies the strategy module to be used (e.g., 'DockerCompose'). This maps to a Groovy script such
  as `src/pipeline/module/strategy/DockerCompose.groovy`, which orchestrates the Continuous Deployment process using
  Docker Compose.

- `MODULE_ARG_MAP`: Provides the required arguments for the specified `MODULE`. This map includes necessary details for
  the deployment process, like credential IDs, project names, and service configurations.

## `jenkinsfile` Example

A `jenkinsfile` serves as the entry point for the Jenkins pipeline, referencing the `ModularArtifactPipeline.groovy`
script and specifying the parameters that guide the automation process. Below is a conceptual outline of
a `jenkinsfile`:

```groovy
@Library('ModularCICD@VERSION') _

// Define Docker images and other parameters
String dockerImage = 'your-docker-image'
// ... other parameter definitions ...

// Set up build and module arguments
ArrayList buildArgs = [ /* ... build arguments ... */]
Map moduleArgs = [/* ... module arguments ... */]

// Execute the Modular Artifact Pipeline
ModularArtifactPipeline {
    // ... settings and configurations ...
}
```

This example illustrates how users can customize their CI/CD pipeline execution by specifying various parameters,
adhering to the IoC principle.

## Key Components

### Ansible Integration

- Automation scripts for generating Dockerfiles tailored for Ansible configurations, facilitating seamless integration
  with automation tools.

### CI/CD Pipeline Scripts

- A suite of Groovy scripts for infrastructure builds, platform-specific builds (e.g., Android, iOS), and Docker
  operations, demonstrating the project's adaptability to various development environments.

### Deployment Configuration Management

- Tools for managing deployment configurations, tags, and remote connections, showcasing sophisticated deployment
  strategies and remote management capabilities.

### Service Launch and Verification

- Interfaces and scripts for launching services and verifying them post-deployment, illustrating the project's
  commitment to reliability and service quality.

### Tools and Utilities

- A collection of utilities for parsing Ansible configurations and handling YAML models, emphasizing the project's
  support for complex configuration management.

### Jenkins Shared Library Variables

- This component underlines the project's integration with Jenkins, facilitating the creation of modular and
  customizable pipelines within the Jenkins ecosystem.
