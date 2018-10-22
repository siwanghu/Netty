package server.protocol;

public class ConstantValue {
    /*
     * 开始标志位
     */
    public static final int HEAD_DATA=0x57534B4A;       //WSKT对应的ascii码
    /*
    * 协议头部定义
    * ————————————————————————————
    *          开始标志位        丨         版本号
    * ————————————————————————————
    *                     设备id  (高位)
    *—————————————————————————————
    *                     设备id  (低位)
    *—————————————————————————————
    *                     回话id  (高位)
    *—————————————————————————————
    *                     回话id  (低位)
    *—————————————————————————————
    *                     序号id
    *—————————————————————————————
    *                     预留字段
    *—————————————————————————————
    *         校验码             丨        数据长度(单位字节)
    * —————————————————————————————
    * */
    public static final int HEAD_LENGTH=4+4+8+8+8+8+8+8+4+4;

    /*
    * 结尾标志位
    * auditoryworks对应的ascii码
    * */
    public static final byte[] Tail={0x61,0x75,0x64,0x69,0x74,0x6F,0x72,0x79,0x77,0x6F,0x72,0x6B,0x73};

    public static final String fileDir="C:\\Users\\ylchen\\Desktop\\upload";
}