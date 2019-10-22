package dev.pandasoft.simplejuke.util;

import dev.pandasoft.simplejuke.Main;

import java.util.Calendar;
import java.util.Date;

public class HibernatePlayerChecker implements Runnable {
    private Date latestCheck;

    @Override
    public void run() {
        latestCheck = new Date();

        Main.getController().getPlayerRegistry().getPlayers().forEach(guildAudioPlayer -> {
            if (!guildAudioPlayer.isPlaying()) {
                Date now = new Date();
                Calendar threshold = Calendar.getInstance();
                threshold.setTime(guildAudioPlayer.getLastPlayed());
                threshold.add(Calendar.MINUTE, 30);
                if (now.after(threshold.getTime())) {
                    Main.getController().getPlayerRegistry().destroyPlayer(guildAudioPlayer.getGuild());
                    MessageUtil.sendMessage(guildAudioPlayer.getGuild(), "長時間使われていないようですね？\n" +
                            "リソース削減のためプレイヤーは自動的に退出します。");
                }
            }
        });
    }

    public Date getLatestCheck() {
        return latestCheck;
    }
}
