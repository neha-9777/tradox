import api from './axiosConfig';

export const brokerApi = {
  // Get broker capabilities comparison matrix
  getBrokerCapabilities: async () => {
    const response = await api.get('/brokers/capabilities');
    return response.data;
  },

  // Get user connected broker accounts
  getUserConnections: async () => {
    const response = await api.get('/brokers');
    return response.data;
  },

  // Connect a broker account
  connectBroker: async (connectionData) => {
    const response = await api.post('/brokers/connect', connectionData);
    return response.data;
  },

  // Trigger manual data sync for connection
  syncBrokerData: async (connectionId) => {
    const response = await api.post(`/brokers/${connectionId}/sync`);
    return response.data;
  },

  // Disconnect broker account
  disconnectBroker: async (connectionId) => {
    const response = await api.delete(`/brokers/${connectionId}`);
    return response.data;
  },

  // Get sync logs for connection
  getConnectionLogs: async (connectionId) => {
    const response = await api.get(`/brokers/${connectionId}/logs`);
    return response.data;
  },

  // Trigger quick dev mock sync
  triggerDevMockSync: async () => {
    const response = await api.post('/brokers/dev/sync');
    return response.data;
  },
};
