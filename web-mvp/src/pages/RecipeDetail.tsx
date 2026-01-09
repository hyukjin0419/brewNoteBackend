import { useState, useEffect, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getRecipeDetail, updateRecipe, getRecipeFormData, deleteRecipe, addFavorite, removeFavorite, getFavorites } from '../lib/api';
import type { 
  RecipeDetailResponse, 
  VariantResponse, 
  RecipeUpdateRequest,
  RecipeFormDataResponse,
  VariantRequest,
} from '../types/recipe';
import { isOwnerOrStaff, getSelectedCafeId, isAdmin } from '../utils/auth';
import './RecipeDetail.css';

function RecipeDetail() {
  const { recipeId } = useParams<{ recipeId: string }>();
  const navigate = useNavigate();
  const [recipe, setRecipe] = useState<RecipeDetailResponse | null>(null);
  const [formData, setFormData] = useState<RecipeFormDataResponse | null>(null);
  const [selectedVariant, setSelectedVariant] = useState<VariantResponse | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isEditMode, setIsEditMode] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isFavorite, setIsFavorite] = useState(false);
  const [isLoadingFavorite, setIsLoadingFavorite] = useState(false);

  // 수정 모드 폼 상태
  const [editForm, setEditForm] = useState<RecipeUpdateRequest>({
    title: '',
    category: '',
    hotThumbnailUrl: '',
    iceThumbnailUrl: '',
    alias: [],
    variants: [],
  });

  // 별칭 관련 상태
  const [confirmedAliases, setConfirmedAliases] = useState<string[]>([]);
  const [currentAliasInput, setCurrentAliasInput] = useState('');
  const aliasInputRef = useRef<HTMLInputElement>(null);
  const isProcessingAlias = useRef(false);
  const isComposingAlias = useRef(false);

  // 옵션 단계 관련 상태
  const [currentVariant, setCurrentVariant] = useState<VariantRequest>({
    optionType: 'HOT_LARGE',
    isDefault: false,
    steps: [],
  });
  const [currentStepInput, setCurrentStepInput] = useState('');
  const stepInputRef = useRef<HTMLInputElement>(null);
  const isProcessingStep = useRef(false);
  const isComposingStep = useRef(false);
  const [editingVariantIndex, setEditingVariantIndex] = useState<number | null>(null);

  useEffect(() => {
    if (!recipeId) return;

    const fetchData = async () => {
      try {
        setIsLoading(true);
        const [recipeData, formDataResponse] = await Promise.all([
          getRecipeDetail(recipeId),
          getRecipeFormData(),
        ]);
        
        setRecipe(recipeData);
        setFormData(formDataResponse);

        // 수정 폼 초기화
        const mappedVariants = recipeData.variants.map(v => ({
          optionType: v.type,
          isDefault: v.default, // API 응답의 default를 isDefault로 매핑
          steps: v.steps || [],
        }));
        
        setEditForm({
          title: recipeData.title,
          category: recipeData.category,
          hotThumbnailUrl: recipeData.hotThumbnailUrl || '',
          iceThumbnailUrl: recipeData.iceThumbnailUrl || '',
          alias: recipeData.alias || [],
          variants: mappedVariants,
        });
        
        // 기존 별칭 로드
        setConfirmedAliases(recipeData.alias || []);

        // default=true인 variant를 초기 선택
        const defaultVariant = recipeData.variants.find((v) => v.default) || recipeData.variants[0];
        setSelectedVariant(defaultVariant);
        
        // 수정 모드 진입 시 기본 옵션을 currentVariant에 설정
        const defaultVariantInForm = mappedVariants.find(v => Boolean(v.isDefault)) || mappedVariants[0];
        if (defaultVariantInForm) {
          setCurrentVariant({ ...defaultVariantInForm });
        }
      } catch (err) {
        console.error('레시피 조회 오류:', err);
        setError('레시피를 불러올 수 없습니다.');
      } finally {
        setIsLoading(false);
      }
    };

    fetchData();
  }, [recipeId]);

  // 즐겨찾기 상태 확인 (점주/스태프인 경우)
  useEffect(() => {
    const fetchFavoriteStatus = async () => {
      if (!isOwnerOrStaff()) return; // 점주/스태프만 즐겨찾기 가능
      
      const selectedCafeId = getSelectedCafeId();
      if (!selectedCafeId || !recipe || !selectedVariant) return;

      try {
        // 즐겨찾기 목록 조회해서 현재 variant가 즐겨찾기되어 있는지 확인
        const favoritesData = await getFavorites(selectedCafeId);
        const isFav = favoritesData.favorites.some(
          fav => fav.recipeId === recipe.recipeId && 
                 fav.variant.variantId === selectedVariant.variantId
        );
        setIsFavorite(isFav);
      } catch (err) {
        console.error('즐겨찾기 상태 조회 오류:', err);
      }
    };

    if (recipe && selectedVariant) {
      fetchFavoriteStatus();
    }
  }, [recipe, selectedVariant]);

  const handleVariantClick = (variant: VariantResponse) => {
    setSelectedVariant(variant);
    // variant 변경 시 즐겨찾기 상태도 다시 확인
    setIsFavorite(false); // 일단 false로 설정하고 useEffect에서 다시 확인
  };

  // 즐겨찾기 추가/삭제 핸들러
  const handleFavoriteToggle = async () => {
    if (!isOwnerOrStaff()) {
      alert('즐겨찾기는 점주/스태프만 사용할 수 있습니다.');
      return;
    }

    const selectedCafeId = getSelectedCafeId();
    if (!selectedCafeId || !recipe || !selectedVariant) {
      alert('카페를 선택해주세요.');
      return;
    }

    try {
      setIsLoadingFavorite(true);
      
      if (isFavorite) {
        // 즐겨찾기 삭제
        await removeFavorite({
          cafeId: selectedCafeId,
          recipeVariantId: selectedVariant.variantId,
        });
        setIsFavorite(false);
      } else {
        // 즐겨찾기 추가
        await addFavorite({
          cafeId: selectedCafeId,
          recipeId: recipe.recipeId,
          recipeVariantId: selectedVariant.variantId,
        });
        setIsFavorite(true);
      }
    } catch (err: any) {
      console.error('즐겨찾기 오류:', err);
      const errorMessage = err?.response?.data?.message ||
                          err?.response?.data?.error ||
                          err?.message ||
                          '즐겨찾기 처리에 실패했습니다.';
      alert(`즐겨찾기 처리에 실패했습니다.\n${errorMessage}`);
    } finally {
      setIsLoadingFavorite(false);
    }
  };

  // variant type에 따라 적절한 썸네일 URL 반환
  const getThumbnailUrl = (variant: VariantResponse | null): string | undefined => {
    if (!variant || !recipe) {
      return undefined;
    }
    
    const isHot = variant.type === 'HOT_LARGE' || variant.type === 'HOT_EXTRA';
    const thumbnailUrl = isHot ? recipe.hotThumbnailUrl : recipe.iceThumbnailUrl;
    
    if (!thumbnailUrl || thumbnailUrl.trim() === '') {
      return undefined;
    }
    
    return thumbnailUrl;
  };

  // 수정 모드 전환
  const handleEditClick = () => {
    setIsEditMode(true);
  };

  // 수정 취소
  const handleCancelEdit = () => {
    if (recipe) {
      setEditForm({
        title: recipe.title,
        category: recipe.category,
        hotThumbnailUrl: recipe.hotThumbnailUrl || '',
        iceThumbnailUrl: recipe.iceThumbnailUrl || '',
        alias: recipe.alias || [],
        variants: recipe.variants.map(v => ({
          optionType: v.type,
          isDefault: v.default,
          steps: v.steps,
        })),
      });
      setConfirmedAliases(recipe.alias || []);
    }
    setIsEditMode(false);
  };

  // 별칭 엔터 핸들러
  const handleAliasKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      e.stopPropagation();
      
      if (isComposingAlias.current || isProcessingAlias.current) {
        return;
      }
      
      const trimmed = currentAliasInput.trim();
      if (trimmed) {
        isProcessingAlias.current = true;
        setConfirmedAliases((prev) => [...prev, trimmed]);
        setCurrentAliasInput('');
        setTimeout(() => {
          aliasInputRef.current?.focus();
          isProcessingAlias.current = false;
        }, 0);
      }
    }
  };

  const handleAliasCompositionStart = () => {
    isComposingAlias.current = true;
  };

  const handleAliasCompositionEnd = () => {
    isComposingAlias.current = false;
  };

  const removeAlias = (index: number) => {
    setConfirmedAliases((prev) => prev.filter((_, i) => i !== index));
  };

  // 단계 엔터 핸들러
  const handleStepKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      e.stopPropagation();
      
      if (isComposingStep.current || isProcessingStep.current) {
        return;
      }
      
      const trimmed = currentStepInput.trim();
      if (trimmed) {
        isProcessingStep.current = true;
        setCurrentVariant((prev) => ({
          ...prev,
          steps: [...prev.steps, trimmed],
        }));
        setCurrentStepInput('');
        setTimeout(() => {
          stepInputRef.current?.focus();
          isProcessingStep.current = false;
        }, 0);
      }
    }
  };

  const handleStepCompositionStart = () => {
    isComposingStep.current = true;
  };

  const handleStepCompositionEnd = () => {
    isComposingStep.current = false;
  };

  const removeStep = (index: number) => {
    setCurrentVariant((prev) => ({
      ...prev,
      steps: prev.steps.filter((_, i) => i !== index),
    }));
  };

  const addVariant = () => {
    if (currentVariant.steps.length === 0) {
      alert('최소 하나의 단계를 입력해주세요.');
      return;
    }

    if (editingVariantIndex !== null) {
      setEditForm((prev) => {
        const newVariants = [...prev.variants];
        newVariants[editingVariantIndex] = { ...currentVariant };
        return { ...prev, variants: newVariants };
      });
      setEditingVariantIndex(null);
    } else {
      setEditForm((prev) => ({
        ...prev,
        variants: [...prev.variants, { ...currentVariant }],
      }));
    }

    setCurrentVariant({
      optionType: 'HOT_LARGE',
      isDefault: false,
      steps: [],
    });
    setCurrentStepInput('');
  };

  const editVariant = (index: number) => {
    const variant = editForm.variants[index];
    setCurrentVariant({ ...variant });
    setCurrentStepInput('');
    setEditingVariantIndex(index);
  };

  const cancelEditVariant = () => {
    setCurrentVariant({
      optionType: 'HOT_LARGE',
      isDefault: false,
      steps: [],
    });
    setCurrentStepInput('');
    setEditingVariantIndex(null);
  };

  const removeVariant = (index: number) => {
    setEditForm((prev) => ({
      ...prev,
      variants: prev.variants.filter((_, i) => i !== index),
    }));
  };

  // 수정 제출
  const handleUpdateSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!editForm.title || !editForm.category) {
      alert('필수 항목을 모두 입력해주세요.');
      return;
    }

    if (editForm.variants.length === 0) {
      alert('최소 하나의 옵션을 추가해주세요.');
      return;
    }

    const variantTypes = editForm.variants.map((v) => v.optionType);
    const uniqueTypes = new Set(variantTypes);
    if (variantTypes.length !== uniqueTypes.size) {
      alert('중복된 옵션 타입이 있습니다.');
      return;
    }

    const defaultCount = editForm.variants.filter((v) => v.isDefault).length;
    if (defaultCount !== 1) {
      alert('기본 옵션은 정확히 하나만 선택해야 합니다.');
      return;
    }

    try {
      setIsSubmitting(true);
      
      const filteredAliases = confirmedAliases.filter((alias) => alias.trim() !== '');
      
      const requestData: RecipeUpdateRequest = {
        title: editForm.title,
        category: editForm.category,
        hotThumbnailUrl: editForm.hotThumbnailUrl?.trim() || undefined,
        iceThumbnailUrl: editForm.iceThumbnailUrl?.trim() || undefined,
        alias: filteredAliases,
        variants: editForm.variants.map(v => ({
          optionType: v.optionType,
          isDefault: v.isDefault,
          steps: v.steps || [],
        })),
      };

      if (!recipeId) return;
      
      await updateRecipe(recipeId, requestData);
      alert('레시피가 성공적으로 수정되었습니다.');
      
      // 데이터 다시 불러오기
      const updatedData = await getRecipeDetail(recipeId);
      setRecipe(updatedData);
      setIsEditMode(false);
    } catch (error: any) {
      console.error('레시피 수정 오류:', error);
      const errorMessage = error?.response?.data?.message || 
                          error?.response?.data?.error || 
                          error?.message || 
                          '레시피 수정에 실패했습니다.';
      alert(`레시피 수정에 실패했습니다.\n${errorMessage}`);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleDelete = async () => {
    if (!recipeId) return;
    
    const confirmed = window.confirm('정말 이 레시피를 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다.');
    if (!confirmed) return;

    try {
      await deleteRecipe(recipeId);
      alert('레시피가 성공적으로 삭제되었습니다.');
      navigate('/');
    } catch (error: any) {
      console.error('레시피 삭제 오류:', error);
      const errorMessage = error?.response?.data?.message || 
                          error?.response?.data?.error || 
                          error?.message || 
                          '레시피 삭제에 실패했습니다.';
      alert(`레시피 삭제에 실패했습니다.\n${errorMessage}`);
    }
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
        <div className="detail-header-actions">
          <button className="back-button" onClick={() => navigate('/')}>
            ← 검색으로 돌아가기
          </button>
          <div className="header-action-buttons">
            {!isEditMode && isAdmin() && (
              <>
                <button className="edit-button" onClick={handleEditClick}>
                  수정하기
                </button>
                <button className="delete-button header-delete-button" onClick={handleDelete}>
                  삭제하기
                </button>
              </>
            )}
          </div>
        </div>

        {!isEditMode ? (
          // 조회 모드
          <>
            <div className="recipe-header">
              <div className="recipe-title-section">
                <h1>{recipe.title}</h1>
                <span className="category-badge">{recipe.category}</span>
              </div>
              {isOwnerOrStaff() && getSelectedCafeId() && (
                <button
                  className={`favorite-button ${isFavorite ? 'active' : ''}`}
                  onClick={handleFavoriteToggle}
                  disabled={isLoadingFavorite}
                >
                  {isFavorite ? '★ 즐겨찾기 해제' : '☆ 즐겨찾기 추가'}
                </button>
              )}
            </div>

            <div className="thumbnail-section">
              {selectedVariant && (() => {
                const thumbnailUrl = getThumbnailUrl(selectedVariant);
                if (thumbnailUrl) {
                  return (
                    <img
                      key={`${selectedVariant.variantId}-${thumbnailUrl}`}
                      src={thumbnailUrl}
                      alt={`${recipe.title} - ${selectedVariant.type}`}
                      className="recipe-thumbnail"
                      onError={(e) => {
                        const img = e.target as HTMLImageElement;
                        img.style.display = 'none';
                      }}
                    />
                  );
                }
                return (
                  <div className="thumbnail-placeholder">
                    <span>이미지 없음</span>
                  </div>
                );
              })()}
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
                    {variant.default && <span className="default-badge">기본</span>}
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
          </>
        ) : (
          // 수정 모드
          <form onSubmit={handleUpdateSubmit} className="recipe-form">
            <h1>레시피 수정</h1>

            <div className="form-group">
              <label>
                레시피명 <span className="required">*</span>
              </label>
              <input
                type="text"
                value={editForm.title}
                onChange={(e) => setEditForm(prev => ({ ...prev, title: e.target.value }))}
                required
              />
            </div>

            <div className="form-group">
              <label>
                카테고리 <span className="required">*</span>
              </label>
              <select
                value={editForm.category}
                onChange={(e) => setEditForm(prev => ({ ...prev, category: e.target.value }))}
                required
              >
                <option value="">선택하세요</option>
                {formData?.recipeCategories.map((category) => (
                  <option key={category.key} value={category.key}>
                    {category.label}
                  </option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label>HOT 이미지 URL (선택사항)</label>
              <input
                type="url"
                value={editForm.hotThumbnailUrl || ''}
                onChange={(e) => setEditForm(prev => ({ ...prev, hotThumbnailUrl: e.target.value }))}
                placeholder="https://example.com/hot-image.jpg"
              />
              {editForm.hotThumbnailUrl && (
                <div className="image-preview">
                  <img
                    src={editForm.hotThumbnailUrl}
                    alt="HOT 미리보기"
                    onError={(e) => {
                      (e.target as HTMLImageElement).style.display = 'none';
                    }}
                    className="preview-image"
                  />
                </div>
              )}
            </div>

            <div className="form-group">
              <label>ICE 이미지 URL (선택사항)</label>
              <input
                type="url"
                value={editForm.iceThumbnailUrl || ''}
                onChange={(e) => setEditForm(prev => ({ ...prev, iceThumbnailUrl: e.target.value }))}
                placeholder="https://example.com/ice-image.jpg"
              />
              {editForm.iceThumbnailUrl && (
                <div className="image-preview">
                  <img
                    src={editForm.iceThumbnailUrl}
                    alt="ICE 미리보기"
                    onError={(e) => {
                      (e.target as HTMLImageElement).style.display = 'none';
                    }}
                    className="preview-image"
                  />
                </div>
              )}
            </div>

            <div className="form-group">
              <label>별칭 (선택사항)</label>
              <div className="alias-section">
                {confirmedAliases.map((alias, index) => (
                  <div key={index} className="confirmed-alias-item">
                    <span>{alias}</span>
                    <button
                      type="button"
                      onClick={() => removeAlias(index)}
                      className="remove-button"
                    >
                      삭제
                    </button>
                  </div>
                ))}
                <div className="alias-input-item">
                  <input
                    ref={aliasInputRef}
                    type="text"
                    value={currentAliasInput}
                    onChange={(e) => setCurrentAliasInput(e.target.value)}
                    onKeyDown={handleAliasKeyDown}
                    onCompositionStart={handleAliasCompositionStart}
                    onCompositionEnd={handleAliasCompositionEnd}
                    placeholder="별칭 입력 후 엔터를 누르세요"
                  />
                </div>
              </div>
            </div>

            <div className="variants-section">
              <h2>옵션 관리</h2>

              {editingVariantIndex !== null && (
                <div className="current-variant">
                  <div className="form-group">
                    <label>옵션 타입</label>
                    <select
                      value={currentVariant.optionType}
                      onChange={(e) =>
                        setCurrentVariant((prev) => ({
                          ...prev,
                          optionType: e.target.value as any,
                        }))
                      }
                    >
                      <option value="HOT_LARGE">HOT_LARGE</option>
                      <option value="HOT_EXTRA">HOT_EXTRA</option>
                      <option value="ICE_LARGE">ICE_LARGE</option>
                      <option value="ICE_EXTRA">ICE_EXTRA</option>
                    </select>
                  </div>

                  <div className="form-group">
                    <label>
                      <input
                        type="checkbox"
                        checked={currentVariant.isDefault}
                        onChange={(e) =>
                          setCurrentVariant((prev) => ({
                            ...prev,
                            isDefault: e.target.checked,
                          }))
                        }
                      />
                      기본 옵션
                    </label>
                  </div>

                  <div className="form-group">
                    <label>제조 단계</label>
                    <div className="steps-section">
                      {currentVariant.steps.map((step, index) => (
                        <div key={index} className="confirmed-step-item">
                          <span>{index + 1}. {step}</span>
                          <button
                            type="button"
                            onClick={() => removeStep(index)}
                            className="remove-button"
                          >
                            삭제
                          </button>
                        </div>
                      ))}
                      <div className="step-input-item">
                        <input
                          ref={stepInputRef}
                          type="text"
                          value={currentStepInput}
                          onChange={(e) => setCurrentStepInput(e.target.value)}
                          onKeyDown={handleStepKeyDown}
                          onCompositionStart={handleStepCompositionStart}
                          onCompositionEnd={handleStepCompositionEnd}
                          placeholder="단계 입력 후 엔터를 누르세요"
                        />
                      </div>
                    </div>
                  </div>

                  <div className="variant-action-buttons">
                    <button type="button" onClick={cancelEditVariant} className="cancel-edit-button">
                      수정 취소
                    </button>
                    <button type="button" onClick={addVariant} className="add-variant-button">
                      옵션 수정하기
                    </button>
                  </div>
                </div>
              )}

              {editForm.variants.length > 0 && (
                <div className="added-variants">
                  <h3>추가된 옵션</h3>
                  {editForm.variants.map((variant, index) => (
                      <div key={index} className="variant-item">
                        <div>
                          <strong>{variant.optionType}</strong>
                          {variant.isDefault && <span className="default-badge">기본</span>}
                          <span className="step-count">({variant.steps.length}단계)</span>
                        </div>
                          <div className="variant-item-actions">
                            <button
                              type="button"
                              onClick={() => editVariant(index)}
                              className="edit-item-button"
                              disabled={editingVariantIndex !== null}
                            >
                              수정
                            </button>
                            <button
                              type="button"
                              onClick={() => removeVariant(index)}
                              className="delete-button"
                              disabled={editingVariantIndex !== null}
                            >
                              삭제
                            </button>
                          </div>
                        </div>
                      ))}
                </div>
              )}
            </div>

            <div className="form-actions">
              {isAdmin() && (
                <button type="button" onClick={handleDelete} className="delete-button">
                  삭제하기
                </button>
              )}
              <div className="form-action-buttons">
                <button type="button" onClick={handleCancelEdit} className="cancel-button">
                  취소
                </button>
                <button type="submit" disabled={isSubmitting} className="submit-button">
                  {isSubmitting ? '수정 중...' : '레시피 수정'}
                </button>
              </div>
            </div>
          </form>
        )}
      </div>
    </div>
  );
}

export default RecipeDetail;
