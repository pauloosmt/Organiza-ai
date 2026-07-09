// TODO: substituir <API_ORIGIN> pela origem real do backend em produção
// assim que a plataforma de deploy for definida (ver specs/005-seguranca-frontend-backend).
// O mesmo valor precisa ser espelhado no connect-src da CSP em src/index.prod.html.
export const environment = {
  apiUrl: 'https://<API_ORIGIN>/api'
};
