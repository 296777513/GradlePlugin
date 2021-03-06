package com.knight.test.transform

import javassist.ClassPool
import javassist.CtClass
import javassist.CtMethod
import org.gradle.api.Project

class MyInjects {
    //初始化类池
    private final static ClassPool pool = ClassPool.getDefault()

    public static void inject(String path, Project project) {
        //将当前路径加入类池，不然找不到这个类
        pool.appendClassPath(path)
        //project.android.bootClasspath 加入android.jar，不然找不到android相关的所有类
        pool.appendClassPath(project.android.bootClasspath[0].toString())
        //引入android.os.Bundle包，因为onCreate方法参数有Bundle
        pool.importPackage("android.os.Bundle")
        //引入android.support.v7.app.AppCompatActivity包
        pool.importPackage("android.support.v7.app.AppCompatActivity")

        File dir = new File(path)
        if (dir.isDirectory()) {
            //遍历文件夹
            dir.eachFileRecurse { File file ->
                String filePath = file.absolutePath
                println("=====Inject filePath: $filePath")
                if (file.getName().endsWith("Activity.class")) {
                    def className = "com.knight.gradleplugin.${file.getName().substring(0, file.getName().length() - 6)}"
                    //获取*Activity.class
                    CtClass ctClass = pool.getCtClass(className)
                    println("======ctClass=$ctClass")
                    //解冻
                    if (ctClass.isFrozen()) {
                        ctClass.defrost()
                    }

                    //获取到OnCreate方法
                    CtMethod ctMethod = ctClass.getDeclaredMethod("onCreate")

                    println("======方法名=$ctMethod")

                    String insetAfterStr = "android.widget.Toast.makeText(this,\"我是被插入的Toast代码~!!\",android.widget.Toast.LENGTH_SHORT).show();"

                    //在方法结尾插入代码
                    ctMethod.insertAfter(insetAfterStr)
                    ctClass.writeFile(path)
                    ctClass.detach()//释放
                }
            }
        }
    }
}