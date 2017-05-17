### 模型路径
模型路径在内置SD卡应用包名下`files` 文件夹下，当前Sample工程目录为`/sdcard/Android/data/cn.wittyneko.live2dsample/files`。<br/>
Live2D文件夹为`/sdcard/Android/data/cn.wittyneko.live2dsample/files/live2d`。<br/>
背景文件夹为 `/sdcard/Android/data/cn.wittyneko.live2dsample/files/image`。<br/>
应用自动遍历识别后缀为`model.json`的Live2D模型配置，和背景图片文件。<br/>

### Live2D 
为什么使用2.0版本，2.1版本采用NDK开发修改量大，并且存在平台兼容性问题，同时3.0也即将发布，2.0是目前最快捷的开发方案，针对官方例子出种种的问题，做了一些修改优化
- 添加中文注释
- 音频播放优化支持更多格式
- 资源文件加载修改
- 动作并发执行优化
- 模型加载优化，先加载背景后加载模型
- 多个Live2dView加载优化
- 添加模型和背景切换功能
- 添加模型加载和刷新监听接口
- 添加动作参数手动调节功能
- 添加模型位置调整功能
- 声音嘴形匹配和过滤贴图嘴形

### 嘴形匹配
嘴形匹配需要对嘴形进行重置，动作参数名称可通过`L2DAppStandardID`修改`PARAM_MOUTH_CHARTLET`设置。<br/>
默认会重置动作参数前缀为`PARAM_MOUTH_CHARTLET`的参数为默认值。

### Face++ 
Face++试用期到2017年7月1号，过期可以设置系统时间在未过期前，清空数据即可进入试用。