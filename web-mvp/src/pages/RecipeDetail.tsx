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
        console.log('=== 레시피 상세 데이터 ===');
        console.log('전체 데이터:', JSON.stringify(data, null, 2));
        console.log('hotThumbnailUrl:', data.hotThumbnailUrl);
        console.log('iceThumbnailUrl:', data.iceThumbnailUrl);
        console.log('variants:', data.variants);
        setRecipe(data);

        // isDefault=true인 variant를 초기 선택
        const defaultVariant = data.variants.find((v) => v.isDefault) || data.variants[0];
        console.log('선택된 variant:', defaultVariant);
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

  // variant type에 따라 적절한 썸네일 URL 반환
  const getThumbnailUrl = (variant: VariantResponse | null): string | undefined => {
    if (!variant || !recipe) {
      return undefined;
    }
    
    // HOT 타입이면 hotThumbnailUrl, ICE 타입이면 iceThumbnailUrl
    const isHot = variant.type === 'HOT_LARGE' || variant.type === 'HOT_EXTRA';
    const thumbnailUrl = isHot ? recipe.hotThumbnailUrl : recipe.iceThumbnailUrl;
    
    // 빈 문자열이나 null 체크
    if (!thumbnailUrl || thumbnailUrl.trim() === '') {
      return undefined;
    }
    
    return thumbnailUrl;
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

        <div className="thumbnail-section">
          {selectedVariant && (() => {
            const thumbnailUrl = getThumbnailUrl(selectedVariant);
            const isHot = selectedVariant.type === 'HOT_LARGE' || selectedVariant.type === 'HOT_EXTRA';
            
            console.log('=== 썸네일 렌더링 ===');
            console.log('variant type:', selectedVariant.type);
            console.log('isHot:', isHot);
            console.log('hotThumbnailUrl:', recipe.hotThumbnailUrl);
            console.log('iceThumbnailUrl:', recipe.iceThumbnailUrl);
            console.log('선택된 thumbnailUrl:', thumbnailUrl);
            
            if (thumbnailUrl) {
              return (
                <img
                  key={`${selectedVariant.variantId}-${thumbnailUrl}`}
                  src={thumbnailUrl}
                  alt={`${recipe.title} - ${selectedVariant.type}`}
                  className="recipe-thumbnail"
                  onLoad={() => {
                    console.log('✅ 이미지 로드 성공:', thumbnailUrl);
                  }}
                  onError={(e) => {
                    console.error('❌ 이미지 로드 실패:', thumbnailUrl);
                    console.error('에러 상세:', e);
                    const img = e.target as HTMLImageElement;
                    img.style.display = 'none';
                    const placeholder = img.parentElement?.querySelector('.thumbnail-placeholder') as HTMLElement;
                    if (placeholder) {
                      placeholder.style.display = 'flex';
                    }
                  }}
                />
              );
            }
            
            console.warn('⚠️ 썸네일 URL이 없습니다');
            return (
              <div className="thumbnail-placeholder">
                <span>이미지 없음</span>
              </div>
            );
          })()}
          {!selectedVariant && (
            <div className="thumbnail-placeholder">
              <span>옵션을 선택해주세요</span>
            </div>
          )}
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

