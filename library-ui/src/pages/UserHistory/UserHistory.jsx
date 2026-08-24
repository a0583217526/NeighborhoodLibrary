import React, { useEffect, useState } from 'react';
import './UserHistory.css';
import { getActivityHistory } from '../../api/activityHistoryApi';

function UserHistory() {
  const [logs, setLogs] = useState([]);

  // פונקציית עזר לעיצוב התאריך בצורה קריאה יותר
  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleString('he-IL', {
      day: 'numeric',
      month: 'long',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (token) {
      getActivityHistory(token)
        .then(data => setLogs(data))
        .catch(err => console.error("Error fetching history:", err));
    }
  }, []);

  return (
    <div className="history-container">
      <div className="history-list">
        {logs.map((log) => (
          // שימוש ב-log.id כמפתח, ואם לא קיים שימוש ב-createdAt כגיבוי
          <div key={log.id || log.createdAt} className="history-item">
            <span className="date">
              {formatDate(log.createdAt)}
            </span>
            <span className="type">
              <strong>{log.actionTypeHebrew}</strong>
            </span>
            <span className="desc">{log.description}</span>
          </div>
        ))}
      </div>
    </div>
  );
}

export default UserHistory;