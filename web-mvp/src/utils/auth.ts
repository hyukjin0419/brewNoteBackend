// 인증 관련 유틸리티 함수

export const getToken = (): string | null => {
  return localStorage.getItem('accessToken');
};

export const setToken = (token: string): void => {
  localStorage.setItem('accessToken', token);
};

export const removeToken = (): void => {
  localStorage.removeItem('accessToken');
  localStorage.removeItem('userRole');
};

export const isAuthenticated = (): boolean => {
  return !!getToken();
};

export const getRole = (): string | null => {
  return localStorage.getItem('userRole');
};

export const setRole = (role: string): void => {
  localStorage.setItem('userRole', role);
};

export const isAdmin = (): boolean => {
  return getRole() === 'ADMIN';
};

export const isOwner = (): boolean => {
  // 점주는 USER role이고 카페 목록이 있어야 함
  if (getRole() !== 'USER') {
    return false;
  }
  // 카페 목록이 있으면 점주, 없으면 스태프
  const cafesJson = getCafes();
  if (!cafesJson || cafesJson === 'undefined' || cafesJson === 'null') {
    return false;
  }
  try {
    const cafes = JSON.parse(cafesJson);
    return Array.isArray(cafes) && cafes.length > 0;
  } catch {
    return false;
  }
};

export const isStaff = (): boolean => {
  return getRole() === 'STAFF';
};

export const isOwnerOrStaff = (): boolean => {
  return isOwner() || isStaff();
};

// 카페 목록 저장/조회
export const setCafes = (cafes: string): void => {
  localStorage.setItem('ownerCafes', cafes);
};

export const getCafes = (): string | null => {
  return localStorage.getItem('ownerCafes');
};

export const removeCafes = (): void => {
  localStorage.removeItem('ownerCafes');
  localStorage.removeItem('selectedCafeId');
};

// 선택한 카페 ID 저장/조회
export const setSelectedCafeId = (cafeId: string): void => {
  localStorage.setItem('selectedCafeId', cafeId);
};

export const getSelectedCafeId = (): string | null => {
  return localStorage.getItem('selectedCafeId');
};

