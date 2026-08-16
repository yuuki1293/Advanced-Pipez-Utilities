package jp.yuuki.advancedpipezutilities.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.maxhenkel.pipez.blocks.PipeBlock;
import jp.yuuki.advancedpipezutilities.AdvancedPipezUtilities;
import jp.yuuki.advancedpipezutilities.item.ModItems;
import jp.yuuki.advancedpipezutilities.pipe.PipeSideSelector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public final class PipeWrenchOverlayRenderer {

    private static final int ADVANCED_WRENCH_TINT = 0xFF4AB9FF;

    private static final double EDGE = 0.25D;
    private static final double FACE_OFFSET = 0.01D;
    private static final double ICON_MARGIN = 0.2D / 16D;
    private static final double ICON_SIZE = 4D / 16D;
    private static final double ICON_CENTER = 6D / 16D;
    private static final double ICON_HIGH = 12D / 16D;

    private static final ResourceLocation PIPE_BLOCK = new ResourceLocation(
            AdvancedPipezUtilities.MOD_ID, "textures/gui/overlay/tool_pipe_block.png");
    private static final ResourceLocation PIPE_CONNECT = new ResourceLocation(
            AdvancedPipezUtilities.MOD_ID, "textures/gui/overlay/tool_pipe_connect.png");

    private PipeWrenchOverlayRenderer() {
    }

    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register(
                (stack, tintIndex) -> tintIndex == 0 ? ADVANCED_WRENCH_TINT : 0xFFFFFFFF,
                ModItems.ADVANCED_PIPE_WRENCH.get()
        );
    }

    @SubscribeEvent
    public static void onBlockHighlight(RenderHighlightEvent.Block event) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        Level level = minecraft.level;
        if (player == null || level == null || !isHoldingAdvancedWrench(player)) {
            return;
        }

        BlockHitResult hit = event.getTarget();
        BlockPos pos = hit.getBlockPos();
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof PipeBlock pipe)) {
            return;
        }

        Direction front = hit.getDirection();
        Direction selected = PipeSideSelector.select(hit);
        Direction back = front.getOpposite();
        Direction left = leftOf(front);
        Direction right = rightOf(front);
        Direction top = topOf(front);
        Direction bottom = bottomOf(front);
        Vec3 camera = event.getCamera().getPosition();
        PoseStack.Pose pose = event.getPoseStack().last();

        float pulse = 0.2F + (float) Math.sin(
                (System.currentTimeMillis() % (Mth.PI * 800D)) / 800D) / 2F;
        VertexConsumer lines = event.getMultiBufferSource().getBuffer(RenderType.lines());
        RenderSystem.lineWidth(3F);
        drawLine(lines, pose, front, pos, camera, EDGE, 0D, EDGE, 1D, pulse);
        drawLine(lines, pose, front, pos, camera, 1D - EDGE, 0D, 1D - EDGE, 1D, pulse);
        drawLine(lines, pose, front, pos, camera, 0D, EDGE, 1D, EDGE, pulse);
        drawLine(lines, pose, front, pos, camera, 0D, 1D - EDGE, 1D, 1D - EDGE, pulse);

        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        drawSideIcon(event, pipe, level, pos, front, camera, left, selected, 0D, ICON_CENTER);
        drawSideIcon(event, pipe, level, pos, front, camera, top, selected, ICON_CENTER, ICON_HIGH);
        drawSideIcon(event, pipe, level, pos, front, camera, right, selected, ICON_HIGH, ICON_CENTER);
        drawSideIcon(event, pipe, level, pos, front, camera, bottom, selected, ICON_CENTER, 0D);
        drawSideIcon(event, pipe, level, pos, front, camera, front, selected, ICON_CENTER, ICON_CENTER);
        drawSideIcon(event, pipe, level, pos, front, camera, back, selected, 0D, 0D);
        drawSideIcon(event, pipe, level, pos, front, camera, back, selected, ICON_HIGH, 0D);
        drawSideIcon(event, pipe, level, pos, front, camera, back, selected, 0D, ICON_HIGH);
        drawSideIcon(event, pipe, level, pos, front, camera, back, selected, ICON_HIGH, ICON_HIGH);

        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
    }

    private static boolean isHoldingAdvancedWrench(Player player) {
        return player.getMainHandItem().is(ModItems.ADVANCED_PIPE_WRENCH.get())
                || player.getOffhandItem().is(ModItems.ADVANCED_PIPE_WRENCH.get());
    }

    private static Direction leftOf(Direction front) {
        return switch (front.getAxis()) {
            case X -> Direction.NORTH;
            case Y, Z -> Direction.WEST;
        };
    }

    private static Direction rightOf(Direction front) {
        return switch (front.getAxis()) {
            case X -> Direction.SOUTH;
            case Y, Z -> Direction.EAST;
        };
    }

    private static Direction topOf(Direction front) {
        return front.getAxis() == Direction.Axis.Y ? Direction.NORTH : Direction.UP;
    }

    private static Direction bottomOf(Direction front) {
        return front.getAxis() == Direction.Axis.Y ? Direction.SOUTH : Direction.DOWN;
    }

    private static void drawSideIcon(RenderHighlightEvent.Block event, PipeBlock pipe, Level level, BlockPos pos,
                                     Direction face, Vec3 camera, Direction side, Direction selected,
                                     double minU, double minV) {
        ResourceLocation texture = pipe.isConnected(level, pos, side) ? PIPE_CONNECT : PIPE_BLOCK;
        int color = side == selected ? 0xFFFFFFFF : 0x44FFFFFF;
        VertexConsumer consumer = event.getMultiBufferSource().getBuffer(RenderType.text(texture));
        drawTexturedQuad(consumer, event.getPoseStack().last(), face, pos, camera,
                minU + ICON_MARGIN, minV + ICON_MARGIN,
                minU + ICON_SIZE - ICON_MARGIN, minV + ICON_SIZE - ICON_MARGIN, color);
    }

    private static void drawTexturedQuad(VertexConsumer consumer, PoseStack.Pose pose, Direction face,
                                         BlockPos pos, Vec3 camera,
                                         double minU, double minV, double maxU, double maxV, int color) {
        Vec3 topLeft = toWorld(face, pos, minU, maxV).subtract(camera);
        Vec3 topRight = toWorld(face, pos, maxU, maxV).subtract(camera);
        Vec3 bottomRight = toWorld(face, pos, maxU, minV).subtract(camera);
        Vec3 bottomLeft = toWorld(face, pos, minU, minV).subtract(camera);

        if (face == Direction.SOUTH || face == Direction.WEST || face == Direction.UP) {
            addTexturedVertex(consumer, pose, topLeft, color, 0F, 1F);
            addTexturedVertex(consumer, pose, bottomLeft, color, 0F, 0F);
            addTexturedVertex(consumer, pose, bottomRight, color, 1F, 0F);
            addTexturedVertex(consumer, pose, topRight, color, 1F, 1F);
        } else {
            addTexturedVertex(consumer, pose, topLeft, color, 0F, 1F);
            addTexturedVertex(consumer, pose, topRight, color, 1F, 1F);
            addTexturedVertex(consumer, pose, bottomRight, color, 1F, 0F);
            addTexturedVertex(consumer, pose, bottomLeft, color, 0F, 0F);
        }
    }

    private static void addTexturedVertex(VertexConsumer consumer, PoseStack.Pose pose, Vec3 point,
                                          int color, float u, float v) {
        consumer.vertex(pose.pose(), (float) point.x, (float) point.y, (float) point.z)
                .color(color)
                .uv(u, v)
                .uv2(LightTexture.FULL_BRIGHT)
                .endVertex();
    }

    private static void drawLine(VertexConsumer consumer, PoseStack.Pose pose, Direction face,
                                 BlockPos pos, Vec3 camera, double u1, double v1, double u2, double v2,
                                 float pulse) {
        Vec3 start = toWorld(face, pos, u1, v1).subtract(camera);
        Vec3 end = toWorld(face, pos, u2, v2).subtract(camera);
        Vec3 normal = start.subtract(end);

        consumer.vertex(pose.pose(), (float) start.x, (float) start.y, (float) start.z)
                .color(pulse, pulse, 1F, 1F)
                .normal(pose.normal(), (float) normal.x, (float) normal.y, (float) normal.z)
                .endVertex();
        consumer.vertex(pose.pose(), (float) end.x, (float) end.y, (float) end.z)
                .color(pulse, pulse, 1F, 1F)
                .normal(pose.normal(), (float) normal.x, (float) normal.y, (float) normal.z)
                .endVertex();
    }

    private static Vec3 toWorld(Direction face, BlockPos pos, double u, double v) {
        double x;
        double y;
        double z;
        switch (face) {
            case NORTH -> {
                x = u;
                y = v;
                z = -FACE_OFFSET;
            }
            case SOUTH -> {
                x = u;
                y = v;
                z = 1D + FACE_OFFSET;
            }
            case WEST -> {
                x = -FACE_OFFSET;
                y = v;
                z = u;
            }
            case EAST -> {
                x = 1D + FACE_OFFSET;
                y = v;
                z = u;
            }
            case UP -> {
                x = u;
                y = 1D + FACE_OFFSET;
                z = 1D - v;
            }
            case DOWN -> {
                x = u;
                y = -FACE_OFFSET;
                z = 1D - v;
            }
            default -> throw new IllegalStateException("Unexpected direction: " + face);
        }
        return new Vec3(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
    }
}
