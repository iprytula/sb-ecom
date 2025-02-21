# Spring Boot eCommerce Application

An advanced eCommerce application built using Spring Boot, demonstrating best practices and production-grade features.

## Overview

This project is the culmination of learning from [Java Spring Boot: Professional eCommerce Project](https://www.udemy.com/course/spring-boot-using-intellij-build-a-real-world-project/) course. It covers fundamental to advanced concepts of Spring Framework and Spring Boot, implementing a full-featured eCommerce platform.

## Features

### Backend
- RESTful APIs with Spring Boot.
- Secure authentication and authorization using Spring Security and JWT.
- Integration with PostgreSQL and MySQL databases using JPA and Hibernate.
- User profile, role, and permission management.
- Shopping cart, order, and payment processing.
- Pagination and sorting for efficient data management.
- Deployment-ready for AWS with advanced deployment options.
- Use of Lombok to reduce boilerplate code.

### Frontend
- Fully responsive UI developed with React.
- Integration of React Router for seamless navigation.
- State management using Redux.
- Advanced React concepts, including custom hooks and forms.
- Styling with Tailwind CSS.

## Technologies Used

### Backend
- Java
- Spring Framework
- Spring Boot
- Spring MVC
- Spring Security
- JSON Web Tokens (JWT)
- JPA/Hibernate
- PostgreSQL/MySQL
- Lombok
- AWS

### Frontend
- React
- React Router
- Redux
- Tailwind CSS
- React Hook Forms

## Getting Started

### Prerequisites
- Basic understanding of Java.
- A computer with an internet connection.
- Tools: IntelliJ IDEA, Java JDK, Node.js (for React).

### Steps to Run the Application

1. **Clone the Repository**
    ```bash
    git clone https://github.com/iprytula/sb-ecom
    ```

2. **Navigate to the Project Directory**
    ```bash
    cd sb-ecom
    ```

3. **Build and Run the Backend**
    ```bash
    ./mvnw spring-boot:run
    ```

4. **Set Up the Frontend**
    ```bash
    cd frontend
    npm install
    npm start
    ```

5. **Access the Application**
   Open your browser and navigate to `http://localhost:3000` for the frontend or `http://localhost:8080` for the backend APIs.
