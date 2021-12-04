package dev.sharpwave.quadmage.core

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.res.painterResource
import dev.sharpwave.quadmage.util.concatToPath
import java.io.File
import javax.imageio.ImageIO

class ResourceLocation(val name: String, val type: Type = Type.icon) {
    enum class Type {
        icon
    }

    fun getPath() = concatToPath(type.name, name)

    fun getFile(): File {
        val path = getPath()
        val resource = this.javaClass.classLoader.getResource(path)
            ?: throw IllegalArgumentException("Resource %s does not exist!".format(path))

        return File(resource.toURI())
    }

    fun asComposeBitmap() = ImageIO.read(getFile()).toComposeImageBitmap()

    @Composable
    fun asPainterResource() = painterResource(getPath())

    companion object {
        fun icon(name: String) = ResourceLocation(name, Type.icon)
    }
}