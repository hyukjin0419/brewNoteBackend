import { Routes, Route, Navigate } from 'react-router-dom';
import { useEffect, useState } from 'react';
import RecipeSearch from './pages/RecipeSearch';
import RecipeDetail from './pages/RecipeDetail';
import RecipeCreate from './pages/RecipeCreate';
import OwnerManagement from './pages/OwnerManagement';
import Login from './pages/Login';
import { isAuthenticated } from './utils/auth';
import './App.css';

function App() {
  const [authChecked, setAuthChecked] = useState(false);
  const [authenticated, setAuthenticated] = useState(false);

  useEffect(() => {
    const checkAuth = () => {
      const auth = isAuthenticated();
      setAuthenticated(auth);
      setAuthChecked(true);
    };
    checkAuth();
  }, []);

  // 인증 상태 확인 중일 때는 로딩 표시
  if (!authChecked) {
    return (
      <div className="app">
        <div style={{ 
          display: 'flex', 
          justifyContent: 'center', 
          alignItems: 'center', 
          height: '100vh',
          fontSize: '18px',
          color: '#666'
        }}>
          로딩 중...
        </div>
      </div>
    );
  }

  return (
    <div className="app">
      <Routes>
        <Route 
          path="/login" 
          element={authenticated ? <Navigate to="/" replace /> : <Login />} 
        />
        <Route 
          path="/" 
          element={authenticated ? <RecipeSearch /> : <Navigate to="/login" replace />} 
        />
        <Route 
          path="/recipes/:recipeId" 
          element={authenticated ? <RecipeDetail /> : <Navigate to="/login" replace />} 
        />
        <Route 
          path="/recipes/create" 
          element={authenticated ? <RecipeCreate /> : <Navigate to="/login" replace />} 
        />
        <Route 
          path="/owners" 
          element={authenticated ? <OwnerManagement /> : <Navigate to="/login" replace />} 
        />
      </Routes>
    </div>
  );
}

export default App;

