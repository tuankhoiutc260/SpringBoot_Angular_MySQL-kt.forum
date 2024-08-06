export interface JwtPayload {
    sub?: string;
    scope?: string;
    iss?: string;
    exp?: number;
    iat?: number;
    jti?: string;
  }
  