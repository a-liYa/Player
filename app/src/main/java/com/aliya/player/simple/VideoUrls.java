package com.aliya.player.simple;

/**
 * 测试数据
 *
 * @author a_liYa
 */
public class VideoUrls {

    private static String[] URLS = {
            "http://api.zjol.com.cn/api/video/140270?avaliable=hd",
            "http://api.zjol.com.cn/api/video/140270?avaliable=hd",
            "http://api.zjol.com.cn/api/video/140270?avaliable=hd",
            "http://cdn.v.zjol.com.cn/151999.mp4",
            "http://cdn.v.zjol.com.cn/151990.mp4",
            "http://cdn.v.zjol.com.cn/152038.mp4",
            "http://cdn.v.zjol.com.cn/152058.mp4",
            "http://v3-tt.ixigua.com/5caafb2558717caf07b804ed8d8d1d05/5992652c/video/m/220360426db79114aa28eee4bec3dc6c4fe114e3b3000030d616ad2dc3/",
            "http://v6-tt.ixigua.com/video/m/220f69d7bc774274be68d86a8a492982cb2114ec820000412d49b8c732/?Expires=1502770121&AWSAccessKeyId=qh0h9TdcEMoS2oPj7aKX&Signature=2lmGRbnecp5uEXP%2Ftlp7B5o91DE%3D",
            "http://v3-tt.ixigua.com/156552a3d18f055ff9c8924c8768fd57/59926770/video/m/220ff40492a65484b57b66f0d7f0c18d57c114ed3b0000654ae4def91a/",
            "http://v6-tt.ixigua.com/video/m/2200bcd7e7568204dee83e290380bed32b3114e45c000084d3a2428fa0/?Expires=1502771419&AWSAccessKeyId=qh0h9TdcEMoS2oPj7aKX&Signature=jWgeM8a4IjTkIb3VYwGgyoMdV1o%3D",
            "http://v3-tt.ixigua.com/a19afa802ae1c3371a76c516b44aca10/59926aef/video/m/22041447f4cf89744549292e46589570497114edb600006adf6942edaf/",
            "http://v6-tt.ixigua.com/video/m/220efb3b0f771f54bda81902a0e6136736f114ee24000018409ae1b87c/?Expires=1502771497&AWSAccessKeyId=qh0h9TdcEMoS2oPj7aKX&Signature=hKhAGiMzo5%2B0Mm0v7mUisxfHStA%3D",
            "http://v6-tt.ixigua.com/video/m/2202c0ba432301e440c81d25474da9bd6c3114eb9800009778fc0a5590/?Expires=1502771539&AWSAccessKeyId=qh0h9TdcEMoS2oPj7aKX&Signature=FULAuMC4Sy287K%2B3JEBdyUGGZrg%3D",
            "http://pull-hls-l6-hs.pstatp.com/live/stream-6454447907377515277/index.m3u8"

    };

    public static String getTestUrl() {
        return URLS[0];
    }

    public static String getHttpsUrl() {
        return "https://v-cdn.zjol.com.cn/149064.mp4";
    }

    public static String[] getUrls() {
        return URLS;
    }

}
