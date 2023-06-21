import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Main extends ListenerAdapter {

    public static List<String> team1 = new ArrayList<>();
    public static List<String> team2 = new ArrayList<>();

    public static void main(String[] args) {
        String token = "MTEyMDY5NDUzOTUxNTA4ODk1Ng.Gg5tZx.UaMSY0Wl2uJJ4d49vEUPukzF9XdV8Lipkjn4f4";
        JDABuilder builder = JDABuilder.createDefault(token);
        builder.enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.MESSAGE_CONTENT);
        JDA jda = builder.build();
        jda.addEventListener(new Main());
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.isFromGuild()) {
            if (!event.getChannel().asTextChannel().getType().isGuild()) return;


            TextChannel channel = event.getChannel().asTextChannel();

            Message message = event.getMessage();
            String content = message.getContentRaw();

            //TODO !칼바람 팀짜기 4 (오현우,이동재/이승서,탁기훈/박예준,세영/한세웅,이상우) 에서 칼바람 팀짜기 제거
            if(content.startsWith("!칼바람 팀짜기 ")) {


                Pattern pattern = Pattern.compile("!칼바람 팀짜기 (\\d*) *\\((.*)\\)");
                Matcher matcher = pattern.matcher(content);

                Map<Integer, List<String>> balanceMap = new HashMap<>();
                int team = 0;
                String teamMember = "";

                while(matcher.find()) {
                    team = Integer.parseInt(matcher.group(1));
                    teamMember = matcher.group(2);
                }

                String[] strings = teamMember.split("/");
                if(strings.length != team) {
                    channel.sendMessage("team 인원수와 입력받은 인원이 맞지 않습니다. 다시 확인해주세요.").queue();
                } else {
                    for (int i = 0; i < team; i++) {
                        String[] strings1 = strings[i].split(",");
                        List<String> balance = new ArrayList<>();
                        balance.add(strings1[0]);
                        balance.add(strings1[1]);
                        balanceMap.put(i + 1, balance);
                    }

                    balanceMap.keySet().forEach(key -> {
                        List<String> list = balanceMap.get(key);
                        getTeam(list);
                    });

                    channel.sendMessage("team 1 = " + team1.toString()).queue();
                    channel.sendMessage("team 2 = " + team2.toString()).queue();

                    team1.clear();
                    team2.clear();
                }
            }
        }
    }

    public static void getTeam(List<String> list) {
        String person1 = list.get(0);
        String person2 = list.get(1);

        Random random = new Random();
        int teamNumber = random.nextInt(2) + 1; // 1 또는 2 중에서 랜덤한 숫자 생성

        if (teamNumber == 1) {
            team1.add(person1);
            team2.add(person2);
        } else {
            team1.add(person2);
            team2.add(person1);
        }
    }
}

