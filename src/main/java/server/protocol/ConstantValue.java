package server.protocol;

public class ConstantValue {
    /*
     * 开始标志位
     */
    public static final int HEAD_DATA=0x76767676;
    /*
    * 头部长度
    * private int head_data = ConstantValue.HEAD_DATA;      //开始标志位
      private long deviceIdHigh;                            //设备id高8位
      private long deviceIdLow;                             //设备id低8位
      private long sessionIdHigh;                           //回话id高8位
      private long sessionIdLow;                            //回话id低8位
      private long sequenceId;                              //序号id
      private long reserve;                                 //预留8字节标志位
      private int contentLength;                            //数据字段长度
    * */
    public static final int HEAD_LENGTH=4+8+8+8+8+8+8+4;
}
