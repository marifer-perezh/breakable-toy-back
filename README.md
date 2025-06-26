## Breakable Toy — Inventory Manager - Backend

This project is part of the Spark Internship Program by Encora.
It is a full-stack Inventory Management System built with:

- 🔙 **Backend**: Java 17 + Spring Boot + Maven 3.5 + H2 DB
- 🎨 **Frontend**: React + Axios + TypeScript + TailwindCSS (don't forget node.js)

```bash
.
├── README.md
├── backend
│   ├── README.md
│   ├── src
│   ├── config
│   ├── controller
│   ├── model
│   ├── repository
│   ├── service
├── frontend
│   ├── README.md
│   ├── src
│   ├── components/products
│   ├── hooks
│   ├── services
│   ├── types
```
---
### How to run backend
- Run the App
```bash
mvn spring-boot:run
```

**The backend runs on port 9090**

### Current Features
- Controller Layer: Handles incoming HTTP requests and delegates to the service layer.
- Service Layer: Contains all business logic. Handles filtering, sorting, pagination, and metric calculations.
- Repository Layer: Uses Spring Data JPA to perform CRUD operations and dynamic queries.
- Database: Uses H2 in persistent mode (saved to file on disk).

### 🧑‍💻 Developed by
- 💡 marifer-perezh
- ✉️ maria.perez@encora.com