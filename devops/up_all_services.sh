#!/bin/bash
echo "Iniciando infraestructura de Yape Challenge..."

docker compose up -d postgres zookeeper kafka kafka-init

echo "Inicializando kafka..."
sleep 10

docker compose up -d transaction-service antifraud-service

echo "success: Transaction API: http://localhost:8080 | Antifraud: http://localhost:8081"