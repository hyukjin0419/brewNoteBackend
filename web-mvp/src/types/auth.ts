// 인증 관련 TypeScript 타입 정의

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  accessToken: string;
  role: 'ADMIN' | 'USER'; // 백엔드 MemberRoleType: ADMIN, USER
}

