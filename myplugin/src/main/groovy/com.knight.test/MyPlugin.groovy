package com.knight.test

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.api.ApplicationVariant
import com.knight.test.transform.MyClassTransform
import org.gradle.api.Plugin
import org.gradle.api.Project


class MyPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        System.println("=====this is my plugin=====")

        // AppExtension就是build.gradle中android{...}这一块
        def android = project.extensions.getByType(AppExtension)

        // 注册一个Transform
        def classTransform = new MyClassTransform(project)
        android.registerTransform(classTransform)

        project.extensions.create('knightConfig', KnightConfig)

        project.afterEvaluate {
            //在gradle 构建完之后执行
            project.logger.error("knightConfig : " + project.knightConfig.sourceDir)

            def rootDir = project.projectDir.toString().plus(project.knightConfig.sourceDir)

            project.logger.error(rootDir)

            DelLogUtil.delLog(new File(rootDir))
        }

        // 必须是'application'的项目，不能为'library'
        if (project.plugins.hasPlugin(AppPlugin)) {
            // 获取到Extension，Extension就是build.gradle中的{}闭包
            android.applicationVariants.all { variant ->
                // 获取到scope，作用域
                def variantData = variant.variantData
                def scope = variantData.scope

                //拿到build.gradle中创建的Extension的值
                def config = project.extensions.getByName("knightConfig")

                //创建一个task
                def createTaskName = scope.getTaskName("knightTest", "MyPlugin")
                def createTask = project.task(createTaskName)

                //设置task要执行的任务
                createTask.doLast {
                    //生成java类
                    createJavaTest(variant, config)
                }
                // 设置task依赖于生成BuildConfig的Task，然后在生成BuildConfig后生成我们的类
                String generateBuildConfigTaskName = variant.getVariantData().getScope().getTaskContainer().generateBuildConfigTask.getName()
                System.println("===generateBuildConfigTaskName: $generateBuildConfigTaskName")
                def generateBuildConfigTask = project.tasks.getByName(generateBuildConfigTaskName)
                if (generateBuildConfigTask) {
                    createTask.dependsOn generateBuildConfigTask
                    generateBuildConfigTask.finalizedBy createTask
                }
            }
        }
        System.println("=========is finished?========")
    }


    static def void createJavaTest(variant, config) {
        //要生成的内容
        def content = """
package com.knight.gradleplugin;
 /**
  * created by knight on 2018/10/17
  */
 public class MyPluginTestClass {
    public static final String str = "${config.str}";
 }
"""
        //获取到BuildConfig类的路径

        File outputDir = variant.getVariantData().getScope().getBuildConfigSourceOutputDir()
        System.println("======outputDir: $outputDir")

        def javaFile = new File(outputDir, "com/knight/gradleplugin/MyPluginTestClass.java")
//        if (!javaFile.exists()) {
//            javaFile.mkdirs()
//        }
        javaFile.write(content, 'UTF-8')
    }
}
