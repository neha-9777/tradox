import api from './axiosConfig';

export const auditApi = {
  // Get security audit logs for user
  getUserAuditLogs: async () => {
    const response = await api.get('/audit-logs');
    return response.data;
  },
};
