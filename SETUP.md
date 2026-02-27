# 🚀 Quick Setup Guide - OpsCore

## Step-by-Step Setup (5 minutes)

### 1. Database Setup

```bash
# Start PostgreSQL (if not running)
# macOS with Homebrew:
brew services start postgresql

# Create database
createdb opscore_db
```

### 2. Backend Setup

```bash
# Navigate to backend directory
cd ~/Desktop/opscore/backend

# Build and run (Flyway will auto-migrate)
mvn clean install
mvn spring-boot:run
```

**Expected output:**
```
Started OpsCoreApplication in X seconds
Flyway migrations completed successfully
```

**Verify backend:**
- Open http://localhost:8080/swagger-ui.html
- You should see the API documentation

### 3. Frontend Setup

```bash
# Open a new terminal
cd ~/Desktop/opscore/frontend

# Install dependencies
npm install

# Start development server
npm run dev
```

**Expected output:**
```
VITE v5.x.x ready in X ms
Local: http://localhost:5173/
```

### 4. Login to Application

1. Open http://localhost:5173 in your browser
2. Login with:
   - **Email:** `admin@demo.com`
   - **Password:** `Admin123!`
3. You'll be redirected to the dashboard

### 5. Test API Endpoints

#### Login via cURL:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -H "X-Tenant-ID: 00000000-0000-0000-0000-000000000001" \
  -d '{
    "email": "admin@demo.com",
    "password": "Admin123!"
  }'
```

#### Get Items (with token):
```bash
# Replace YOUR_ACCESS_TOKEN with token from login response
curl -X GET http://localhost:8080/api/items \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

#### Create an Item:
```bash
curl -X POST http://localhost:8080/api/items \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Test Item",
    "description": "This is a test item",
    "category": "Testing"
  }'
```

## Troubleshooting

### Backend won't start
- **Check Java version:** `java -version` (should be 17+)
- **Check PostgreSQL:** `psql -U postgres -d opscore_db -c "SELECT 1;"`
- **Check port 8080:** `lsof -i :8080` (kill if occupied)

### Frontend won't start
- **Check Node version:** `node -v` (should be 18+)
- **Clear cache:** `rm -rf node_modules package-lock.json && npm install`
- **Check port 5173:** `lsof -i :5173`

### Can't login
- **Check backend logs** for authentication errors
- **Verify database has demo user:**
  ```sql
  psql -U postgres -d opscore_db
  SELECT * FROM users WHERE email = 'admin@demo.com';
  ```

### Database migration issues
- **Reset database:**
  ```bash
  dropdb opscore_db
  createdb opscore_db
  # Restart backend - Flyway will re-migrate
  ```

## Next Steps

✅ **Explore the Dashboard** - View KPIs and recent items  
✅ **Create Users** - Add team members with different roles  
✅ **Create Items** - Test the workflow system  
✅ **Test Approval Flow** - Submit → Approve → Close  
✅ **View Audit Logs** - See all system activities (Admin only)  
✅ **Generate Reports** - Export items as CSV  
✅ **Customize Tenant** - Update branding and label overrides  

## Default Accounts

| Email | Password | Role | Description |
|-------|----------|------|-------------|
| admin@demo.com | Admin123! | ADMIN | Full system access |

Create additional users via:
- API: `POST /api/users`
- Or add to `V2__create_users.sql` migration

## Architecture at a Glance

```
┌─────────────┐      ┌─────────────┐      ┌─────────────┐
│   Browser   │ ───▶ │   Vite      │ ───▶ │   Spring    │
│  (React)    │      │   (5173)    │      │   Boot      │
│             │ ◀─── │   Proxy     │ ◀─── │   (8080)    │
└─────────────┘      └─────────────┘      └─────────────┘
                                                  │
                                                  ▼
                                           ┌─────────────┐
                                           │ PostgreSQL  │
                                           │   (5432)    │
                                           └─────────────┘
```

## Resources

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **Frontend:** http://localhost:5173
- **API Base:** http://localhost:8080/api

## Support

Found an issue? Check [README.md](README.md) for detailed documentation.

---

**Happy Coding! 🎉**
