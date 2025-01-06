package net.gahvila.rtp.API;

import net.gahvila.rtp.RTP;
import net.gahvila.rtp.Teleportation.RandomTeleportAction;
import net.gahvila.rtp.Teleportation.RandomTeleporter;
import net.gahvila.rtp.Teleportation.RtpProfile;
import org.bukkit.entity.Player;

public class rtpAPI {
    private final RTP rtp;
    private final RandomTeleporter randomTeleporter;

    public rtpAPI(RTP rtp, RandomTeleporter randomTeleporter){
        this.randomTeleporter = randomTeleporter;
        this.rtp = rtp;
    }

    /**
     * Teleport player with a specific configuration
     *
     * @param teleportPlayer which player to teleport
     * @param config which config to use for teleport
     */
    public void randomTeleportPlayerConfig(Player teleportPlayer, String config) throws Exception{
        RtpProfile rtpProfile = randomTeleporter.getRtpSettingsByName(config);

        new RandomTeleportAction(
                randomTeleporter, rtpProfile, teleportPlayer.getLocation(), true, true,
                randomTeleporter.logRtpFromAPI, "Rtp-from-API triggered!"
        ).teleportAsync(teleportPlayer);
    }
}
