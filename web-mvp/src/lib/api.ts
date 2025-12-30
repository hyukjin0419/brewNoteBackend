import axios from 'axios';
import type {
  RecipeSearchResponse,
  RecipeDetailResponse,
  RecipeFormDataResponse,
  RecipeCreateRequest,
} from '../types/recipe';

const baseURL = import.meta.env.VITE_API_URL || '/api';

const apiClient = axios.create({
  baseURL: baseURL,
  headers: {
    'Content-Type': 'application/json',
  },
});

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

