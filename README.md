### 模型路径
模型路径在内置SD卡应用包名下`files` 文件夹下，当前Sample工程目录为`/sdcard/Android/data/cn.wittyneko.live2dsample/files`。
Live2D文件夹为`/sdcard/Android/data/cn.wittyneko.live2dsample/files/live2d`。
背景文件夹为 `/sdcard/Android/data/cn.wittyneko.live2dsample/files/image`。
应用自动遍历识别后者为`model.json`的Live2D模型配置，和背景图片文件。

### Live2D 
为什么使用2.0版本，2.1版本采用NDK开发修改量大，并且存在平台兼容性问题，同时3.0也即将发布，2.0是目前最快捷的开发方案，针对官方例子出那种的问题，做了一些优化
- 添加中文注释
- 音频播放优化支持更多格式
- 资源文件加载修改
- 动作并发执行优化
- 模型加载优化，先加载背景后加载模型
- 添加模型和背景切换功能
- 添加模型加载和刷新监听接口
- 添加动作参数手动调节功能

### Face++ 
Face++试用期到2017年7月1号，过期可以设置系统时间在未过期前，清空数据即可进入试用。