package club.maxstats.weave.playerscreenshot

import club.maxstats.weave.playerscreenshot.event.RenderEntityLayersEvent
import club.maxstats.weave.playerscreenshot.event.RenderEntityModelEvent
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.shader.Framebuffer
import net.minecraft.util.ChatComponentText
import net.weavemc.weave.api.ModInitializer
import net.weavemc.weave.api.event.EventBus
import net.weavemc.weave.api.event.KeyboardEvent
import net.weavemc.weave.api.event.SubscribeEvent
import org.lwjgl.BufferUtils
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import java.awt.image.BufferedImage
import java.io.File
import java.lang.instrument.Instrumentation
import java.nio.IntBuffer
import java.text.SimpleDateFormat
import java.util.*
import javax.imageio.ImageIO

class Main: ModInitializer {
    override fun preInit(inst: Instrumentation) {
        EventBus.subscribe(KeyboardEvent::class.java) { event ->
            if (event.keyCode == Keyboard.KEY_P && event.keyState) {
                EventBus.subscribe(Listener())
            }
        }
    }
}

class Listener {
    val mc = Minecraft.getMinecraft()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")

    val frameBuffer = Framebuffer(mc.displayWidth, mc.displayHeight, true)
    var pixelBuffer: IntBuffer = IntBuffer.allocate(0)
    lateinit var pixelValues: IntArray

    @SubscribeEvent
    fun preModel(event: RenderEntityModelEvent.Pre) {
        if (event.entity.uniqueID == mc.thePlayer.uniqueID) {
            frameBuffer.framebufferClear()
            frameBuffer.bindFramebuffer(true)
        }
    }

    @SubscribeEvent
    fun postModel(event: RenderEntityModelEvent.Post) {
        if (event.entity.uniqueID == mc.thePlayer.uniqueID)
            frameBuffer.unbindFramebuffer()
    }

    @SubscribeEvent
    fun preLayers(event: RenderEntityLayersEvent.Pre) {
        if (event.entity.uniqueID == mc.thePlayer.uniqueID)
            frameBuffer.bindFramebuffer(true)
    }

    @SubscribeEvent
    fun postLayers(event: RenderEntityLayersEvent.Post) {
        if (event.entity.uniqueID == mc.thePlayer.uniqueID) {
            frameBuffer.unbindFramebuffer()

            val pixels = frameBuffer.framebufferWidth * frameBuffer.framebufferHeight
            if (pixelBuffer.capacity() < pixels) {
                pixelBuffer = BufferUtils.createIntBuffer(pixels)
                pixelValues = IntArray(pixels)
            }

            GL11.glPixelStorei(3333, 1)
            GL11.glPixelStorei(3317, 1)
            pixelBuffer.clear()

            GlStateManager.bindTexture(frameBuffer.framebufferTexture)
            GL11.glGetTexImage(3553, 0, 32993, 33639, pixelBuffer)

            pixelBuffer.get(pixelValues)
            val buffImage = BufferedImage(frameBuffer.framebufferWidth, frameBuffer.framebufferHeight, BufferedImage.TYPE_INT_ARGB)
            val diff = frameBuffer.framebufferTextureHeight - frameBuffer.framebufferHeight

            for (i in frameBuffer.framebufferTextureHeight - 1 downTo diff) {
                for (j in 0..<frameBuffer.framebufferWidth) {
                    buffImage.setRGB(j, frameBuffer.framebufferTextureHeight - 1 - i, pixelValues[i * frameBuffer.framebufferTextureWidth + j])
                }
            }

            val fileName = "${dateFormat.format(Date())}.png"
            File(
                "${System.getProperty("user.home")}/Desktop/PlayerScreenshots",
                fileName
            ).also {
                it.parentFile.mkdir()
                ImageIO.write(buffImage.trimImage(), "png", it)
                mc.thePlayer.addChatMessage(ChatComponentText("Saved player screenshot: $fileName"))
            }

            EventBus.unsubscribe(this)
        }
    }

    private fun BufferedImage.trimImage(): BufferedImage {
        val width = this.width
        val height = this.height

        var top = 0
        var bottom = height - 1
        var left = 0
        var right = width - 1

        // Find the top edge with non-transparent pixels
        loop@ for (y in 0..<height) {
            for (x in 0..<width) {
                if ((this.getRGB(x, y) shr 24) and 0xFF != 0) {
                    top = y
                    break@loop
                }
            }
        }

        // Find the bottom edge with non-transparent pixels
        loop@ for (y in height - 1 downTo top) {
            for (x in 0..<width) {
                if ((this.getRGB(x, y) shr 24) and 0xFF != 0) {
                    bottom = y
                    break@loop
                }
            }
        }

        // Find the left edge with non-transparent pixels
        loop@ for (x in 0..<width) {
            for (y in top..bottom) {
                if ((this.getRGB(x, y) shr 24) and 0xFF != 0) {
                    left = x
                    break@loop
                }
            }
        }

        // Find the right edge with non-transparent pixels
        loop@ for (x in width - 1 downTo left) {
            for (y in top..bottom) {
                if ((this.getRGB(x, y) shr 24) and 0xFF != 0) {
                    right = x
                    break@loop
                }
            }
        }

        // Create a new BufferedImage with trimmed dimensions
        val trimmedWidth = right - left + 1
        val trimmedHeight = bottom - top + 1
        val trimmedImage = BufferedImage(trimmedWidth, trimmedHeight, BufferedImage.TYPE_INT_ARGB)

        // Copy the non-transparent pixels to the new image
        for (y in top..bottom) {
            for (x in left..right) {
                val rgb = this.getRGB(x, y)
                trimmedImage.setRGB(x - left, y - top, rgb)
            }
        }

        return trimmedImage
    }
}