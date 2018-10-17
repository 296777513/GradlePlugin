package com.knight.test

class DelLogUtil {

    public static final String HEAD = 'Log.'
    public static final String TAIL_END = ');'
    public static final String TAIL_KOTLIN_END = ')'
    public static final String SUFFIX = '.java'
    public static final String KOTLIN = '.kt'
    public static final String CHARSET = "utf-8"

    static void delLog(File rootFile) {
        if (rootFile == null) {
            return
        }

        if (rootFile.isDirectory()) {
            rootFile.eachFile { File file ->
                if (file.isFile()) {
                    if (file.canRead() && (file.name.endsWith(SUFFIX) || file.name.endsWith(KOTLIN))) {
                        println "file: " + file.getAbsolutePath()
                        delFileLog(file, file.name.endsWith(KOTLIN))
                    }
                } else if (file.isDirectory()) {
                    println "dir: " + file.getAbsolutePath()
                    delLog(file)
                }
            }
        }
    }


    private static void delFileLog(File file, boolean isKotlin) {
        def endFlag = 0;
        File ftmp = File.createTempFile(file.getAbsolutePath(), ".tmp")
        def printWriter = ftmp.newPrintWriter(CHARSET)
        def reader = file.newReader(CHARSET)
        def tmpline = null
        String line
        while ((line = reader.readLine()) != null) {
            if (line != null) {
                tmpline = line.trim()
                if (tmpline.startsWith(HEAD) || endFlag == 1) {

                    if (tmpline.endsWith(isKotlin ? TAIL_KOTLIN_END : TAIL_END)) {
                        endFlag = 0
                        printWriter.write("\n")
                    } else {
                        endFlag = 1
                    }
                } else {
                    printWriter.write(line + "\n")
                }
            }
        }

        reader.close()

        printWriter.flush()
        printWriter.close()

        file.delete()
        ftmp.renameTo(file.getAbsolutePath())
    }

}
