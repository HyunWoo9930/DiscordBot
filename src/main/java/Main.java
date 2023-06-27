import com.google.gson.JsonObject;
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
    public static JsonObject history = new JsonObject();

    public static void main(String[] args) {
        DiscordBotToken discordBotToken = new DiscordBotToken();
        JDABuilder builder = JDABuilder.createDefault(discordBotToken.getDiscordBotToken());
        builder.enableIntents(GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT);
        JDA jda = builder.build();
        jda.addEventListener(new Main());
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.getAuthor().getId().equals(event.getJDA().getSelfUser().getId())) {
            return;
        }
        if (event.isFromGuild()) {
            if (!event.getChannel().asTextChannel().getType().isGuild()) {
                return;
            }
            TextChannel channel = event.getChannel().asTextChannel();

            Message message = event.getMessage();
            String content = message.getContentRaw();
            if (content.equals("! 전적")) {
                if (history.has("gameCount")) {
                    int gameCount = history.get("gameCount").getAsInt();
                    channel.sendMessage("오늘 총 게임 수 : " + gameCount + " 판").queue();
                    for (String name : history.keySet()) {
                        if(!name.equals("gameCount")) {
                            JsonObject inner = history.get(name).getAsJsonObject();
                            String winCount = inner.get("winCount").getAsString();
                            String loseCount = inner.get("loseCount").getAsString();
                            channel.sendMessage(name + " : " + winCount + "승 " + loseCount + "패")
                                .queue();
                        }
                    }
                } else {
                    channel.sendMessage("아직 전적이 없습니다!").complete();
                }
            } else if (content.equals("help") || content.equals("!help")) {
                channel.sendMessage("팀 balance = ! 이름,이름/.../이름,이름").queue();
                channel.sendMessage("팀 확인 = ! 팀").queue();
                channel.sendMessage("전적 저장 = ! 승패 팀(1 or 2) 승").queue();
                channel.sendMessage("전적 불러오기 = ! 전적").queue();
            } else if (content.equals("! 전적 초기화")) {
                history = new JsonObject();
                channel.sendMessage("전적 초기화 하였습니다. ").queue();
            } else if (content.startsWith("! 승패")) {
                Pattern pattern = Pattern.compile("! 승패 팀(\\d) *승");
                Matcher matcher = pattern.matcher(content);
                if (team1.size() == 0 || team2.size() == 0) {
                    channel.sendMessage("팀이 만들어지지 않았습니다. 팀을 짜시고 다시 실행해주세요.").queue();
                } else {
                    while (matcher.find()) {
                        String group = matcher.group(1);
                        if (group.equals("1")) {
                            saveWinHistory(team1, team2);
                        } else if (group.equals("2")) {
                            saveWinHistory(team2, team1);
                        }
                    }
                    channel.sendMessage("저장하였습니다.").queue();
                    team1.clear();
                    team2.clear();
                }
            } else if (content.equals("! 팀")) {
                if (team1.size() == 0 || team2.size() == 0) {
                    channel.sendMessage("팀이 만들어지지 않았습니다. 팀을 짜시고 다시 실행해주세요.").queue();
                } else {
                    channel.sendMessage("team 1 = " + team1).queue();
                    channel.sendMessage("team 2 = " + team2).queue();
                }
            } else if (content.startsWith("! ")) {
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
            } else {
                channel.sendMessage("제대로 된 명령어를 입력해주세요!\nhelp 명령어를 사용하면 명령어 목록을 확인할 수 있습니다.").queue();
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

    public static void saveWinHistory(List<String> winTeam, List<String> loseTeam) {
        for (String value : winTeam) {
            if (history.has(value)) {
                int winCount = history.get(value).getAsJsonObject().get("winCount").getAsInt() + 1;
                history.get(value).getAsJsonObject().addProperty("winCount", winCount);
            } else {
                JsonObject innerObj = new JsonObject();
                innerObj.addProperty("winCount", 1);
                innerObj.addProperty("loseCount", 0);
                history.add(value, innerObj);
            }
        }

        for (String s : loseTeam) {
            if (history.has(s)) {
                int loseCount = history.get(s).getAsJsonObject().get("loseCount").getAsInt() + 1;
                history.get(s).getAsJsonObject().addProperty("loseCount", loseCount);
            } else {
                JsonObject innerObj = new JsonObject();
                innerObj.addProperty("winCount", 0);
                innerObj.addProperty("loseCount", 1);
                history.add(s, innerObj);
            }
        }

        if (history.has("gameCount")) {
            int gameCount = history.get("gameCount").getAsInt() + 1;
            history.addProperty("gameCount", gameCount);
        } else {
            history.addProperty("gameCount", 1);
        }

    }

}

