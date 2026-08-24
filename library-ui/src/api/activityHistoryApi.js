
import api from './axios'; 

export const getActivityHistory = async () => {
  const response = await api.get('/activities/user');
  return response.data;
};