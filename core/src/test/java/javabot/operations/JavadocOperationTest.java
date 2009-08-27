package javabot.operations;

import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import javabot.dao.ApiDao;
import javabot.dao.ClazzDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Test(dependsOnGroups = {"javadoc"})
public class JavadocOperationTest extends BaseOperationTest {
    private static final Logger log = LoggerFactory.getLogger(JavadocOperationTest.class);
    @Autowired
    private ApiDao apiDao;
    @Autowired
    private ClazzDao clazzDao;

    public void methods() {
        testMessage("javadoc String.split(String)",
            getTestBot().getNick() + ": http://is.gd/2ABgG [JDK: java.lang.String.split(String)]");
        testMessage("javadoc String.split(java.lang.String)",
            getTestBot().getNick() + ": http://is.gd/2ABgG [JDK: java.lang.String.split(String)]");
        final TestBot bot = getTestBot();
        bot.sendMessage(getJavabotChannel(), String.format("%s %s", getJavabot().getNick(), "javadoc String.split(*)"));
        waitForResponses(bot, 1);
        final String response = bot.getOldestResponse().getMessage();
        final List<String> strings = Arrays
            .asList(response.substring((getTestBot().getNick() + ": ").length()).split(";"));
        Assert.assertEquals(strings.size(), 2);
    }

    public void nestedClasses() {
        final TestBot bot = getTestBot();
        bot.sendMessage(getJavabotChannel(),
            String.format("%s %s", getJavabot().getNick(), "javadoc Map.Entry"));
        waitForResponses(bot, 1);
        final String response = bot.getOldestResponse().getMessage();
        Assert.assertEquals(response, getTestBot().getNick() + ": http://is.gd/2ABgy [JDK: java.util.Map.Entry]");

    }
    
    public void format() {
        final TestBot bot = getTestBot();
        bot.sendMessage(getJavabotChannel(),
            String.format("%s %s", getJavabot().getNick(), "javadoc java.lang.String.format(*)"));
        waitForResponses(bot, 1);
        final String[] response = bot.getOldestResponse().getMessage().split(";");
        StringTokenizer tz = new StringTokenizer(response[0], "[]");
        tz.nextToken();
        final String method = tz.nextToken();
        final boolean comma = method.substring(method.indexOf("(")+1, method.length()).endsWith(",");
    }

    @SuppressWarnings({"StringContatenationInLoop"})
    public void doFinal() throws InterruptedException {
        final TestBot bot = getTestBot();
        bot.sendMessage(getJavabotChannel(),
            String.format("%s %s", getJavabot().getNick(), "javadoc Cipher.doFinal(*)"));
        waitForResponses(bot, 2);
        final List<String> responses = Arrays.asList(
            "http://is.gd/igel [JDK: javax.crypto.Cipher.doFinal()]",
            "http://is.gd/igep [JDK: javax.crypto.Cipher.doFinal(ByteBuffer,ByteBuffer)]",
            "http://is.gd/igen [JDK: javax.crypto.Cipher.doFinal(byte[])]",
            "http://is.gd/igem [JDK: javax.crypto.Cipher.doFinal(byte[],int)]",
            "http://is.gd/igeq [JDK: javax.crypto.Cipher.doFinal(byte[],int,int)]",
            "http://is.gd/igeo [JDK: javax.crypto.Cipher.doFinal(byte[],int,int,byte[])]",
            "http://is.gd/igee [JDK: javax.crypto.Cipher.doFinal(byte[],int,int,byte[],int)]"
        );
        String response = bot.getOldestResponse().getMessage();
        Assert.assertEquals(response, getTestBot().getNick() + ", too many results found.  Please see your"
            + " private messages for results");
        Thread.sleep(5000);
        final StringBuilder hits = new StringBuilder();
        while ((response = bot.getOldestMessage()) != null) {
            hits.append(" " + response);
        }
        final List<String> strings = Arrays.asList(hits.substring((getTestBot().getNick() + ": ").length()).trim().split("; "));
        Assert.assertEquals(strings.size(), responses.size());
    }

    protected void testList(final String message, final String target) {
        final TestBot bot = getTestBot();
        bot.sendMessage(getJavabotChannel(), String.format("%s %s", getJavabot().getNick(), message));
        waitForResponses(bot, 1);
        final String oldest = bot.getOldestResponse().getMessage();
        final String response = oldest.substring((getTestBot().getNick() + ": ").length());
        Assert.assertTrue(response.contains(target));
    }
}
