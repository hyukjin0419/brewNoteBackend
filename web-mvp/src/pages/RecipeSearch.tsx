import { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { searchRecipes } from '../lib/api';
import type { RecipeSearchResponse } from '../types/recipe';
import './RecipeSearch.css';

function RecipeSearch() {
  const [keyword, setKeyword] = useState('');
  const [results, setResults] = useState<RecipeSearchResponse[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [showResults, setShowResults] = useState(false);
  const navigate = useNavigate();
  const searchRef = useRef<HTMLDivElement>(null);
  const timeoutRef = useRef<NodeJS.Timeout>();

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

  return (
    <div className="recipe-search-page">
      <div className="search-container">
        <div className="search-header">
          <h1>레시피 검색</h1>
          <button className="create-button" onClick={handleCreateClick}>
            레시피 추가하기
          </button>
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

