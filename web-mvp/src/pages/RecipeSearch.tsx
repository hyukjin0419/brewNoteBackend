import { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { searchRecipes } from '../lib/api';
import type { RecipeSearchResponse } from '../types/recipe';
import { isOwner, getCafes, getSelectedCafeId, setSelectedCafeId, removeToken, removeCafes, isAdmin } from '../utils/auth';
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
    if (isOwner()) {
      const cafesJson = getCafes();
      if (cafesJson) {
        try {
          const cafesData: OwnedCafeSummary[] = JSON.parse(cafesJson);
          if (cafesData.length > 0) {
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
          }
        } catch (error) {
          console.error('카페 목록 파싱 오류:', error);
        }
      }
    }
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

  return (
    <div className="recipe-search-page">
      <div className="search-container">
        <div className="search-header">
          <h1>레시피 검색</h1>
          <div className="header-actions">
            {isOwner() && cafes.length > 0 && (
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
                  <div className="result-title">{recipe.title}</div>
                  <div className="result-meta">
                    <span className="category">{recipe.category}</span>
                    {recipe.isSignature && <span className="badge signature">시그니처</span>}
                    {recipe.isNew && <span className="badge new">신규</span>}
                  </div>
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

