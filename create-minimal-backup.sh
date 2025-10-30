#!/bin/bash

# 오프라인 빌드를 위한 최소 Gradle 백업 스크립트 (개선 버전)
# 목표: 1.1GB -> 약 600MB로 감소

set -e

GRADLE_HOME="${HOME}/.gradle"
BACKUP_NAME="gradle-minimal-backup.tar.gz"

echo "======================================"
echo "  Gradle 최소 백업 생성"
echo "======================================"
echo ""

echo "현재 .gradle 크기 확인 중..."
ORIGINAL_SIZE=$(du -sh "$GRADLE_HOME" 2>/dev/null | cut -f1 || echo "unknown")
echo "원본 크기: $ORIGINAL_SIZE"
echo ""

echo "백업 생성 중..."
echo "  ✓ modules-2 (의존성 JAR 파일)"
echo "  ✓ jars-9 (변환된 JAR)"
echo "  ✓ wrapper (Gradle 배포본)"
echo "  ✓ 8.14.3 메타데이터 (transforms 제외)"
echo ""

cd "$GRADLE_HOME"
tar -czf "$OLDPWD/$BACKUP_NAME" \
    --exclude='caches/8.14.3/transforms' \
    --exclude='daemon' \
    --exclude='native' \
    --exclude='notifications' \
    --exclude='workers' \
    --exclude='.tmp' \
    caches/modules-2 \
    caches/jars-9 \
    caches/8.14.3 \
    caches/journal-1 \
    caches/build-cache-1 \
    wrapper

cd "$OLDPWD"

BACKUP_SIZE=$(du -sh "$BACKUP_NAME" | cut -f1)

echo "======================================"
echo "  백업 완료!"
echo "======================================"
echo ""
echo "원본 크기:      $ORIGINAL_SIZE"
echo "백업 크기:      $BACKUP_SIZE"
echo "절약:           약 500MB (transforms 제외)"
echo ""
echo "생성된 파일: $BACKUP_NAME"
echo ""

echo "======================================"
echo "  제외된 항목 (재생성 가능)"
echo "======================================"
echo ""
echo "✗ transforms/      (~505MB) - 빌드 시 자동 재생성"
echo "✗ daemon/          (~1MB)   - Gradle 데몬 로그"
echo "✗ native/          (~600KB) - 네이티브 라이브러리"
echo "✗ notifications/   (작음)   - 알림 설정"
echo "✗ workers/         (작음)   - 워커 캐시"
echo ""

echo "======================================"
echo "  오프라인 환경에서 복원 방법"
echo "======================================"
echo ""
echo "tar -xzf $BACKUP_NAME -C ~/"
echo "./gradlew clean build --offline"
echo ""
