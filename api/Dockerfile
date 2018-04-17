FROM node:9.11-alpine

RUN apk add --update --no-cache \
    curl \
    bash

WORKDIR /usr/src/app
COPY package*.json ./

RUN npm install --only=production

COPY . .

EXPOSE 8080
HEALTHCHECK --interval=5s --timeout=3s \
            CMD curl --fail http://localhost:8080/status || exit 1

ENTRYPOINT npm start