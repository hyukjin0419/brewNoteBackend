import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getRecipeDetail } from '../lib/api';
import type { RecipeDetailResponse, VariantResponse } from '../types/recipe';
import './RecipeDetail.css';

function RecipeDetail() {
  const { recipeId } = useParams<{ recipeId: string }>();
  const navigate = useNavigate();
  const [recipe, setRecipe] = useState<RecipeDetailResponse | null>(null);
  const [selectedVariant, setSelectedVariant] = useState<VariantResponse | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!recipeId) return;

    const fetchRecipe = async () => {
      try {
        setIsLoading(true);
        const data = await getRecipeDetail(recipeId);
        setRecipe(data);

        // isDefault=true인 variant를 초기 선택
        const defaultVariant = data.variants.find((v) => v.isDefault) || data.variants[0];
        setSelectedVariant(defaultVariant);
      } catch (err) {
        console.error('레시피 조회 오류:', err);
        setError('레시피를 불러올 수 없습니다.');
      } finally {
        setIsLoading(false);
      }
    };

    fetchRecipe();
  }, [recipeId]);

  const handleVariantClick = (variant: VariantResponse) => {
    setSelectedVariant(variant);
  };

  if (isLoading) {
    return (
      <div className="recipe-detail-page">
        <div className="loading">로딩 중...</div>
      </div>
    );
  }

  if (error || !recipe) {
    return (
      <div className="recipe-detail-page">
        <div className="error">{error || '레시피를 찾을 수 없습니다.'}</div>
        <button className="back-button" onClick={() => navigate('/')}>
          돌아가기
        </button>
      </div>
    );
  }

  return (
    <div className="recipe-detail-page">
      <div className="detail-container">
        <button className="back-button" onClick={() => navigate('/')}>
          ← 검색으로 돌아가기
        </button>

        <div className="recipe-header">
          <h1>{recipe.title}</h1>
          <span className="category-badge">{recipe.category}</span>
        </div>

        <div className="variants-section">
          <h2>옵션 선택</h2>
          <div className="variant-buttons">
            {recipe.variants.map((variant) => (
              <button
                key={variant.variantId}
                className={`variant-button ${selectedVariant?.variantId === variant.variantId ? 'active' : ''}`}
                onClick={() => handleVariantClick(variant)}
              >
                {variant.type}
                {variant.isDefault && <span className="default-badge">기본</span>}
              </button>
            ))}
          </div>
        </div>

        {selectedVariant && (
          <div className="steps-section">
            <h2>제조 방법</h2>
            <ol className="steps-list">
              {selectedVariant.steps.map((step, index) => (
                <li key={index} className="step-item">
                  {step}
                </li>
              ))}
            </ol>
          </div>
        )}
      </div>
    </div>
  );
}

export default RecipeDetail;

