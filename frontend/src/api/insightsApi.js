import api from './axiosConfig';

export const insightsApi = {
  // Get complete intelligent insights dashboard
  getInsightsDashboard: async () => {
    const response = await api.get('/insights/dashboard');
    return response.data;
  },

  // Generate or get AI Commentary with optional API key
  getAiCommentary: async (apiKey = '', customPrompt = '') => {
    const response = await api.post('/insights/ai-commentary', {
      apiKey,
      customPrompt
    });
    return response.data;
  }
};
