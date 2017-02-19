package com.haxademic.core.draw.image;


public class DrawCommand 
{
    public interface Command 
    {
        public void execute(Object data, float t);
    }

    public static void callCommand(Command command, Object data, float t) 
    {
        command.execute(data, t);
    }

}
