# Tetris 게임
서울과학기술대학교 소프트웨어 공학 실습 프로젝트

## 빌드 및 실행

### 일반 실행
```bash
./gradlew run
```

### JAR 파일 빌드
```bash
./gradlew build
```
생성된 JAR 파일: `app/build/libs/tetris-app.jar`

### macOS 실행 파일 만들기

#### 1. .app 번들 생성
```bash
./gradlew createMacApp
```
생성된 앱: `app/build/dist/Tetris.app`

#### 2. DMG 파일 직접 생성
```bash
./gradlew packageMac
```
생성된 DMG: `app/build/dist/Tetris.dmg`

#### 3. .app 번들에서 DMG 생성
```bash
./gradlew createMacDmg
```
먼저 `.app` 번들을 생성한 후 DMG로 변환합니다.

## 데이터 저장 위치

게임 설정과 스코어 파일은 다음 위치에 저장됩니다:

- **macOS**: `~/Library/Application Support/Tetris/`
- **Windows**: `%APPDATA%/Tetris/`
- **Linux**: `~/.local/share/Tetris/`

JAR 파일이나 실행 파일을 어디에 설치하더라도, 사용자 데이터는 위의 표준 위치에 저장되므로 정상적으로 작동합니다.

## 요구사항

- Java 21 이상
- macOS에서 DMG 생성 시 macOS에서 실행해야 합니다
