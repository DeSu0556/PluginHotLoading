package ary.code.anticheat.hotloading

/**
Author: DeSu
Date: 2022/3/15 9:43
 **/
class AryClassLoader : ClassLoader() {
    fun addClass(name: String, b: ByteArray, off: Int, int: Int) {
        defineClass(name, b, off, int)
    }
}