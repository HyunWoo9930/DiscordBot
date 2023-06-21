import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class DiscordBotToken {
    @Value("${discord.bot.token}")
    private String discordBotToken;

}
