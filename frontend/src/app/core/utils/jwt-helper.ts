import { jwtDecode } from "jwt-decode";
import {  JwtPayload } from '../../api/model/entity/jwt-payload';

export function getRoleFromToken(token: string): string | null {
  try {
    const decodedToken = jwtDecode<JwtPayload>(token);
    if (decodedToken.scope) {
      const rolePart = decodedToken.scope.split(' ').find(part => part.startsWith('role:'));

      if (rolePart) {
        return rolePart.split(':')[1];
      }
    }

    return null;
  } catch (error) {
    console.error('Error decoding token:', error);
    return null;
  }
}

export function getSubFromToken(token: string): string | null {
  try {
    const decodedToken = jwtDecode<JwtPayload>(token);
    return decodedToken.sub || null;
  } catch (error) {
    console.error('Error decoding token:', error);
    return null;
  }
}
