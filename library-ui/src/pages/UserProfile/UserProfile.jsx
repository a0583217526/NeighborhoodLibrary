import React, { useState, useEffect } from 'react';
import { getProfile, updateProfile } from '../../api/userApi';
import { fetchNeighborhoods } from '../../api/authApi'; // ייבוא הפונקציה להבאת השכונות
import './UserProfile.css';

function UserProfile() {
    const [isEditing, setIsEditing] = useState(false);
    const [user, setUser] = useState({ fullName: '', email: '', phone: '', neighborhoodName: '' });
    const [neighborhoods, setNeighborhoods] = useState([]); // State עבור רשימת השכונות

    useEffect(() => {
        // משיכת פרטי משתמש
        getProfile().then(res => setUser(res.data)).catch(err => console.error(err));
        
        // משיכת השכונות בטעינת העמוד
        fetchNeighborhoods().then(data => setNeighborhoods(data)).catch(err => console.error("שגיאה בטעינת שכונות", err));
    }, []);

    const handleSave = async () => {
        try {
            await updateProfile(user);
            setIsEditing(false);
            alert("הפרופיל עודכן בהצלחה!");
        } catch (err) { alert("שגיאה בעדכון הפרופיל"); }
    };

    return (
        <div className="profile-container">
            <h1>הפרופיל שלי</h1>
            <div className="profile-field">
                <label>שם מלא:</label>
                <input disabled={!isEditing} value={user.fullName} 
                       onChange={e => setUser({...user, fullName: e.target.value})} />
            </div>
            <div className="profile-field">
                <label>אימייל:</label>
                <input disabled value={user.email} />
            </div>
            <div className="profile-field">
                <label>טלפון:</label>
                <input disabled={!isEditing} value={user.phone} 
                       onChange={e => setUser({...user, phone: e.target.value})} />
            </div>
            <div className="profile-field">
                <label>שכונה:</label>
                {/* שימוש ב-list המקושר ל-datalist */}
                <input 
                    disabled={!isEditing} 
                    value={user.neighborhoodName} 
                    list="neighborhoods-list"
                    onChange={e => setUser({...user, neighborhoodName: e.target.value})} 
                />
                
                {/* הצגת ה-datalist רק כאשר נמצאים במצב עריכה */}
                {isEditing && (
                    <datalist id="neighborhoods-list">
                        {neighborhoods.map((n) => (
                            <option key={n.id || n.name} value={n.name} />
                        ))}
                    </datalist>
                )}
            </div>
            
            <div className="actions">
                {isEditing ? (
                    <button onClick={handleSave}>שמור שינויים</button>
                ) : (
                    <button onClick={() => setIsEditing(true)}>עריכת פרופיל</button>
                )}
            </div>
        </div>
    );
}

export default UserProfile;