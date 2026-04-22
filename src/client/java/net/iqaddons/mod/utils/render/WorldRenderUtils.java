package net.iqaddons.mod.utils.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;
import lombok.experimental.UtilityClass;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import net.minecraft.util.shape.VoxelShapes;
import java.util.OptionalDouble;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderSetup;
import net.minecraft.client.render.LayeringTransform;
@UtilityClass
public class WorldRenderUtils {

    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static void drawFilled(
            @NotNull MatrixStack matrices, VertexConsumerProvider.Immediate consumer,
            @NotNull Camera camera, @NotNull Box box, boolean throughWalls, @NotNull RenderColor color
    ) {
        matrices.push();

        Vec3d camPos = camera.getCameraPos().negate();
        matrices.translate(camPos.x, camPos.y, camPos.z);

        VertexConsumer buffer = throughWalls ? consumer.getBuffer(Layers.BoxFilledNoCull) : consumer.getBuffer(Layers.BoxFilled);
                int argb = ((int)(color.a * 255.0F) << 24) |
                ((int)(color.r * 255.0F) << 16) |
                ((int)(color.g * 255.0F) << 8)  |
                ((int)(color.b * 255.0F));

                RenderUtils.drawFilledBox(
                matrices, buffer,
                box.minX, box.minY, box.minZ,
                box.maxX, box.maxY, box.maxZ,
                argb
        );
        matrices.pop();
    }

    public static void drawOutline(
            @NotNull MatrixStack matrices, VertexConsumerProvider.Immediate consumer,
            @NotNull Camera camera, Box box, boolean throughWalls, @NotNull RenderColor color
    ) {
        matrices.push();
        Vec3d camPos = camera.getCameraPos().negate();
        matrices.translate(camPos.x, camPos.y, camPos.z);

        VertexConsumer buffer = throughWalls
                ? consumer.getBuffer(Layers.BoxOutlineNoCull)
                : consumer.getBuffer(Layers.BoxOutline);

        int argb = ((int)(color.a * 255.0F) << 24) |
                ((int)(color.r * 255.0F) << 16) |
                ((int)(color.g * 255.0F) << 8)  |
                ((int)(color.b * 255.0F));

        net.minecraft.util.shape.VoxelShape shape = VoxelShapes.cuboid(
                box.minX, box.minY, box.minZ,
                box.maxX, box.maxY, box.maxZ
        );

        VertexRendering.drawOutline(
                matrices,
                buffer,
                shape,
                0.0, 0.0, 0.0,
                argb,
                1.0F
        );
        matrices.pop();
    }

    public static void drawFilledCircle(
            @NotNull MatrixStack matrices,
            VertexConsumerProvider.Immediate consumer,
            @NotNull Camera camera,
            @NotNull Vec3d center,
            float radius,
            int segments,
            boolean throughWalls,
            @NotNull RenderColor color
    ) {
        if (segments < 3) segments = 3;

        matrices.push();

        Vec3d camPos = camera.getCameraPos();
        matrices.translate(-camPos.x, -camPos.y, -camPos.z);

        MatrixStack.Entry entry = matrices.peek();

        VertexConsumer buffer = consumer.getBuffer(
                throughWalls ? Layers.CircleFilledNoCull : Layers.CircleFilled
        );

        for (int i = 0; i < segments; i++) {
            double angle1 = (Math.PI * 2.0) * i / segments;
            double angle2 = (Math.PI * 2.0) * (i + 1) / segments;

            float x1 = (float) (center.x + Math.cos(angle1) * radius);
            float z1 = (float) (center.z + Math.sin(angle1) * radius);

            float x2 = (float) (center.x + Math.cos(angle2) * radius);
            float z2 = (float) (center.z + Math.sin(angle2) * radius);

            buffer.vertex(entry, (float) center.x, (float) center.y, (float) center.z)
                    .color(color.r, color.g, color.b, color.a);
            buffer.vertex(entry, x1, (float) center.y, z1)
                    .color(color.r, color.g, color.b, color.a);
            buffer.vertex(entry, x2, (float) center.y, z2)
                    .color(color.r, color.g, color.b, color.a);
        }

        matrices.pop();
    }

    public static void drawCircleOutline(
            @NotNull MatrixStack matrices,
            VertexConsumerProvider.Immediate consumer,
            @NotNull Camera camera,
            @NotNull Vec3d center,
            float radius,
            int segments,
            boolean throughWalls,
            @NotNull RenderColor color
    ) {
        if (segments < 3) segments = 3;

        matrices.push();

        Vec3d camPos = camera.getCameraPos();
        matrices.translate(-camPos.x, -camPos.y, -camPos.z);

        MatrixStack.Entry entry = matrices.peek();

        VertexConsumer buffer = consumer.getBuffer(
                throughWalls ? Layers.CircleOutlineNoCull : Layers.CircleOutline
        );

        for (int i = 0; i <= segments; i++) {
            double angle = (Math.PI * 2.0) * i / segments;
            float x = (float) (center.x + Math.cos(angle) * radius);
            float y = (float) center.y;
            float z = (float) (center.z + Math.sin(angle) * radius);

            buffer.vertex(entry, x, y, z)
                    .color(color.r, color.g, color.b, color.a);
        }

        matrices.pop();
    }

    public static void drawText(
            @NotNull MatrixStack matrices, VertexConsumerProvider.Immediate consumer,
            @NotNull Camera camera, @NotNull Vec3d pos, Text text,
            float scale, boolean throughWalls, @NotNull RenderColor color
    ) {
        matrices.push();
        Vec3d camPos = camera.getCameraPos();

        matrices.translate(
                pos.x - camPos.x,
                pos.y - camPos.y,
                pos.z - camPos.z
        );

        matrices.multiply(camera.getRotation());
        matrices.scale(scale, -scale, scale);

        mc.textRenderer.draw(
                text,
                -mc.textRenderer.getWidth(text) / 2f,
                0,
                color.argb,
                true,
                matrices.peek().getPositionMatrix(),
                consumer,
                throughWalls
                        ? TextRenderer.TextLayerType.SEE_THROUGH
                        : TextRenderer.TextLayerType.NORMAL,
                0,
                LightmapTextureManager.MAX_LIGHT_COORDINATE
        );

        consumer.draw();
        matrices.pop();
    }

    public static void drawBeam(
            MatrixStack matrices, VertexConsumerProvider.Immediate consumer,
            Camera camera, Vec3d pos, int height,
            boolean throughWalls, RenderColor color
    ) {
        drawFilled(
                matrices, consumer, camera,
                Box.of(pos, 0.5, 0, 0.5).stretch(0, height, 0),
                throughWalls, color
        );
    }

    public static void drawStyledBox(
            @NotNull MatrixStack matrices, VertexConsumerProvider.Immediate consumer,
            @NotNull Camera camera, @NotNull Box box, boolean throughWalls,
            @NotNull RenderColor color, @NotNull RenderStyle style
    ) {
        switch (style) {
            case SOLID -> drawFilled(matrices, consumer, camera, box, throughWalls, color);
            case OUTLINE -> drawOutline(matrices, consumer, camera, box, throughWalls, color);
            case BOTH -> {
                drawFilled(matrices, consumer, camera, box, throughWalls, color.withOpacity(color.a * 0.5f));
                drawOutline(matrices, consumer, camera, box, throughWalls, color);
            }
        }
    }

    public static void drawStyledHitBox(
            @NotNull MatrixStack matrices, VertexConsumerProvider.Immediate consumer,
            @NotNull Camera camera, @NotNull Entity entity, @NotNull RenderTickCounter tickCounter,
            boolean throughWalls, @NotNull RenderColor color, RenderStyle style
    ) {
        float tickDelta = tickCounter.getTickProgress(true);
        Box box = getBox(entity, tickDelta);

        drawStyledBox(matrices, consumer, camera, box, throughWalls, color, style);
    }

    public static void drawTracer(
            @NotNull MatrixStack matrices, VertexConsumerProvider.@NotNull Immediate consumer,
            @NotNull Camera camera, @NotNull Vec3d pos, @NotNull RenderColor color
    ) {
        Vec3d camPos = camera.getCameraPos();

        matrices.push();
        matrices.translate(-camPos.getX(), -camPos.getY(), -camPos.getZ());

        MatrixStack.Entry entry = matrices.peek();
        VertexConsumer buffer = consumer.getBuffer(Layers.GuiLine);
        Vec3d point = camPos.add(Vec3d.fromPolar(camera.getPitch(), camera.getYaw()));

        Vector3f normal = pos.toVector3f()
                .sub((float) point.getX(), (float) point.getY(), (float) point.getZ())
                .normalize(new Vector3f(1.0f, 1.0f, 1.0f));

        buffer.vertex(entry, (float) point.getX(), (float) point.getY(), (float) point.getZ()).color(color.r, color.g, color.b, color.a).normal(entry, normal);
        buffer.vertex(entry, (float) pos.getX(), (float) pos.getY(), (float) pos.getZ()).color(color.r, color.g, color.b, color.a).normal(entry, normal);
        matrices.pop();
    }

    public static void drawHitBox(
            @NotNull MatrixStack matrices, VertexConsumerProvider.Immediate consumer,
            @NotNull Camera camera, Entity entity, @NotNull RenderTickCounter tickCounter,
            boolean troughWalls, RenderColor color
    ) {
        float tickDelta = tickCounter.getTickProgress(true);
        drawOutline(matrices, consumer, camera, getBox(entity, tickDelta), troughWalls, color);
    }

    @Contract("_, _ -> new")
    private static @NotNull Box getBox(@NotNull Entity entity, float tickDelta) {
        double x = entity.lastX + (entity.getX() - entity.lastX) * tickDelta;
        double y = entity.lastY + (entity.getY() - entity.lastY) * tickDelta;
        double z = entity.lastZ + (entity.getZ() - entity.lastZ) * tickDelta;

        float width = entity.getWidth();
        float height = entity.getHeight();
        float halfWidth = width / 2.0f;

        return new Box(
                x - halfWidth,
                y,
                z - halfWidth,
                x + halfWidth,
                y + height,
                z + halfWidth
        );
    }


    public enum RenderStyle {
        SOLID, OUTLINE, BOTH;
    }

    public static class Pipelines {

        public static final RenderPipeline.Snippet filledSnippet = RenderPipelines.POSITION_COLOR_SNIPPET;
        public static final RenderPipeline.Snippet outlineSnippet = RenderPipelines.RENDERTYPE_LINES_SNIPPET;

        public static final RenderPipeline filledNoCull = RenderPipelines.register(RenderPipeline.builder(filledSnippet)
                .withLocation(Identifier.of("iqaddons", "pipeline/iqaddons_filled_no_cull"))
                .withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.TRIANGLE_STRIP)
                .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                .build());
        
        public static final RenderPipeline filledCull = RenderPipelines.register(RenderPipeline.builder(filledSnippet)
                .withLocation(Identifier.of("iqaddons", "pipeline/iqaddons_filled_cull"))
                .withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.TRIANGLE_STRIP)
                .build());
        
        public static final RenderPipeline outlineNoCull = RenderPipelines.register(RenderPipeline.builder(outlineSnippet)
                .withLocation(Identifier.of("iqaddons", "pipeline/iqaddons_outline_no_cull"))
                .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                .build());
        
        public static final RenderPipeline outlineCull = RenderPipelines.register(RenderPipeline.builder(outlineSnippet)
                .withLocation(Identifier.of("iqaddons", "pipeline/iqaddons_outline_cull"))
                .build());

        public static final RenderPipeline lineNoCull = RenderPipelines.register(RenderPipeline.builder(outlineSnippet)
                .withLocation(Identifier.of("iqaddons", "pipeline/iqaddons_line_no_cull"))
                .withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.DEBUG_LINE_STRIP)
                .withVertexShader("core/position_color")
                .withFragmentShader("core/position_color")
                .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                .build());

        public static final RenderPipeline circleFilledNoCull = RenderPipelines.register(
                RenderPipeline.builder(filledSnippet)
                        .withLocation(Identifier.of("iqaddons", "pipeline/iqaddons_circle_filled_no_cull"))
                        .withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.TRIANGLES)
                        .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                        .build()
        );

        public static final RenderPipeline circleFilledCull = RenderPipelines.register(
                RenderPipeline.builder(filledSnippet)
                        .withLocation(Identifier.of("iqaddons", "pipeline/iqaddons_circle_filled_cull"))
                        .withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.TRIANGLES)
                        .build()
        );

        public static final RenderPipeline circleOutlineNoCull = RenderPipelines.register(
                RenderPipeline.builder(outlineSnippet)
                        .withLocation(Identifier.of("iqaddons", "pipeline/iqaddons_circle_outline_no_cull"))
                        .withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.DEBUG_LINE_STRIP)
                        .withVertexShader("core/position_color")
                        .withFragmentShader("core/position_color")
                        .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                        .build()
        );

        public static final RenderPipeline circleOutlineCull = RenderPipelines.register(
                RenderPipeline.builder(outlineSnippet)
                        .withLocation(Identifier.of("iqaddons", "pipeline/iqaddons_circle_outline_cull"))
                        .withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.DEBUG_LINE_STRIP)
                        .withVertexShader("core/position_color")
                        .withFragmentShader("core/position_color")
                        .build()
        );
    }

    public class MyCustomRenderLayers {

        // Gotowa warstwa dla WYPEŁNIONYCH kształtów (Filled)
        public static final RenderLayer FILLED = RenderLayer.of(
                "iqaddons_filled",
                RenderSetup.builder(RenderPipelines.POSITION_COLOR_SUNRISE_SUNSET) // Podajemy potok dla brył z kolorem
                        .layeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING) // Nowy odpowiednik RenderPhase
                        .build()
        );

        // Gotowa warstwa dla LINII (Lines)
        public static final RenderLayer LINES = RenderLayer.of(
                "iqaddons_lines",
                RenderSetup.builder(RenderPipelines.LINES) // Podajemy dedykowany potok dla linii
                        .layeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                        // Nie ma już .lineWidth() - zarządza tym sam potok
                        .build()
        );
    }
    public static class Layers {
        public static final RenderLayer BoxFilled = RenderLayer.of(
                "iqaddons_box_filled", // 1. argument: nazwa (bez zmian)

                // 2. argument: Wszystko inne ląduje w nowym RenderSetup
                RenderSetup.builder(Pipelines.filledCull)
                        .expectedBufferSize(1536)
                        .translucent()
                        .layeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)

                        .build()
        );

        public static final RenderLayer BoxFilledNoCull = RenderLayer.of(
                "iqaddons_box_filled_no_cull",
                RenderSetup.builder(Pipelines.filledNoCull)
                        .expectedBufferSize(1536)
                        .translucent()
                        .layeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                        .build()
        );

        public static final RenderLayer BoxOutline = RenderLayer.of(
                "iqaddons_box_outline",
                RenderSetup.builder(Pipelines.outlineCull)
                        .expectedBufferSize(1536)
                        .layeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                        .build()
        );

        public static final RenderLayer BoxOutlineNoCull = RenderLayer.of(
                "iqaddons_box_outline_no_cull",
                RenderSetup.builder(Pipelines.outlineNoCull)
                        .expectedBufferSize(1536)
                        .layeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                        .build()
        );

        public static final RenderLayer GuiLine = RenderLayer.of(
                "iqaddons_gui_line",
                RenderSetup.builder(Pipelines.lineNoCull)
                        .expectedBufferSize(1536)
                        .layeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                        .build()
        );

        public static final RenderLayer CircleFilled = RenderLayer.of(
                "iqaddons_circle_filled",
                RenderSetup.builder(Pipelines.circleFilledCull)
                        .expectedBufferSize(1536)
                        .translucent()
                        .layeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                        .build()
        );

        public static final RenderLayer CircleFilledNoCull = RenderLayer.of(
                "iqaddons_circle_filled_no_cull",
                RenderSetup.builder(Pipelines.circleFilledNoCull)
                        .expectedBufferSize(1536)
                        .translucent()
                        .layeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                        .build()
        );

        public static final RenderLayer CircleOutline = RenderLayer.of(
                "iqaddons_circle_outline",
                RenderSetup.builder(Pipelines.circleOutlineCull)
                        .expectedBufferSize(1536)
                        .layeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                        .build()
        );

        public static final RenderLayer CircleOutlineNoCull = RenderLayer.of(
                "iqaddons_circle_outline_no_cull",
                RenderSetup.builder(Pipelines.circleOutlineNoCull)
                        .expectedBufferSize(1536)
                        .layeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                        .build()
        );
    }
}
