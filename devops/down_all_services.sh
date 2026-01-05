#!/bin/bash
echo "Deteniendo servicios de Yape Challenge..."

# Detiene contenedores y elimina volumen
docker compose down -v

echo "success: Servicios detenidos correctamente."