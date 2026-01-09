import { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { searchRecipes, toggleFavorite, getFavorites, getRecipeDetail } from '../lib/api';
import type { RecipeSearchResponse } from '../types/recipe';
import { isOwner, isOwnerOrStaff, getCafes, getSelectedCafeId, setSelectedCafeId, removeToken, removeCafes, isAdmin, getRole, setCafes } from '../utils/auth';
import { getOwnersCafes, getStaffCafes } from '../lib/api';
import type { OwnedCafeSummary } from '../types/member';
import './RecipeSearch.css';

function RecipeSearch() {
  const [keyword, setKeyword] = useState('');
  const [results, setResults] = useState<RecipeSearchResponse[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [showResults, setShowResults] = useState(false);
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
        setShowResults(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  useEffect(() => {
    if (timeoutRef.current) {
      clearTimeout(timeoutRef.current);
    }

    if (keyword.trim() === '') {
      setResults([]);
      setShowResults(false);
      return;
    }

    setIsLoading(true);
    timeoutRef.current = setTimeout(async () => {
      try {
        const data = await searchRecipes(keyword);
        setResults(data);
        setShowResults(true);
      } catch (error) {
        console.error('검색 오류:', error);
        setResults([]);
      } finally {
        setIsLoading(false);
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
    setShowResults(false);
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
    
    // 낙관적 업데이트: UI를 먼저 업데이트
    const optimisticValue = !prevFavorite;
    setResults((prev) =>
      prev.map((item) =>
        item.recipeId === recipe.recipeId
          ? { ...item, isFavorite: Boolean(optimisticValue) }
          : item
      )
    );

    try {
      // 레시피 상세 정보를 조회하여 기본 variant ID 가져오기
      const recipeDetail = await getRecipeDetail(recipe.recipeId);
      const defaultVariant = recipeDetail.variants.find((v) => v.default) || recipeDetail.variants[0];
      
      if (!defaultVariant) {
        // 에러 발생 시 롤백
        setResults((prev) =>
          prev.map((item) =>
            item.recipeId === recipe.recipeId
              ? { ...item, isFavorite: prevFavorite }
              : item
          )
        );
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
      setResults((prev) =>
        prev.map((item) =>
          item.recipeId === recipe.recipeId
            ? { ...item, isFavorite: favoriteValue }
            : item
        )
      );
    } catch (err: any) {
      // 에러 발생 시 낙관적 업데이트 롤백
      setResults((prev) =>
        prev.map((item) =>
          item.recipeId === recipe.recipeId
            ? { ...item, isFavorite: prevFavorite }
            : item
        )
      );
      
      console.error('❌ 즐겨찾기 오류:', err);
      const errorMessage = err?.response?.data?.message ||
                          err?.response?.data?.error ||
                          err?.message ||
                          '즐겨찾기 처리에 실패했습니다.';
      alert(`즐겨찾기 처리에 실패했습니다.\n${errorMessage}`);
    }
  };

  // 검색 결과 로드 시 즐겨찾기 상태 확인
  useEffect(() => {
    const loadFavoriteStatus = async () => {
      if (!isOwnerOrStaff() || results.length === 0) return;

      const selectedCafeId = getSelectedCafeId();
      if (!selectedCafeId) return;

      try {
        const favoritesData = await getFavorites(selectedCafeId);
        const favoriteRecipeIds = new Set(
          favoritesData.favorites.map((fav) => fav.recipeId)
        );

        // results 상태를 업데이트하여 즐겨찾기 상태 반영
        // Boolean으로 강제 변환하여 undefined 방지
        setResults((prev) =>
          prev.map((item) => ({
            ...item,
            isFavorite: Boolean(favoriteRecipeIds.has(item.recipeId)),
          }))
        );
      } catch (err) {
        console.error('즐겨찾기 상태 조회 오류:', err);
      }
    };

    // 검색 결과가 있을 때만 즐겨찾기 상태 확인
    if (results.length > 0 && !isLoading) {
      loadFavoriteStatus();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [keyword, results.length]); // keyword나 results.length가 변경될 때만 실행

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

        <div className="search-box" ref={searchRef}>
          <input
            type="text"
            className="search-input"
            placeholder="레시피를 검색하세요..."
            value={keyword}
            onChange={(e) => setKeyword(e.target.value)}
            onFocus={() => results.length > 0 && setShowResults(true)}
          />
          {isLoading && <div className="loading-indicator">검색 중...</div>}

          {showResults && results.length > 0 && (
            <div className="search-results">
              {results.map((recipe) => (
                <div
                  key={recipe.recipeId}
                  className="result-item"
                  onClick={() => handleRecipeClick(recipe.recipeId)}
                >
                  <div className="result-content">
                    <div className="result-title">{recipe.title}</div>
                    <div className="result-meta">
                      <span className="category">{recipe.category}</span>
                      {recipe.isSignature && <span className="badge signature">시그니처</span>}
                      {recipe.isNew && <span className="badge new">신규</span>}
                    </div>
                  </div>
                  {isOwnerOrStaff() && getSelectedCafeId() && (
                    <button
                      className={`favorite-button ${recipe.isFavorite ? 'active' : ''}`}
                      onClick={(e) => handleFavoriteToggle(e, recipe)}
                      title={recipe.isFavorite ? '즐겨찾기 해제' : '즐겨찾기 추가'}
                    >
                      {recipe.isFavorite ? '★' : '☆'}
                    </button>
                  )}
                </div>
              ))}
            </div>
          )}

          {showResults && !isLoading && results.length === 0 && keyword.trim() !== '' && (
            <div className="search-results">
              <div className="no-results">검색 결과가 없습니다.</div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default RecipeSearch;

