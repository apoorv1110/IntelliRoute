# IntelliRoute  -  AI-Powered Query Assignment System


**AI-Powered Query Assignment System** - An intelligent platform that automatically routes technical support queries to the most suitable engineers based on complexity analysis, skill matching, and availability.

## 🎯 Overview

IntelliRoute is a full-stack application that streamlines technical support workflows by:
- **AI-powered complexity scoring** using Google Gemini to analyze query descriptions
- **Intelligent engineer assignment** based on complexity, skills, designation, and current workload
- **Real-time dashboard** for managing engineers, queries, and assignments
- **Automatic SLA monitoring** with escalation for overdue queries
- **Work completion tracking** that frees up engineers automatically

## 🛠️ Tech Stack

### Backend
- **Java 17** - Core language
- **Spring Boot 3.3.2** - Framework
- **Spring Data MongoDB** - Database integration
- **Spring WebFlux** - Reactive HTTP client for Gemini API
- **MongoDB** - NoSQL database
- **Google Gemini API** - AI complexity scoring
- **Lombok** - Boilerplate reduction
- **Maven** - Build tool

### Frontend
- **React 18** - UI framework
- **TypeScript** - Type safety
- **Vite** - Build tool & dev server
- **Tailwind CSS** - Styling
- **Axios** - HTTP client

## 📁 Project Structure

```
IntelliRoute/
├── backend/
│   └── IntelliRoute/
│       ├── Dockerfile                       # Backend Docker config
│       ├── pom.xml                          # Maven dependencies
│       └── src/
│           └── main/
│               ├── java/com/intelliroute/
│               │   ├── IntelliRouteApplication.java    # Main entry point
│               │   ├── config/
│               │   │   └── WebClientConfig.java         # HTTP client setup
│               │   ├── controller/
│               │   │   ├── EngineerController.java      # Engineer CRUD APIs
│               │   │   ├── QueryController.java         # Query CRUD APIs
│               │   │   └── AssignmentController.java   # Assignment APIs
│               │   ├── model/
│               │   │   ├── Engineer.java                # Engineer entity
│               │   │   ├── SupportQuery.java           # Query entity
│               │   │   ├── Assignment.java             # Assignment entity
│               │   │   ├── Designation.java            # Enum: JUNIOR, MID, SENIOR, TECH_LEAD
│               │   │   ├── Priority.java               # Enum: P1, P2, P3
│               │   │   ├── QueryStatus.java            # Enum: PENDING, ASSIGNED, RESOLVED, ESCALATED
│               │   │   └── AssignmentStatus.java       # Enum: ACTIVE, COMPLETED
│               │   ├── repository/
│               │   │   ├── EngineerRepository.java      # MongoDB repository
│               │   │   ├── SupportQueryRepository.java
│               │   │   └── AssignmentRepository.java
│               │   ├── service/
│               │   │   ├── AIClient.java               # Gemini API integration
│               │   │   ├── EngineerService.java         # Engineer business logic
│               │   │   ├── QueryService.java            # Query business logic
│               │   │   └── AssignmentService.java       # Assignment & routing logic
│               │   └── dto/
│               │       ├── EngineerRequest.java         # Request DTOs
│               │       └── CreateQueryRequest.java
│               └── resources/
│                   └── application.yml                  # Configuration
├── frontend/
│   ├── Dockerfile                         # Frontend Docker config
│   ├── nginx.conf                         # Nginx config for Docker
│   ├── package.json                       # NPM dependencies
│   ├── vite.config.ts                     # Vite configuration
│   ├── tailwind.config.js                 # Tailwind CSS config
│   ├── tsconfig.json                      # TypeScript config
│   ├── index.html                         # HTML entry point
│   └── src/
│       ├── main.tsx                       # React entry point
│       ├── App.tsx                        # Main component (tabs UI)
│       ├── api.ts                         # API client functions
│       └── index.css                      # Global styles
├── .env                                   # Environment variables (Docker)
├── .gitignore                             # Git ignore rules
├── docker-compose.yml                     # Docker services orchestration
└── README.md                              # This file
```
## 🔄 Data Flow

```
┌─────────────┐
│   Frontend  │
│  (React)    │
└──────┬──────┘
       │ HTTP REST API
       ▼
┌─────────────────────────────────┐
│   Spring Boot Backend           │
│   ┌─────────────────────────┐   │
│   │  REST Controllers       │   │
│   └───────────┬─────────────┘   │
│               │                 │
│   ┌───────────▼─────────────┐   │
│   │  Service Layer          │   │
│   │  - AssignmentService    │   │
│   │  - QueryService         │   │
│   │  - EngineerService      │   │
│   └───────────┬─────────────┘   │
│               │                 │
│   ┌───────────▼─────────────┐   │
│   │  AIClient (Gemini API)  │   │
│   └───────────┬─────────────┘   │
│               │                 │
│   ┌───────────▼─────────────┐   │
│   │  MongoDB Repositories   │   │
│   └───────────┬─────────────┘   │
└───────────────┼─────────────────┘
                │
                ▼
         ┌──────────────┐
         │   MongoDB    │
         │  (Database)  │
         └──────────────┘

Assignment Flow:
1. User creates query → POST /api/queries
2. Query saved with status=PENDING
3. User clicks "Run Assignment" → POST /api/assignments/run
4. AssignmentService runs:
   a. Finds pending queries
   b. Calls AIClient.predictComplexity() → Gemini API
   c. Scores engineers (skills, capacity, designation match)
   d. Assigns best engineer → Creates Assignment
   e. Updates Engineer.currentLoad++
   f. Updates Query.status=ASSIGNED
5. User marks complete → PUT /api/assignments/{id}/complete
6. AssignmentService:
   a. Sets Assignment.status=COMPLETED
   b. Sets Query.status=RESOLVED
   c. Decrements Engineer.currentLoad--
```

## User Interface 
<img width="1240" height="649" alt="Screenshot 2025-12-16 at 9 02 36 PM" src="https://github.com/user-attachments/assets/826c338c-af8b-4c4c-98c4-3042fd0f448f" />
<img width="1259" height="686" alt="Screenshot 2025-12-16 at 9 02 47 PM" src="https://github.com/user-attachments/assets/2f351c0e-1366-46d6-a041-cf240ce58111" />
<img width="1267" height="691" alt="Screenshot 2025-12-16 at 9 03 46 PM" src="https://github.com/user-attachments/assets/b4c136fc-e93b-48e9-b97e-5fccbb4ea468" />
<img width="1277" height="707" alt="Screenshot 2025-12-16 at 9 33 12 PM" src="https://github.com/user-attachments/assets/96021fa6-ba44-4904-b03a-4deb8817647e" />

## 📋 Prerequisites

Before running the project, ensure you have:

- **Java 17+** - [Download](https://adoptium.net/)
- **Maven 3.6+** - [Download](https://maven.apache.org/download.cgi)
- **Node.js 18+** - [Download](https://nodejs.org/)
- **Docker & Docker Compose** - [Download](https://www.docker.com/products/docker-desktop/)
- **MongoDB** - Cloud (MongoDB Atlas) or local instance
- **Google Gemini API Key** - [Get from Google AI Studio](https://makersuite.google.com/app/apikey)

## 🐳 Running with Docker (Recommended)

The easiest way to run IntelliRoute is using Docker Compose. This spins up the backend, frontend, and a local MongoDB instance automatically.

### 1. Configure Environment

Create a `.env` file in the project root (where `docker-compose.yml` is located):

```bash
touch .env
```

Add your Gemini API key to the `.env` file:

```env
GEMINI_API_KEY=AIzaSy...your-key-here
```

### 2. Start the Application

Run the following command to build and start all services:

```bash
docker-compose up -d --build
```

- **Frontend:** http://localhost:5173
- **Backend:** http://localhost:8080
- **MongoDB:** localhost:27017

### 3. Manage the Application

- **View Logs:** `docker-compose logs -f`
- **Stop App:** `docker-compose down`
- **Rebuild:** `docker-compose up -d --build`

##  Manual Setup Instructions

### Step 1: Clone the Repository

```bash
git clone https://github.com/Ujjwal0207/IntelliRoute.git
cd IntelliRoute
```

### Step 2: Backend Setup

#### 2.1 Configure MongoDB

**Option A: MongoDB Atlas (Cloud - Recommended)**

1. Create a free cluster at [MongoDB Atlas](https://www.mongodb.com/cloud/atlas)
2. Create a database user (e.g., `IntellIRouteDB`)
3. Whitelist your IP address (or `0.0.0.0/0` for development)
4. Copy the connection string (format: `mongodb+srv://user:password@cluster.mongodb.net/database`)

**Option B: Local MongoDB**

1. Install MongoDB locally
2. Start MongoDB service
3. Connection string: `mongodb://localhost:27017/intelliroute`

#### 2.2 Get Gemini API Key

1. Go to [Google AI Studio](https://makersuite.google.com/app/apikey)
2. Sign in with Google account
3. Click "Create API Key"
4. Copy the API key (starts with `AIza...`)

#### 2.3 Configure Environment Variables

Create a `.env` file in `backend/IntelliRoute/`:

```bash
cd backend/IntelliRoute
touch .env
```

Add the following (replace with your actual values):

```env
# MongoDB Configuration
MONGODB_URI=mongodb+srv://username:password@cluster.mongodb.net/intelliroute
MONGODB_DATABASE=intelliroute

# Gemini AI Configuration
GEMINI_API_KEY=AIzaSy...your-key-here
GEMINI_MODEL=gemini-1.5-flash
GEMINI_ENDPOINT=https://generativelanguage.googleapis.com/v1beta

# Optional: Scheduler Configuration
ASSIGNMENT_SCHEDULER_DELAY_MS=5000
SLA_CHECK_MS=60000

# Optional: Server Port
SERVER_PORT=8080
```

**Important:** If your MongoDB password contains `@`, URL-encode it as `%40` in the URI.

**Example:**
```
MONGODB_URI=mongodb+srv://user:pass%40word@cluster.mongodb.net/intelliroute
```

### Step 3: Frontend Setup

```bash
cd frontend
npm install
```

## ▶️ Running the Application

### Start Backend

Open a terminal:

If you have java version 17 installed
```bash
cd backend/IntelliRoute
mvn spring-boot:run
```
If any other java version is installed

```bash
cd backend/IntelliRoute
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
mvn spring-boot:run
```
Wait for the message: `Started IntelliRouteApplication in X.XXX seconds`

The backend will be available at: `http://localhost:8080`

### Start Frontend

Open a **new terminal**:

```bash
cd frontend
npm run dev
```

The frontend will be available at: `http://localhost:5173`

### Access the Application

1. Open your browser and go to: `http://localhost:5173`
2. You'll see three tabs:
   - **Engineers** - Register and manage engineers
   - **Queries** - Create queries and trigger assignments
   - **Assignments** - View and complete assignments

## 📡 API Endpoints

### Engineers

- `GET /api/engineers` - List all engineers
- `POST /api/engineers` - Create engineer
  ```json
  {
    "name": "John Doe",
    "designation": "SENIOR",
    "capacity": 3,
    "skills": ["java", "spring", "kafka"]
  }
  ```

### Queries

- `GET /api/queries` - List all queries
- `POST /api/queries` - Create query
  ```json
  {
    "description": "Kafka consumer lag causing latency",
    "priority": "P1",
    "tags": ["kafka", "infra"]
  }
  ```

### Assignments

- `GET /api/assignments` - List all assignments
- `POST /api/assignments/run` - Trigger assignment cycle manually
- `PUT /api/assignments/{id}/complete` - Mark assignment as complete

### Health Check

- `GET /actuator/health` - Application health status

## 🎨 Features

### 1. AI-Powered Complexity Scoring
- Uses Google Gemini to analyze query descriptions
- Returns complexity score (1.0 - 5.0)
- Falls back to heuristic scoring if Gemini fails

### 2. Intelligent Assignment Algorithm
- **Complexity-based routing:**
  - 1.0-2.0 → Junior Engineers
  - 2.1-3.5 → Mid-level Engineers
  - 3.6-5.0 → Senior/Tech Lead Engineers
- **Scoring factors:**
  - Skill match (query tags vs engineer skills)
  - Available capacity (currentLoad < capacity)
  - Designation fit (matches target designation)
  - Priority boost (P1 queries get preference)

### 3. Automatic Scheduling
- Assignment cycle runs every 5 seconds (configurable)
- SLA check runs every 60 seconds (configurable)
- Auto-escalates queries past SLA deadline

### 4. Work Completion
- Mark assignments as complete via toggle button
- Automatically:
  - Frees up engineer (decrements currentLoad)
  - Marks query as RESOLVED
  - Updates assignment status to COMPLETED

### 5. Real-time Dashboard
- View all engineers with current load
- See all queries with complexity scores
- Track active and completed assignments

## ⚙️ Configuration

### Backend Configuration (`application.yml`)

```yaml
spring:
  data:
    mongodb:
      uri: ${MONGODB_URI:mongodb://localhost:27017/intelliroute}
      database: ${MONGODB_DATABASE:intelliroute}

ai:
  gemini:
    api-key: ${GEMINI_API_KEY:}
    model: ${GEMINI_MODEL:gemini-1.5-flash}
    endpoint: ${GEMINI_ENDPOINT:https://generativelanguage.googleapis.com/v1beta}

assignment:
  scheduler:
    delay-ms: ${ASSIGNMENT_SCHEDULER_DELAY_MS:5000}  # Assignment cycle interval
  sla:
    check-ms: ${SLA_CHECK_MS:60000}                  # SLA check interval

server:
  port: ${SERVER_PORT:8080}
```

### Frontend Configuration (`vite.config.ts`)

The frontend proxies API requests to `http://localhost:8080` by default. To change:

```typescript
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true
    }
  }
}
```

## 🧪 Testing the Application

### 1. Register Engineers

```bash
curl -X POST http://localhost:8080/api/engineers \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Alex",
    "designation": "SENIOR",
    "capacity": 3,
    "skills": ["kafka", "react"]
  }'
```

### 2. Create a Query

```bash
curl -X POST http://localhost:8080/api/queries \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Kafka consumer lag on production causing critical latency",
    "priority": "P1",
    "tags": ["kafka", "infra"]
  }'
```

### 3. Trigger Assignment

```bash
curl -X POST http://localhost:8080/api/assignments/run
```

### 4. View Assignments

```bash
curl http://localhost:8080/api/assignments
```

## 📊 Monitoring

The backend includes Spring Boot Actuator for monitoring:

- **Health:** `http://localhost:8080/actuator/health`
- **Metrics:** `http://localhost:8080/actuator/metrics`
- **Info:** `http://localhost:8080/actuator/info`

## 🔒 Security Notes

- **Never commit** `.env` files or API keys to Git
- Use environment variables or secret managers in production
- Enable CORS properly for production frontend domains
- Consider adding authentication/authorization for production use

## 🐛 Troubleshooting

### Backend won't start
- Check MongoDB connection string (URL-encode `@` as `%40`)
- Verify `GEMINI_API_KEY` is set correctly
- Check Java version: `java -version` (should be 17+)

### Frontend can't connect to backend
- Ensure backend is running on `http://localhost:8080`
- Check browser console for CORS errors
- Verify `vite.config.ts` proxy settings

### 404 on assignment completion
- Restart backend after code changes
- Verify assignment ID exists: `curl http://localhost:8080/api/assignments`

### Gemini API errors
- Check API key validity
- Verify API quota/limits
- Check network connectivity



## 👥 Contributors

- Initial development by Ujjwal0207

---

**Happy Routing! 🚀**


