<div align="center">
  <img src="docs/assets/cctv-tv-icon.png" alt="CCTV 电视图标" width="128" height="128">

  # CCTV 电视

  **面向 Android TV 与投影设备的轻量电视播放器**

  在一个遥控器友好的界面中观看 CCTV 官方网页直播，也可以扫描并播放设备中的本地视频。

  <p>
    <img src="https://img.shields.io/badge/Android-5.0%2B-3DDC84?style=flat-square&logo=android&logoColor=white" alt="Android 5.0+">
    <img src="https://img.shields.io/badge/Android_TV-Remote_ready-11161B?style=flat-square" alt="Android TV">
    <img src="https://img.shields.io/badge/Media3-ExoPlayer-F2A33A?style=flat-square" alt="Media3 ExoPlayer">
    <img src="https://img.shields.io/badge/Language-Java-E76F00?style=flat-square" alt="Java">
  </p>
</div>

## 为什么做这个项目

CCTV 电视最初是为投影仪设计的简单电视应用：打开即可播放，使用方向键即可换台，不需要输入复杂地址或维护直播源。

当前版本在保留 CCTV 官方网页直播方案的基础上，增加了本地视频扫描与播放能力，并把频道、本地媒体和播放控制整理为统一的电视端交互。

## 核心能力

| 模块 | 能力 | 说明 |
| --- | --- | --- |
| 电视直播 | CCTV 频道目录 | 从本地 JSON 加载频道，避免在代码中硬编码 |
| 网页播放 | 官方页面封装 | 使用 WebView 打开 CCTV 官方直播页并自动进入播放状态 |
| 本地视频 | 自动扫描 | 通过 MediaStore 扫描设备及外接存储中已登记的视频 |
| 媒体列表 | 信息与缩略图 | 展示文件名、目录、时长、大小和视频缩略图 |
| 原生播放 | Media3 / ExoPlayer | 支持常见本地视频格式、进度控制和播放状态管理 |
| 电视交互 | 遥控器优先 | 方向键切换媒体，确认键暂停或继续，返回键回到列表 |
| 触摸交互 | 单击与双击 | 单击显示或隐藏列表，双击暂停或继续播放 |

## 操作方式

### 遥控器

- 播放状态下使用上下或左右方向键切换频道 / 视频。
- 按确认键暂停或继续播放。
- 按返回键显示频道 / 视频列表，再次选择即可播放。

### 鼠标与触摸

- 单击播放区域显示或隐藏左侧列表。
- 双击播放区域暂停，继续双击恢复播放。
- 点击“电视直播”或“本地视频”切换内容来源。

## 系统要求

- Android 5.0（API 21）或更高版本
- Android TV、投影仪或支持横屏的 Android 设备
- 观看电视直播需要可用网络和 Android System WebView
- 播放本地视频需要授予视频读取权限

权限策略：

- Android 13 及以上使用 `READ_MEDIA_VIDEO`。
- Android 12 及以下使用 `READ_EXTERNAL_STORAGE`。

## 快速开始

克隆仓库并构建 Debug APK：

```bash
git clone https://github.com/baiyazi/CCTV.git
cd CCTV
./gradlew assembleDebug
```

构建产物位于：

```text
app/build/outputs/apk/debug/app-debug.apk
```

连接 Android 设备后可以直接安装：

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## 技术栈

- **Java / AndroidX**：应用界面、生命周期与遥控器交互
- **WebView**：加载 CCTV 官方直播页面
- **Media3 / ExoPlayer**：本地视频播放
- **MediaStore**：本地视频索引与元数据扫描
- **RecyclerView**：频道和本地视频列表
- **Glide**：本地视频缩略图加载

## 项目结构

```text
CCTV/
├── app/src/main/assets/cctv/      # CCTV 频道配置与频道图标
├── app/src/main/java/             # 播放、扫描、列表与页面控制代码
├── app/src/main/res/              # Android 布局、图标、横幅与样式资源
├── docs/assets/                   # README 品牌素材
├── docs/                          # 方案评估与设计文档
├── tools/                         # 品牌资源生成脚本
└── apk_release_note/              # 历史版本 APK
```

## 频道配置

频道目录位于 [`app/src/main/assets/cctv/channels.json`](app/src/main/assets/cctv/channels.json)。每个频道包含名称、官方页面地址和本地图标路径，修改 JSON 后重新构建即可更新频道列表。

## 品牌资产

应用图标和 Android TV 横幅由 [`tools/generate_brand_assets.py`](tools/generate_brand_assets.py) 统一生成。安装 Pillow 后运行：

```bash
python3 tools/generate_brand_assets.py
```

脚本会同步更新 README 图标、Android launcher 图标和电视桌面横幅，避免多份品牌素材发生偏差。

## 版本记录

- **v1.0**：提供基础 WebView 直播封装和按键换台能力。
- **v1.1**：优化 WebView 加载策略，缩短直播页面启动时间。
- **当前版本**：增加频道列表、本地视频扫描与 Media3 播放，统一遥控器、单击和双击交互。

## 说明

本项目为个人学习与设备自用项目，并非央视官方应用。电视节目页面、名称和相关内容版权归其各自权利人所有；项目不提供或维护第三方直播源。

---

<p align="center">
  如果这个项目对你有帮助，欢迎 Star 支持。
</p>
