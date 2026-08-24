import { useState } from 'react';
import UserProfile from '../UserProfile/UserProfile';
import UserHistory from '../UserHistory/UserHistory';
import './MainScreen.css'; 

const menuItems = [
  { label: 'הגדרות משתמש', key: 'settings' },
  { label: 'היסטוריית פעילות', key: 'history' },
  { label: 'ספרים', key: 'books' },
  { label: 'השאלה', key: 'loans' },
  { label: 'התראות', key: 'notifications' },
  { label: 'הצעות בינה מלאכותית', key: 'ai' },
];

function MainScreen() {
  const [selected, setSelected] = useState('settings');

  const renderContent = () => {
    switch (selected) {
      case 'settings':
        return <UserProfile />;
      case 'history':
        return <UserHistory />;
      default:
        return <p className="placeholder">תוכן המסך יופיע כאן בהמשך הפיתוח.</p>;
    }
  };

  return (
    <div className="container">
      <div className="sidebar">
        <h2 className="logo">ספריית השכונה</h2>
        {menuItems.map((item) => (
          <div
            key={item.key}
            className={`menu-item ${selected === item.key ? 'active' : ''}`}
            onClick={() => setSelected(item.key)}
          >
            {item.label}
          </div>
        ))}
      </div>

      <div className="content">
        <h2 className="content-title">
          {menuItems.find((i) => i.key === selected)?.label}
        </h2>
        {renderContent()}
      </div>
    </div>
  );
}

export default MainScreen;