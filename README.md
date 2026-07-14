# Saasen-Planning-Support-System
A centralized planning-support prototype designed to improve information visibility and coordination in training course scheduling. The system helps planners manage customer requests, track resource availability (instructors, classrooms, and equipment), and reduce reliance on manual communication such as phone calls and emails.

## Problem & Solution
Scheduling a training course at Saasen requires coordinating multiple resources — instructors, classrooms, and equipment — at the same time. In the current process, much of this information is scattered across emails, phone calls, and individual knowledge, making scheduling slow and dependent on manual back-and-forth between planners and instructors.
This system addresses that gap by centralizing planning-related data and making resource availability directly visible to planners, reducing manual coordination and helping course sessions get scheduled faster.

## Key Features

- Centralized customer request and course scheduling management
- Real-time visibility into instructor and classroom availability
- Automatic conflict detection to prevent double-booking of resources
- Freelancer fallback workflow for sessions without an available internal instructor
- Support for both on-site (Saasen) and external (customer-location) training sessions
- Role-based access for planners, instructors, freelancers, and customers

## Tech Stack

- Backend: Spring Boot (Java)
- Framework: JHipster (entity-driven, rapid prototyping)
- Database: Relational database (JHipster-generated persistence layer)
- Architecture: Layered architecture (UI, Application/Workflow, Domain, Data/Persistence, Integration & Communication)

## Architecture
The system follows a layered architecture separating UI, workflow orchestration, scheduling logic, data persistence, and external integrations (notifications, calendar sync).

Data Model: View on dbdiagram.io
Data Flow Diagram: View diagram
High-Level Architecture Diagrams: View diagrams
