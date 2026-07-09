#!/usr/bin/env node
'use strict';

const fs = require('fs');
const path = require('path');

const ALLOWED_KEYS = new Set(['apiUrl']);
const SECRET_LIKE = /secret|password|senha|token|private|apikey|api_key/i;
const ENV_DIR = path.resolve(__dirname, '..', 'src', 'environments');

let hasError = false;

for (const file of fs.readdirSync(ENV_DIR)) {
  if (!file.endsWith('.ts')) {
    continue;
  }

  const filePath = path.join(ENV_DIR, file);
  const content = fs.readFileSync(filePath, 'utf8');
  const objectMatch = content.match(/environment\s*=\s*{([\s\S]*?)}/);
  if (!objectMatch) {
    continue;
  }

  const keys = [...objectMatch[1].matchAll(/(?:^|[{,])\s*(\w+)\s*:/gm)].map((m) => m[1]);

  for (const key of keys) {
    if (!ALLOWED_KEYS.has(key)) {
      console.error(`[check-no-secrets] ${file}: chave "${key}" nao esta na allowlist (${[...ALLOWED_KEYS].join(', ')}).`);
      hasError = true;
    }
    if (SECRET_LIKE.test(key)) {
      console.error(`[check-no-secrets] ${file}: chave "${key}" parece um segredo.`);
      hasError = true;
    }
  }
}

if (hasError) {
  console.error('[check-no-secrets] FALHOU: variaveis de build do frontend nao podem conter segredos.');
  process.exit(1);
}

console.log('[check-no-secrets] OK: nenhum segredo encontrado em src/environments/*.ts');
