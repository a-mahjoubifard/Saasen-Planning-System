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

- **Data Model:** [View on dbdiagram.io](https://dbdiagram.io/d/Saasen_datamodel-6a0593b154a51d93d3280549)
- **Data Flow Diagram:** [View diagram](https://stichtingfontys-my.sharepoint.com/:u:/r/personal/576001_student_fontys_nl/_layouts/15/doc2.aspx?sourcedoc=%7BF161978B-914F-48E4-8A3B-982C8081D54B%7D&file=Drawing.vsdx&action=default&mobileredirect=true&DefaultItemOpen=1&ct=1784018091098&wdOrigin=OFFICECOM-WEB.START.EDGEWORTH&cid=86c2de3d-1c6f-43f8-be3c-3ca70e1a0668&wdPreviousSessionSrc=HarmonyWeb&wdPreviousSession=e39f8e0e-4904-4ef2-8b1d-fcf43b66f2a3)
- **High-Level Architecture Diagrams:** [View diagrams](https://drive.google.com/file/d/1XTn_EyY5GEifvLfKudASrGAVWBBz7VZa/view?usp=sharing)
