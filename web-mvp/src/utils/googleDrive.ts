/**
 * Google Drive 링크를 직접 이미지 URL로 변환
 * @param url Google Drive 공유 링크 또는 이미 변환된 링크
 * @returns 직접 이미지로 사용 가능한 URL
 */
export function convertGoogleDriveUrl(url: string | undefined | null): string | undefined {
  if (!url || url.trim() === '') {
    return undefined;
  }

  const trimmedUrl = url.trim();

  // 이미 직접 이미지 URL 형식인 경우 (uc?export=view) 그대로 반환
  if (trimmedUrl.includes('uc?export=view') || trimmedUrl.includes('uc?id=')) {
    return trimmedUrl;
  }

  // Google Drive 공유 링크에서 파일 ID 추출
  // 형식: https://drive.google.com/file/d/{FILE_ID}/view
  // 또는: https://drive.google.com/open?id={FILE_ID}
  let fileId: string | null = null;

  // /file/d/{FILE_ID}/view 형식
  const fileIdMatch = trimmedUrl.match(/\/file\/d\/([a-zA-Z0-9_-]+)/);
  if (fileIdMatch) {
    fileId = fileIdMatch[1];
  }

  // /open?id={FILE_ID} 형식
  if (!fileId) {
    const openIdMatch = trimmedUrl.match(/[?&]id=([a-zA-Z0-9_-]+)/);
    if (openIdMatch) {
      fileId = openIdMatch[1];
    }
  }

  // 파일 ID를 찾았으면 직접 이미지 URL로 변환
  if (fileId) {
    return `https://drive.google.com/uc?export=view&id=${fileId}`;
  }

  // 변환할 수 없으면 원본 URL 반환
  return trimmedUrl;
}


