一款通过adb 实现远程连接android设备的软件

ROOT后apk执行adb命令 打开tcp/ip调试Android设备的功能

非ROOT 设备也可以通过连接电脑的方式手动启动tcp/ip调试

此软件是为了方便ROOT设备以及连接了有[公网IP](https://jingyan.baidu.com/article/0964eca240949a8285f53697.html)的用户使用，通过定时访问获取公网IP地址的链接，发现运营商公网IP变更后利用[Server酱](http://sc.ftqq.com/)推送变更后的IP到微信。

注意事项：

家用路由器须手动启用[端口映射]([https://baike.baidu.com/item/%E7%AB%AF%E5%8F%A3%E6%98%A0%E5%B0%84/98247](https://baike.baidu.com/item/端口映射/98247))。公网地址刷新间隔设为-1将不会定时获取公网ip，此时可当作内网调试工具使用。需要查看设备实时画面可使用[scrcpy](https://github.com/Genymobile/scrcpy)，若没有公网ip可使用[zerotier](https://my.zerotier.com/)进行内网穿透，此处不再展开。

 

![](/pic.jpg)
![](/pic2.jpg)