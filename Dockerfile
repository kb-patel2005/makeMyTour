# ---------- FRONTEND BUILD ----------
FROM node:20-alpine AS frontend-build

WORKDIR /frontend

# copy frontend only
COPY makemytour/package*.json ./
RUN npm install

COPY makemytour/ .
RUN npm run build


# ---------- BACKEND BUILD ----------
FROM maven:3.9.5-eclipse-temurin-17 AS backend-build

WORKDIR /backend

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests


# ---------- FINAL IMAGE ----------
FROM eclipse-temurin:17-jre

WORKDIR /app

# copy backend jar
COPY --from=backend-build /backend/target/*.jar app.jar

# copy frontend build (Next.js standalone)
COPY --from=frontend-build /frontend/.next /app/.next
COPY --from=frontend-build /frontend/public /app/public
COPY --from=frontend-build /frontend/package.json /app/package.json
COPY --from=frontend-build /frontend/node_modules /app/node_modules

# expose ports
EXPOSE 8080 3000

# run both (backend + frontend)
CMD ["sh", "-c", "java -jar app.jar & npm start"]