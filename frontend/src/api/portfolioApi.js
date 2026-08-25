import api from './axiosConfig';

export const portfolioApi = {
  // Get all portfolios
  getUserPortfolios: async () => {
    const response = await api.get('/portfolios');
    return response.data;
  },

  // Create portfolio
  createPortfolio: async (portfolioData) => {
    const response = await api.post('/portfolios', portfolioData);
    return response.data;
  },

  // Get portfolio summary
  getOverallSummary: async () => {
    const response = await api.get('/portfolios/summary');
    return response.data;
  },

  // Get portfolio by ID
  getPortfolioById: async (id) => {
    const response = await api.get(`/portfolios/${id}`);
    return response.data;
  },

  // Update portfolio
  updatePortfolio: async (id, portfolioData) => {
    const response = await api.put(`/portfolios/${id}`, portfolioData);
    return response.data;
  },

  // Delete portfolio
  deletePortfolio: async (id) => {
    const response = await api.delete(`/portfolios/${id}`);
    return response.data;
  },

  // Get transactions for portfolio
  getTransactions: async (portfolioId) => {
    const response = await api.get(`/portfolios/${portfolioId}/transactions`);
    return response.data;
  },

  // Add transaction
  addTransaction: async (portfolioId, transactionData) => {
    const response = await api.post(`/portfolios/${portfolioId}/transactions`, transactionData);
    return response.data;
  },

  // Delete transaction
  deleteTransaction: async (portfolioId, txId) => {
    const response = await api.delete(`/portfolios/${portfolioId}/transactions/${txId}`);
    return response.data;
  },

  // Get all user transactions
  getAllTransactions: async () => {
    const response = await api.get('/portfolios/transactions/all');
    return response.data;
  },
};
