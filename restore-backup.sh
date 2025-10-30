#!/bin/bash

# Gradle 백업 복원 스크립트 (Git에서 클론 후 사용)

set -e

echo "======================================"
echo "  Gradle 백업 복원"
echo "======================================"
echo ""

# part 파일 확인
PARTS=($(ls gradle-backup.tar.gz.part* 2>/dev/null | sort))
PART_COUNT=${#PARTS[@]}

if [ $PART_COUNT -eq 0 ]; then
    echo "❌ 오류: gradle-backup.tar.gz.part* 파일을 찾을 수 없습니다."
    echo ""
    echo "현재 디렉토리에 part 파일이 있는지 확인하세요:"
    echo "  - gradle-backup.tar.gz.part00"
    echo "  - gradle-backup.tar.gz.part01"
    echo "  - gradle-backup.tar.gz.part02"
    echo ""
    exit 1
fi

echo "발견된 part 파일: $PART_COUNT 개"
for part in "${PARTS[@]}"; do
    SIZE=$(du -sh "$part" 2>/dev/null | cut -f1)
    echo "  ✓ $part ($SIZE)"
done
echo ""

echo "1. 분할된 파일 합치는 중..."
cat gradle-backup.tar.gz.part* > gradle-backup.tar.gz

if [ $? -eq 0 ]; then
    MERGED_SIZE=$(du -sh gradle-backup.tar.gz | cut -f1)
    echo "   ✓ 합치기 완료 ($MERGED_SIZE)"
else
    echo "   ❌ 파일 합치기 실패"
    exit 1
fi
echo ""

echo "2. ~/.gradle/ 디렉토리 백업 중..."
if [ -d ~/.gradle ]; then
    BACKUP_DIR=~/.gradle.backup.$(date +%Y%m%d_%H%M%S)
    mv ~/.gradle "$BACKUP_DIR"
    echo "   ✓ 기존 디렉토리 백업됨: $BACKUP_DIR"
fi
echo ""

echo "3. Gradle 캐시 압축 해제 중..."
tar -xzf gradle-backup.tar.gz -C ~/

if [ $? -eq 0 ]; then
    echo "   ✓ ~/.gradle/ 복원 완료"
else
    echo "   ❌ 압축 해제 실패"
    exit 1
fi
echo ""

# 복원된 내용 확인
RESTORED_SIZE=$(du -sh ~/.gradle 2>/dev/null | cut -f1)

echo "======================================"
echo "  복원 완료!"
echo "======================================"
echo ""
echo "복원된 디렉토리: ~/.gradle"
echo "크기: $RESTORED_SIZE"
echo ""
echo "포함된 내용:"
echo "  ✓ caches/modules-2  - 의존성 JAR 파일"
echo "  ✓ caches/jars-9     - 변환된 JAR"
echo "  ✓ caches/8.14.3     - Gradle 메타데이터"
echo "  ✓ wrapper           - Gradle 배포본"
echo ""

echo "======================================"
echo "  오프라인 빌드 실행"
echo "======================================"
echo ""
echo "1. Java 환경 설정:"
echo "   export JAVA_HOME=\"/c/Program Files/Java/jdk-17\""
echo "   export PATH=\"\$JAVA_HOME/bin:\$PATH\""
echo ""
echo "2. 오프라인 빌드 실행:"
echo "   ./gradlew clean build --offline"
echo ""
echo "3. 성공 시:"
echo "   BUILD SUCCESSFUL in Xs"
echo "   생성 파일: build/libs/core-0.0.1-SNAPSHOT.war"
echo ""

# 정리 옵션
read -p "합쳐진 gradle-backup.tar.gz 파일을 삭제할까요? (part 파일은 유지) (y/N): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    rm gradle-backup.tar.gz
    echo "✓ gradle-backup.tar.gz 삭제됨"
else
    echo "✓ gradle-backup.tar.gz 유지됨"
fi
echo ""

echo "완료!"
