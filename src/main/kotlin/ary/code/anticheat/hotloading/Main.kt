package ary.code.anticheat.hotloading

import dove.dmdxcz.launcher.antidump.CookieFuckery
import org.apache.logging.log4j.LogManager
import org.bukkit.plugin.java.JavaPlugin
import java.io.ByteArrayInputStream
import java.io.DataInputStream
import java.net.Socket
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

/**
Author: DeSu
Date: 2022/3/15 8:46
 **/

val log = LogManager.getLogger("AryAntiCheat")

class Main : JavaPlugin() {
    override fun onLoad() {
        super.onLoad()

        log.info("Loading...")

        CookieFuckery.checkLaunchFlags()
        CookieFuckery.disableJavaAgents()
        CookieFuckery.setPackageNameFilter()
        CookieFuckery.dissasembleStructs()

        val classLoader = AryClassLoader()

        val socket = Socket("127.0.0.1", 19199)
        val input = DataInputStream(socket.getInputStream())

        val bytes = input.readBytes()

        ZipInputStream(ByteArrayInputStream(bytes)).use { zipStream ->
            var zipEntry: ZipEntry?
            while (zipStream.nextEntry.also { zipEntry = it } != null) {
                var name = zipEntry!!.name
                if (name.endsWith(".class")) {
                    name = name.removeSuffix(".class")
                    name.replace('/', '.')

                    val b = zipStream.readBytes()
                    classLoader.addClass(name, b, 0, b.size)
                }
            }
        }

        classLoader.loadClass("ary.code.anticheat.Main").apply {
            val m1 = getDeclaredMethod("onLoad")
            val m2 = getDeclaredMethod("onEnable")
            m1.invoke(this)
            m2.invoke(this)
        }

        log.info("Loaded successfully.")
    }
}