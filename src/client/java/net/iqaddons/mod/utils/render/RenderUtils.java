package net.iqaddons.mod.utils.render;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumer;

public class RenderUtils {

    public static void drawFilledBox(MatrixStack matrices, VertexConsumer vertexConsumer,
                                     double minX, double minY, double minZ,
                                     double maxX, double maxY, double maxZ,
                                     int color) {

        MatrixStack.Entry entry = matrices.peek();

        vertexConsumer.vertex(entry, (float)minX, (float)minY, (float)minZ).color(color);
        vertexConsumer.vertex(entry, (float)maxX, (float)minY, (float)minZ).color(color);
        vertexConsumer.vertex(entry, (float)maxX, (float)minY, (float)maxZ).color(color);
        vertexConsumer.vertex(entry, (float)minX, (float)minY, (float)maxZ).color(color);

        vertexConsumer.vertex(entry, (float)minX, (float)maxY, (float)minZ).color(color);
        vertexConsumer.vertex(entry, (float)minX, (float)maxY, (float)maxZ).color(color);
        vertexConsumer.vertex(entry, (float)maxX, (float)maxY, (float)maxZ).color(color);
        vertexConsumer.vertex(entry, (float)maxX, (float)maxY, (float)minZ).color(color);


        vertexConsumer.vertex(entry, (float)minX, (float)minY, (float)minZ).color(color);
        vertexConsumer.vertex(entry, (float)minX, (float)maxY, (float)minZ).color(color);
        vertexConsumer.vertex(entry, (float)maxX, (float)maxY, (float)minZ).color(color);
        vertexConsumer.vertex(entry, (float)maxX, (float)minY, (float)minZ).color(color);

        // Ściana południowa (+Z)
        vertexConsumer.vertex(entry, (float)minX, (float)minY, (float)maxZ).color(color);
        vertexConsumer.vertex(entry, (float)maxX, (float)minY, (float)maxZ).color(color);
        vertexConsumer.vertex(entry, (float)maxX, (float)maxY, (float)maxZ).color(color);
        vertexConsumer.vertex(entry, (float)minX, (float)maxY, (float)maxZ).color(color);

        // Ściana zachodnia (-X)
        vertexConsumer.vertex(entry, (float)minX, (float)minY, (float)minZ).color(color);
        vertexConsumer.vertex(entry, (float)minX, (float)minY, (float)maxZ).color(color);
        vertexConsumer.vertex(entry, (float)minX, (float)maxY, (float)maxZ).color(color);
        vertexConsumer.vertex(entry, (float)minX, (float)maxY, (float)minZ).color(color);

        // Ściana wschodnia (+X)
        vertexConsumer.vertex(entry, (float)maxX, (float)minY, (float)minZ).color(color);
        vertexConsumer.vertex(entry, (float)maxX, (float)maxY, (float)minZ).color(color);
        vertexConsumer.vertex(entry, (float)maxX, (float)maxY, (float)maxZ).color(color);
        vertexConsumer.vertex(entry, (float)maxX, (float)minY, (float)maxZ).color(color);
    }
}