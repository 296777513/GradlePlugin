package com.knight.test.transform

import com.android.build.api.transform.Context
import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformOutputProvider
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import org.apache.commons.codec.digest.DigestUtils
import org.gradle.api.Project


class MyClassTransform extends Transform {
    private Project mProject

    public MyClassTransform(Project p) {
        mProject = p
    }

    /**
     * transform名称
     * transformClassesWithMyClassTransformForDebug 运行时的名字
     * transformClassesWith + getName() + For + Debug或Release
     * @return
     */
    @Override
    String getName() {
        return "MyClassTransform"
    }

    /**
     * 需要处理的数据类型，有两种枚举类型
     * CLASSES和RESOURCES，CLASSES代表处理的java的class文件，RESOURCES代表要处理的java的资源
     * @return
     */
    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    /**
     * Transform要操作内容的范围，Scope有5种类型,废弃了PROJECT_LOCAL_DEPS和SUB_PROJECTS_LOCAL_DEPS,这两种使用EXTERNAL_LIBRARIES
     * PROJECT 只有项目的内容
     * SUB_PROJECTS 只有子项目
     * EXTERNAL_LIBRARIES 只有外部库
     * TESTED_CODE 由当前变量（包括依赖项）测试的代码
     * PROVIDED_ONLY 只提供本地或者远程依赖项
     *
     * @return
     */
    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }


    @Override
    void transform(Context context,
                   Collection<TransformInput> inputs,
                   Collection<TransformInput> referencedInputs,
                   TransformOutputProvider outputProvider,
                   boolean isIncremental) throws IOException, TransformException, InterruptedException {
        System.println("=====now,enter the transform====")

        //遍历input
        inputs.each { TransformInput input ->
            //遍历文件夹
            input.directoryInputs.each { DirectoryInput directoryInput ->
                //注入代码
                MyInjects.inject(directoryInput.file.absolutePath, mProject)

                //获取output目录
                def dest = outputProvider.getContentLocation(directoryInput.name,
                        directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)//这里写代码片

                //将input目录复制到output指定目录
                FileUtils.copyDirectory(directoryInput.file, dest)
            }

            //遍历jar文件 对jar不操作，但是要输出到out路径
            input.jarInputs.each { JarInput jarInput ->
                // 重命名输出文件（同目录copyFile会冲突）
                def jarName = jarInput.name
                println("=====jar = " + jarInput.file.getAbsolutePath())
                def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length() - 4)
                }
                def dest = outputProvider.getContentLocation(jarName + md5Name, jarInput.contentTypes, jarInput.scopes, Format.JAR)
                FileUtils.copyFile(jarInput.file, dest)
            }

        }
    }

}