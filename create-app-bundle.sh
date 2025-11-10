#!/bin/bash

APP_NAME="Tetris"
APP_DIR="app/build/dist/${APP_NAME}.app"
JAR_FILE="app/build/libs/app.jar"

# 앱 번들 구조 생성
mkdir -p "${APP_DIR}/Contents/MacOS"
mkdir -p "${APP_DIR}/Contents/Resources"

# 실행 스크립트 생성
cat > "${APP_DIR}/Contents/MacOS/${APP_NAME}" << 'EOF'
#!/bin/bash
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
APP_DIR="$(cd "$SCRIPT_DIR/../.." && pwd)"
JAR_FILE="$APP_DIR/Resources/app.jar"

# 시스템 Java 찾기
JAVA_HOME=$(/usr/libexec/java_home 2>/dev/null)
JAVA="$JAVA_HOME/bin/java"

# JavaFX JAR 파일 경로 (Fat JAR에 포함되어 있음)
exec "$JAVA" \
    --add-exports=javafx.graphics/com.sun.javafx.application=ALL-UNNAMED \
    -jar "$JAR_FILE"
EOF

chmod +x "${APP_DIR}/Contents/MacOS/${APP_NAME}"

# JAR 파일 복사
cp "${JAR_FILE}" "${APP_DIR}/Contents/Resources/"

# Info.plist 생성
cat > "${APP_DIR}/Contents/Info.plist" << EOF
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
    <key>CFBundleExecutable</key>
    <string>${APP_NAME}</string>
    <key>CFBundleIdentifier</key>
    <string>com.tetris.game</string>
    <key>CFBundleName</key>
    <string>${APP_NAME}</string>
    <key>CFBundlePackageType</key>
    <string>APPL</string>
    <key>CFBundleVersion</key>
    <string>1.0.0</string>
    <key>CFBundleShortVersionString</key>
    <string>1.0.0</string>
    <key>CFBundleIconFile</key>
    <string></string>
    <key>NSHighResolutionCapable</key>
    <true/>
</dict>
</plist>
EOF

echo "✅ ${APP_NAME}.app이 생성되었습니다: ${APP_DIR}"


