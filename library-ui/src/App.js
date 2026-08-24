import { BrowserRouter, Routes, Route } from 'react-router-dom';
import LoginPage from './pages/LoginPage/LoginPage';
import MainScreen from './pages/MainScreen/MainScreen';
import UserHistory from './pages/UserHistory/UserHistory';
import AuthSuccessPage from './pages/AuthSuccessPage/AuthSuccessPage';
import CompleteGooglePage from './pages/CompleteGooglePage/CompleteGooglePage';
import RegisterPage from './pages/RegisterPage/RegisterPage';
import UserProfile from './pages/UserProfile/UserProfile';
import Navbar from './components/Navbar/Navbar';

function App() {
  return (
    <BrowserRouter>
      <Navbar />
      <Routes>
        <Route path="/" element={<LoginPage />} />
        <Route path="/main" element={<MainScreen />} />
        <Route path="/profile" element={<UserProfile />} />
        <Route path="/history" element={<UserHistory />} />
        <Route path="/auth-success" element={<AuthSuccessPage />} />
        <Route path="/complete-google" element={<CompleteGooglePage />} />
        <Route path="/register" element={<RegisterPage />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;