import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.MessageHistory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class MainHeh {
    public static void main(String... args) throws IOException {
        IDiscordClient idc = new ClientBuilder().withToken(Files.readAllLines(new File("Token.txt").toPath()).get(0))
                .registerListener((IListener<ReadyEvent>) event -> {
                    IChannel chan = event.getClient().getChannelByID(249663564057411596L);
                    final AtomicInteger x = new AtomicInteger();
                    /*while(true) {
                        chan.getMessageHistory(100).stream()
                                .filter(msg -> msg.getContent().matches(
                                        "(\\S+( \\(formerly.+\\))* joined the game)" +
                                        "|(\\S+ left the game)")
                                || (msg.getEmbeds().size()>0&&msg.getEmbeds().get(0).getTitle()!=null
                                        &&msg.getEmbeds().get(0).getTitle().matches(
                                        "(Server recovered from a crash - chat connected\\.)"
                                )))
                                .map(IMessage::getContent).forEach(System.out::println);
                        //break;
                        chan.getMessageHistoryFrom()
                    }*/
                    Consumer<MessageHistory> cmh = mh -> {
                        mh.stream()
                                .filter(msg -> msg.getContent().matches(
                                        "(\\S+( \\(formerly.+\\))* joined the game)" +
                                                "|(\\S+ left the game)")
                                        || (msg.getEmbeds().size() > 0 && msg.getEmbeds().get(0).getTitle() != null
                                        && msg.getEmbeds().get(0).getTitle().matches(
                                        "(Server recovered from a crash - chat connected\\.)"
                                )))
                                .map(IMessage::getContent).forEach(System.out::println);
                    };
                    MessageHistory mh = chan.getMessageHistoryFrom(Instant.now(), 100);
                    cmh.accept(mh);
                    while (mh.size() > 0) {
                        mh = chan.getMessageHistoryFrom(mh.get(mh.size() - 1).getLongID(), 100);
                        cmh.accept(mh);
                    }
                }).login();
    }
}
