# 界面宣传素材

当前 README 使用 Cloudflare Stream 托管的 **60 秒版宣传片**，展示 `ui/apple-tv-redesign` 的界面：

- 在线观看：https://customer-nkorbwmhxz04cbfd.cloudflarestream.com/b941aa27d5013806eb8fcab30cce4807/iframe
- HLS 直链（供播放器或应用直接使用）：https://customer-nkorbwmhxz04cbfd.cloudflarestream.com/b941aa27d5013806eb8fcab30cce4807/manifest/video.m3u8
- 下载 MP4（1080p，约 9.9 MiB）：https://customer-nkorbwmhxz04cbfd.cloudflarestream.com/b941aa27d5013806eb8fcab30cce4807/downloads/default.mp4

`TV-promo-cover-60s.jpg` 为该片的标题卡画面，2026-10-17 取自 Cloudflare Stream 的 `thumbnails/thumbnail.jpg?time=8s&height=1080`。

以下文件是 2026-10-04 交付的 **90 秒版**，保留作存档与离线使用。宣传片基于 `2dc4576fe` 的模拟器 UI 录制；此后的播放器和细节修复以当前源码及验证记录为准。

| 文件 | 用途 | 规格 |
| --- | --- | --- |
| [TV-promo-cover-60s.jpg](TV-promo-cover-60s.jpg) | 当前宣传片封面 | 1920×1080，视频标题卡画面 |
| [TV-promo.mp4](TV-promo.mp4) | 上一版 90 秒宣传片 | 1920×1080，30 fps，H.264 / AAC，faststart，约 12.1 MiB |
| [TV-promo-cover.jpg](TV-promo-cover.jpg) | 上一版宣传封面 | 1920×1080 |
| [TV-promo-contact.jpg](TV-promo-contact.jpg) | 上一版 13 个章节的分镜总览 | 1184×1128 |

影片使用演示片库；推荐页面包含录制时应用展示的海报。分镜总览是成片章节预览，并非另一份独立海报。

README 以封面链接至在线播放页，并提供 MP4 下载；不依赖 README 中不稳定的 HTML `video` 支持。视频无需 Git LFS。

完整来源、改编说明和字体许可见 [CREDITS.md](CREDITS.md)。演示影片为 **Sintel © Blender Foundation，CC BY 3.0**，照片来自 Unsplash，配乐为合成音乐。媒体文件的内容哈希见 [SHA256SUMS.txt](SHA256SUMS.txt)。
