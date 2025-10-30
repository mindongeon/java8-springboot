#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
ZIP 파일을 95MB 이하로 분할 (Git 업로드용)
"""

import os
import sys
from pathlib import Path

def split_file(input_file, chunk_size_mb=95):
    """파일을 여러 part로 분할"""

    input_path = Path(input_file)

    if not input_path.exists():
        print("ERROR: {} 파일을 찾을 수 없습니다.".format(input_file))
        sys.exit(1)

    chunk_size = chunk_size_mb * 1024 * 1024  # MB to bytes
    file_size = input_path.stat().st_size
    file_size_mb = file_size / (1024 * 1024)

    print("=" * 60)
    print("  ZIP 파일 분할 (Git 업로드용)")
    print("=" * 60)
    print()
    print("입력 파일: {}".format(input_path))
    print("파일 크기: {:.1f}MB".format(file_size_mb))
    print("분할 크기: {}MB".format(chunk_size_mb))
    print()

    # 예상 part 개수
    num_parts = (file_size + chunk_size - 1) // chunk_size
    print("예상 part 개수: {}개".format(num_parts))
    print()
    print("분할 중...")

    part_num = 0
    bytes_read = 0

    with open(input_path, 'rb') as f:
        while True:
            chunk = f.read(chunk_size)
            if not chunk:
                break

            part_filename = "{}.part{:02d}".format(input_path, part_num)

            with open(part_filename, 'wb') as part_file:
                part_file.write(chunk)

            part_size = len(chunk) / (1024 * 1024)
            bytes_read += len(chunk)
            progress = (bytes_read / file_size) * 100

            print("  [{}/{}] {} ({:.1f}MB) - {:.1f}% 완료".format(
                part_num + 1, num_parts, Path(part_filename).name, part_size, progress))

            part_num += 1

    print()
    print("=" * 60)
    print("  분할 완료!")
    print("=" * 60)
    print()
    print("생성된 파일:")
    for i in range(part_num):
        part_filename = "{}.part{:02d}".format(input_path, i)
        part_size = Path(part_filename).stat().st_size / (1024 * 1024)
        print("  {} ({:.1f}MB)".format(part_filename, part_size))
    print()

    print("=" * 60)
    print("  Git에 업로드")
    print("=" * 60)
    print()
    print("git add {}.part*".format(input_path.name))
    print("git commit -m \"Add Gradle cache (ZIP, {} parts)\"".format(part_num))
    print("git push")
    print()

    print("=" * 60)
    print("  복원 방법")
    print("=" * 60)
    print()
    print("Windows CMD:")
    print("  copy /b {}.part* {}".format(input_path.name, input_path.name))
    print()
    print("Windows PowerShell:")
    print("  Get-Content {}.part* -Raw -AsByteStream | Set-Content {} -AsByteStream".format(
        input_path.name, input_path.name))
    print()
    print("Linux/Mac/Git Bash:")
    print("  cat {}.part* > {}".format(input_path.name, input_path.name))
    print()
    print("그 후 압축 해제:")
    print("  Expand-Archive -Path {} -DestinationPath $env:USERPROFILE".format(input_path.name))
    print()

if __name__ == '__main__':
    if len(sys.argv) != 2:
        print("사용법: python split-zip.py <파일명>")
        print("예: python split-zip.py gradle-backup.zip")
        sys.exit(1)

    try:
        split_file(sys.argv[1])
    except KeyboardInterrupt:
        print("\n\n중단됨")
        sys.exit(1)
    except Exception as e:
        print("\nERROR: {}".format(e))
        import traceback
        traceback.print_exc()
        sys.exit(1)
