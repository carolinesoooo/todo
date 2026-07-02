const CACHE_NAME = 'todo-app-v2';
const ASSETS = [
  './todo.html',
  './manifest.json',
  './icon-192.png',
  './icon-512.png',
  './icon.svg'
];

// 安装：预缓存静态资源
self.addEventListener('install', (e) => {
  e.waitUntil(
    caches.open(CACHE_NAME).then((cache) => {
      console.log('[SW] 缓存资源中…');
      return cache.addAll(ASSETS).catch((err) => {
        console.warn('[SW] 部分资源缓存失败:', err);
      });
    })
  );
  // 立即激活，不等旧 SW 退出
  self.skipWaiting();
});

// 激活：清理旧缓存
self.addEventListener('activate', (e) => {
  e.waitUntil(
    caches.keys().then((keys) => {
      return Promise.all(
        keys.filter((key) => key !== CACHE_NAME).map((key) => caches.delete(key))
      );
    })
  );
  // 立即接管所有页面
  self.clients.claim();
});

// 请求拦截：缓存优先 + 网络回退
self.addEventListener('fetch', (e) => {
  // 跳过非 GET 请求和 chrome-extension
  if (e.request.method !== 'GET') return;
  if (!e.request.url.startsWith('http')) return;

  e.respondWith(
    caches.match(e.request).then((cached) => {
      // 缓存命中直接返回
      if (cached) return cached;

      // 否则走网络，成功后缓存
      return fetch(e.request).then((response) => {
        if (!response || response.status !== 200 || response.type !== 'basic') {
          return response;
        }
        const clone = response.clone();
        caches.open(CACHE_NAME).then((cache) => {
          cache.put(e.request, clone);
        });
        return response;
      }).catch(() => {
        // 网络失败，返回离线页面（HTML 请求）
        if (e.request.headers.get('Accept')?.includes('text/html')) {
          return caches.match('./todo.html');
        }
        return new Response('离线中', { status: 503 });
      });
    })
  );
});
