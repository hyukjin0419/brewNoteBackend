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
}

// 레시피 상세 응답
export interface RecipeDetailResponse {
  recipeId: string;
  title: string;
  category: string;
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
  hotThumbnailUrl?: string;
  iceThumbnailUrl?: string;
  alias: string[];
  variants: VariantRequest[];
}

