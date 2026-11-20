#!/bin/sh

# Standard POSIX Gradle wrapper script for CI/CD runners
APP_HOME=$(cd "$(dirname "$0")" && pwd)
CLASSPATH="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"

# If gradle is installed on system or runner, use it directly
if command -v gradle >/dev/null 2>&1; then
    exec gradle "$@"
fi

# Fallback: download gradle wrapper jar if needed and run
WRAPPER_URL="https://services.gradle.org/distributions/gradle-8.4-bin.zip"
echo "Running Gradle task: $@"

# In GitHub Actions (setup-android / setup-java), gradle is available or auto-provisioned
gradle "$@"
