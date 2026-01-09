import axios from 'axios';
import type {
  RecipeSearchResponse,
  RecipeDetailResponse,
  RecipeFormDataResponse,
  RecipeCreateRequest,
  RecipeUpdateRequest,
  RecipeFavoriteToggleRequest,
  ToggleResponse,
  RecipeFavoriteListResponse,
  RecipeCategory,
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

// 레시피 목록 조회 (카테고리, 신메뉴 필터 지원)
// 백엔드는 RecipeDetailResponse[]를 반환하지만, 프론트엔드에서는 RecipeSearchResponse[] 형태로 변환
export const getRecipes = async (params?: {
  franchiseId?: string;
  category?: string;
  isNew?: boolean;
}): Promise<RecipeSearchResponse[]> => {
  console.log('🔍 getRecipes 호출:', params);
  
  try {
    const { data } = await apiClient.get<any[]>('/recipe/recipes', {
      params: params || {},
    });
    
    console.log('✅ getRecipes 응답:', data);
    
    // RecipeDetailResponse를 RecipeSearchResponse로 변환
    // 백엔드 RecipeDetailResponse에는 isSignature와 isNew가 없으므로 기본값 사용
    // 하지만 isNew 필터는 백엔드에서 처리되므로 신메뉴만 반환됨
    const converted = data.map((item: any) => ({
      recipeId: item.recipeId,
      title: item.title,
      category: item.category as RecipeCategory,
      isSignature: item.isSignature ?? false, // 백엔드에 없으면 false
      isNew: params?.isNew ?? item.isNew ?? false, // 백엔드 필터링 결과 반영
      isFavorite: false, // 초기값, 나중에 즐겨찾기 상태로 업데이트됨
      hotThumbnailUrl: item.hotThumbnailUrl,
      iceThumbnailUrl: item.iceThumbnailUrl,
    }));
    
    console.log('✅ 변환된 레시피:', converted);
    return converted;
  } catch (error: any) {
    console.error('❌ getRecipes 에러:', {
      message: error?.message,
      response: error?.response?.data,
      status: error?.response?.status,
      url: error?.config?.url,
      params: error?.config?.params,
    });
    throw error;
  }
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

// 즐겨찾기 토글
export const toggleFavorite = async (request: RecipeFavoriteToggleRequest): Promise<ToggleResponse> => {
  console.log('🔍 toggleFavorite 요청:', {
    url: '/recipe/recipe-favorites/toggle',
    method: 'POST',
    data: request,
    fullUrl: `${apiClient.defaults.baseURL}/recipe/recipe-favorites/toggle`,
  });
  
  const response = await apiClient.post<ToggleResponse>('/recipe/recipe-favorites/toggle', request);
  
  console.log('🔍 toggleFavorite 응답:', {
    status: response.status,
    statusText: response.statusText,
    data: response.data,
    headers: response.headers,
  });
  console.log('🔍 toggleFavorite response.data 타입:', typeof response.data);
  console.log('🔍 toggleFavorite response.data.isFavorite:', response.data?.isFavorite);
  console.log('🔍 toggleFavorite response.data.favorite:', (response.data as any)?.favorite);
  console.log('🔍 toggleFavorite response.data keys:', response.data ? Object.keys(response.data) : 'null/undefined');
  
  // Jackson이 isFavorite를 favorite로 직렬화할 수 있으므로 둘 다 확인
  const data = response.data;
  if (data && typeof data === 'object') {
    // favorite 필드가 있으면 isFavorite로 매핑
    if ('favorite' in data && !('isFavorite' in data)) {
      console.log('🔍 favorite 필드를 isFavorite로 매핑');
      return { isFavorite: Boolean((data as any).favorite) } as ToggleResponse;
    }
  }
  
  return data;
};

// 즐겨찾기 목록 조회
export const getFavorites = async (cafeId: string): Promise<RecipeFavoriteListResponse> => {
  console.log('🔍 getFavorites 요청:', {
    url: '/recipe/recipe-favorites',
    method: 'GET',
    params: { cafeId },
    fullUrl: `${apiClient.defaults.baseURL}/recipe/recipe-favorites?cafeId=${cafeId}`,
  });
  
  const response = await apiClient.get<RecipeFavoriteListResponse>('/recipe/recipe-favorites', {
    params: { cafeId },
  });
  
  console.log('🔍 getFavorites 응답:', {
    status: response.status,
    statusText: response.statusText,
    data: response.data,
  });
  
  return response.data;
};

