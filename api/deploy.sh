#!/bin/bash
# Deploys the API to the production server.

ssh -o StrictHostKeyChecking=no ${DEPLOY_USER}@${DEPLOY_SRV} << EOF
  cd ng_core
  sudo docker-compose pull
  sudo docker-compose up -d --build
EOF