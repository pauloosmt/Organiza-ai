export interface User {
  id: string;
  name: string;
  email: string;
  tema: 'light' | 'dark' | null;
  createdAt: string;
}
