官方版本描述请移步到官方存档仓库

由于淘宝等3A大作APP极其耗资源，每次打开淘宝稍微买个东西手机就巨热，然后会触发华为的杀后台机制，应该是doze啥的，导致syncthing一天要被杀好几十回服务，很难受，我就是需要它无感在后台运行。
上个月找了不少方法保活syncthing，都不太行，最终找到automate，通过automate的流程发广播来保活，但是也有个问题，automate的流程你让它响应快一点呢它一直很sharp的在那检测，一样耗电，隔30分钟呢又让我觉得很膈应，而且automate也需要打开无障碍来保活。开始我也不知道，没打开无障碍，automate一样被杀的死透透的。
后来看到GKD为了跳广告打开了无障碍，后面才知道automate也需要打开无障碍才能保活。

然后，用了几天，不是很爽，automate也占一个通知栏，你把他隐藏了呢，我也不知道有没有在给syncthing保活。
特么syncthing不是开源吗，我自己改个算球。

我是小白，完全不懂，早年有点basic，html，asp的基础，已经八百年没编过程。

这个版本完全是由AI帮我编完的。
刚开始只用了无障碍保活，用的是deepseek帮写的，但是发现没用，还是被杀。
后面干脆加上workermanager的15分钟保活，再加上无障碍一起了，结果是成功的，后面用的是GPT-4o写的。
感觉deepseek很亏，gpt胜在经验多（有大量的编程语料）

我fork的这个版本主要是增加了如下内容：
1. 增加了一个啥动作都没有的无障碍服务。
2. 增加了Android系统的workermanager，15分钟保活一次，也写入了Doze的白名单里面。


     adb日志如下：
       u0a429 tag=*job*/com.nutomic.syncthingandroid/androidx.work.impl.background.systemjob.SystemJobService
    Source: uid=u0a429 user=0 pkg=com.nutomic.syncthingandroid
    JobInfo:
      Service: com.nutomic.syncthingandroid/androidx.work.impl.background.systemjob.SystemJobService
      Requires: charging=false batteryNotLow=false deviceIdle=false
      Extras: mParcelledData.dataSize=248
      Minimum latency: +14m59s979ms
      Backoff: policy=1 initial=+30s0ms
      Has early constraint
    Required constraints: TIMING_DELAY [0x80000000]
    Dynamic constraints:
    Satisfied constraints: DEVICE_NOT_DOZING BACKGROUND_NOT_RESTRICTED WITHIN_QUOTA [0x3400000]
    Unsatisfied constraints: TIMING_DELAY [0x80000000]
    Constraint history:
      -5m2s428ms = BACKGROUND_NOT_RESTRICTED [0x400000]
      -5m2s428ms = DEVICE_NOT_DOZING BACKGROUND_NOT_RESTRICTED [0x2400000]
      -5m2s428ms = DEVICE_NOT_DOZING BACKGROUND_NOT_RESTRICTED WITHIN_QUOTA [0x3400000]
    Doze whitelisted: true
    Uid: active
    Tracking: TIME QUOTA
    Implicit constraints:
      readyNotDozing: true
      readyNotRestrictedInBg: true
      readyComponentEnabled: true
    Standby bucket: ACTIVE
    Enqueue time: -5m2s428ms
    Run time: earliest=+9m57s551ms, latest=none, original latest=none
    Restricted due to: none.
    Ready: false (job=false user=true !restricted=true !pending=true !active=true !backingup=true comp=true)



      经过一天多的测试，以及导出日志，还是成功的，所以就fork上来，给有需要的bro下载用，不用重复造车了，因为整个github也没人搞这玩意，难道你们都无所谓被杀后台么，我特么从几年前用小米的时候就被疯狂杀过后台。

   注意：
   需要手动操作的地方：
   1. 打开syncthing的开机启动服务
   2. 手动启用syncthing的无障碍服务
   3. 多任务界面把syncthing锁住
   4. 电池优化管理去掉自动管理，允许自启动，关联启动，后台活动。这三个不打开好像是workermanager保活机制貌似不起作用。
