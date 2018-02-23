package sole.memory.command;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import sole.memory.HideAndSeek;

public class AddRoomCommand extends Command{

    private HideAndSeek plugin;
    public AddRoomCommand(String name) {
        super(name,"HideAndSeek 主命令","/has help 查看帮助");
        this.plugin = HideAndSeek.getInstance();
        this.commandParameters.clear();
        this.addCommandParameters("1arg",new CommandParameter[]{new CommandParameter("add|del",false,new String[]{"add","del"}),new CommandParameter("Room","string",false)});
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {

        return false;
    }
}
