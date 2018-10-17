# 为什么要学习Gradle

> Gradle目前已经应用于多个Android开发的技术体系中，比如构建系统、插件化、热修复和组件化，如果不了解Gradle，那么对于上述技术体系的了解会大打折扣

上述一段文字是比较传统的解释，我开始系统的学习gradle是因为，项目由于引入了组件化的开发模式，模块之间的通信就需要引入一种新的方式，之前一直使用的是SPI，但是在service比较多的时候会影响到冷起的时长（性能的瓶颈），于是就想到了使用apt和transform来实现。

[Gradle Transform](http://tools.android.com/tech-docs/new-build-system/transform-api)是Android官方提供给开发者在项目构建阶段由class到dex转换期间修改class文件的一套api。
要使用Transorm，首先需要自定义plugin

# 自定义Plugin

## 创建plugin工程

![1.png](https://raw.githubusercontent.com/296777513/Picture/master/gradle_plugin/plugin_1.png)

![2.png](https://raw.githubusercontent.com/296777513/Picture/master/gradle_plugin/plugin_2.png)

![3.png](https://raw.githubusercontent.com/296777513/Picture/master/gradle_plugin/plugin_3.png)

![4.png](https://raw.githubusercontent.com/296777513/Picture/master/gradle_plugin/plugin_4.png)

![5.png](https://raw.githubusercontent.com/296777513/Picture/master/gradle_plugin/plugin_5.png)

1. 首先创建一个Libray（Android Studio不支持穿件Plugin的工程），名字按照Plugin的功能起一个。
2. 删除多余的文件
3. 修改build.gradle文件，支持Plugin的工程。
4. 创建如图4所示的目录，这里有几点需要说明一下
    * `MyPlugin.groovy`不是.java文件，这里也直接创建java文件，然后修改后缀名，groovy是支持Java语言的（所以说Groovy的学习成本不是很大，感兴趣可以在网上搜索一下）
    * `resources`目录下的`META-INF.gradle-plugins`的顺序不能错。
    * `com.knight.plugin.test`(ps:图片中的字母写错了，项目中已改)就是你的plugin名称
    
到目前为止，就完成了一个Plugin的工程，接下来使用自定义的plugin实现一个小功能，删除项目中所有的Log日志（这个只是测试，真正项目开发中是不会用到）

# 发布自定义的plugin到本地仓库


![6.png](https://raw.githubusercontent.com/296777513/Picture/master/gradle_plugin/plugin_6.png)

![7.png](https://raw.githubusercontent.com/296777513/Picture/master/gradle_plugin/plugin_7.png)

![8.png](https://raw.githubusercontent.com/296777513/Picture/master/gradle_plugin/plugin_8.png)

![9.png](https://raw.githubusercontent.com/296777513/Picture/master/gradle_plugin/plugin_9.png)

![10.png](https://raw.githubusercontent.com/296777513/Picture/master/gradle_plugin/plugin_10.png)

![11.png](https://raw.githubusercontent.com/296777513/Picture/master/gradle_plugin/plugin_11.png)

1. 首先配置`myplugin`的`module`下的`build.gradle`，支持upload
2. 然后点击task中的`uploadArchives`或者在命令行中输入`./gradlew :myplugin:uploadArchives`来执行task
3. 编译成功后会在根目录中出现一个`repo`目录，如图8所示
4. 然后配置project的`build.gradle`，如图9所示，配置app的module中的`build.gradle`，如图10所示
5. 在命令行中输入`./gradlew assembleDubug`就能看到图11的结果，说明我们自定义的plugin，成功用到项目中了。


# 自定义删除Log语句的plugin

这个功能只是为了小试牛刀，并没有什么卵用，大家可以看看。

![11.png](https://raw.githubusercontent.com/296777513/Picture/master/gradle_plugin/plugin_12.png)

![13.png](https://raw.githubusercontent.com/296777513/Picture/master/gradle_plugin/plugin_13.png)

![14.png](https://raw.githubusercontent.com/296777513/Picture/master/gradle_plugin/plugin_14.png)

![15.png](https://raw.githubusercontent.com/296777513/Picture/master/gradle_plugin/plugin_15.png)

1. 这是源文件，是有一大堆log的代码
2. 执行`./gradlew assembleDebug`，可以看日志，已经执行了我们的plugin
3. 再看源文件，log日志已经没有了
4. 多加了几个类，这几个类就不详细讲了，如果感兴趣的同学可以下载源码看看。
