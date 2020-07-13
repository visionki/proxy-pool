# IP代理池
依赖Mongodb数据库
# 使用方法
- 克隆代码到本地
- 修改`application.yml`配置文件中的mongodb数据库连接信息
- 打包运行即可

# 数据源
| 名称 | 链接 | 对应java类 | 备注 |
| ---- | ---- | ---- | ---- |
| 高可用全球免费代理IP库 | https://ip.jiangxianli.com | IpJob1 | 可用IP多 |
| 小幻代理 | https://ip.ihuan.me | IpJob2 | 可用IP多 |
| 89代理 | http://www.89ip.cn | IpJob3 | 可用IP少，如不用可去掉 |

暂时只抓取这两个网站的开放代理IP，可自己拓展，仿照`IpJob1`新建抓取线程类，再添加到`LoadIpTask`定时任务抓取。
# 数据库
有两个表，`check_ip_pool`和`available_ip_pool`
- check_ip_pool 待检测表，所有获取到的原始数据都会进入这里。
- available_ip_pool 检测可用的IP存入此表。
# 检测规则
**频率**

每分钟执行一次ip检测，若上次检测未完成则不执行。

**多线程**

线程池的最大线程设置为`CPU数 * 5`，实际工作启用线程目前是10，可在`AppConst.CHECK_THREAD_SIZE`修改。

**连接超时**

由于IP较多，所以目前IP检测的链接超时都是5秒，读取超时也是5秒。