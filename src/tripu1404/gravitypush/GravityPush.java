package tripu1404.gravitypush;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockSand;
import cn.nukkit.block.BlockGravel;
import cn.nukkit.block.BlockConcretePowder;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.math.Vector3;

import java.util.HashMap;
import java.util.UUID;

public class GravityPush extends PluginBase implements Listener {

    private final HashMap<UUID, Block> lastBlock = new HashMap<>();
    private final HashMap<UUID, Integer> pushAttempts = new HashMap<>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("GravityPush enabled!");
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        // detects the block
        Block currentBlock = player.getLevel().getBlock(player.floor());

        // last block
        Block previousBlock = lastBlock.getOrDefault(uuid, null);

        // tries to push the player
        lastBlock.put(uuid, currentBlock);

        // if succes stop the plugin
        if (previousBlock == null || !previousBlock.equals(currentBlock)) {
            pushAttempts.put(uuid, 0);
        }

        // detects if the player is on sand
        if (isGravityBlock(currentBlock) && player.y < currentBlock.getY() + 1) {
            int attempts = pushAttempts.getOrDefault(uuid, 0);

            if (attempts < 3) {
                // push the player
                Vector3 velocity = player.getMotion().add(0, 0.5, 0);
                player.setMotion(velocity);

                // tries 3 times
                pushAttempts.put(uuid, attempts + 1);
            }
        }
    }

    private boolean isGravityBlock(Block block) {
        return block instanceof BlockSand
                || block instanceof BlockGravel
                || block instanceof BlockConcretePowder;
    }
}
