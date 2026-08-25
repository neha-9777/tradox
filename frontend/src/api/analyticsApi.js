import api from './axiosConfig';

export const analyticsApi = {
  // Get complete analytics dashboard data
  getDashboardAnalytics: async () => {
    const response = await api.get('/analytics/dashboard');
    return response.data;
  },

  // Get historical portfolio performance trend curve points
  getPerformanceTrend: async (period = '1Y') => {
    const response = await api.get('/analytics/performance-trend', {
      params: { period }
    });
    return response.data;
  }
};
