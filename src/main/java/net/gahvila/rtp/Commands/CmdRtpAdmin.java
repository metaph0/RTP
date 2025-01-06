package net.gahvila.rtp.Commands;

import net.gahvila.rtp.argsChecker.ArgsChecker;
import net.gahvila.rtp.argsChecker.ArgsTester;
import net.gahvila.rtp.argsChecker.DynamicArgsMap;
import net.gahvila.rtp.Utils.GeneralUtil;
import net.gahvila.rtp.Utils.GeneralUtil.Pair;
import net.gahvila.rtp.RTP;
import net.gahvila.rtp.Teleportation.RandomTeleporter;
import net.gahvila.rtp.Teleportation.RtpProfile;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CmdRtpAdmin extends DynamicArgsMap implements TabExecutor {

    Map<String, Object> cmdMap;

    public CmdRtpAdmin(Map<String, Object> commandMap) {
        cmdMap = commandMap;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        ArgsChecker argsChecker = new ArgsChecker(args);

        if (argsChecker.matches(true, "reload"))
            subReload(sender);
        else if (argsChecker.matches(true, "status"))
            sender.sendMessage("Incorrect usage. Try:\n/rtp-admin status <#static|name-of-config>");
        else if (argsChecker.matches(true, "status", null))
            subStatus(sender, argsChecker.getRemainingArgs());
        else if (argsChecker.matches(true, "reload-messages"))
            subReloadMessages(sender);
        else return false;
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return ArgsTester.nextCompleteInTree(args, cmdMap, this);
    }

    @Override
    public void getPotential(String[] path) throws ResultAlreadySetException { }

    @Override
    public void getPotential(String path) throws ResultAlreadySetException {
        //noinspection SwitchStatementWithTooFewBranches
        switch (path) {
            case "status":
                setResult(getConfigNames());
        }
    }

    private void subReload(CommandSender sender) {
        RTP.plugin.reloadCommands();
        RTP.plugin.loadRandomTeleporter();
        RTP.plugin.loadLocationCacheFiller();
        sender.sendMessage("Reloaded.");
    }

    private void subReloadMessages(CommandSender sender) {
        RTP.plugin.loadMessageMap();
    }


    /**
     * The result of {@code getConfigNames()}, stored with an expiration time. If the data has not expired, the method
     * should return the value of {@code getConfigNamesResults}. If it has expired, it should compute the new value,
     * save
     * it here, then return it.
     */
    private Pair<Long, List<String>> getConfigNamesResults;

    /**
     * Gets a list of the RTP config names. Because this method is expected to be called multiple times per second,
     * yet returns data that changed infrequently, it temporarily stores the resulting list and only rechecks the
     * after 1000 milliseconds.
     *
     * @return A list of the names of the RTP configs.
     */
    private List<String> getConfigNames() {
        if (getConfigNamesResults == null || getConfigNamesResults.key < System.currentTimeMillis()) {
            ArrayList<String> settingsNames = new ArrayList<>();
            for (RtpProfile settings : RTP.plugin.getRandomTeleporter().getRtpSettings()) {
                settingsNames.add(settings.name);
            }
            getConfigNamesResults = new Pair<>(System.currentTimeMillis() + 1000, settingsNames);
        }
        return getConfigNamesResults.value;
    }


    private void subStatus(CommandSender sender, String[] args) {
        RandomTeleporter theRandomTeleporter = RTP.plugin.getRandomTeleporter();
        if (args.length == 1 && args[0].equalsIgnoreCase("#static")) {
            StringBuilder msg = new StringBuilder();
            for (String line : theRandomTeleporter.infoStringAll(true))
                msg.append(line).append('\n');
            sender.sendMessage(msg.toString());
        } else try {
            RtpProfile settings = theRandomTeleporter.getRtpSettingsByName(args[0]);
            for (String message : settings.infoStringAll(true, true))
                sender.sendMessage(message);
        } catch (Exception e) {
            sender.sendMessage(
                "Could not find any settings with the name " + args[0] + ", " +
                GeneralUtil.listText(theRandomTeleporter.getRtpSettingsNames())
            );
        }

    }

}
