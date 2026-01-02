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

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
}

