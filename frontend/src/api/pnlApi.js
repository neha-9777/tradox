import api from './axiosConfig';

export const pnlApi = {
  // Get comprehensive P&L summary
  getPnLSummary: async () => {
    const response = await api.get('/pnl/summary');
    return response.data;
  },
};
