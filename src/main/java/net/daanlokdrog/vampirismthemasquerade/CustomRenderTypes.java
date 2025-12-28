package net.daanlokdrog.vampirismthemasquerade; 

import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.renderer.RenderType;
import com.mojang.blaze3d.vertex.DefaultVertexFormat; 
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CustomRenderTypes extends RenderType {
    private CustomRenderTypes(String name, VertexFormat format, Mode mode, int bufferSize, boolean sortOnUpload, boolean multiTexture, Runnable setupTask, Runnable clearTask) {
        super(name, format, mode, bufferSize, sortOnUpload, multiTexture, setupTask, clearTask);
    }
}