import { Routes, Route } from 'react-router-dom';
import RecipeSearch from './pages/RecipeSearch';
import RecipeDetail from './pages/RecipeDetail';
import RecipeCreate from './pages/RecipeCreate';
import './App.css';

function App() {
  return (
    <div className="app">
      <Routes>
        <Route path="/" element={<RecipeSearch />} />
        <Route path="/recipes/:recipeId" element={<RecipeDetail />} />
        <Route path="/recipes/create" element={<RecipeCreate />} />
      </Routes>
    </div>
  );
}

export default App;

