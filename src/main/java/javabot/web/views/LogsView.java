package javabot.web.views;

import com.antwerkz.sofia.Sofia;
import com.google.inject.Injector;
import javabot.dao.LogsDao;
import javabot.dao.util.CleanHtmlConverter;
import javabot.model.Logs;
import javabot.web.resources.BotResource;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;

public class LogsView extends MainView {
    public static final DateTimeFormatter LOG_FORMAT = DateTimeFormatter.ofPattern("hh:mm");
    @Inject
    private LogsDao logsDao;
    private String today;
    private String yesterday;
    private String tomorrow;
    private final String channel;
    private final LocalDateTime date;

    public LogsView(final Injector injector, final HttpServletRequest request, final String channel, final LocalDateTime date) {
        super(injector, request);
        this.channel = channel;
        this.date = date;
        today = BotResource.FORMAT.format(date);
        yesterday = BotResource.FORMAT.format(date.minusDays(1));
        tomorrow = BotResource.FORMAT.format(date.plusDays(1));
    }

    public String getChannel() {
        return channel;
    }

    public String getToday() {
        return today;
    }

    public String getYesterday() {
        return yesterday;
    }

    public String getTomorrow() {
        return tomorrow;
    }

    public String format(LocalDateTime date) {
        return LOG_FORMAT.format(date);
    }

    public List<Logs> logs() {
        List<Logs> logs=logsDao.findByChannel(channel, date, isAdmin());
        // filter the log content
        for(Logs log:logs) {
            log.setMessage(CleanHtmlConverter.convert(log.getMessage(), s -> Sofia.logsAnchorFormat(s, s)));
        }
        return logs;
    }
    @Override
    public String getChildView() {
        return "logs.ftl";
    }

}