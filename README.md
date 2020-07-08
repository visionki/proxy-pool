# IP代理池
依赖Mongodb数据库
# 使用方法
- 克隆代码到本地
- 修改`application.yml`配置文件中的mongodb数据库连接信息
- 打包运行即可

# 数据源
| 名称 | 链接 |
| ---- | ---- |
| 高可用全球免费代理IP库 | https://ip.jiangxianli.com/ |
| 小幻代理 | https://ip.ihuan.me/ |

暂时只抓取这两个网站的开放代理IP，可自己拓展，仿照`IpJob1`新建抓取线程类，再添加到`LoadIpTask`定时任务抓取。
# 检测规则
数据库内有两个表，`check_ip_pool`和`available_ip_pool`
- check_ip_pool
待检测表，所有获取到的原始数据都会进入这里。
- available_ip_pool
检测可用的IP存入此表。

