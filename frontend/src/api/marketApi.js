import api from './axiosConfig';

export const marketApi = {
  // Get current market quote for symbol
  getMarketQuote: async (symbol) => {
    const response = await api.get(`/market/quote/${symbol}`);
    return response.data;
  },

  // Get historical price candles
  getHistoricalPrices: async (symbol, days = 30) => {
    const response = await api.get(`/market/history/${symbol}?days=${days}`);
    return response.data;
  },

  // Refresh market prices for portfolio holdings
  refreshPortfolioHoldings: async (portfolioId) => {
    const response = await api.post(`/market/portfolios/${portfolioId}/refresh`);
    return response.data;
  },
};
