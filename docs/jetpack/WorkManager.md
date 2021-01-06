[toc]

# WorkManager
[https://sguotao.top/Jetpack-2019-12-07-%E6%9E%B6%E6%9E%84%E7%BB%84%E4%BB%B6-WorkManger.html](https://sguotao.top/Jetpack-2019-12-07-%E6%9E%B6%E6%9E%84%E7%BB%84%E4%BB%B6-WorkManger.html)

WorkManager 为后台任务提供了一套统一的解决方案，它可以根据 Android 电量优化以及设备的 API 等级，选择合适
的方式执行。WorkManager 向后兼容到 API 14，并且对无论集成 Google Play Service 服务与否的设备都予以支持。

使用 WorkManager 管理任务，允许任务延迟，并且保证任务能够执行到，即使应用关闭或设备重启。
WorkManager 不适用于需要在特定时间触发的任务，也不适用立即任务。

- 针对特定时间触发的任务使用 AlarmManager，
- 立即执行的任务使用 ForegroundService。




