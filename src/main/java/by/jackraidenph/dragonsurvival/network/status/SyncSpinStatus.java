package by.jackraidenph.dragonsurvival.network.status;

import by.jackraidenph.dragonsurvival.capability.DragonStateProvider;
import by.jackraidenph.dragonsurvival.handlers.ServerSide.NetworkHandler;
import by.jackraidenph.dragonsurvival.network.IMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.DistExecutor.SafeRunnable;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Supplier;

import static net.minecraftforge.fml.network.NetworkDirection.PLAY_TO_SERVER;

public class SyncSpinStatus implements IMessage<SyncSpinStatus>
{
    public int playerId;
    public int spinAttack;
    public int spinCooldown;
    
    public SyncSpinStatus(int playerId, int spinAttack, int spinCooldown)
    {
        this.playerId = playerId;
        this.spinAttack = spinAttack;
        this.spinCooldown = spinCooldown;
    }
    
    public SyncSpinStatus() {
    }
    
    @Override
    public void encode(SyncSpinStatus message, PacketBuffer buffer) {
        buffer.writeInt(message.playerId);
        buffer.writeInt(message.spinAttack);
        buffer.writeInt(message.spinCooldown);
    }
    
    @Override
    public SyncSpinStatus decode(PacketBuffer buffer) {
        int playerId = buffer.readInt();
        int spinAttack = buffer.readInt();
        int spinCooldown = buffer.readInt();
        return new SyncSpinStatus(playerId, spinAttack, spinCooldown);
    }
    
    @Override
    public void handle(SyncSpinStatus message, Supplier<NetworkEvent.Context> supplier) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> (SafeRunnable)() -> run(message, supplier));
        
        if(supplier.get().getDirection() == PLAY_TO_SERVER){
            ServerPlayerEntity entity = supplier.get().getSender();
            if(entity != null){
                DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
                  dragonStateHandler.getMovementData().spinAttack = message.spinAttack;
                    dragonStateHandler.getMovementData().spinCooldown = message.spinCooldown;
                });
                
                NetworkHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), new SyncSpinStatus(entity.getId(), message.spinAttack, message.spinCooldown));
            }
        }
    }
    
    @OnlyIn( Dist.CLIENT)
    public void run(SyncSpinStatus message, Supplier<NetworkEvent.Context> supplier){
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            PlayerEntity thisPlayer = Minecraft.getInstance().player;
            if (thisPlayer != null) {
                World world = thisPlayer.level;
                Entity entity = world.getEntity(message.playerId);
                if (entity instanceof PlayerEntity) {
                    DragonStateProvider.getCap(entity).ifPresent(dragonStateHandler -> {
                        dragonStateHandler.getMovementData().spinAttack = message.spinAttack;
                        dragonStateHandler.getMovementData().spinCooldown = message.spinCooldown;
                    });
                }
            }
            context.setPacketHandled(true);
        });
    }
}
