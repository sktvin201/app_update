
## 安装和上传

在Android Studio Terminal窗口中运行命令

```

./gradlew install

./gradlew bintrayUpload

```

## 功能描述

1. 普通更新
2. 强制更新

## 版本描述

#### 1.0.0

support方式

#### 1.1.0

更换为Androidx方式

## 默认下载目录（暂时不支持更改下载路径）

应用自身的缓存目录

```
getCacheDir()方法用于获取/data/data/<application package>/cache目录
```