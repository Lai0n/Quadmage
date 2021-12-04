package dev.sharpwave.quadmage.gui.core.misc

import java.awt.datatransfer.DataFlavor
import java.awt.dnd.DnDConstants
import java.awt.dnd.DropTarget
import java.awt.dnd.DropTargetDropEvent

open class FileDropTarget(private val onDrop: (droppedFiles: List<*>) -> Unit) : DropTarget() {
    @Synchronized
    override fun drop(event: DropTargetDropEvent) {
        try {
            event.acceptDrop(DnDConstants.ACTION_REFERENCE)

            if (event.transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                val droppedFiles = event
                    .transferable.getTransferData(
                        DataFlavor.javaFileListFlavor
                    ) as List<*>
                onDrop.invoke(droppedFiles)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}