import { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { searchRecipes, toggleFavorite, getFavorites, getRecipeDetail, getRecipes, getFranchises } from '../lib/api';
import type { RecipeSearchResponse, RecipeCategory } from '../types/recipe';
import { isOwner, isOwnerOrStaff, getCafes, getSelectedCafeId, setSelectedCafeId, removeToken, removeCafes, isAdmin, getRole, setCafes } from '../utils/auth';
import { getOwnersCafes, getStaffCafes } from '../lib/api';
import type { OwnedCafeSummary } from '../types/member';
import './RecipeSearch.css';

// 필터 타입 정의
type FilterType = 'FAVORITE' | 'NEW' | RecipeCategory;

// 카테고리 라벨 매핑
const categoryLabels: Record<RecipeCategory, string> = {
  COFFEE: '커피',
  COLD_BREW: '콜드브루',
  DECAFEINE: '디카페인',
  NON_COFFEE: '논커피',
  BLENDED: '블렌디드',
  TEA: '티',
  ADE: '에이드 & 과일주스',
  SOFT_ICE_CREAM: '소프트 아이스크림',
  BREAD: '브레드/베이커리',
};

function RecipeSearch() {
  const [keyword, setKeyword] = useState('');
  const [searchResults, setSearchResults] = useState<RecipeSearchResponse[]>([]); // 검색 결과
  const [filteredRecipes, setFilteredRecipes] = useState<RecipeSearchResponse[]>([]); // 필터 결과
  const [isLoading, setIsLoading] = useState(false);
  const [isSearching, setIsSearching] = useState(false); // 검색 중인지 여부
  const [showSearchResults, setShowSearchResults] = useState(false); // 검색 결과 표시 여부
  const [selectedFilter, setSelectedFilter] = useState<FilterType>('NEW');
  const navigate = useNavigate();
  const searchRef = useRef<HTMLDivElement>(null);
  const timeoutRef = useRef<NodeJS.Timeout>();
  
  // 카페 관련 상태
  const [cafes, setCafesState] = useState<OwnedCafeSummary[]>([]);
  const [selectedCafeId, setSelectedCafeIdState] = useState<string>('');


  // 카페 목록 로드
  useEffect(() => {
    const loadCafes = async () => {
      if (!isOwnerOrStaff()) return;

      // 먼저 localStorage에서 카페 목록 확인
      const cafesJson = getCafes();
      if (cafesJson && cafesJson !== 'undefined' && cafesJson !== 'null') {
        try {
          const cafesData: OwnedCafeSummary[] = JSON.parse(cafesJson);
          if (cafesData && Array.isArray(cafesData) && cafesData.length > 0) {
            setCafesState(cafesData);
            const savedCafeId = getSelectedCafeId();
            if (savedCafeId && cafesData.some(c => c.cafeId === savedCafeId)) {
              setSelectedCafeIdState(savedCafeId);
            } else {
              // 저장된 카페 ID가 없거나 유효하지 않으면 첫 번째 카페 선택
              const firstCafeId = cafesData[0].cafeId;
              setSelectedCafeIdState(firstCafeId);
              setSelectedCafeId(firstCafeId);
            }
            return; // 저장된 데이터 사용
          }
        } catch (error) {
          console.error('카페 목록 파싱 오류:', error);
        }
      }

      // localStorage에 없으면 API 호출
      try {
        const role = getRole();
        let cafesData;
        if (role === 'USER') {
          cafesData = await getOwnersCafes();
        } else if (role === 'STAFF') {
          cafesData = await getStaffCafes();
        } else {
          return;
        }

        if (cafesData && cafesData.cafes && Array.isArray(cafesData.cafes)) {
          setCafesState(cafesData.cafes);
          // 카페 목록을 localStorage에 저장
          setCafes(JSON.stringify(cafesData.cafes));
          
          // 첫 번째 카페를 기본 선택
          if (cafesData.cafes.length > 0) {
            const firstCafeId = cafesData.cafes[0].cafeId;
            setSelectedCafeIdState(firstCafeId);
            setSelectedCafeId(firstCafeId);
          }
        }
      } catch (error) {
        console.error('카페 목록 조회 오류:', error);
      }
    };

    loadCafes();
  }, []);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (searchRef.current && !searchRef.current.contains(event.target as Node)) {
        setShowSearchResults(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  // 필터 변경 시 레시피 목록 로드
  useEffect(() => {
    const loadFilteredRecipes = async () => {
      try {
        setIsLoading(true);

        if (selectedFilter === 'FAVORITE') {
          // 즐겨찾기는 /api/recipe/recipe-favorites/ GET API 사용
          const selectedCafeId = getSelectedCafeId();
          if (!selectedCafeId) {
            setFilteredRecipes([]);
            setIsLoading(false);
            return;
          }

          try {
            const favoritesData = await getFavorites(selectedCafeId);
            
            // favorites 배열이 없거나 비어있는 경우 처리
            if (!favoritesData || !favoritesData.favorites || favoritesData.favorites.length === 0) {
              setFilteredRecipes([]);
              return;
            }
            
            // RecipeFavoriteListResponse를 RecipeSearchResponse[]로 변환
            const convertedRecipes: RecipeSearchResponse[] = favoritesData.favorites.map((fav) => ({
              recipeId: fav.recipeId,
              title: fav.title,
              category: fav.category as RecipeCategory,
              isSignature: false, // 즐겨찾기 응답에 없으면 false
              isNew: false, // 즐겨찾기 응답에 없으면 false
              isFavorite: true, // 즐겨찾기 목록이므로 항상 true
              hotThumbnailUrl: fav.hotThumbnailUrl,
              iceThumbnailUrl: fav.iceThumbnailUrl,
            }));
            setFilteredRecipes(convertedRecipes);
          } catch (error: any) {
            setFilteredRecipes([]);
          }
        } else if (selectedFilter === 'NEW') {
          // 신메뉴는 백엔드 API 사용
          // franchiseId가 필요한 경우를 대비해 모든 프랜차이즈 조회
          try {
            const franchises = await getFranchises();
            if (franchises && franchises.length > 0) {
              // 모든 프랜차이즈의 신메뉴 조회
              const allPromises = franchises.map((franchise) =>
                getRecipes({ franchiseId: franchise.franchiseId, isNew: true })
              );
              const allResults = await Promise.all(allPromises);
              const allRecipes = allResults.flat();
              setFilteredRecipes(allRecipes);
            } else {
              // franchiseId 없이 시도
              const data = await getRecipes({ isNew: true });
              setFilteredRecipes(data);
            }
          } catch (error: any) {
            console.error('신메뉴 조회 오류 (franchiseId 포함):', error);
            // franchiseId 없이 재시도
            try {
              const data = await getRecipes({ isNew: true });
              setFilteredRecipes(data);
            } catch (retryError) {
              throw retryError;
            }
          }
        } else {
          // 카테고리는 백엔드 API 사용
          // franchiseId가 필요한 경우를 대비해 모든 프랜차이즈 조회
          try {
            const franchises = await getFranchises();
            if (franchises && franchises.length > 0) {
              // 모든 프랜차이즈의 카테고리별 레시피 조회
              const allPromises = franchises.map((franchise) =>
                getRecipes({ franchiseId: franchise.franchiseId, category: selectedFilter })
              );
              const allResults = await Promise.all(allPromises);
              const allRecipes = allResults.flat();
              setFilteredRecipes(allRecipes);
            } else {
              // franchiseId 없이 시도
              const data = await getRecipes({ category: selectedFilter });
              setFilteredRecipes(data);
            }
          } catch (error: any) {
            console.error('카테고리 조회 오류 (franchiseId 포함):', error);
            // franchiseId 없이 재시도
            try {
              const data = await getRecipes({ category: selectedFilter });
              setFilteredRecipes(data);
            } catch (retryError) {
              throw retryError;
            }
          }
        }
      } catch (error: any) {
        console.error('레시피 목록 조회 오류:', error);
        console.error('에러 상세:', {
          message: error?.message,
          response: error?.response?.data,
          status: error?.response?.status,
          url: error?.config?.url,
          params: error?.config?.params,
        });
        setFilteredRecipes([]);
      } finally {
        setIsLoading(false);
      }
    };

    loadFilteredRecipes();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [selectedFilter, selectedCafeId]); // selectedCafeId도 의존성에 추가하여 카페 변경 시 재로드

  // 검색어 변경 시 검색 (필터와 완전히 독립적)
  useEffect(() => {
    if (timeoutRef.current) {
      clearTimeout(timeoutRef.current);
    }

    if (keyword.trim() === '') {
      // 검색어가 없으면 검색 결과 초기화
      setSearchResults([]);
      setShowSearchResults(false);
      setIsSearching(false);
      return;
    }

    setIsSearching(true);
    setIsLoading(true);
    timeoutRef.current = setTimeout(async () => {
      try {
        const data = await searchRecipes(keyword);
        setSearchResults(data);
        setShowSearchResults(true);
      } catch (error) {
        console.error('검색 오류:', error);
        setSearchResults([]);
      } finally {
        setIsLoading(false);
        setIsSearching(false);
      }
    }, 300); // 300ms 디바운스

    return () => {
      if (timeoutRef.current) {
        clearTimeout(timeoutRef.current);
      }
    };
  }, [keyword]);

  const handleRecipeClick = (recipeId: string) => {
    navigate(`/recipes/${recipeId}`);
    setShowSearchResults(false);
  };

  const handleCreateClick = () => {
    navigate('/recipes/create');
  };

  const handleCafeChange = (cafeId: string) => {
    setSelectedCafeIdState(cafeId);
    setSelectedCafeId(cafeId);
  };

  const handleLogout = () => {
    removeToken();
    removeCafes();
    // 페이지 새로고침하여 App의 인증 상태 업데이트
    window.location.href = '/login';
  };

  const handleStaffManagementClick = () => {
    navigate('/staffs');
  };

  const handleOwnerManagementClick = () => {
    navigate('/owners');
  };

  // 즐겨찾기 토글 핸들러 (낙관적 업데이트 → 서버 응답으로 확정)
  const handleFavoriteToggle = async (e: React.MouseEvent, recipe: RecipeSearchResponse) => {
    e.stopPropagation(); // 클릭 이벤트 전파 방지 (레시피 상세 페이지로 이동하지 않도록)

    if (!isOwnerOrStaff()) {
      alert('즐겨찾기는 점주/스태프만 사용할 수 있습니다.');
      return;
    }

    const selectedCafeId = getSelectedCafeId();
    if (!selectedCafeId) {
      alert('카페를 선택해주세요.');
      return;
    }

    // 이전 상태 저장 (롤백용) - Boolean으로 강제 변환하여 undefined 방지
    const prevFavorite = Boolean(recipe.isFavorite);
    
    // 낙관적 업데이트: UI를 먼저 업데이트 (검색 결과와 필터 결과 모두 업데이트)
    const optimisticValue = !prevFavorite;
    const updateRecipe = (item: RecipeSearchResponse) =>
      item.recipeId === recipe.recipeId
        ? { ...item, isFavorite: Boolean(optimisticValue) }
        : item;

    if (keyword.trim() !== '') {
      setSearchResults((prev) => prev.map(updateRecipe));
    }
    setFilteredRecipes((prev) => prev.map(updateRecipe));

    try {
      // 레시피 상세 정보를 조회하여 기본 variant ID 가져오기
      const recipeDetail = await getRecipeDetail(recipe.recipeId);
      const defaultVariant = recipeDetail.variants.find((v) => v.default) || recipeDetail.variants[0];
      
      if (!defaultVariant) {
        // 에러 발생 시 롤백
        const rollbackRecipe = (item: RecipeSearchResponse) =>
          item.recipeId === recipe.recipeId
            ? { ...item, isFavorite: prevFavorite }
            : item;

        if (keyword.trim() !== '') {
          setSearchResults((prev) => prev.map(rollbackRecipe));
        }
        setFilteredRecipes((prev) => prev.map(rollbackRecipe));
        alert('레시피 variant를 찾을 수 없습니다.');
        return;
      }
      
      const response = await toggleFavorite({
        cafeId: selectedCafeId,
        recipeId: recipe.recipeId,
        recipeVariantId: defaultVariant.variantId,
      });

      // 서버 응답 확인 및 디버깅
      console.log('🔵 TOGGLE SUCCESS (Search) - 전체 응답:', JSON.stringify(response, null, 2));
      console.log('🔵 TOGGLE SUCCESS (Search) - response.isFavorite:', response.isFavorite);
      console.log('🔵 TOGGLE SUCCESS (Search) - response.favorite:', (response as any).favorite);
      
      // 응답에서 isFavorite 또는 favorite 필드 확인
      // Jackson이 boolean 필드를 직렬화할 때 isFavorite -> favorite로 변환할 수 있음
      let favoriteValue: boolean;
      if (response.isFavorite !== undefined) {
        favoriteValue = Boolean(response.isFavorite);
      } else if ((response as any).favorite !== undefined) {
        favoriteValue = Boolean((response as any).favorite);
      } else {
        // 응답이 없으면 낙관적 값 유지
        console.warn('⚠️ 서버 응답에 isFavorite/favorite 필드가 없음. 낙관적 값 유지:', optimisticValue);
        favoriteValue = optimisticValue;
      }

      // 서버 응답으로 상태 확정 (진실의 소스)
      const updateRecipeWithFavorite = (item: RecipeSearchResponse) =>
        item.recipeId === recipe.recipeId
          ? { ...item, isFavorite: favoriteValue }
          : item;

      if (keyword.trim() !== '') {
        setSearchResults((prev) => prev.map(updateRecipeWithFavorite));
      }
      setFilteredRecipes((prev) => prev.map(updateRecipeWithFavorite));
    } catch (err: any) {
      // 에러 발생 시 낙관적 업데이트 롤백
      const rollbackRecipe = (item: RecipeSearchResponse) =>
        item.recipeId === recipe.recipeId
          ? { ...item, isFavorite: prevFavorite }
          : item;

      if (keyword.trim() !== '') {
        setSearchResults((prev) => prev.map(rollbackRecipe));
      }
      setFilteredRecipes((prev) => prev.map(rollbackRecipe));
      
      const errorMessage = err?.response?.data?.message ||
                          err?.response?.data?.error ||
                          err?.message ||
                          '즐겨찾기 처리에 실패했습니다.';
      alert(`즐겨찾기 처리에 실패했습니다.\n${errorMessage}`);
    }
  };

  // 즐겨찾기 상태 로드 (검색 결과와 필터 결과 모두)
  // 단, 즐겨찾기 필터 선택 시에는 이미 isFavorite: true로 설정되어 있으므로 스킵
  useEffect(() => {
    // 즐겨찾기 필터 선택 시에는 스킵 (이미 getFavorites API로 목록을 가져왔고 isFavorite: true로 설정됨)
    if (selectedFilter === 'FAVORITE') {
      return;
    }

    const loadFavoriteStatus = async (recipes: RecipeSearchResponse[], setRecipes: (recipes: RecipeSearchResponse[]) => void) => {
      if (!isOwnerOrStaff() || recipes.length === 0) return;

      const selectedCafeId = getSelectedCafeId();
      if (!selectedCafeId) return;

      try {
        const favoritesData = await getFavorites(selectedCafeId);
        const favoriteRecipeIds = new Set(
          favoritesData.favorites.map((fav) => fav.recipeId)
        );

        // 즐겨찾기 상태 반영 (이미 즐겨찾기인 경우 업데이트하지 않음)
        const updatedRecipes = recipes.map((item) => {
          const isFav = Boolean(favoriteRecipeIds.has(item.recipeId));
          // 이미 같은 값이면 업데이트하지 않음 (무한 루프 방지)
          if (item.isFavorite === isFav) {
            return item;
          }
          return {
            ...item,
            isFavorite: isFav,
          };
        });

        // 실제로 변경된 항목이 있는 경우에만 업데이트
        const hasChanges = updatedRecipes.some((item, index) => item !== recipes[index]);
        if (hasChanges) {
          setRecipes(updatedRecipes);
        }
      } catch (err) {
        // 즐겨찾기 상태 조회 실패 시 무시
      }
    };

    // 검색 결과와 필터 결과 모두에 즐겨찾기 상태 적용
    if (!isLoading && !isSearching) {
      if (keyword.trim() !== '' && searchResults.length > 0) {
        loadFavoriteStatus(searchResults, setSearchResults);
      } else if (filteredRecipes.length > 0 && (selectedFilter as FilterType) !== 'FAVORITE') {
        // 즐겨찾기 필터가 아닐 때만 실행
        loadFavoriteStatus(filteredRecipes, setFilteredRecipes);
      }
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [keyword, searchResults.length, selectedCafeId, selectedFilter]); // filteredRecipes.length 제거하여 무한 루프 방지

  // 즐겨찾기 필터 적용 (클라이언트에서)
  // 즐겨찾기 상태가 로드된 후에 필터링
  useEffect(() => {
    if (selectedFilter === 'FAVORITE') {
      // 즐겨찾기 상태가 업데이트된 후 필터링
      // 이 useEffect는 즐겨찾기 상태 로드 useEffect 이후에 실행됨
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [selectedFilter]);

  return (
    <div className="recipe-search-page">
      <div className="search-container">
        <div className="search-header">
          <h1>레시피 검색</h1>
          <div className="header-actions">
            {isOwnerOrStaff() && cafes.length > 0 && (
              <select
                className="cafe-select"
                value={selectedCafeId || cafes[0]?.cafeId || ''}
                onChange={(e) => handleCafeChange(e.target.value)}
              >
                {cafes.map((cafe) => (
                  <option key={cafe.cafeId} value={cafe.cafeId}>
                    {cafe.CafeName}
                  </option>
                ))}
              </select>
            )}
            {isAdmin() && (
              <>
                <button className="create-button" onClick={handleCreateClick}>
                  레시피 추가하기
                </button>
                <button className="owner-management-button" onClick={handleOwnerManagementClick}>
                  점주 관리
                </button>
              </>
            )}
            {isOwner() && (
              <button className="staff-management-button" onClick={handleStaffManagementClick}>
                매장 관리
              </button>
            )}
            <button className="logout-button" onClick={handleLogout}>
              로그아웃
            </button>
          </div>
        </div>

        {/* 검색창 (검색 전용) */}
        <div className="search-box" ref={searchRef}>
          <input
            type="text"
            className="search-input"
            placeholder="레시피를 검색하세요..."
            value={keyword}
            onChange={(e) => setKeyword(e.target.value)}
            onFocus={() => searchResults.length > 0 && setShowSearchResults(true)}
          />
          {isSearching && <div className="loading-indicator">검색 중...</div>}

          {/* 검색 결과 표시 (검색어가 있을 때만) */}
          {showSearchResults && searchResults.length > 0 && keyword.trim() !== '' && (
            <div className="search-results">
              {searchResults.map((recipe) => {
                // 썸네일 이미지 URL 결정 (HOT 우선, 없으면 ICE)
                const thumbnailUrl = recipe.hotThumbnailUrl || recipe.iceThumbnailUrl;
                
                return (
                  <div
                    key={recipe.recipeId}
                    className="result-item"
                    onClick={() => handleRecipeClick(recipe.recipeId)}
                  >
                    {thumbnailUrl && (
                      <div className="search-result-thumbnail">
                        <img 
                          src={thumbnailUrl} 
                          alt={recipe.title}
                          onError={(e) => {
                            (e.target as HTMLImageElement).style.display = 'none';
                          }}
                        />
                      </div>
                    )}
                    <div className="result-content">
                      <div className="result-title">{recipe.title}</div>
                      <div className="result-meta">
                        <span className="category">{recipe.category}</span>
                        {recipe.isSignature && <span className="badge signature">시그니처</span>}
                        {recipe.isNew && <span className="badge new">신규</span>}
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          )}

          {showSearchResults && !isSearching && searchResults.length === 0 && keyword.trim() !== '' && (
            <div className="search-results">
              <div className="no-results">검색 결과가 없습니다.</div>
            </div>
          )}
        </div>

        {/* 필터 탭 및 레시피 목록 (검색어가 없을 때만 표시) */}
        {keyword.trim() === '' && (
          <>
            <div className="filter-tabs">
              <button
                className={`filter-tab ${selectedFilter === 'FAVORITE' ? 'active' : ''}`}
                onClick={() => setSelectedFilter('FAVORITE')}
                disabled={!isOwnerOrStaff()}
              >
                즐겨찾기
              </button>
              <button
                className={`filter-tab ${selectedFilter === 'NEW' ? 'active' : ''}`}
                onClick={() => setSelectedFilter('NEW')}
              >
                신메뉴
              </button>
              {(Object.keys(categoryLabels) as RecipeCategory[]).map((category) => (
                <button
                  key={category}
                  className={`filter-tab ${selectedFilter === category ? 'active' : ''}`}
                  onClick={() => setSelectedFilter(category)}
                >
                  {categoryLabels[category]}
                </button>
              ))}
            </div>

            {/* 레시피 목록 표시 */}
            {isLoading && (
              <div className="loading-indicator" style={{ marginTop: '20px' }}>로딩 중...</div>
            )}

            {!isLoading && filteredRecipes.length > 0 && (
              <div className="recipe-list">
                {filteredRecipes.map((recipe) => {
                  // 썸네일 이미지 URL 결정 (HOT 우선, 없으면 ICE)
                  const thumbnailUrl = recipe.hotThumbnailUrl || recipe.iceThumbnailUrl;
                  
                  return (
                    <div
                      key={recipe.recipeId}
                      className="result-item"
                      onClick={() => handleRecipeClick(recipe.recipeId)}
                    >
                      {thumbnailUrl && (
                        <div className="recipe-thumbnail">
                          <img 
                            src={thumbnailUrl} 
                            alt={recipe.title}
                            onError={(e) => {
                              (e.target as HTMLImageElement).style.display = 'none';
                            }}
                          />
                        </div>
                      )}
                      <div className="result-content">
                        <div className="result-title">{recipe.title}</div>
                        <div className="result-meta">
                          <span className="category">{recipe.category}</span>
                          {recipe.isSignature && <span className="badge signature">시그니처</span>}
                          {recipe.isNew && <span className="badge new">신규</span>}
                        </div>
                      </div>
                      {/* 즐겨찾기 필터에서만 즐겨찾기 별표 표시 (recipeVariants 레벨이므로) */}
                      {selectedFilter === 'FAVORITE' && isOwnerOrStaff() && getSelectedCafeId() && (
                        <button
                          className={`favorite-button ${recipe.isFavorite ? 'active' : ''}`}
                          onClick={(e) => handleFavoriteToggle(e, recipe)}
                          title={recipe.isFavorite ? '즐겨찾기 해제' : '즐겨찾기 추가'}
                        >
                          {recipe.isFavorite ? '★' : '☆'}
                        </button>
                      )}
                    </div>
                  );
                })}
              </div>
            )}

            {!isLoading && filteredRecipes.length === 0 && (
              <div className="recipe-list">
                <div className="no-results">
                  {selectedFilter === 'FAVORITE' 
                    ? '즐겨찾기한 레시피가 없습니다.' 
                    : selectedFilter === 'NEW'
                    ? '신메뉴가 없습니다.'
                    : '해당 카테고리의 레시피가 없습니다.'}
                </div>
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
}

export default RecipeSearch;

