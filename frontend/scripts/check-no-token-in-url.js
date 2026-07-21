#!/usr/bin/env node
'use strict';

const fs = require('fs');
const path = require('path');

const APP_DIR = path.resolve(__dirname, '..', 'src', 'app');
const CREDENTIAL_LIKE = /^(token|codigo|senha|jwt|password|secret)$/i;

let hasError = false;

function checkRouteParams(fullPath, content) {
  for (const match of content.matchAll(/path:\s*['"]([^'"]*)['"]/g)) {
    const segments = match[1].split('/');
    for (const segment of segments) {
      if (segment.startsWith(':') && CREDENTIAL_LIKE.test(segment.slice(1))) {
        console.error(`[check-no-token-in-url] ${path.relative(APP_DIR, fullPath)}: rota "${match[1]}" tem parametro "${segment}" com cara de credencial.`);
        hasError = true;
      }
    }
  }
}

function checkQueryParams(fullPath, content) {
  for (const match of content.matchAll(/queryParams\]?\s*[:=]\s*['"{]?\s*{([^}]*)}/g)) {
    const keys = [...match[1].matchAll(/(?:^|[{,])\s*['"]?(\w+)['"]?\s*:/g)].map((m) => m[1]);
    for (const key of keys) {
      if (CREDENTIAL_LIKE.test(key)) {
        console.error(`[check-no-token-in-url] ${path.relative(APP_DIR, fullPath)}: queryParams usa a chave "${key}", com cara de credencial.`);
        hasError = true;
      }
    }
  }
}

function walk(dir) {
  for (const entry of fs.readdirSync(dir, { withFileTypes: true })) {
    const fullPath = path.join(dir, entry.name);
    if (entry.isDirectory()) {
      walk(fullPath);
      continue;
    }
    if (!entry.isFile() || (!fullPath.endsWith('.ts') && !fullPath.endsWith('.html')) || fullPath.endsWith('.spec.ts')) {
      continue;
    }

    const content = fs.readFileSync(fullPath, 'utf8');
    if (fullPath.endsWith('.routes.ts')) {
      checkRouteParams(fullPath, content);
    }
    checkQueryParams(fullPath, content);
  }
}

walk(APP_DIR);

if (hasError) {
  console.error('[check-no-token-in-url] FALHOU: token/codigo/senha nunca pode ir em rota ou query string.');
  process.exit(1);
}

console.log('[check-no-token-in-url] OK: nenhuma rota ou queryParams com cara de credencial.');
