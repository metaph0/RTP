package biz.donvi.jakesRTP.API;

import biz.donvi.jakesRTP.Teleportation.RandomTeleportAction;
import biz.donvi.jakesRTP.Teleportation.RandomTeleporter;
import biz.donvi.jakesRTP.Teleportation.RtpProfile;
import biz.donvi.jakesRTP.*;
import org.bukkit.entity.Player;

public class rtpAPI {
    private final JakesRtpPlugin jakesRtpPlugin;
    private final RandomTeleporter randomTeleporter;

    public rtpAPI(JakesRtpPlugin jakesRtpPlugin, RandomTeleporter randomTeleporter){
        this.randomTeleporter = randomTeleporter;
        this.jakesRtpPlugin = jakesRtpPlugin;
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
