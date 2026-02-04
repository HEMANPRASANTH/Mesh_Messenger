FROM eclipse-temurin:17-jdk-jammy

# Input arguments to facilitate caching or specific versions
ARG ANDROID_COMPILE_SDK="35"
ARG ANDROID_BUILD_TOOLS="35.0.0"
ARG ANDROID_CMDLINE_TOOLS="11076708" 

# Set Environment Variables
ENV ANDROID_HOME=/opt/android-sdk
ENV PATH="${PATH}:${ANDROID_HOME}/cmdline-tools/latest/bin:${ANDROID_HOME}/platform-tools"

# Install dependencies
# 'file' is sometimes needed for analytics checks in some gradle plugins
RUN apt-get update && apt-get install -y \
    wget \
    unzip \
    git \
    file \
    && rm -rf /var/lib/apt/lists/*

# Download and install Android Command Line Tools
WORKDIR /opt
# URL constructed based on standard Google dl layout
RUN wget -q "https://dl.google.com/android/repository/commandlinetools-linux-${ANDROID_CMDLINE_TOOLS}_latest.zip" -O cmdline-tools.zip \
    && unzip -q cmdline-tools.zip \
    && mkdir -p ${ANDROID_HOME}/cmdline-tools \
    && mv cmdline-tools ${ANDROID_HOME}/cmdline-tools/latest \
    && rm cmdline-tools.zip

# Accept Licenses
RUN yes | sdkmanager --licenses > /dev/null

# Install Build Tools and Platforms
RUN sdkmanager "platform-tools" \
    "platforms;android-${ANDROID_COMPILE_SDK}" \
    "build-tools;${ANDROID_BUILD_TOOLS}"

# Create app directory
WORKDIR /app

# Copy Gradle wrapper and properties to cache dependencies
COPY gradle/ gradle/
COPY gradlew .
COPY gradle.properties .
# Remove the Windows-specific java.home property if it exists, as it breaks the Linux build
RUN sed -i '/org.gradle.java.home/d' gradle.properties

COPY build.gradle.kts .
COPY settings.gradle.kts .

# Make gradlew executable
RUN chmod +x gradlew

# Attempt to download dependencies (optional, helps caching)
# We use 'assembleDebug --dry-run' or just 'androidDependencies' if available, 
# but usually just running a build failure is acceptable or we skip this optimization 
# to ensure correctness. We'll skip for robustness.

# Copy source code
COPY . .

# Default command to build the debug APK
# Output will be in /app/app/build/outputs/apk/debug/
CMD ["./gradlew", "assembleDebug"]
