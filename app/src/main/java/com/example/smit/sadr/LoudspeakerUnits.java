package com.example.smit.sadr;


public class LoudspeakerUnits {
    public  String name;
    public byte canal;
    public Integer id;
    public Integer status;
    public String ip;
    public int  port;
    public Long tstamp;
    public  Integer turn;
    public Integer FF;
    public LoudspeakerUnits(String name, byte canal, Integer id, Integer status,String ip,int port, Long tstamp,Integer turn){
        this.name = name;
        this.canal = canal;
        this.id = id;
        this.status = status;
        this.ip = ip;
        this.tstamp = tstamp;
        this.turn = turn;
        this.port = port;
        this.FF = General.NYES;
    }
}
