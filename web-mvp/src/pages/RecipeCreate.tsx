import { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { getRecipeFormData, createRecipe } from '../lib/api';
import type {
  RecipeFormDataResponse,
  RecipeCreateRequest,
  VariantRequest,
} from '../types/recipe';
import './RecipeCreate.css';

function RecipeCreate() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState<RecipeFormDataResponse | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);

  // refs
  const aliasInputRef = useRef<HTMLInputElement>(null);
  const stepInputRef = useRef<HTMLInputElement>(null);
  
  // 중복 처리 방지 플래그
  const isProcessingAlias = useRef(false);
  const isProcessingStep = useRef(false);
  
  // IME 조합 상태 추적 (한글 입력 중인지 확인)
  const isComposingAlias = useRef(false);
  const isComposingStep = useRef(false);

  const [form, setForm] = useState<RecipeCreateRequest>({
    franchiseId: '',
    title: '',
    category: '',
    hotImgUrl: '',
    iceImgUrl: '',
    alias: [],
    variants: [],
  });

  // 별칭: 확정된 별칭과 현재 입력 중인 별칭 분리
  const [confirmedAliases, setConfirmedAliases] = useState<string[]>([]);
  const [currentAliasInput, setCurrentAliasInput] = useState('');
  const [editingAliasIndex, setEditingAliasIndex] = useState<number | null>(null);
  const [editingAliasValue, setEditingAliasValue] = useState('');

  // 옵션 단계: 확정된 단계와 현재 입력 중인 단계 분리
  const [currentVariant, setCurrentVariant] = useState<VariantRequest>({
    optionType: 'HOT_LARGE',
    isDefault: false,
    steps: [],
  });
  const [currentStepInput, setCurrentStepInput] = useState('');
  const [editingStepIndex, setEditingStepIndex] = useState<number | null>(null);
  const [editingStepValue, setEditingStepValue] = useState('');
  
  // 수정 중인 옵션 인덱스 (null이면 추가 모드)
  const [editingVariantIndex, setEditingVariantIndex] = useState<number | null>(null);

  useEffect(() => {
    const fetchFormData = async () => {
      try {
        const data = await getRecipeFormData();
        setFormData(data);
      } catch (error) {
        console.error('폼 데이터 조회 오류:', error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchFormData();
  }, []);

  const handleInputChange = (field: keyof RecipeCreateRequest, value: string) => {
    setForm((prev) => ({ ...prev, [field]: value }));
  };

  // 별칭 IME 조합 시작
  const handleAliasCompositionStart = () => {
    isComposingAlias.current = true;
  };

  // 별칭 IME 조합 종료
  const handleAliasCompositionEnd = () => {
    isComposingAlias.current = false;
  };

  // 별칭 엔터 핸들러
  const handleAliasKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      e.stopPropagation();
      
      // IME 조합 중이면 무시 (한글 입력 중)
      if (isComposingAlias.current) {
        return;
      }
      
      // 이미 처리 중이면 무시
      if (isProcessingAlias.current) {
        return;
      }
      
      const trimmed = currentAliasInput.trim();
      
      if (trimmed) {
        isProcessingAlias.current = true;
        
        // 값이 있으면 확정 리스트에 추가
        setConfirmedAliases((prev) => {
          // 중복 방지: 이미 같은 값이 있는지 확인
          if (prev.includes(trimmed)) {
            isProcessingAlias.current = false;
            return prev;
          }
          return [...prev, trimmed];
        });
        setCurrentAliasInput('');
        
        // 다음 입력창에 포커스
        setTimeout(() => {
          aliasInputRef.current?.focus();
          isProcessingAlias.current = false;
        }, 0);
      }
    }
  };

  // 별칭 삭제
  const removeAlias = (index: number) => {
    setConfirmedAliases((prev) => prev.filter((_, i) => i !== index));
  };

  // 별칭 수정 시작
  const startEditAlias = (index: number) => {
    setEditingAliasIndex(index);
    setEditingAliasValue(confirmedAliases[index]);
  };

  // 별칭 수정 완료
  const finishEditAlias = () => {
    if (editingAliasIndex !== null) {
      const trimmed = editingAliasValue.trim();
      if (trimmed) {
        setConfirmedAliases((prev) => {
          const newAliases = [...prev];
          newAliases[editingAliasIndex] = trimmed;
          return newAliases;
        });
      }
      setEditingAliasIndex(null);
      setEditingAliasValue('');
    }
  };

  // 별칭 수정 취소
  const cancelEditAlias = () => {
    setEditingAliasIndex(null);
    setEditingAliasValue('');
  };

  // 별칭 수정 엔터 핸들러
  const handleEditAliasKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      e.stopPropagation();
      finishEditAlias();
    } else if (e.key === 'Escape') {
      e.preventDefault();
      cancelEditAlias();
    }
  };

  // 단계 IME 조합 시작
  const handleStepCompositionStart = () => {
    isComposingStep.current = true;
  };

  // 단계 IME 조합 종료
  const handleStepCompositionEnd = () => {
    isComposingStep.current = false;
  };

  // 단계 엔터 핸들러
  const handleStepKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      e.stopPropagation();
      
      // IME 조합 중이면 무시 (한글 입력 중)
      if (isComposingStep.current) {
        return;
      }
      
      // 이미 처리 중이면 무시
      if (isProcessingStep.current) {
        return;
      }
      
      const trimmed = currentStepInput.trim();
      
      if (trimmed) {
        isProcessingStep.current = true;
        
        // 값이 있으면 확정 리스트에 추가
        setCurrentVariant((prev) => {
          // 중복 방지: 이미 같은 값이 있는지 확인
          if (prev.steps.includes(trimmed)) {
            isProcessingStep.current = false;
            return prev;
          }
          return {
            ...prev,
            steps: [...prev.steps, trimmed],
          };
        });
        setCurrentStepInput('');
        
        // 다음 입력창에 포커스
        setTimeout(() => {
          stepInputRef.current?.focus();
          isProcessingStep.current = false;
        }, 0);
      }
    }
  };

  // 단계 삭제
  const removeStep = (index: number) => {
    setCurrentVariant((prev) => ({
      ...prev,
      steps: prev.steps.filter((_, i) => i !== index),
    }));
  };

  // 단계 수정 시작
  const startEditStep = (index: number) => {
    setEditingStepIndex(index);
    setEditingStepValue(currentVariant.steps[index]);
  };

  // 단계 수정 완료
  const finishEditStep = () => {
    if (editingStepIndex !== null) {
      const trimmed = editingStepValue.trim();
      if (trimmed) {
        setCurrentVariant((prev) => {
          const newSteps = [...prev.steps];
          newSteps[editingStepIndex] = trimmed;
          return {
            ...prev,
            steps: newSteps,
          };
        });
      }
      setEditingStepIndex(null);
      setEditingStepValue('');
    }
  };

  // 단계 수정 취소
  const cancelEditStep = () => {
    setEditingStepIndex(null);
    setEditingStepValue('');
  };

  // 단계 수정 엔터 핸들러
  const handleEditStepKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      e.stopPropagation();
      finishEditStep();
    } else if (e.key === 'Escape') {
      e.preventDefault();
      cancelEditStep();
    }
  };

  const addVariant = () => {
    if (currentVariant.steps.length === 0) {
      alert('최소 하나의 단계를 입력해주세요.');
      return;
    }

    if (editingVariantIndex !== null) {
      // 수정 모드: 기존 옵션 업데이트
      setForm((prev) => {
        const newVariants = [...prev.variants];
        newVariants[editingVariantIndex] = { ...currentVariant };
        return {
          ...prev,
          variants: newVariants,
        };
      });
      setEditingVariantIndex(null);
    } else {
      // 추가 모드: 새 옵션 추가
      setForm((prev) => ({
        ...prev,
        variants: [...prev.variants, { ...currentVariant }],
      }));
    }

    // 폼 초기화
    setCurrentVariant({
      optionType: 'HOT_LARGE',
      isDefault: false,
      steps: [],
    });
    setCurrentStepInput('');
  };

  // 옵션 수정 모드로 전환
  const editVariant = (index: number) => {
    const variant = form.variants[index];
    setCurrentVariant({ ...variant });
    setCurrentStepInput('');
    setEditingVariantIndex(index);
    
    // 스크롤을 편집 영역으로 이동
    setTimeout(() => {
      document.querySelector('.current-variant')?.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }, 100);
  };

  // 수정 취소
  const cancelEdit = () => {
    setCurrentVariant({
      optionType: 'HOT_LARGE',
      isDefault: false,
      steps: [],
    });
    setCurrentStepInput('');
    setEditingVariantIndex(null);
  };

  const removeVariant = (index: number) => {
    setForm((prev) => ({
      ...prev,
      variants: prev.variants.filter((_, i) => i !== index),
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!form.franchiseId || !form.title || !form.category) {
      alert('필수 항목을 모두 입력해주세요.');
      return;
    }

    if (form.variants.length === 0) {
      alert('최소 하나의 옵션을 추가해주세요.');
      return;
    }

    // variant type 중복 체크
    const variantTypes = form.variants.map((v) => v.optionType);
    const uniqueTypes = new Set(variantTypes);
    if (variantTypes.length !== uniqueTypes.size) {
      alert('중복된 옵션 타입이 있습니다. 각 옵션 타입은 하나만 선택할 수 있습니다.');
      return;
    }

    // default variant는 정확히 1개
    const defaultCount = form.variants.filter((v) => v.isDefault).length;
    if (defaultCount === 0) {
      alert('기본 옵션을 하나 선택해주세요.');
      return;
    }
    if (defaultCount > 1) {
      alert('기본 옵션은 정확히 하나만 선택해야 합니다.');
      return;
    }

    try {
      setIsSubmitting(true);
      
      // 확정된 별칭만 필터링 (빈 값 제외)
      const filteredAliases = confirmedAliases.filter((alias) => alias.trim() !== '');
      
      // 빈 문자열을 undefined로 변환 (백엔드에서 null로 처리되도록)
      const requestData: RecipeCreateRequest = {
        ...form,
        franchiseId: form.franchiseId,
        title: form.title,
        category: form.category,
        hotImgUrl: form.hotImgUrl?.trim() || undefined,
        iceImgUrl: form.iceImgUrl?.trim() || undefined,
        alias: filteredAliases,
        variants: form.variants,
      };

      console.log('전송할 데이터:', JSON.stringify(requestData, null, 2));
      
      await createRecipe(requestData);
      alert('레시피가 성공적으로 등록되었습니다.');
      navigate('/');
    } catch (error: any) {
      console.error('레시피 생성 오류:', error);
      const errorMessage = error?.response?.data?.message || 
                          error?.response?.data?.error || 
                          error?.message || 
                          '레시피 등록에 실패했습니다.';
      alert(`레시피 등록에 실패했습니다.\n${errorMessage}`);
    } finally {
      setIsSubmitting(false);
    }
  };

  if (isLoading) {
    return (
      <div className="recipe-create-page">
        <div className="loading">로딩 중...</div>
      </div>
    );
  }

  return (
    <div className="recipe-create-page">
      <div className="create-container">
        <button className="back-button" onClick={() => navigate('/')}>
          ← 검색으로 돌아가기
        </button>

        <h1>레시피 추가하기</h1>

        <form onSubmit={handleSubmit} className="recipe-form">
          <div className="form-group">
            <label>
              프랜차이즈 <span className="required">*</span>
            </label>
            <select
              value={form.franchiseId}
              onChange={(e) => handleInputChange('franchiseId', e.target.value)}
              required
            >
              <option value="">선택하세요</option>
              {formData?.franchises.map((franchise) => (
                <option key={franchise.franchiseId} value={franchise.franchiseId}>
                  {franchise.name}
                </option>
              ))}
            </select>
          </div>

          <div className="form-group">
            <label>
              레시피명 <span className="required">*</span>
            </label>
            <input
              type="text"
              value={form.title}
              onChange={(e) => handleInputChange('title', e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label>
              카테고리 <span className="required">*</span>
            </label>
            <select
              value={form.category}
              onChange={(e) => handleInputChange('category', e.target.value)}
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
              value={form.hotImgUrl || ''}
              onChange={(e) => handleInputChange('hotImgUrl', e.target.value)}
              placeholder="https://example.com/hot-image.jpg"
            />
            {form.hotImgUrl && (
              <div className="image-preview">
                <img
                  src={form.hotImgUrl}
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
              value={form.iceImgUrl || ''}
              onChange={(e) => handleInputChange('iceImgUrl', e.target.value)}
              placeholder="https://example.com/ice-image.jpg"
            />
            {form.iceImgUrl && (
              <div className="image-preview">
                <img
                  src={form.iceImgUrl}
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
              {/* 확정된 별칭들 */}
              {confirmedAliases.map((alias, index) => (
                <div key={index} className="confirmed-alias-item">
                  {editingAliasIndex === index ? (
                    <input
                      type="text"
                      value={editingAliasValue}
                      onChange={(e) => setEditingAliasValue(e.target.value)}
                      onKeyDown={handleEditAliasKeyDown}
                      onBlur={finishEditAlias}
                      autoFocus
                      className="edit-input"
                    />
                  ) : (
                    <>
                      <span onClick={() => startEditAlias(index)} className="editable-text">
                        {alias}
                      </span>
                      <div className="item-actions">
                        <button
                          type="button"
                          onClick={() => startEditAlias(index)}
                          className="edit-item-button"
                        >
                          수정
                        </button>
                        <button
                          type="button"
                          onClick={() => removeAlias(index)}
                          className="remove-button"
                        >
                          삭제
                        </button>
                      </div>
                    </>
                  )}
                </div>
              ))}
              {/* 현재 입력 중인 별칭 */}
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
            <h2>{editingVariantIndex !== null ? '옵션 수정' : '옵션 추가'}</h2>

            {editingVariantIndex !== null && (
              <div className="edit-mode-notice">
                옵션을 수정 중입니다. 수정을 완료하거나 취소해주세요.
              </div>
            )}

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
                  {/* 확정된 단계들 */}
                  {currentVariant.steps.map((step, index) => (
                    <div key={index} className="confirmed-step-item">
                      {editingStepIndex === index ? (
                        <input
                          type="text"
                          value={editingStepValue}
                          onChange={(e) => setEditingStepValue(e.target.value)}
                          onKeyDown={handleEditStepKeyDown}
                          onBlur={finishEditStep}
                          autoFocus
                          className="edit-input"
                        />
                      ) : (
                        <>
                          <span onClick={() => startEditStep(index)} className="editable-text">
                            {index + 1}. {step}
                          </span>
                          <div className="item-actions">
                            <button
                              type="button"
                              onClick={() => startEditStep(index)}
                              className="edit-item-button"
                            >
                              수정
                            </button>
                            <button
                              type="button"
                              onClick={() => removeStep(index)}
                              className="remove-button"
                            >
                              삭제
                            </button>
                          </div>
                        </>
                      )}
                    </div>
                  ))}
                  {/* 현재 입력 중인 단계 */}
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
                {editingVariantIndex !== null && (
                  <button type="button" onClick={cancelEdit} className="cancel-edit-button">
                    수정 취소
                  </button>
                )}
                <button type="button" onClick={addVariant} className="add-variant-button">
                  {editingVariantIndex !== null ? '옵션 수정하기' : '옵션 추가하기'}
                </button>
              </div>
            </div>

            {form.variants.length > 0 && (
              <div className="added-variants">
                <h3>추가된 옵션</h3>
                {form.variants.map((variant, index) => (
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
                        className="edit-button"
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
            <button type="button" onClick={() => navigate('/')} className="cancel-button">
              취소
            </button>
            <button type="submit" disabled={isSubmitting} className="submit-button">
              {isSubmitting ? '등록 중...' : '레시피 등록'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default RecipeCreate;

