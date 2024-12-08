package biz.donvi.jakesRTP.PluginHooks;

import net.crashcraft.crashclaim.CrashClaim;
import net.crashcraft.crashclaim.claimobjects.Claim;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;


public class LrCrashClaim implements LocationRestrictor{
    protected CrashClaim cmPlugin;
    public LrCrashClaim(CrashClaim cmPlugin) {
        this.cmPlugin = cmPlugin;
    }

    @Override
    public Plugin supporterPlugin() {return cmPlugin;}

    private Claim lastClaim = null;

    @Override
    public boolean denyLandingAtLocation(Location location) {
        return cmPlugin.getApi().getClaim(location) != null;
    }
}
