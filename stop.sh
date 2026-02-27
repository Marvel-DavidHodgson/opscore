#!/bin/bash

# OpsCore Stop Script
# Stops both backend and frontend services

echo "🛑 Stopping OpsCore services..."

if [ -f .pids ]; then
    while read pid; do
        if ps -p $pid > /dev/null 2>&1; then
            echo "Stopping process $pid..."
            kill $pid
        fi
    done < .pids
    rm .pids
    echo "✅ All services stopped"
else
    echo "No PID file found. Searching for processes..."
    
    # Kill Spring Boot
    pkill -f "spring-boot:run" && echo "✅ Backend stopped" || echo "⚠️  Backend not running"
    
    # Kill Vite
    pkill -f "vite" && echo "✅ Frontend stopped" || echo "⚠️  Frontend not running"
fi

echo ""
echo "Services stopped successfully!"
