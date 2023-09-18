package sample.Webhook;


import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

public class HookManagement {

    private static DiscordWebhook hook;
    private final static String url = "XXXX";

    static {
        try {
           hook = new DiscordWebhook(url);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }


    public static void sendMsg(String message) {
        try {
            String avatar = "https://i.imgur.com/znlX7YC.png";
            hook.setUsername("FXBot");
            hook.setAvatarUrl(avatar);
            hook.setContent("> "+message);
            hook.execute();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
