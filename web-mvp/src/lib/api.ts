import axios from 'axios';
import type {
  RecipeSearchResponse,
  RecipeDetailResponse,
  RecipeFormDataResponse,
  RecipeCreateRequest,
  RecipeUpdateRequest,
  RecipeFavoriteAddRequest,
  RecipeFavoriteRemoveRequest,
  RecipeFavoriteListResponse,
} from '../types/recipe';
import type {
  CafesResponse,
  StaffSummaryResponse,
  StaffCreateRequest,
  StaffDetailResponse,
  PageResponse,
  OwnerSummaryResponse,
  OwnerDetailResponse,
  CreateOwnerRequest,
  FranchiseResponse,
  MemberUpdateRequest,
} from '../types/member';
import type { LoginRequest, LoginResponse } from '../types/auth';

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
      console.log(`[API Request] ${config.method?.toUpperCase()} ${config.url}`, {
        hasToken: !!token,
        tokenPrefix: token.substring(0, 20) + '...',
      });
    } else {
      console.warn(`[API Request] ${config.method?.toUpperCase()} ${config.url} - No token found`);
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

// 점주 카페 목록 조회
export const getOwnersCafes = async (): Promise<CafesResponse> => {
  const { data } = await apiClient.get<CafesResponse>('/members/owner/cafes');
  return data;
};

// 스태프 카페 목록 조회
export const getStaffCafes = async (): Promise<CafesResponse> => {
  const { data } = await apiClient.get<CafesResponse>('/members/staff/cafes');
  return data;
};

// 스태프 목록 조회
export const getStaffs = async (
  cafeId: string,
  page: number = 0,
  size: number = 20
): Promise<PageResponse<StaffSummaryResponse>> => {
  const { data } = await apiClient.get<PageResponse<StaffSummaryResponse>>('/members/owner/staffs', {
    params: { cafeId, page, size },
  });
  return data;
};

// 스태프 생성
export const createStaff = async (request: StaffCreateRequest): Promise<void> => {
  await apiClient.post('/members/owner/staffs', request);
};

// 스태프 상세 조회
export const getStaff = async (cafeId: string, staffId: string): Promise<StaffDetailResponse> => {
  const { data } = await apiClient.get<StaffDetailResponse>(`/members/owner/cafes/${cafeId}/staffs/${staffId}`);
  return data;
};

// 로그인
export const login = async (request: LoginRequest): Promise<LoginResponse> => {
  const { data } = await apiClient.post<LoginResponse>('/auth/login', request);
  return data;
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

// 점주 정보 수정
export const updateMember = async (memberId: string, request: MemberUpdateRequest): Promise<void> => {
  await apiClient.put(`/members/admin/member/${memberId}`, request);
};

// 즐겨찾기 추가
export const addFavorite = async (request: RecipeFavoriteAddRequest): Promise<void> => {
  await apiClient.post('/recipe/recipe-favorites', request);
};

// 즐겨찾기 삭제
export const removeFavorite = async (request: RecipeFavoriteRemoveRequest): Promise<void> => {
  await apiClient.delete('/recipe/recipe-favorites', { data: request });
};

// 즐겨찾기 목록 조회
export const getFavorites = async (cafeId: string): Promise<RecipeFavoriteListResponse> => {
  const { data } = await apiClient.get<RecipeFavoriteListResponse>('/recipe/recipe-favorites', {
    params: { cafeId },
  });
  return data;
};

