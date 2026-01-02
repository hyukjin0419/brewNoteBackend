import axios from 'axios';
import type {
  RecipeSearchResponse,
  RecipeDetailResponse,
  RecipeFormDataResponse,
  RecipeCreateRequest,
  RecipeUpdateRequest,
} from '../types/recipe';
import type { LoginRequest, LoginResponse } from '../types/auth';
import type { CreateOwnerRequest, FranchiseResponse, OwnerSummaryResponse, OwnerDetailResponse, PageResponse } from '../types/member';

const baseURL = import.meta.env.VITE_API_URL || '/api';

const apiClient = axios.create({
  baseURL: baseURL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 요청 인터셉터: 토큰 자동 추가
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 에러 인터셉터 추가
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error('API Error:', {
      status: error.response?.status,
      statusText: error.response?.statusText,
      data: error.response?.data,
      message: error.message,
    });
    return Promise.reject(error);
  }
);

// 레시피 검색
export const searchRecipes = async (keyword: string): Promise<RecipeSearchResponse[]> => {
  const { data } = await apiClient.get<RecipeSearchResponse[]>('/recipe/search/recipes', {
    params: { keyword },
  });
  return data;
};

// 레시피 상세 조회
export const getRecipeDetail = async (recipeId: string): Promise<RecipeDetailResponse> => {
  const { data } = await apiClient.get<RecipeDetailResponse>(`/recipe/${recipeId}`);
  
  // API 응답에서 isDefault 값 확인
  console.log('=== API 응답 확인 ===');
  console.log('전체 응답:', data);
  console.log('variants:', data.variants);
  console.log('각 variant의 isDefault:', data.variants.map(v => ({
    type: v.type,
    isDefault: v.default,
    isDefaultType: typeof v.default,
    variantId: v.variantId,
  })));
  
  return data;
};

// 레시피 폼 데이터 조회
export const getRecipeFormData = async (): Promise<RecipeFormDataResponse> => {
  const { data } = await apiClient.get<RecipeFormDataResponse>('/recipe/admin/recipes/form-data');
  return data;
};

// 레시피 생성
export const createRecipe = async (request: RecipeCreateRequest): Promise<void> => {
  await apiClient.post('/recipe/admin/recipe', request);
};

// 레시피 수정
export const updateRecipe = async (recipeId: string, request: RecipeUpdateRequest): Promise<void> => {
  await apiClient.put(`/recipe/admin/recipe/${recipeId}`, request);
};

// 레시피 삭제
export const deleteRecipe = async (recipeId: string): Promise<void> => {
  await apiClient.delete(`/recipe/admin/recipe/${recipeId}`);
};

// 로그인
export const login = async (request: LoginRequest): Promise<LoginResponse> => {
  const response = await apiClient.post<LoginResponse>('/auth/login', request);
  console.log('로그인 응답 전체:', response);
  console.log('로그인 응답 데이터:', response.data);
  console.log('로그인 응답 상태:', response.status);
  console.log('로그인 응답 헤더:', response.headers);
  
  // 백엔드가 ResponseEntity<Void>를 반환하는 경우 response.data가 undefined일 수 있음
  // 하지만 실제로는 LoginResponse를 반환해야 함
  if (response.data) {
    // 응답 body가 있으면 사용
    if (response.data.accessToken) {
      return response.data;
    }
  }
  
  // 응답 body가 없거나 토큰이 없으면 에러
  // 실제로는 백엔드가 LoginResponse를 반환해야 함
  throw new Error('로그인 응답에 토큰이 없습니다. 백엔드가 LoginResponse를 반환하는지 확인해주세요.');
};

// 프랜차이즈 목록 조회
export const getFranchises = async (): Promise<FranchiseResponse[]> => {
  const { data } = await apiClient.get<FranchiseResponse[]>('/admin/franchise');
  return data;
};

// 점주 목록 조회
export const getOwners = async (page: number = 0, size: number = 20): Promise<PageResponse<OwnerSummaryResponse>> => {
  const { data } = await apiClient.get<PageResponse<OwnerSummaryResponse>>('/members/admin/owners', {
    params: { page, size },
  });
  return data;
};

// 점주 상세 조회
export const getOwner = async (ownerId: string): Promise<OwnerDetailResponse> => {
  const { data } = await apiClient.get<OwnerDetailResponse>(`/members/admin/owners/${ownerId}`);
  return data;
};

// 점주 생성
export const createOwner = async (request: CreateOwnerRequest): Promise<void> => {
  await apiClient.post('/members/admin/owners', request);
};

