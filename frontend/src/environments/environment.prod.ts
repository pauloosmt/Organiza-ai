// URL relativa: a Vercel faz proxy de /api/* pro backend no Render (ver
// frontend/vercel.json), então o navegador só fala com o próprio domínio da
// Vercel. Isso evita que o cookie de auth seja tratado como "de terceiro" e
// bloqueado por navegadores mobile (Safari/Chrome Android). Se o domínio do
// backend no Render mudar, atualizar o destino do rewrite em vercel.json.
export const environment = {
  apiUrl: '/api',
  // /healthz é proxiado pra /actuator/health no Render (ver vercel.json), mesmo
  // motivo do /api: manter tudo same-origin.
  healthUrl: '/healthz'
};
