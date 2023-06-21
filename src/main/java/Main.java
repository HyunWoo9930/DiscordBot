import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;


public class Main extends ListenerAdapter {

    public static List<String> team1 = new ArrayList<>();
    public static List<String> team2 = new ArrayList<>();

    public static void main(String[] args) {
        String token = "MTEyMDY5NDUzOTUxNTA4ODk1Ng.GLDLEj.x1aHyC_ig2ERi57iM0HHHbcbHPRPxqzbW38imw";
        JDABuilder builder = JDABuilder.createDefault(token);
        builder.enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.MESSAGE_CONTENT);
        JDA jda = builder.build();
        jda.addEventListener(new Main());
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.isFromGuild()) {
            if (!event.getChannel().asTextChannel().getType().isGuild()) {
                return;
            }

            TextChannel channel = event.getChannel().asTextChannel();

            Message message = event.getMessage();
            String content = message.getContentRaw();
            if (content.contains("전적")) {
                channel.sendMessage("아직 구현되지 않았습니다!").queue();
            } else if (content.contains("! 승패")) {
                System.out.println("content = " + content);
                Pattern pattern = Pattern.compile("! 승패 팀(\\d) *승");
                Matcher matcher = pattern.matcher(content);
                while(matcher.find()) {
                    String group = matcher.group(1);
                    if(group.equals("1")) {
                        System.out.println("team1 win!");
                    } else if(group.equals("2")) {
                        System.out.println("team2 win!");
                    }
                }
                System.out.println("team1 = " + team1);
                System.out.println("team2 = " + team2);

            } else if(content.equals("! 팀")) {
                channel.sendMessage("team 1 = " + team1.toString()).queue();
                channel.sendMessage("team 2 = " + team2.toString()).queue();
            }
            else if(content.startsWith("! ")) {
                String[] balancesArray = content.substring(2).split("/");
                int count = balancesArray.length;
                Map<Integer, List<String>> balanceMap = new HashMap<>();

                for (int i = 0; i < count; i++) {
                    String[] strings1 = balancesArray[i].split(",");
                    List<String> balance = new ArrayList<>();
                    balance.add(strings1[0].trim());
                    balance.add(strings1[1].trim());
                    balanceMap.put(i + 1, balance);
                }
                balanceMap.keySet().forEach(key -> {
                    List<String> list = balanceMap.get(key);
                    getTeam(list);
                });

                channel.sendMessage("team 1 = " + team1.toString()).queue();
                channel.sendMessage("team 2 = " + team2.toString()).queue();

//                team1.clear();
//                team2.clear();


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

