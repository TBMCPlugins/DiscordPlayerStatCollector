import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.MessageHistory;
import sx.blah.discord.util.RequestBuffer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainHeh {
    public static void main(String... args) throws IOException {
        IDiscordClient idc = new ClientBuilder().withToken(Files.readAllLines(new File("Token.txt").toPath()).get(0))
                .registerListener((IListener<ReadyEvent>) event -> {
                    IChannel chan = event.getClient().getChannelByID(249663564057411596L);
                    Pattern patplayer = Pattern.compile("(\\S+).*");
                    File f = new File("data.csv");
                    f.delete();
                    long mc = 0;
                    BiConsumer<MessageHistory, Boolean> cmh = (mh, skip) -> {
                        RequestBuffer.request(() -> { //? eh
                            try {
                                Files.write(f.toPath(),
                                        (Iterable<String>) mh.stream().skip(skip ? 1 : 0) //Skip 'from' message to avoid double processing
                                                .filter(msg -> msg.getContent().matches(
                                                        "(\\S+( \\(formerly.+\\))* joined the game)" +
                                                                "|(\\S+ left the game)")
                                                        || (msg.getEmbeds().size() > 0 && msg.getEmbeds().get(0).getTitle() != null
                                                        && msg.getEmbeds().get(0).getTitle().matches(
                                                        "(Server recovered from a crash - chat connected\\.)"
                                                )))
                                                .map(msg -> {
                                                    Matcher m = patplayer.matcher(msg.getContent());
                                                    return (m.matches()
                                                            ? m.group(1) + (msg.getContent().contains("joined") ? "\tjoin" : "\tleave")
                                                            : "EVERYBODY\tLEFT")
                                                            + "\t" + msg.getTimestamp().toString();
                                                })::iterator, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }).get(); //Wait for completion
                    };
                    MessageHistory mh = chan.getMessageHistoryFrom(Instant.now(), 100);
                    cmh.accept(mh, false);
                    System.out.println("Processed " + (mc += mh.size()) + " messages.");
                    while (mh.size() > 0) {
                        cmh.accept(mh, true);
                        mh = chan.getMessageHistoryFrom(mh.get(mh.size() - 1).getLongID(), 100);
                        System.out.println("Processed " + (mc += mh.size()) + " messages.");
                    }
                    System.out.println("DONE!");
                }).login();
    }
}
