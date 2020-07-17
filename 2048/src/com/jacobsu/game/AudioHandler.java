package com.jacobsu.game;

import javax.sound.sampled.*;
import java.net.URL;
import java.util.HashMap;

//音乐
public class AudioHandler {

    private static AudioHandler handler;
    private HashMap<String, Clip>sounds;

    private AudioHandler(){
        sounds=new HashMap<String, Clip>();
    }

    public static AudioHandler getInstance(){
        if(handler==null){
            handler=new AudioHandler();
        }
        return handler;

    }

    //加载；文件名，自定义名
    public void load(String resourcePath,String name){
        //获取地址
        URL resource =AudioHandler.class.getClassLoader().getResource(resourcePath);

        AudioInputStream input=null;

        try {
            input= AudioSystem.getAudioInputStream(resource);
        }catch (Exception e){
            e.printStackTrace();
        }

        AudioFormat baseFormat=input.getFormat();

        if (baseFormat.getEncoding()==AudioFormat.Encoding.PCM_SIGNED){
            try {
                Clip c=AudioSystem.getClip();
                c.open(input);
                sounds.put(name,c);
                return;
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        AudioFormat decodeFormat =new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                baseFormat.getSampleRate(),
                16,
                baseFormat.getChannels(),
                baseFormat.getChannels()*2,
                baseFormat.getSampleRate(),
                false
        );


        AudioInputStream decodeIn= AudioSystem.getAudioInputStream(decodeFormat,input);
        try {
            Clip c=AudioSystem.getClip();
            c.open(decodeIn);
            sounds.put(name,c);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    //播放；自定义名，循环次数
    public void play(String name,int loopCount){
        if(sounds.get(name).isRunning()){
            sounds.get(name).stop();
        }
        sounds.get(name).setFramePosition(0);//从第0帧开始播放
        sounds.get(name).loop(loopCount);
    }

    //设置音量
    public void adjustVolume(String name,int value){
        FloatControl control=(FloatControl)sounds.get(name).getControl(FloatControl.Type.MASTER_GAIN);
        control.setValue(value);
    }
}
