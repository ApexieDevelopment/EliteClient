package org.apexie.eliteclient.command.commands.fun;

import com.github.natanbc.reliqua.util.StatusCodeValidator;
import me.duncte123.botcommons.messaging.EmbedUtils;
import me.duncte123.botcommons.web.WebUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import org.apexie.eliteclient.command.CommandCategory;
import org.apexie.eliteclient.command.CommandContext;
import org.apexie.eliteclient.command.ICommand;
import org.apexie.eliteclient.command.UsageMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MinecraftCommand implements ICommand {

    @Override
    public void handle(CommandContext ctx) {
        final List<String> args = ctx.getArgs();
        final TextChannel channel = ctx.getChannel();

        if(args.isEmpty()) {
            new UsageMessage(this, ctx);
            return;
        }

        fetchUUID(args.get(0), (uuid) -> {
            if (uuid == null) {
                channel.sendMessage("User with name " + args.get(0) + " was not found").queue();
                return;
            }
            fetchUsername(args.get(0), (username) -> {
                fetchNameHistory(uuid, (names) -> {
                    final String namesJoined =  String.join(", ", names);

                    EmbedBuilder builder = EmbedUtils.embedMessage("")
                            .setTitle(username, "https://minotar.net/download/" + username)
                            .setColor(ctx.getSelfMember().getColor())
                            .addField("UUID", "`" + uuid + "`", false)
                            .addField("Names history", namesJoined, false)
                            .setImage("https://crafatar.com/renders/body/" + uuid + "?overlay")
                            .setFooter("Requested by " + ctx.getAuthor().getName(), ctx.getAuthor().getEffectiveAvatarUrl());

                    channel.sendMessage(builder.build()).queue();
                });
            });
        });
    }

    @Override
    public String getName() {
        return "minecraft";
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.FUN;
    }

    @Override
    public String getHelp() {
        return "Helpful to get Minecraft historical usernames and skins directly from Discord";
    }

    @Override
    public String getUsage() {
        return "minecraft <username|uuid>";
    }

    @Override
    public List<String> getAliases() {
        return List.of("mc", "skin", "namehistory");
    }

    private void fetchUUID(String username, Consumer<String> callback) {
        WebUtils.ins.getJSONObject(
                "https://api.mojang.com/users/profiles/minecraft/" + username,
                (builder) -> builder.setStatusCodeValidator(StatusCodeValidator.ACCEPT_200)
        ).async(
                (json) -> {
                    callback.accept(json.get("id").asText());
                },
                (error) -> {
                    callback.accept(null);
                }
        );
    }

    private void fetchUsername(String username, Consumer<String> callback) {
        WebUtils.ins.getJSONObject(
                "https://api.mojang.com/users/profiles/minecraft/" + username,
                (builder) -> builder.setStatusCodeValidator(StatusCodeValidator.ACCEPT_200)
        ).async(
                (json) -> {
                    callback.accept(json.get("name").asText());
                },
                (error) -> {
                    callback.accept(null);
                }
        );
    }

    private void fetchNameHistory(String uuid, Consumer<List<String>> callback) {
        WebUtils.ins.getJSONArray(
                "https://api.mojang.com/user/profiles/" + uuid + "/names",
                (builder) -> builder.setStatusCodeValidator(StatusCodeValidator.ACCEPT_200)
        ).async(
                (json) -> {
                    List<String> names = new ArrayList<>();
                    json.forEach((item) -> names.add(item.get("name").asText()));

                    callback.accept(names);
                },
                (error) -> {
                    callback.accept(null);
                }
        );
    }
}
