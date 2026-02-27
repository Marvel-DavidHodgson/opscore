#!/bin/bash

# OpsCore Development Startup Script
# This script starts both backend and frontend in development mode

echo "🚀 Starting OpsCore Development Environment..."
echo ""

# Check if PostgreSQL is running
if ! pg_isready -q; then
    echo "⚠️  PostgreSQL is not running. Starting PostgreSQL..."
    brew services start postgresql 2>/dev/null || echo "Please start PostgreSQL manually"
    sleep 2
fi

# Check if database exists
if ! psql -U postgres -lqt | cut -d \| -f 1 | grep -qw opscore_db; then
    echo "📦 Creating database opscore_db..."
    createdb opscore_db
fi

echo ""
echo "Starting Backend (Spring Boot)..."
echo "→ http://localhost:8080"
echo "→ Swagger UI: http://localhost:8080/swagger-ui.html"
cd backend
mvn spring-boot:run > ../backend.log 2>&1 &
BACKEND_PID=$!
echo "Backend PID: $BACKEND_PID"

# Wait for backend to start
echo "Waiting for backend to start..."
for i in {1..30}; do
    if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1 || \
       curl -s http://localhost:8080/api/auth/login > /dev/null 2>&1; then
        echo "✅ Backend is ready!"
        break
    fi
    sleep 2
    echo -n "."
done

echo ""
echo "Starting Frontend (Vite + React)..."
echo "→ http://localhost:5173"
cd ../frontend
npm run dev > ../frontend.log 2>&1 &
FRONTEND_PID=$!
echo "Frontend PID: $FRONTEND_PID"

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "✅ OpsCore is now running!"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "📱 Frontend:  http://localhost:5173"
echo "🔧 Backend:   http://localhost:8080"
echo "📚 Swagger:   http://localhost:8080/swagger-ui.html"
echo ""
echo "📊 Login credentials:"
echo "   Email:    admin@demo.com"
echo "   Password: Admin123!"
echo ""
echo "📝 Logs:"
echo "   Backend:  tail -f backend.log"
echo "   Frontend: tail -f frontend.log"
echo ""
echo "🛑 To stop servers:"
echo "   kill $BACKEND_PID $FRONTEND_PID"
echo "   or run: ./stop.sh"
echo ""

# Save PIDs to file for stop script
echo "$BACKEND_PID" > .pids
echo "$FRONTEND_PID" >> .pids

# Keep script running
echo "Press Ctrl+C to view logs, or run './stop.sh' to stop all services"
wait
