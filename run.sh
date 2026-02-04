#!/bin/zsh
# Start Kafka Demo Application
# Auto-handles Docker Desktop installation and startup on macOS

set -e

cd "$(dirname "$0")"

DOCKER_DMG_URL="https://desktop.docker.com/mac/main/arm64/Docker.dmg"
DOCKER_DMG_INTEL_URL="https://desktop.docker.com/mac/main/amd64/Docker.dmg"
DOCKER_APP="/Applications/Docker.app"
DOWNLOADS_DIR="$HOME/Downloads"

echo "=== Kafka Demo Application ==="

# Detect architecture
ARCH=$(uname -m)
if [[ "$ARCH" == "arm64" ]]; then
    DMG_URL="$DOCKER_DMG_URL"
    echo "Detected: Apple Silicon (arm64)"
else
    DMG_URL="$DOCKER_DMG_INTEL_URL"
    echo "Detected: Intel (x86_64)"
fi

# Function: Check if Docker CLI exists
check_docker_cli() {
    command -v docker &> /dev/null
}

# Function: Check if Docker Desktop is installed
check_docker_desktop_installed() {
    [[ -d "$DOCKER_APP" ]]
}

# Function: Check if Docker daemon is running
check_docker_running() {
    docker info &> /dev/null 2>&1
}

# Function: Wait for Docker to be ready
wait_for_docker() {
    local max_attempts=60
    local attempt=1
    
    echo "⏳ Waiting for Docker to start..."
    while [[ $attempt -le $max_attempts ]]; do
        if check_docker_running; then
            echo "✅ Docker is ready!"
            return 0
        fi
        printf "."
        sleep 2
        ((attempt++))
    done
    
    echo ""
    echo "❌ Docker failed to start within 2 minutes"
    return 1
}

# Function: Start Docker Desktop
start_docker_desktop() {
    echo "🚀 Starting Docker Desktop..."
    open -a Docker
    wait_for_docker
}

# Function: Download Docker Desktop
download_docker() {
    local dmg_path="$DOWNLOADS_DIR/Docker.dmg"
    
    echo "📥 Downloading Docker Desktop..."
    echo "   URL: $DMG_URL"
    echo "   Destination: $dmg_path"
    echo ""
    
    if curl -L --progress-bar -o "$dmg_path" "$DMG_URL"; then
        echo ""
        echo "✅ Download complete: $dmg_path"
        return 0
    else
        echo "❌ Download failed"
        return 1
    fi
}

# Function: Install Docker Desktop from DMG
install_docker() {
    local dmg_path="$DOWNLOADS_DIR/Docker.dmg"
    
    if [[ ! -f "$dmg_path" ]]; then
        echo "❌ Docker.dmg not found at $dmg_path"
        return 1
    fi
    
    echo "📦 Mounting Docker.dmg..."
    local mount_point=$(hdiutil attach "$dmg_path" -nobrowse | grep "/Volumes" | awk '{print $3}')
    
    if [[ -z "$mount_point" ]]; then
        echo "❌ Failed to mount DMG"
        return 1
    fi
    
    echo "📁 Copying Docker.app to /Applications..."
    echo "   (This may require your password)"
    
    if cp -R "$mount_point/Docker.app" /Applications/ 2>/dev/null; then
        echo "✅ Docker Desktop installed"
    else
        # Try with sudo if permission denied
        sudo cp -R "$mount_point/Docker.app" /Applications/
        echo "✅ Docker Desktop installed (with sudo)"
    fi
    
    echo "💿 Unmounting DMG..."
    hdiutil detach "$mount_point" -quiet
    
    echo "🗑️  Cleaning up downloaded DMG..."
    rm -f "$dmg_path"
    
    return 0
}

# Main logic
main() {
    # Step 1: Check if Docker Desktop is installed
    if ! check_docker_desktop_installed; then
        echo "❌ Docker Desktop is not installed"
        echo ""
        echo "Options:"
        echo "  1) Auto-download and install Docker Desktop"
        echo "  2) Open Docker website to download manually"
        echo "  3) Exit"
        echo ""
        read -r "choice?Select option [1/2/3]: "
        
        case "$choice" in
            1)
                download_docker && install_docker
                if [[ $? -ne 0 ]]; then
                    echo "❌ Installation failed"
                    exit 1
                fi
                ;;
            2)
                echo "Opening Docker Desktop download page..."
                open "https://www.docker.com/products/docker-desktop/"
                echo "After installation, run this script again."
                exit 0
                ;;
            *)
                echo "Exiting."
                exit 0
                ;;
        esac
    fi
    
    # Step 2: Check if Docker daemon is running
    if ! check_docker_running; then
        echo "⚠️  Docker Desktop is installed but not running"
        start_docker_desktop
        if [[ $? -ne 0 ]]; then
            echo ""
            echo "💡 Try starting Docker Desktop manually:"
            echo "   open -a Docker"
            exit 1
        fi
    else
        echo "✅ Docker is running"
    fi
    
    # Step 3: Build and run application
    echo ""
    echo "🔨 Building application..."
    mvn clean package -DskipTests -q
    
    echo ""
    echo "🚀 Starting application..."
    echo "   App URL:      http://localhost:8081"
    echo "   Kafka UI:     http://localhost:8080"
    echo "   Actuator:     http://localhost:8081/actuator/health"
    echo ""
    echo "Press Ctrl+C to stop"
    echo ""
    
    mvn spring-boot:run
}

main "$@"
