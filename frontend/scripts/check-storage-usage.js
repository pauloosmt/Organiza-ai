#!/usr/bin/env node
'use strict';

const fs = require('fs');
const path = require('path');

const APP_DIR = path.resolve(__dirname, '..', 'src', 'app');
const ALLOWLIST = new Set([
  path.join(APP_DIR, 'core', 'theme', 'theme.service.ts')
]);
const STORAGE_PATTERN = /\b(localStorage|sessionStorage)\b/;

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
    if (STORAGE_PATTERN.test(content) && !ALLOWLIST.has(fullPath)) {
      console.error(`[check-storage-usage] ${path.relative(APP_DIR, fullPath)}: usa localStorage/sessionStorage fora da allowlist.`);
      hasError = true;
    }
  }
}

walk(APP_DIR);

if (hasError) {
  console.error('[check-storage-usage] FALHOU: localStorage/sessionStorage so pode ser usado em core/theme/theme.service.ts.');
  process.exit(1);
}

console.log('[check-storage-usage] OK: localStorage/sessionStorage usado somente onde permitido.');
