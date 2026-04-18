# ---------- FRONTEND BUILD ----------
FROM node:20-alpine AS frontend-build

WORKDIR /frontend

# install deps
COPY makemytour/package*.json ./
RUN npm install

# copy source
COPY makemytour/ .

# build Next.js (must have output: "standalone")
RUN npm run build


# ---------- BACKEND BUILD ----------
FROM maven:3.9.5-eclipse-temurin-17 AS backend-build

WORKDIR /backend

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests


# ---------- FINAL IMAGE ----------
FROM node:20-alpine

# install Java
RUN apk add --no-cache openjdk17

WORKDIR /app

# copy backend jar
COPY --from=backend-build /backend/target/*.jar app.jar

# copy Next.js standalone build
COPY --from=frontend-build /frontend/.next/standalone ./
COPY --from=frontend-build /frontend/.next/static ./.next/static
COPY --from=frontend-build /frontend/public ./public

# expose ports
EXPOSE 8080 3000

# run both backend + frontend
CMD ["sh", "-c", "java -jar app.jar & node server.js"]