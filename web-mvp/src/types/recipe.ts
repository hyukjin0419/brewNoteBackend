// 레시피 관련 TypeScript 타입 정의

export type RecipeCategory =
  | 'COFFEE'
  | 'COLD_BREW'
  | 'DECAFEINE'
  | 'NON_COFFEE'
  | 'BLENDED'
  | 'TEA'
  | 'ADE'
  | 'SOFT_ICE_CREAM'
  | 'BREAD';

export type RecipeOptionType = 'HOT_LARGE' | 'HOT_EXTRA' | 'ICE_LARGE' | 'ICE_EXTRA';

// 검색 응답
export interface RecipeSearchResponse {
  recipeId: string;
  title: string;
  category: RecipeCategory;
  isSignature: boolean;
  isNew: boolean;
  isFavorite?: boolean; // 즐겨찾기 상태 (프론트엔드에서 관리)
  hotThumbnailUrl?: string;
  iceThumbnailUrl?: string;
}

// 레시피 상세 응답
export interface RecipeDetailResponse {
  recipeId: string;
  title: string;
  category: string;
  isNew?: boolean; // Jackson이 new로 직렬화할 수 있으므로 둘 다 확인 필요
  new?: boolean; // Jackson 직렬화 대응
  hotThumbnailUrl?: string;
  iceThumbnailUrl?: string;
  alias?: string[];
  variants: VariantResponse[];
}

export interface VariantResponse {
  variantId: number;
  type: RecipeOptionType;
  default: boolean; // API 응답에서 default로 오므로
  steps: string[];
}

// 레시피 생성 요청
export interface RecipeCreateRequest {
  franchiseId: string;
  title: string;
  category: string;
  isNew?: boolean;
  hotImgUrl?: string;
  iceImgUrl?: string;
  alias: string[];
  variants: VariantRequest[];
}

export interface VariantRequest {
  optionType: RecipeOptionType;
  isDefault: boolean;
  steps: string[];
}

// 폼 데이터 응답
export interface RecipeFormDataResponse {
  recipeCategories: RecipeEnumOption[];
  franchises: FranchiseResponse[];
}

export interface RecipeEnumOption {
  key: string;
  label: string;
}

export interface FranchiseResponse {
  franchiseId: string;
  name: string;
}

// 레시피 수정 요청
export interface RecipeUpdateRequest {
  title: string;
  category: string;
  isNew?: boolean;
  hotThumbnailUrl?: string;
  iceThumbnailUrl?: string;
  alias: string[];
  variants: VariantRequest[];
}

// 즐겨찾기 관련 타입
export interface RecipeFavoriteToggleRequest {
  cafeId: string;
  recipeId: string;
  recipeVariantId: number;
}

export interface ToggleResponse {
  isFavorite: boolean;
}

export interface RecipeFavoriteListResponse {
  cafeId: string;
  favorites: RecipeFavoriteItem[];
}

export interface RecipeFavoriteItem {
  recipeId: string;
  title: string;
  category: string;
  hotThumbnailUrl?: string;
  iceThumbnailUrl?: string;
  variant: RecipeFavoriteVariant;
}

export interface RecipeFavoriteVariant {
  variantId: number;
  type: RecipeOptionType;
  isDefault: boolean;
  steps: string[];
}

