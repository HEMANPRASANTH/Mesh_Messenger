# Docker Build Instructions

This project has been containerized to allow for consistent Android builds using Docker.

## Prerequisites
- **Docker Desktop for Windows**: You need to install Docker Desktop. [Download here](https://www.docker.com/products/docker-desktop/).
  - After installing, make sure to start the "Docker Desktop" application.
  - You may need to log out and log back in (or restart your computer) for the `docker` command to work in your terminal.

## How to Build

### Using Docker Compose (Recommended)
This method automatically maps the output directory so you can easily access the built APK.

1. Open a terminal in the project root.
2. Run the build:
   ```powershell
   docker compose up --build
   ```
3. Once the process completes, the APK will be available on your host machine at:
   `app/build/outputs/apk/debug/app-debug.apk`

### Using Docker Directly

1. Build the image:
   ```bash
   docker build -t mesh-messenger-builder .
   ```

2. Run the container:
   ```bash
   docker run --rm -v "$(pwd)/app/build/outputs:/app/app/build/outputs" mesh-messenger-builder
   ```

## Notes
- The Dockerfile uses **OpenJDK 17**.
- It installs **Android SDK 35** and **Build Tools 35.0.0** as defined in the project configuration.
- The `google-services.json` file (if used) needs to be present in the `app/` directory before building.
