// 멤버 관련 TypeScript 타입 정의

export interface CreateOwnerRequest {
  email: string;
  name: string;
  phoneNumber: string;
  franchiseId: string;
  cafeName: string;
  address: string;
}

export interface FranchiseResponse {
  franchiseId: string;
  name: string;
}

export interface OwnerSummaryResponse {
  id: string;
  email: string;
  name: string;
  phoneNumber: string;
  status: string;
  representativeCafeId: string;
  representativeCafe: string;
}

export interface OwnerDetailResponse {
  id: string;
  email: string;
  name: string;
  phoneNumber: string;
  status: string;
  cafeSummaries: CafeSummaryResponse[];
}

export interface CafeSummaryResponse {
  cafeId: string;
  cafeName: string;
  status: string;
  cafeAddress: string;
}

export interface MemberUpdateRequest {
  email?: string;
  name?: string;
  phoneNumber?: string;
  status?: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
}

// 점주 카페 목록
export interface CafesResponse {
  cafes: CafeSummary[];
}

export interface CafeSummary {
  cafeId: string;
  CafeName: string;
}

// 호환성을 위한 별칭 (기존 코드에서 사용 중)
export type OwnersCafesResponse = CafesResponse;
export type OwnedCafeSummary = CafeSummary;

// 스태프 관련
export enum CafeMemberRoleType {
  OWNER = 'OWNER',
  MANAGER = 'MANAGER',
  STAFF = 'STAFF',
}

export enum CafeMemberStatus {
  PENDING = 'PENDING',
  ACTIVATED = 'ACTIVATED',
  LEAVE = 'LEAVE',
}

export interface StaffSummaryResponse {
  memberId: string;
  cafeId: string;
  cafeMemberId: string;
  role: CafeMemberRoleType;
  status: CafeMemberStatus;
  name: string;
  nickName: string;
  email: string;
  phoneNumber: string;
}

export interface StaffCreateRequest {
  cafeId: string;
  email: string;
  name: string;
  phoneNumber: string;
}

export interface StaffDetailResponse {
  name: string;
  nickName: string;
  email: string;
  phoneNumber: string;
  role: CafeMemberRoleType;
  status: CafeMemberStatus;
}

