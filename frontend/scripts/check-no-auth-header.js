#!/usr/bin/env node
'use strict';

const fs = require('fs');
const path = require('path');

const APP_DIR = path.resolve(__dirname, '..', 'src', 'app');
// Nomes de header que carregariam credencial se fossem montados manualmente.
// A autenticação deste app é só via cookie HttpOnly (ver credentials.interceptor.ts) —
// nenhum arquivo do frontend deveria bater com isso.
const AUTH_HEADER_PATTERN = /(['"`])(Authorization|Proxy-Authorization|X-Api-Key|X-Auth-Token|X-Access-Token)\1|\bAuthorization\s*:/i;

let hasError = false;

function walk(dir) {
  for (const entry of fs.readdirSync(dir, { withFileTypes: true })) {
    const fullPath = path.join(dir, entry.name);
    if (entry.isDirectory()) {
      walk(fullPath);
      continue;
    }
    if (!entry.isFile() || !fullPath.endsWith('.ts') || fullPath.endsWith('.spec.ts')) {
      continue;
    }

    const content = fs.readFileSync(fullPath, 'utf8');
    if (AUTH_HEADER_PATTERN.test(content)) {
      console.error(`[check-no-auth-header] ${path.relative(APP_DIR, fullPath)}: monta um header de autenticacao manualmente.`);
      hasError = true;
    }
  }
}

walk(APP_DIR);

if (hasError) {
  console.error('[check-no-auth-header] FALHOU: autenticacao deve usar somente o cookie HttpOnly, nunca header manual.');
  process.exit(1);
}

console.log('[check-no-auth-header] OK: nenhum header de autenticacao montado manualmente.');
