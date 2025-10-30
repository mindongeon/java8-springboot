#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Windows용 Gradle 캐시 ZIP 백업 생성 스크립트
Windows 기본 압축 프로그램으로 풀 수 있는 ZIP 형식
"""

import os
import zipfile
import sys
from pathlib import Path

def should_exclude(file_path):
    """제외할 파일/디렉토리 확인"""
    exclude_patterns = [
        '.lock',
        'transforms',
        'daemon',
        'native',
        'notifications',
        'workers',
        '.tmp'
    ]

    path_str = str(file_path)
    for pattern in exclude_patterns:
        if pattern in path_str:
            return True
    return False

def create_gradle_backup():
    """Gradle 캐시를 ZIP으로 백업"""

    gradle_home = Path.home() / '.gradle'
    output_file = Path('gradle-backup.zip')

    print("=" * 60)
    print("  Windows용 Gradle 캐시 ZIP 백업 생성")
    print("=" * 60)
    print()

    if not gradle_home.exists():
        print("ERROR: {} 디렉토리를 찾을 수 없습니다.".format(gradle_home))
        sys.exit(1)

    # 백업할 디렉토리 목록
    backup_dirs = [
        'caches/modules-2',
        'caches/jars-9',
        'caches/8.14.3',
        'caches/journal-1',
        'caches/build-cache-1',
        'wrapper'
    ]

    print("원본: {}".format(gradle_home))
    print("대상: {}".format(output_file.absolute()))
    print()

    print("백업 중...")

    file_count = 0
    excluded_count = 0
    total_size = 0

    with zipfile.ZipFile(output_file, 'w', zipfile.ZIP_DEFLATED, compresslevel=9) as zipf:
        for backup_dir in backup_dirs:
            source_path = gradle_home / backup_dir

            if not source_path.exists():
                print("  [SKIP] {}: 존재하지 않음".format(backup_dir))
                continue

            print("  [*] 처리 중: {}".format(backup_dir))

            if source_path.is_file():
                # 파일인 경우
                if not should_exclude(source_path):
                    arcname = str(source_path.relative_to(gradle_home))
                    zipf.write(source_path, arcname)
                    file_count += 1
                    total_size += source_path.stat().st_size
            else:
                # 디렉토리인 경우
                for root, dirs, files in os.walk(source_path):
                    root_path = Path(root)

                    # 제외할 디렉토리는 순회하지 않음
                    dirs[:] = [d for d in dirs if not should_exclude(root_path / d)]

                    for file in files:
                        file_path = root_path / file

                        if should_exclude(file_path):
                            excluded_count += 1
                            continue

                        try:
                            arcname = str(file_path.relative_to(gradle_home))
                            zipf.write(file_path, arcname)
                            file_count += 1
                            total_size += file_path.stat().st_size

                            # 진행상황 표시
                            if file_count % 100 == 0:
                                size_mb = total_size / (1024 * 1024)
                                print("     {} 파일 처리됨 ({:.1f}MB)".format(file_count, size_mb))

                        except Exception as e:
                            print("  [SKIP] {}: {}".format(file_path.name, e))
                            excluded_count += 1

    # 결과 출력
    output_size = output_file.stat().st_size
    output_mb = output_size / (1024 * 1024)
    original_mb = total_size / (1024 * 1024)
    compression_ratio = (1 - output_size / total_size) * 100 if total_size > 0 else 0

    print()
    print("=" * 60)
    print("  백업 완료!")
    print("=" * 60)
    print()
    print("생성된 파일: {}".format(output_file.absolute()))
    print("압축 전 크기: {:.1f}MB".format(original_mb))
    print("압축 후 크기: {:.1f}MB".format(output_mb))
    print("압축률: {:.1f}%".format(compression_ratio))
    print()
    print("포함된 파일: {}개".format(file_count))
    print("제외된 파일: {}개".format(excluded_count))
    print()

    print("=" * 60)
    print("  제외된 항목 (빌드 시 자동 재생성)")
    print("=" * 60)
    print()
    print("  *.lock         - 락 파일")
    print("  transforms/    - 변환 캐시 (~505MB 절약)")
    print("  daemon/        - Gradle 데몬 로그")
    print("  native/        - 네이티브 라이브러리")
    print("  notifications/ - 알림 설정")
    print("  workers/       - 워커 캐시")
    print()

    print("=" * 60)
    print("  Windows에서 압축 해제 방법")
    print("=" * 60)
    print()
    print("1. gradle-backup.zip 파일 우클릭")
    print("2. '압축 풀기' 또는 'Extract All' 선택")
    print("3. 대상 폴더 선택: C:\\Users\\<사용자이름>\\")
    print("   (자동으로 .gradle 폴더가 생성됨)")
    print()
    print("또는 PowerShell:")
    print("  Expand-Archive -Path gradle-backup.zip -DestinationPath $env:USERPROFILE")
    print()

    # ZIP 파일 크기 확인
    if output_mb > 100:
        print("주의: ZIP 파일이 100MB를 초과합니다.")
        print("현재 크기: {:.1f}MB".format(output_mb))
        print()
        print("Git에 올리려면 분할이 필요합니다.")
        print()
    else:
        print("OK! ZIP 파일 크기가 100MB 이하입니다.")
        print("Git에 직접 업로드 가능!")
        print()

if __name__ == '__main__':
    try:
        create_gradle_backup()
    except KeyboardInterrupt:
        print("\n\n중단됨")
        sys.exit(1)
    except Exception as e:
        print("\nERROR: {}".format(e))
        import traceback
        traceback.print_exc()
        sys.exit(1)
